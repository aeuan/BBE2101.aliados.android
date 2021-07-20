package com.dacodes.bepensa.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.ofline.packageSurvey.OflineSurveyEntity;
import com.dacodes.bepensa.entities.surveys.SurveyResult;
import com.google.gson.Gson;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.dacodes.bepensa.api.RetrofitWebServices.PUT_SURVEY_QUESTION;

public class UploadSurveyOfline extends Service implements RetrofitWebServices.DefaultResponseListeners{

    String TAG=UploadSurveyOfline.class.getSimpleName();
    AppController appController;
    Context mContext;
    Realm realm;
    RetrofitWebServices retrofitWebServices;

    CountDownTimer countDownTimer;
    ArrayList<OflineSurveyEntity> itemLocal=new ArrayList<>();
    int cont_item=0;

    @Override
    public void onCreate() {
        super.onCreate();
        appController = (AppController)getApplication();
        mContext =getApplicationContext();
        realm = Realm.getDefaultInstance();
        retrofitWebServices = new RetrofitWebServices(appController,TAG,mContext,this);
    }

    public UploadSurveyOfline() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        checkSync();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id){
            case PUT_SURVEY_QUESTION:
                try {
                    int id_survey = itemLocal.get(cont_item).getId();
                    final RealmResults<OflineSurveyEntity> results = realm.where(OflineSurveyEntity.class).equalTo("id", id_survey).findAll();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            results.deleteAllFromRealm();
                        }
                    });
                    cont_item++;
                }catch (Exception e){
                    e.printStackTrace();
                }
                if (cont_item<itemLocal.size()){
                    syncDataLocal(itemLocal);
                }else {
                    cont_item=0;
                    stopService();
                }
                break;
        }
    }

    @Override
    public void onFailError(int id, int status, String message) {
        switch (status){
            case -1:
            case 500:
                Toast.makeText(mContext,"No pudimos sincronizar tus datos",Toast.LENGTH_LONG).show();
                stopService();
                break;
            default:
                try {
                    int id_survey=itemLocal.get(cont_item).getId();
                    final RealmResults<OflineSurveyEntity> results = realm.where(OflineSurveyEntity.class).equalTo("id",id_survey).findAll();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            results.deleteAllFromRealm();
                        }
                    });
                    cont_item++;
                }catch (Exception e){
                    e.printStackTrace();
                }
                if (cont_item<itemLocal.size()){
                    syncDataLocal(itemLocal);
                }else {
                    cont_item=0;
                    stopService();
                }
                stopService();
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

    public void stopService(){
        Log.i(TAG,"Stop service");
        Intent intent = new Intent(mContext,UploadSurveyOfline.class);
        startService(intent);
    }

    public void syncDataLocal( ArrayList<OflineSurveyEntity> items){
        if (itemLocal.size()>0){
            Gson gson = new Gson();
            try{
                String dta=itemLocal.get(cont_item).getResult();
                Log.i("json",""+dta);
                SurveyResult surveyResult =gson.fromJson(dta,SurveyResult.class);
                retrofitWebServices.putSurveyQuestion(surveyResult);
            }catch (Exception e){
                Log.e(TAG,"CONVERT TO JSON:"+e.getLocalizedMessage());
                stopService();
            }
        }
    }

    public void checkSync(){
        String id_colaborator=appController.getUsername();
        RealmResults<OflineSurveyEntity> persons = realm.where(OflineSurveyEntity.class)
                .equalTo("has_sync", true).equalTo("id_colaborator",id_colaborator).findAll();
        if (persons.size()>0){
            this.itemLocal.addAll(persons);
            if (itemLocal.size()>0){
                syncDataLocal(itemLocal);
            }
        }
    }
}
