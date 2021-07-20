package com.dacodes.bepensa.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.DeviceId;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.activities.MainActivity;
import com.dacodes.bepensa.activities.PrincipalTest;
import com.dacodes.bepensa.adapters.RecyclerAdapterTestOne;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.CategoriesEntity;
import com.dacodes.bepensa.entities.OptionsItem;
import com.dacodes.bepensa.entities.ofline.packageSurvey.OflineSurveyEntity;
import com.dacodes.bepensa.entities.surveys.SurveyItemsEntity;
import com.dacodes.bepensa.entities.surveys.SurveyOptions;
import com.dacodes.bepensa.entities.surveys.SurveyResult;
import com.dacodes.bepensa.utils.AlertDIalogLogout;
import com.dacodes.bepensa.utils.ValidateNetwork;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.dacodes.bepensa.api.RetrofitWebServices.DELETE_DEVICE;
import static com.dacodes.bepensa.api.RetrofitWebServices.PUT_SURVEY_QUESTION;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTestPageOne extends Fragment implements RetrofitWebServices.DefaultResponseListeners
        ,AlertDIalogLogout.logout,AlertDIalogLogout.opportunity,AlertDIalogLogout.survey,RecyclerAdapterTestOne.addItem{
    String LOG_TAG=FragmentTestPageOne.class.getSimpleName();
    RetrofitWebServices retrofitWebServices;
    AppController appController;
    Context mContext;
    SurveyItemsEntity surveyItemEntitie=null;
    @BindView(R.id.tv_test)TextView tv_test;
    @BindView(R.id.recycler)RecyclerView recyclerView;
    Boolean statusFinalize =false;
    @BindView(R.id.btn_send)Button btn_send;
    private optionMultiple nextPage;
    int status_survey=0;
    int status_opportunity=0;
    int status_logout=0;

    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    List<CategoriesEntity> items=new ArrayList<>();
    ArrayList<Integer> itemId=new ArrayList<>();

    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id){
            case PUT_SURVEY_QUESTION:
                deleteItem();
                totalQuestion();
                break;
            case DELETE_DEVICE:
                retrofitWebServices.logoutAction();
                break;
        }
    }

    @Override
    public void onFailError(int id, int status, String message) {
        switch (status){
            case 1001:
                AlertDIalogLogout alertDIalogLogout = new AlertDIalogLogout(getActivity(),appController,this,this,this);
                alertDIalogLogout.sesionTerminate();
                break;
             default:{
                 Toast.makeText(mContext,""+message,Toast.LENGTH_LONG).show();
                 break;
             }
        }

        switch (id) {
            /*
            case PUT_SURVEY_QUESTION:
                final int id_survey = ((PrincipalTest) getActivity()).getSurvey_id();
                Gson gson = new Gson();
                final String res = gson.toJson(surveyResult);
                insertData(id_survey, res, true);
                break;*/
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
        deviceId.setToken(appController.getAccessToken());
        if (status_logout==1 && status_opportunity==1 &&status_survey==1){
            retrofitWebServices.deleteDevice(appController.getAccessToken(),appController.getAndroid_id(),deviceId);
        }
    }

    @Override
    public void addIdItem(int id) {
        if (itemId.size()>0){
            itemId.clear();
            itemId.add(0,id);
        }else {
            itemId.add(0, id);
        }
    }

    public interface optionMultiple{
        void optionMultiple(SurveyOptions surveyOptions);
    }

    ArrayList<SurveyOptions> item;
    SurveyResult surveyResult;
    Realm realm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nextPage =(optionMultiple)getActivity();
        realm =Realm.getDefaultInstance();
        mContext = getContext();
        appController=(AppController)getActivity().getApplication();
        retrofitWebServices =new RetrofitWebServices(appController,LOG_TAG,mContext,this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_test_page_one, container, false);
        ButterKnife.bind(this,view);
        Bundle bundle=new Bundle();
        surveyItemEntitie = (SurveyItemsEntity)getArguments().getSerializable("item");
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerAdapterTestOne(mContext,this.items,this);
        recyclerView.setAdapter(mAdapter);
        if (surveyItemEntitie !=null)
            loadOptions();
        if (getArguments().getString("status")!=null) {
            statusFinalize = true;
            btn_send.setText("Finalizar");
        }

        tv_test.setText(""+surveyItemEntitie.getName());
        return  view;
    }

    @OnClick(R.id.btn_send)
    public void compareqTO(){
        addTest();
    }

    public void loadOptions(){
        if (surveyItemEntitie.getOptions().size()>0){
            List<CategoriesEntity> list = new ArrayList<>();
            for (OptionsItem options: surveyItemEntitie.getOptions()){
                CategoriesEntity categoriesEntity = new CategoriesEntity();
                categoriesEntity.setId(options.getId());
                categoriesEntity.setName(options.getName());
                list.add(categoriesEntity);
            }
            populateRecyclerView(list);
        }
    }

    private void populateRecyclerView(List<CategoriesEntity> categoriesEntities){
        this.items.addAll(categoriesEntities);
        mAdapter.notifyDataSetChanged();
    }

    public void surveyData(){
        surveyResult = new SurveyResult();
        surveyResult.setSurvey_id(((PrincipalTest)getActivity()).getSurvey_id());
        item=((PrincipalTest)getActivity()).getItems();
        surveyResult.setItems(item);
        sendData(surveyResult);
    }

    public void sendData(final SurveyResult surveyResult){
        if (ValidateNetwork.getNetwork(mContext)) {
            retrofitWebServices.putSurveyQuestion(surveyResult);
        }else{
            showAlertDialog();
        }
    }

    public void insertData(final int id,final String data,final boolean status){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                OflineSurveyEntity object = bgRealm.createObject(OflineSurveyEntity.class);
                object.setResult(data);
                object.setHas_sync(status);
                object.setId(id);
                object.setHas_sync_load(true);
                object.setId_colaborator(appController.getUsername());
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.e(LOG_TAG,"Success Realm");
                totalQuestion();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.e(LOG_TAG,"onError Realm");
            }
        });
    }

    public void addTest(){
        SurveyOptions surveyOptions = new SurveyOptions();
        if (itemId.size()==0){
            Toast.makeText(mContext,"Porfavor seleccione una opcion",Toast.LENGTH_LONG).show();
        }else{
            surveyOptions.setOptions(itemId);
            surveyOptions.setType(surveyItemEntitie.getType());
            surveyOptions.setId(surveyItemEntitie.getId());
            nextPage.optionMultiple(surveyOptions);
            int cont=((PrincipalTest)getActivity()).getCont_test()+1;
            ((PrincipalTest)getActivity()).setCont_test(cont);
            if (statusFinalize) {
                surveyData();
            }else
                ((PrincipalTest)getActivity()).loadTest(cont);
        }
    }


    public void totalQuestion(){
        FragmentTotalQuestion fragmentTotalQuestion = new FragmentTotalQuestion();
        ((PrincipalTest) getActivity()).include();
        ((PrincipalTest)getActivity()).openFragment(fragmentTotalQuestion);
    }

    public void deleteItem(){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                int id=((PrincipalTest)getActivity()).getSurvey_id();
                RealmResults<OflineSurveyEntity> resul = realm.where(OflineSurveyEntity.class).equalTo("id",id).findAll();
                if (resul.size()>0){
                    resul.deleteAllFromRealm();
                }else{
                    Log.i(LOG_TAG,"Not found");
                }
            }
        });
    }

    public void loadLocal(){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<OflineSurveyEntity> realmResults = realm.where(OflineSurveyEntity.class).findAll();
                if (realmResults.size()>0){
                    boolean status_res=false;
                    final int id_survey=((PrincipalTest)getActivity()).getSurvey_id();
                    for (int i=0;i<realmResults.size();i++){
                        if (realmResults.get(i).getId()==id_survey){
                            Gson gson = new Gson();
                            final String res = gson.toJson(surveyResult);
                            Log.i("JSON_SURVEY_ONE",""+res);
                            status_res=true;
                            realmResults.get(i).setHas_sync(true);
                            realmResults.get(i).setResult(res);
                            realmResults.get(i).setHas_sync_load(true);
                            break;
                        }else{
                            realmResults.get(i).setHas_sync_load(false);
                        }
                    }
                    if (!status_res){
                        Gson gson = new Gson();
                        String res = gson.toJson(surveyResult);
                        insertData(id_survey,res,true);
                    }else{
                        backPressed();
                    }
                }

            }
        });
    }

    public void showAlertDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setMessage("Gracias por responder la encuesta, hemos detectado que el dispositivo no tiene acceso a internet, el resultado de " +
                "la encuesta ha sido almacenado localmente y lo publicaremos cuando encuentre una conexión a internet")
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    loadLocal();
                });
        builder.show();
    }

    public void backPressed(){
        if (((PrincipalTest)getActivity()).getBoletin()>=1){
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("intentTypeBack",6);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.no_anim, R.anim.exit_down);
        }else{
            if (((PrincipalTest)getActivity()).getEncuesta()>=1){
                Intent intent = new Intent(getActivity(),MainActivity.class);
                intent.putExtra("intentTypeBack",6);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.no_anim, R.anim.exit_down);
            }else{
                Intent intent = new Intent(getActivity(),MainActivity.class);
                intent.putExtra("intentTypeBack",1);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.no_anim, R.anim.exit_down);
            }
        }
    }
}
