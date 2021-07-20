package com.dacodes.bepensa.fragments;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.DeviceId;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.activities.MainActivity;
import com.dacodes.bepensa.activities.PrincipalTest;
import com.dacodes.bepensa.adapters.RecyclerViewSurveyAdapter;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.ofline.packageSurvey.OflineSurveyEntity;
import com.dacodes.bepensa.entities.surveys.result.SurveyDataEntity;
import com.dacodes.bepensa.models.SurveyResponseModel;
import com.dacodes.bepensa.service.DownloadJsonBrands;
import com.dacodes.bepensa.service.DownloadJsonDivision;
import com.dacodes.bepensa.service.DownloadJsonOppotunityType;
import com.dacodes.bepensa.service.DownloadJsonSurvey;
import com.dacodes.bepensa.utils.AlertDIalogLogout;
import com.dacodes.bepensa.utils.ValidateNetwork;
import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.dacodes.bepensa.api.RetrofitWebServices.DELETE_DEVICE;
import static com.dacodes.bepensa.api.RetrofitWebServices.GET_SURVEY;


public class FragmentTest extends Fragment implements  RecyclerViewSurveyAdapter.customClickListener
        ,RetrofitWebServices.DefaultResponseListeners,AlertDIalogLogout.logout,AlertDIalogLogout.opportunity,AlertDIalogLogout.survey{


    private Context mContext;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    ArrayList<SurveyDataEntity> surveyEntities = new ArrayList<>();
    @BindView(R.id.progressBar)ProgressBar progress_bar;
    @BindView(R.id.swipe)SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.et_search)EditText et_search;
    @BindView(R.id.recycler)RecyclerView mRecyclerView;
    @BindView(R.id.not_values_tv) TextView not_values_tv;
    @BindView(R.id.not_available_network_ll) LinearLayout not_available_network;

    Activity activity;
    AppController app;
    private static final String LOG_TAG = FragmentTest.class.getSimpleName();

    public int PAGE_SIZE = 0;
    public void setPAGE_SIZE(int PAGE_SIZE) {
        this.PAGE_SIZE = PAGE_SIZE;
    }
    private boolean isLoading = false;
    private boolean isLastPage = false;
    public void setLastPage(boolean lastPage) {
        isLastPage = lastPage;
    }
    private String pagination_next;
    RetrofitWebServices retrofitWebServices;
    Realm realm;
    boolean has_sync=false;
    ArrayList<Integer> id_has_sync=new ArrayList<>();
    String query_search="";
    int status_survey=0;
    int status_opportunity=0;
    int status_logout=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        activity = getActivity();
        app = (AppController)getActivity().getApplication();
        mContext = getActivity().getApplicationContext();
        retrofitWebServices = new RetrofitWebServices(app,LOG_TAG,mContext,this);
        realm = Realm.getDefaultInstance();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_fragment_test, container, false);
        ButterKnife.bind(this, view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerViewSurveyAdapter(mContext, this, this.surveyEntities);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(scrollListener);
        query_search=getResources().getString(R.string.base_url)+getResources().getString(R.string.api_get_survey)+"?name=";
        et_search.addTextChangedListener(textWatcher);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            downloadDatas();
            progress_bar.setVisibility(View.GONE);
            restartValues();
            validateConnectionNetwork();
        });
        ((MainActivity)getActivity()).setToolbar(6);
        return view;
    }

    public void newAdapter(){
        restartValues();
        mAdapter = new RecyclerViewSurveyAdapter(mContext,this,this.surveyEntities);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void validateConnectionNetwork(){
        if (ValidateNetwork.getNetwork(mContext)){
            disableDefaultValues();
            restartValues();
            checkSync();
            progress_bar.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            retrofitWebServices.getSurvey(null, app.getAccessToken());
        }else{
            newAdapter();
            restartValues();
            progress_bar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            loadLocalData();
            swipeRefreshLayout.setRefreshing(false);
            progress_bar.setVisibility(View.GONE);
        }

    }

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int visibleItemCount = mLayoutManager.getChildCount();
            int totalItemCount = mLayoutManager.getItemCount();
            int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();

            if (!isLoading && !isLastPage) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0) {
                    loadMoreItems();
                }
            }

        }
    };

    private void loadMoreItems(){
        isLoading = true;
        if(pagination_next!=null){
            retrofitWebServices.getSurvey(pagination_next, app.getAccessToken());
        }
    }

    public void checkSync(){
        RealmResults<OflineSurveyEntity> persons = realm.where(OflineSurveyEntity.class)
                .equalTo("has_sync_load", true).findAll();
        if (persons.size()>0){
            for (OflineSurveyEntity oflineSurveyEntity:persons){
                id_has_sync.add(oflineSurveyEntity.getId());
            }
            has_sync=true;
        }else{
            has_sync=false;
        }
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void afterTextChanged(Editable s) {
            retrofitWebServices.getSurvey(query_search+""+et_search.getText().toString(),app.getAccessToken());
        }
    };

    public void loadLocalData(){
        RealmResults<OflineSurveyEntity> realmResults = realm.where(OflineSurveyEntity.class).findAll();
        if (realmResults.size()>0){
            ArrayList<SurveyDataEntity> list = new ArrayList<>();
            for (int i=0;i<realmResults.size();i++){
                if (!realmResults.get(i).isHas_sync()) {
                    Gson gson = new Gson();
                    try {
                        SurveyDataEntity surveyDataEntitie = gson.fromJson(realmResults.get(i).getData(), SurveyDataEntity.class);
                        list.add(surveyDataEntitie);
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "ConverToGson:" + e.getLocalizedMessage());
                    }
                }
            }
            populateRecyclerView(list);
        }else{
            disableNetork();
        }
    }

    public void checkSyncLocalNetwork(ArrayList<SurveyDataEntity> surveyDataEntities){
        if (has_sync){
            ArrayList<SurveyDataEntity> item = new ArrayList<>();
            for (SurveyDataEntity surveyData:surveyDataEntities){
                boolean state=false;
                for (int i=0;i<id_has_sync.size();i++){
                    if (id_has_sync.get(i)==surveyData.getId()){
                        state=true;
                        break;
                    }else{
                        state=false;
                    }
                }
                if (!state){
                    item.add(surveyData);
                }
            }
            populateRecyclerView(item);
        }else{
            populateRecyclerView(surveyDataEntities);
        }
    }

    private void populateRecyclerView(ArrayList<SurveyDataEntity> surveysDataEntities){
        this.surveyEntities.addAll(surveysDataEntities);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id) {
            case GET_SURVEY:
                isLoading = false;
                progress_bar.setVisibility(View.GONE);
                final SurveyResponseModel responseModel= (SurveyResponseModel) object;
                if(responseModel.getPagination().getLinks().getNext().toString() !=null){
                    pagination_next = responseModel.getPagination().getLinks().getNext().toString();
                }else {
                    pagination_next = null;
                    isLastPage = true;
                }
                SurveyResponseModel response = (SurveyResponseModel) object;
                if (response.getData().size()<=0){
                    enableNotValues();
                }else{
                    checkSyncLocalNetwork(response.getData());
                    disableNotValues();
                }
                swipeRefreshLayout.setRefreshing(false);
                break;
            case DELETE_DEVICE:
                retrofitWebServices.logoutAction();
                break;
        }
    }

    @Override
    public void onFailError(int id, int status, String message) {
        progress_bar.setVisibility(View.GONE);
        switch (status){
            case 1001:
                AlertDIalogLogout alertDIalogLogout = new AlertDIalogLogout(getActivity(),app,this,this,this);
                alertDIalogLogout.sesionTerminate();
                break;
        }
        switch (id){
            case DELETE_DEVICE:
                retrofitWebServices.logoutAction();
                break;
        }
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

    @Override
    public void onItemClickListener(SurveyDataEntity surveyDataEntitie) {
        if (!surveyDataEntitie.isHasAnswered()) {
            Intent intent = new Intent(getActivity(), PrincipalTest.class);
            intent.putExtra("test", surveyDataEntitie);
            intent.putExtra("encuesta",1);
            startActivity(intent);
        }else{
            alertDialog();
        }
    }

    public void alertDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Bepensa")
                .setCancelable(false)
                .setMessage("Esta encuesta ha sido resuelta, gracias por colaborar con Bepensa")
                .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss());
            builder.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        validateConnectionNetwork();
    }

    @Override
    public void dialogSurvey(int status) {
        this.status_survey=status;
        compareDialog();
    }

    @Override
    public void dialogOpportunity(int status) {
        this.status_opportunity=status;
        compareDialog();
    }

    @Override
    public void dialogLogout(int status) {
        this.status_logout=status;
        compareDialog();
    }

    public void compareDialog(){
        DeviceId deviceId= new DeviceId();
        deviceId.setToken(app.getAccessToken());
        if (status_logout==1 && status_opportunity==1 &&status_survey==1){
            retrofitWebServices.deleteDevice(app.getAccessToken(),app.getAndroid_id(),deviceId);
        }
    }

    public void disableDefaultValues(){
        not_available_network.setVisibility(View.GONE);
        not_values_tv.setVisibility(View.GONE);
    }

    public void enableNotValues(){
        not_available_network.setVisibility(View.GONE);
        progress_bar.setVisibility(View.GONE);
        not_values_tv.setVisibility(View.VISIBLE);
    }

    public void disableNotValues(){
        progress_bar.setVisibility(View.GONE);
        not_available_network.setVisibility(View.GONE);
        not_values_tv.setVisibility(View.GONE);
    }

    public void disableNetork(){
        not_values_tv.setVisibility(View.GONE);
        not_available_network.setVisibility(View.VISIBLE);
    }

    public void restartValues(){
        if (surveyEntities.size()>0 && mAdapter!=null) {
            surveyEntities.clear();
            mAdapter.notifyDataSetChanged();
        }
    }


    public void downloadDatas(){
        if (!isMyServiceRunning(DownloadJsonSurvey.class)
                && !isMyServiceRunning(DownloadJsonDivision.class)
                && !isMyServiceRunning(DownloadJsonBrands.class) &&!isMyServiceRunning(DownloadJsonOppotunityType.class)){

            if (ValidateNetwork.getNetwork(mContext)){
                Intent intent = new Intent(mContext,DownloadJsonSurvey.class);
                getActivity().startService(intent);
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
