package com.dacodes.bepensa.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.PackegeOpportunity.DivisionEntity;
import com.dacodes.bepensa.entities.ofline.packageDivisions.OflineDivisionEntity;
import com.dacodes.bepensa.utils.ObjectToList;
import com.dacodes.bepensa.utils.ValidateNetwork;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.dacodes.bepensa.api.RetrofitWebServices.GET_DIVISIONS;

public class DownloadJsonDivision extends Service implements RetrofitWebServices.DefaultResponseListeners{
    String LOG_TAG=DownloadJsonDivision.class.getSimpleName();
    AppController appController;
    Context mContext;
    RetrofitWebServices retrofitWebServices;
    Realm realm;
    List<?> divisions = new ArrayList();

    public DownloadJsonDivision() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appController = (AppController)getApplication();
        mContext = getApplicationContext();
        retrofitWebServices=new RetrofitWebServices(appController,LOG_TAG,mContext,this);
        realm=Realm.getDefaultInstance();
        Log.e(LOG_TAG,"Servicio iniciado");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        retrofitWebServices.getDivisions();
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
            case GET_DIVISIONS:
                divisions = ObjectToList.ObjectToList(object);
                selectDivision();
                break;
        }
    }

    @Override
    public void onFailError(int id, int status, String message) {
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

    public void stop(){
        Intent intent = new Intent(mContext,DownloadJsonDivision.class);
        stopService(intent);
        realm.close();
        if (ValidateNetwork.getNetwork(mContext)) {
            try {
                Intent jsonBrands = new Intent(mContext, DownloadJsonBrands.class);
                startService(jsonBrands);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        Log.v("JsonDivision","Stop");
    }

    public void selectDivision(){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<OflineDivisionEntity> response = realm.where(OflineDivisionEntity.class).findAll();
                if (response.size()>0){
                    for (int i=0;i<divisions.size();i++){
                        DivisionEntity divisionEntity=(DivisionEntity)divisions.get(i);
                        boolean status=false;
                        for (OflineDivisionEntity list:response){
                            if (list.getId()==divisionEntity.getId()){
                                status=true;
                                break;
                            }else{
                                status=false;
                            }
                        }
                        if (!status){
                            if (divisionEntity.getId()!=4)
                                insertDataDivisions(divisionEntity);
                        }
                    }
                }else{
                    for (int i=0;i<divisions.size();i++){
                        DivisionEntity divisionEntity=(DivisionEntity)divisions.get(i);
                        if (divisionEntity.getId()!=4)
                            insertDataDivisions(divisionEntity);
                    }
                }
            }
        });

        stop();
    }

    public void insertDataDivisions(final DivisionEntity divisionEntity){
        if (divisionEntity.isAllow_reports()) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    OflineDivisionEntity listdivisions = realm.createObject(OflineDivisionEntity.class);
                    listdivisions.setId(divisionEntity.getId());
                    listdivisions.setName(divisionEntity.getName());
                    listdivisions.setAllow_reports(divisionEntity.isAllow_reports());
                    listdivisions.setBrands_title(divisionEntity.getBrands_title());
                    listdivisions.setStates(new Gson().toJson(divisionEntity.getStates()));
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    Log.w(LOG_TAG, "REALM_SUCCESS");
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    Log.w(LOG_TAG, "REALM_ERROR" + error.getLocalizedMessage());
                }
            });
        }
    }
}
