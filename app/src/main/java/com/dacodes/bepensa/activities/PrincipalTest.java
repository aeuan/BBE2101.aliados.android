package com.dacodes.bepensa.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.surveys.SurveyOptions;
import com.dacodes.bepensa.entities.surveys.result.SurveyDataEntity;
import com.dacodes.bepensa.fragments.FragmentTestPageOne;
import com.dacodes.bepensa.fragments.FragmentTestPageThree;
import com.dacodes.bepensa.fragments.FragmentTestPageTwo;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.dacodes.bepensa.api.RetrofitWebServices.GET_SURVEY_DETAIL;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PrincipalTest extends AppCompatActivity implements FragmentTestPageOne.optionMultiple
,FragmentTestPageTwo.openQuestion,FragmentTestPageThree.multipleResponse,RetrofitWebServices.DefaultResponseListeners {

    String LOG_TAG=PrincipalTest.class.getSimpleName();

    SurveyDataEntity surveyDataEntities;
    int cont_test=0;
    int survey_id=0;
    int survey_points=0;
    int total_page=0;
    RetrofitWebServices retrofitWebServices ;
    AppController app;
    Context mContext;

    public int getSurvey_id() {
        return survey_id;
    }

    public void setSurvey_id(int survey_id) {
        this.survey_id = survey_id;
    }

    public int getSurvey_points() {
        return survey_points;
    }

    public void setSurvey_points(int survey_points) {
        this.survey_points = survey_points;
    }

    public int getCont_test() {
        return cont_test;
    }

    public void setCont_test(int cont_test) {
        this.cont_test = cont_test;
    }

    @BindView(R.id.tv_title)TextView tv_title;
    @BindView(R.id.tv_opcion)TextView tv_opcion;
    @BindView(R.id.tv_page)TextView tv_page;
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.progressBar)ProgressBar progressBar;
    @BindView(R.id.include)View include;

    int boletin=0;
    int encuesta=0;

    ArrayList<SurveyOptions> items = new ArrayList<>();

    public ArrayList<SurveyOptions> getItems() {
        return items;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_test);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
        app=(AppController)getApplication();
        retrofitWebServices = new RetrofitWebServices(app,LOG_TAG,mContext,this);
        toolbar.setTitle("ENCUESTA");
        toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorOrange));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar.setVisibility(View.VISIBLE);
        include.setVisibility(View.GONE);
        if (getIntent().getExtras().getString("boletin")!=null){
            setBoletin(3);
        }
        if (getIntent().getExtras().getString("encuesta")!=null){
            setEncuesta(5);
        }

        encuesta=getIntent().getIntExtra("encuesta",0);
        if (getIntent().getExtras() !=null){
            int eventId = getIntent().getIntExtra("surveyId", 0);
            if (eventId>=1){
                retrofitWebServices.getSurveyDetail(eventId);
            }else {
                progressBar.setVisibility(View.GONE);
                include.setVisibility(View.VISIBLE);
                surveyDataEntities = (SurveyDataEntity) getIntent().getSerializableExtra("test");
                setSurvey_id(surveyDataEntities.getId());
                setSurvey_points(surveyDataEntities.getPoints());
                total_page = surveyDataEntities.getItems().size();
                loadTest(getCont_test());
            }
        }
    }

    public int getBoletin() {
        return boletin;
    }

    public void setBoletin(int boletin) {
        this.boletin = boletin;
    }

    public int getEncuesta() {
        return encuesta;
    }

    public void setEncuesta(int encuesta) {
        this.encuesta = encuesta;
    }

    public void loadTextView(int page_next, int type){
        tv_title.setText(surveyDataEntities.getName());
        switch (type){
            case 1:
                tv_opcion.setText("Opción multiple");
                break;
            case 2:
                tv_opcion.setText("Selección multiple");
                break;
            case 3:
                tv_opcion.setText("Pregunta abierta");
                break;
        }
        tv_page.setText(""+page_next+"/"+total_page);
    }

    public void loadTest(int pos){
        if (getCont_test()<surveyDataEntities.getItems().size()) {
            Bundle bundle;
            if ((getCont_test()+1)==surveyDataEntities.getItems().size())
                bundle = sendBundeFinalize(pos);
            else
                bundle=sendBunde(pos);
            int type = surveyDataEntities.getItems().get(pos).getType();
            switch (type) {
                case 1: {
                    loadTextView(pos+1,1);
                    FragmentTestPageOne fragmentTestPageOne = new FragmentTestPageOne();
                    fragmentTestPageOne.setArguments(bundle);
                    openFragment(fragmentTestPageOne);
                    break;
                }
                case 2: {
                    loadTextView(pos+1,2);
                    FragmentTestPageThree fragmentTestPageThree = new FragmentTestPageThree();
                    fragmentTestPageThree.setArguments(bundle);
                    openFragment(fragmentTestPageThree);
                    break;
                }
                case 3: {
                    loadTextView(pos+1,3);
                    FragmentTestPageTwo fragmentTestPageTwo = new FragmentTestPageTwo();
                    fragmentTestPageTwo.setArguments(bundle);
                    openFragment(fragmentTestPageTwo);
                    break;
                }
            }
        }
    }

    public void openFragment(final Fragment fragment)   {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, fragment);
        fragmentManager.popBackStack();
        transaction.commit();
    }

    private Bundle sendBunde(int pos){
        Bundle bundle = new Bundle();
        bundle.putSerializable("item", surveyDataEntities.getItems().get(pos));
        return bundle;
    }

    private Bundle sendBundeFinalize(int pos){
        Bundle bundle = new Bundle();
        bundle.putSerializable("item", surveyDataEntities.getItems().get(pos));
        bundle.putString("status","1");
        return bundle;
    }

    public void include(){
        View view = findViewById(R.id.include);
        view.setVisibility(View.GONE);
    }

    public void loadAllSurvey(SurveyDataEntity surveyDataEntitie){
        surveyDataEntities =surveyDataEntitie;
        loadTest(getCont_test());
    }

    @Override
    public void optionMultiple(SurveyOptions surveyOptions) {
        items.add(surveyOptions);
    }

    @Override
    public void openQuestion(SurveyOptions surveyOptions) {
        items.add(surveyOptions);
    }

    @Override
    public void multipleResponse(SurveyOptions surveyOptions) {
        items.add(surveyOptions);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackItem();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        onBackItem();
    }

    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id) {
            case GET_SURVEY_DETAIL:
                SurveyDataEntity responseModel= (SurveyDataEntity) object;
                loadAllSurvey(responseModel);
                include.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onFailError(int id, int status, String message) {

    }

    @Override
    public void onWebServiceStart() {

    }

    @Override
    public void onWebServiceEnd() {

    }

    @Override
    public void onExpireToken() {

    }

    public void onBackItem(){
        if (getBoletin()>0){
            Intent intent = new Intent(mContext,MainActivity.class);
            intent.putExtra("intentTypeBack",2);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
        }else{
            int eventId = getIntent().getIntExtra("surveyId", 0);
            if (getEncuesta()>0){
                Intent intent = new Intent(mContext,MainActivity.class);
                intent.putExtra("intentTypeBack",6);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
            }else if(eventId>0){
                Intent intent = new Intent(mContext,MainActivity.class);
                intent.putExtra("intentTypeBack",6);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
            }else{
                Intent intent = new Intent(mContext,MainActivity.class);
                intent.putExtra("intentTypeBack",1);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
            }
        }
    }
}
