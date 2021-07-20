package com.dacodes.bepensa.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.PackegeOpportunity.BrandEntity;
import com.dacodes.bepensa.entities.ofline.packageBrands.OflineBrandsEntity;
import com.dacodes.bepensa.entities.ofline.packageDivisions.OflineDivisionEntity;
import com.dacodes.bepensa.utils.ObjectToList;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.dacodes.bepensa.api.RetrofitWebServices.GET_BRANDS;

public class DownloadJsonBrands extends Service implements RetrofitWebServices.DefaultResponseListeners {
    String LOG_TAG=DownloadJsonBrands.class.getSimpleName();
    AppController appController;
    Context mContext;
    RetrofitWebServices retrofitWebServices;
    Realm realm;
    private List<BrandEntity> brandsList = new ArrayList<>();

    public DownloadJsonBrands() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appController = (AppController)getApplication();
        mContext = getApplicationContext();
        retrofitWebServices=new RetrofitWebServices(appController,LOG_TAG,mContext,this);
        realm = Realm.getDefaultInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        retrofitWebServices.getBrand(appController.getAccessToken());
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
            case GET_BRANDS:
                List<? >list = ObjectToList.ObjectToList(object);
                for (Object entity : list) {
                    BrandEntity brandEntity = (BrandEntity) entity;
                    brandsList.add(brandEntity);
                }
                selectBrands();
                break;
        }
    }

    @Override
    public void onFailError(int id, int status, String message) {
        Log.e(LOG_TAG,"status:"+status+"::message::"+message);
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
        Intent intent = new Intent(mContext,DownloadJsonBrands.class);
        stopService(intent);
        Log.e(LOG_TAG,"stopService");
        realm.close();
    }

    public void selectBrands(){
        realm.executeTransaction(realm -> {
            RealmResults<OflineBrandsEntity> response = realm.where(OflineBrandsEntity.class).findAll();
            response.deleteAllFromRealm();
            for (int i=0;i<brandsList.size();i++){
                insertDataBrandsV4(brandsList.get(i));
            }
        });
        stop();
    }

    private void insertDataBrandsV4(final BrandEntity brandEntity){
        realm.executeTransactionAsync(rboject -> {
                    OflineBrandsEntity  listBrands = rboject.createObject(OflineBrandsEntity.class);
                    RealmResults<OflineDivisionEntity> ltdivision=rboject.where(OflineDivisionEntity.class).findAll();
                    if (ltdivision.size()>0){
                        for (OflineDivisionEntity div:ltdivision){
                            if (div.getId()==brandEntity.getDivisionEntity().getId()){
                                listBrands.setDivisionEntity(div);
                                break;
                            }
                        }
                    }else{
                        OflineDivisionEntity oflineBrandsEntity=insertDataDivisions(listBrands);
                        if (oflineBrandsEntity!=null){
                            listBrands.setDivisionEntity(oflineBrandsEntity);
                        }
                    }
                    listBrands.setId(brandEntity.getId());
                    listBrands.setName(brandEntity.getName());
                    listBrands.setLogo(brandEntity.getLogo());
                    listBrands.setRequired_location(brandEntity.isRequired_location());
                    listBrands.setRequired_media(brandEntity.isRequired_media());
                    listBrands.setExtra_field_title(brandEntity.getExtra_field_title());
                    listBrands.setOpportunity_types(new Gson().toJson(brandEntity));

                }, () -> Log.e(LOG_TAG,"REALM_SUCCESS"),
                error -> Log.e(LOG_TAG,"REALM_ERROR"+error.getLocalizedMessage()));
    }

    public OflineDivisionEntity insertDataDivisions(final OflineBrandsEntity brandEntity){
        final OflineDivisionEntity[] oflineDivisionEntity = new OflineDivisionEntity[2];
        realm.executeTransactionAsync(realm -> {
            oflineDivisionEntity[0] = realm.createObject(OflineDivisionEntity.class);
            oflineDivisionEntity[0].setName(brandEntity.getDivisionEntity().getName());
            oflineDivisionEntity[0].setId(brandEntity.getDivisionEntity().getId());
            oflineDivisionEntity[0].setAllow_reports(brandEntity.getDivisionEntity().isAllow_reports());
            oflineDivisionEntity[0].setBrands_title(brandEntity.getDivisionEntity().getBrands_title());
        }, () -> Log.w(LOG_TAG,"REALM_SUCCESS")
                , error -> Log.w(LOG_TAG,"REALM_ERROR"+error.getLocalizedMessage()));

        return oflineDivisionEntity[0];
    }

}
