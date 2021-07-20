package com.dacodes.bepensa.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.ofline.packageSurvey.OflineSurveyEntity;
import com.dacodes.bepensa.entities.surveys.result.SurveyDataEntity;
import com.dacodes.bepensa.models.SurveyResponseModel;
import com.dacodes.bepensa.utils.ValidateNetwork;
import com.google.gson.Gson;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.dacodes.bepensa.api.RetrofitWebServices.GET_SURVEY;

public class DownloadJsonSurvey extends Service implements RetrofitWebServices.DefaultResponseListeners {

    String LOG_TAG=DownloadJsonSurvey.class.getSimpleName();
    Realm realm;
    AppController appController;
    Context mContext;
    RetrofitWebServices retrofitWebServices;
    ArrayList<SurveyDataEntity> surveyDataEntities = new ArrayList<>();
    String last_page="";

    boolean status_local=false;
    ArrayList<OflineSurveyEntity> items = new ArrayList<>();

    public DownloadJsonSurvey() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appController = (AppController)getApplication();
        mContext = getApplicationContext();
        realm = Realm.getDefaultInstance();
        retrofitWebServices = new RetrofitWebServices(appController,LOG_TAG,mContext,this);
        Log.e(LOG_TAG,"Servicio iniciado");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        selectLocal();
        retrofitWebServices.getSurvey(null,appController.getAccessToken());
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id){
            case GET_SURVEY:
                SurveyResponseModel surveyResponseModel = (SurveyResponseModel)object;
                if (surveyResponseModel.getPagination().getLinks().getNext().toString()!=null){
                    last_page=surveyResponseModel.getPagination().getLinks().getNext().toString();
                }
                if (surveyResponseModel.getData().size()>0){
                    addModel(surveyResponseModel.getData());
                }else{
                    stop();
                }
                break;
        }
    }

    @Override
    public void onFailError(int id, int status, String message) {
        Log.e(LOG_TAG,"onFailError");
        stop();
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

    public void addModel(ArrayList<SurveyDataEntity> surveyData){
        for (SurveyDataEntity surveyDataEntitie:surveyData){
            if (!surveyDataEntitie.isHasAnswered()){
                this.surveyDataEntities.add(surveyDataEntitie);
            }
        }
        if (surveyDataEntities.size()>0) {
            if (status_local) {
                for (int i=0;i<surveyDataEntities.size();i++){
                    boolean status=false;
                    for (int j=0;j<items.size();j++){
                        if (items.get(j).getId()==surveyDataEntities.get(i).getId()){
                            status=true;
                            break;
                        }else{
                            status=false;
                        }
                    }
                    if (!status){
                        int id = surveyDataEntities.get(i).getId();
                        Gson gson = new Gson();
                        String data = gson.toJson(surveyDataEntities.get(i));
                        boolean has_sync = surveyDataEntities.get(i).isHasAnswered();
                        insertData(id, data, has_sync);
                    }else{
                        Log.i(LOG_TAG,"EXISTE");
                    }
                }
            } else {
                for (SurveyDataEntity surveyDataEntitie : surveyDataEntities) {
                    int id = surveyDataEntitie.getId();
                    Gson gson = new Gson();
                    String data = gson.toJson(surveyDataEntitie);
                    boolean has_sync = surveyDataEntitie.isHasAnswered();
                    insertData(id, data, has_sync);
                }
            }
        }
        stop();
    }

    public void insertData(final int id, final String data,final boolean status){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                OflineSurveyEntity object = bgRealm.createObject(OflineSurveyEntity.class);
                object.setData(data);
                object.setHas_sync(status);
                object.setId(id);
                object.setId_colaborator(appController.getUsername());
                object.setHas_sync_load(false);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.i(LOG_TAG,"Success Realm");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.e(LOG_TAG,"onError Realm");
            }
        });
    }

    public void selectLocal(){
        RealmResults<OflineSurveyEntity> realmResults =realm.where(OflineSurveyEntity.class).findAll();
        if (realmResults.size()>0){
            status_local=true;
            Log.e(LOG_TAG,"Success Local"+realmResults.size());
            ArrayList<OflineSurveyEntity> list=new ArrayList<>();
            for (OflineSurveyEntity oflineSurveyEntity:realmResults){
                list.add(oflineSurveyEntity);
            }
            setItems(list);
        }else{
            Log.e(LOG_TAG,"Sin datos");
            status_local=false;
        }
    }

    public void setItems(ArrayList<OflineSurveyEntity> items) {
        this.items = items;
    }

    public ArrayList<OflineSurveyEntity> getItems(){
        return items;
    }

    public void stop(){
        Intent intent = new Intent(mContext,DownloadJsonSurvey.class);
        stopService(intent);
        realm.close();
        if (ValidateNetwork.getNetwork(mContext)){
            if (last_page!=null && last_page!=""){
                retrofitWebServices.getSurvey(last_page,appController.getAccessToken());
                last_page=null;
            }else {
                try {
                    Intent jsonDivision = new Intent(mContext, DownloadJsonDivision.class);
                    startService(jsonDivision);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        Log.e(LOG_TAG,"stopService");
    }
}
