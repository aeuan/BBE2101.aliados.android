package com.dacodes.bepensa.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.Opportunity;
import com.dacodes.bepensa.entities.OpportunityType;
import com.dacodes.bepensa.entities.ofline.packageDivisions.OflineDivisionEntity;
import com.dacodes.bepensa.entities.ofline.packageOpportunity.OflineOpportunity;
import com.dacodes.bepensa.entities.ofline.packageOpportunity.OflineOpportunityType;
import com.dacodes.bepensa.utils.ObjectToList;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static com.dacodes.bepensa.api.RetrofitWebServices.GET_OPPORTUNITIES_TYPE;

public class DownloadJsonOppotunityType extends Service implements RetrofitWebServices.DefaultResponseListeners {

    String LOG_TAG=DownloadJsonOppotunityType.class.getSimpleName();
    AppController appController;
    Context mContext;
    RetrofitWebServices retrofitWebServices;
    Realm realm;

    ArrayList<Opportunity> opportunities = new ArrayList<>();
    public DownloadJsonOppotunityType() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appController=(AppController)getApplication();
        mContext = getApplicationContext();
        retrofitWebServices = new RetrofitWebServices(appController,LOG_TAG,mContext,this);
        realm = Realm.getDefaultInstance();
        Log.e(LOG_TAG,"Servicio iniciado");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        retrofitWebServices.getOpportunitiesTypes(appController.getAccessToken());
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
            case GET_OPPORTUNITIES_TYPE:
                List<? > lista1 = ObjectToList.ObjectToList(object);
                for (Object entity1 : lista1) {
                    Opportunity opportunity = (Opportunity) entity1;
                    opportunities.add(opportunity);
                }
                selectBrands();
                break;
        }
    }

    @Override
    public void onFailError(int id, int status, String message) {
        stop();
        Log.e(LOG_TAG,"onFailError");
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

    public void selectBrands(){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<OflineOpportunity> response = realm.where(OflineOpportunity.class).findAll();
                if (response.size()>0){
                    for (int i=0;i<opportunities.size();i++){
                        //DivisionEntity divisionEntity=(DivisionEntity)divisions.get(i);
                        boolean status=false;
                        for (OflineOpportunity list:response){
                            if (list.getId()==opportunities.get(i).getId()){
                                status=true;
                                break;
                            }else{
                                status=false;
                            }
                        }
                        if (!status && opportunities.get(i).getOpportunityTypes().size()>0){
                            insertDataBrands(opportunities.get(i));
                        }
                    }
                }else{
                    for (int i=0;i<opportunities.size();i++){
                        if (opportunities.get(i).getOpportunityTypes().size()>0)
                            insertDataBrands(opportunities.get(i));
                        else{
                            Log.w(LOG_TAG,"NOT_ADD_OPPORTUNITIES");
                        }
                    }
                }
            }
        });

        stop();
    }

    public void insertDataBrands(final Opportunity opportunity){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                OflineOpportunity  listBrands = realm.createObject(OflineOpportunity.class);
                RealmList<OflineOpportunityType> list= new RealmList<>();
                for (OpportunityType types:opportunity.getOpportunityTypes()){
                    OflineOpportunityType ofline = realm.createObject(OflineOpportunityType.class);
                    ofline.setId(types.getId());
                    ofline.setName(types.getName());
                    ofline.setPoints(types.getPoints());

                    RealmResults<OflineDivisionEntity> ltdivision=realm.where(OflineDivisionEntity.class).findAll();
                    if (ltdivision.size()>0){
                        for (OflineDivisionEntity div:ltdivision){
                            if (div.getId()==types.getDivision().getId()){
                                ofline.setDivisionEntity(div);
                                break;
                            }
                        }
                    }else {
                        OflineDivisionEntity oflineBrandsEntity=insertDataDivisions(types);
                        if (oflineBrandsEntity!=null){
                            ofline.setDivisionEntity(oflineBrandsEntity);
                        }
                    }
                    list.add(ofline);
                }
                listBrands.setId(opportunity.getId());
                listBrands.setName(opportunity.getName());
                listBrands.setOflineOpportunityTypes(list);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.w(LOG_TAG,"REALM_SUCCESS");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.w(LOG_TAG,"REALM_ERROR");
            }
        });
    }
    public void stop(){
        Intent intent = new Intent(mContext,DownloadJsonOppotunityType.class);
        stopService(intent);
        realm.close();
        Log.e(LOG_TAG,"stopService");
    }

    public OflineDivisionEntity insertDataDivisions(OpportunityType types){
        final OflineDivisionEntity[] division = new OflineDivisionEntity[2];
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                division[0] = realm.createObject(OflineDivisionEntity.class);
                division[0].setId(types.getDivision().getId());
                division[0].setName(types.getDivision().getName());
                division[0].setAllow_reports(types.getDivision().isAllow_reports());
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.w(LOG_TAG,"REALM_SUCCESS");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.w(LOG_TAG,"REALM_ERROR"+error.getLocalizedMessage());
            }
        });
        return division[0];
    }
}
