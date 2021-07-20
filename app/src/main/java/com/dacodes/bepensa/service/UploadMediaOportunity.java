package com.dacodes.bepensa.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.BuildConfig;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.MediasFilesEntity;
import com.dacodes.bepensa.entities.ofline.packageSurvey.OflineSurveyEntity;
import com.dacodes.bepensa.entities.ofline.postOpportunity.OflineListMedia;
import com.dacodes.bepensa.entities.ofline.postOpportunity.OflineMediaOpportunity;
import com.dacodes.bepensa.entities.ofline.postOpportunity.OflinePostOpportunity;
import com.dacodes.bepensa.models.NewOpportunityResponse;
import com.dacodes.bepensa.utils.ValidateNetwork;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.dacodes.bepensa.api.RetrofitWebServices.POST_OPPORTUNITY;
import static com.dacodes.bepensa.api.RetrofitWebServices.PUT_MEDIA_OPPORTUNITY;

public class UploadMediaOportunity extends Service implements RetrofitWebServices.DefaultResponseListeners {
    String LOG_TAG  = UploadMediaOportunity.class.getSimpleName();
    private MultipartBody.Part media_image0 = null;
    private MultipartBody.Part media_image1 = null;
    private MultipartBody.Part media_image2 = null;
    private MultipartBody.Part media_video0 = null;
    private int id_oportunity;
    RetrofitWebServices retrofitWebServices;
    AppController appController;
    Context context;
    Realm realm;
    ArrayList<Integer> ids=new ArrayList<>();
    ArrayList<OflineListMedia> itemsMedias=new ArrayList<>();
    CountDownTimer countDownTimer;

    ArrayList<OflineSurveyEntity> itemLocal=new ArrayList<>();
    int cont_item=0;
    int cont_opor_local=0;

    int cont_error_media=0;
    int con_media_load=0;
    ArrayList<OflinePostOpportunity> itemOpportunityLoca=new ArrayList<>();
    ArrayList<String> hash_opportunity=new ArrayList<>();
    int con_medias=0;
    public UploadMediaOportunity() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appController = (AppController)getApplication();
        context = getApplicationContext();
        retrofitWebServices = new RetrofitWebServices(appController, LOG_TAG, context, this);
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        checkSuncMediaFails();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void checkSuncMediaFails(){
        final RealmResults<OflineListMedia> result1 = realm.where(OflineListMedia.class).equalTo("has_sync", true).findAll();
        if (result1.size() > 0) {
            this.itemsMedias.addAll(result1);
            if (ValidateNetwork.getNetwork(context)) {
                if (itemsMedias.size()>0) {
                    checkUploadMedia(itemsMedias);
                }else{
                    stop();
                }
            }else{
                stop();
            }
        } else {
            checkSyncOpportunity();
        }
    }

    public void checkSyncOpportunity(){
        RealmResults<OflinePostOpportunity> persons = realm.where(OflinePostOpportunity.class)
                .equalTo("has_sync", true).findAll();
        if (persons.size()>0){
            this.itemOpportunityLoca.addAll(persons);
            if (ValidateNetwork.getNetwork(context)){
                if (itemOpportunityLoca.size()>0){
                    for (OflinePostOpportunity lista:itemOpportunityLoca){
                        hash_opportunity.add(lista.getKey_hash());
                    }
                    newUpload(itemOpportunityLoca);
                }else{
                    stop();
                }
            }else{
                stop();
            }
        }else{
            stop();
        }
    }

    public void mediaLoad(int id_oportun,List<MediasFilesEntity> mediasFilesEnti){
        for(MediasFilesEntity mediasFilesEntity : mediasFilesEnti){
            if(mediasFilesEntity.getType() == 1){//Image
                if(media_image0==null){
                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), mediasFilesEntity.getFile());
                    media_image0 = MultipartBody.Part.createFormData("media_image0", mediasFilesEntity.getFile().getName(), requestFile);
                }else if(media_image1==null){
                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), mediasFilesEntity.getFile());
                    media_image1 = MultipartBody.Part.createFormData("media_image1", mediasFilesEntity.getFile().getName(), requestFile);
                }else if(media_image2==null){
                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), mediasFilesEntity.getFile());
                    media_image2 = MultipartBody.Part.createFormData("media_image2", mediasFilesEntity.getFile().getName(), requestFile);
                }
            }else if(mediasFilesEntity.getType() == 2){//Video
                RequestBody requestFile = RequestBody.create(MediaType.parse("video/mp4"), mediasFilesEntity.getFile());
                media_video0 = MultipartBody.Part.createFormData("media_video0", mediasFilesEntity.getFile().getName(), requestFile);
            }
        }
        appController.setIdOpportunity(id_oportun);
        retrofitWebServices.putMediaOpportunity(id_oportun,media_image0,media_image1,media_image2,media_video0);
    }

    public void deleteMedias(List<MediasFilesEntity> mediasFilesEntities){
        for (MediasFilesEntity mediasFilesEntity:mediasFilesEntities){
            try {
                File file = mediasFilesEntity.getFile();
                file.delete();
            }catch (Exception e){
                Log.i(LOG_TAG,"error delete file");
            }
        }
    }

    public void stop(){
        Log.i("STOP_SERVICE","STOP");
        Intent intent = new Intent(context,UploadMediaOportunity.class);
        stopService(intent);
        stopCountDownTimer();
        realm.close();
    }
    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id){
            case PUT_MEDIA_OPPORTUNITY: {
                if (itemsMedias.size()>0){
                    final int id_oportunity = itemsMedias.get(con_media_load).getId_opportunity();
                    final RealmResults<OflineListMedia> result = realm.where(OflineListMedia.class).equalTo("id_opportunity", id_oportunity).findAll();
                    if (result.size()>0) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                result.deleteAllFromRealm();
                            }
                        });
                    }
                    appController.setIdOpportunity(0);
                    con_media_load++;
                    if (con_media_load<itemsMedias.size()) {
                        checkUploadMedia(itemsMedias);
                    }else{
                        itemsMedias.clear();
                        checkSyncOpportunity();
                    }
                }else {
                    try {
                        final String key_hash = hash_opportunity.get(cont_opor_local);
                        final RealmResults<OflineListMedia> result = realm.where(OflineListMedia.class).equalTo("key_hash", key_hash).findAll();
                        if (result.size()>0) {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    result.deleteAllFromRealm();
                                }
                            });
                        }
                        // deleteOpportunity();
                        Log.i("DELETE_POST_OPPORTUNITY","True");
                        cont_opor_local++;
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.e("error",""+e.getLocalizedMessage());
                    }
                    if (cont_opor_local < itemOpportunityLoca.size()) {
                        int id_division = itemOpportunityLoca.get(cont_opor_local).getId_division();
                        int id_oportunity = itemOpportunityLoca.get(cont_opor_local).getId_oportunity();
                        int id_region = itemOpportunityLoca.get(cont_opor_local).getId_region();
                        int has_media = itemOpportunityLoca.get(cont_opor_local).getHas_media();
                        String brands = itemOpportunityLoca.get(cont_opor_local).getBrands();
                        String state = ""+itemOpportunityLoca.get(cont_opor_local).getState();
                        String description = itemOpportunityLoca.get(cont_opor_local).getDescription();
                        String city = "";
                        double lat = itemOpportunityLoca.get(cont_opor_local).getLat();
                        double lng = itemOpportunityLoca.get(cont_opor_local).getLng();
                        String text_option_title=itemOpportunityLoca.get(cont_opor_local).getExtra_field_title();
                        String text_option_value=itemOpportunityLoca.get(cont_opor_local).getExtra_field_value();
                        String key_hash=itemOpportunityLoca.get(cont_opor_local).getKey_hash();
                        String address= itemOpportunityLoca.get(cont_opor_local).getAddress();
                        String version= BuildConfig.VERSION_NAME;
                        if (text_option_title!=null && text_option_value!=null && (itemOpportunityLoca.get(cont_opor_local).isRequire_location()) || itemOpportunityLoca.get(cont_opor_local).isContainer_location()) {
                            retrofitWebServices.postOpportunitiesLocal(key_hash,address,version,id_division,id_oportunity,brands,description,lat,lng,state,city
                                    ,has_media,id_region,text_option_title,text_option_value);
                        }else if (text_option_title!=null && text_option_value!=null && !itemOpportunityLoca.get(cont_opor_local).isRequire_location()) {
                            retrofitWebServices.postOpportunitiesLocal(key_hash,version,id_division,id_oportunity,brands,description
                                    ,has_media,text_option_title,text_option_value);
                        }else if (text_option_title!=null && (itemOpportunityLoca.get(cont_opor_local).isRequire_location()) || itemOpportunityLoca.get(cont_opor_local).isContainer_location()){
                            retrofitWebServices.postOpportunitiesLocal(key_hash,address,version,id_division,id_oportunity,brands,description,lat,lng,state,city
                                    ,has_media,id_region,text_option_title);
                        }else if (text_option_title!=null && !itemOpportunityLoca.get(cont_opor_local).isRequire_location()){
                            retrofitWebServices.postOpportunitiesLocal(key_hash,version,id_division,id_oportunity,brands,description
                                    ,has_media,text_option_title);
                        }else if (itemOpportunityLoca.get(cont_opor_local).isRequire_location() || itemOpportunityLoca.get(cont_opor_local).isContainer_location()){
                            retrofitWebServices.postOpportunitiesLocal(key_hash,address,version,id_division, id_oportunity, brands, description, lat, lng, state, city
                                    , has_media, id_region);
                        }else{
                            retrofitWebServices.postOpportunitiesLocal(key_hash,version,id_division, id_oportunity, brands, description, has_media);
                        }
                        //retrofitWebServices.postOpportunities(id_division, id_oportunity, brands, description, lat, lng, state, city, has_media, id_region);
                    } else {
                        appController.setIdOpportunity(0);
                        stop();
                    }
                }
                break;
            }case POST_OPPORTUNITY:{
                Intent broadcast = new Intent(BuildConfig.APPLICATION_ID+"update");
                broadcast.putExtra("update",true);
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);

                NewOpportunityResponse newOpportunityResponse = (NewOpportunityResponse) object;
                int points = appController.getPoints();
                int opportunityPoints = newOpportunityResponse.getPoints();
                points = points + opportunityPoints;
                appController.setPoints(points);
                Toast.makeText(context, "Gracias por reportar tu oportunidad en Aliados Bepensa. En breve nos pondremos en contacto contigo.", Toast.LENGTH_SHORT).show();
                String key_hash = hash_opportunity.get(cont_opor_local);
                final RealmResults<OflinePostOpportunity> result1 = realm.where(OflinePostOpportunity.class).equalTo("key_hash", key_hash).findAll();
                if (result1.size()>0) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            result1.deleteAllFromRealm();
                        }
                    });
                }

                if (newOpportunityResponse.getHasMedia()) {
                    RealmResults<OflineListMedia> persons = realm.where(OflineListMedia.class)
                            .equalTo("key_hash", key_hash).findAll();
                    if (persons.size() > 0) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                Log.i("HAS_MEDIA","True");
                                persons.get(0).setId_opportunity(newOpportunityResponse.getId());
                                persons.get(0).setHas_sync(true);
                            }
                        });
                        List<MediasFilesEntity> mediasFiles = new ArrayList<>();
                        mediasFiles.addAll(mediasFilesStatus(persons));
                        if (mediasConvertFiles(mediasFiles)) {
                            mediaLoad(newOpportunityResponse.getId(),mediasFiles);
                        }else{
                            final RealmResults<OflinePostOpportunity> result = realm.where(OflinePostOpportunity.class).equalTo("key_hash", key_hash).findAll();
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    result.deleteAllFromRealm();
                                }
                            });
                        }
                    }

                } else {
                    final RealmResults<OflinePostOpportunity> result = realm.where(OflinePostOpportunity.class).equalTo("key_hash", key_hash).findAll();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            result.deleteAllFromRealm();
                        }
                    });
                    cont_opor_local++;
                    if (cont_opor_local < itemOpportunityLoca.size()) {
                        int id_division = itemOpportunityLoca.get(cont_opor_local).getId_division();
                        int id_oportunity = itemOpportunityLoca.get(cont_opor_local).getId_oportunity();
                        int id_region = itemOpportunityLoca.get(cont_opor_local).getId_region();
                        int has_media = itemOpportunityLoca.get(cont_opor_local).getHas_media();
                        String brands = itemOpportunityLoca.get(cont_opor_local).getBrands();
                        String state = ""+itemOpportunityLoca.get(cont_opor_local).getState();
                        String description = itemOpportunityLoca.get(cont_opor_local).getDescription();
                        String city = "";
                        double lat = itemOpportunityLoca.get(cont_opor_local).getLat();
                        double lng = itemOpportunityLoca.get(cont_opor_local).getLng();
                        String text_option_title=itemOpportunityLoca.get(cont_opor_local).getExtra_field_title();
                        String text_option_value=itemOpportunityLoca.get(cont_opor_local).getExtra_field_value();
                        String hash=itemOpportunityLoca.get(cont_opor_local).getKey_hash();
                        String address= itemOpportunityLoca.get(cont_opor_local).getAddress();
                        String version= BuildConfig.VERSION_NAME;
                        if (text_option_title!=null && text_option_value!=null && (itemOpportunityLoca.get(cont_opor_local).isRequire_location()) || itemOpportunityLoca.get(cont_opor_local).isContainer_location()) {
                            retrofitWebServices.postOpportunitiesLocal(key_hash,address,version,id_division,id_oportunity,brands,description,lat,lng,state,city
                                    ,has_media,id_region,text_option_title,text_option_value);
                        }else if (text_option_title!=null && text_option_value!=null && !itemOpportunityLoca.get(cont_opor_local).isRequire_location()) {
                            retrofitWebServices.postOpportunitiesLocal(key_hash,version,id_division,id_oportunity,brands,description
                                    ,has_media,text_option_title,text_option_value);
                        }else if (text_option_title!=null && (itemOpportunityLoca.get(cont_opor_local).isRequire_location()) || itemOpportunityLoca.get(cont_opor_local).isContainer_location()){
                            retrofitWebServices.postOpportunitiesLocal(key_hash,address,version,id_division,id_oportunity,brands,description,lat,lng,state,city
                                    ,has_media,id_region,text_option_title);
                        }else if (text_option_title!=null && !itemOpportunityLoca.get(cont_opor_local).isRequire_location()){
                            retrofitWebServices.postOpportunitiesLocal(key_hash,version,id_division,id_oportunity,brands,description
                                    ,has_media,text_option_title);
                        }else if (itemOpportunityLoca.get(cont_opor_local).isRequire_location() || itemOpportunityLoca.get(cont_opor_local).isContainer_location()){
                            retrofitWebServices.postOpportunitiesLocal(key_hash,address,version,id_division, id_oportunity, brands, description, lat, lng, state, city
                                    , has_media, id_region);
                        }else{
                            retrofitWebServices.postOpportunitiesLocal(key_hash,version,id_division, id_oportunity, brands, description, has_media);
                        }
                        /*if (text_option_title!=null && text_option_value!=null){
                            retrofitWebServices.postOpportunitiesLocal(hash,address,version,id_division,id_oportunity,brands,description,lat,lng,state,city
                                    ,has_media,id_region,text_option_title,text_option_value);
                        }else if (text_option_title!=null){
                            retrofitWebServices.postOpportunitiesLocal(hash,address,version,id_division,id_oportunity,brands,description,lat,lng,state,city
                                    ,has_media,id_region,text_option_title);
                        }else {
                            retrofitWebServices.postOpportunitiesLocal(hash,address,version,id_division, id_oportunity, brands, description, lat, lng, state, city
                                    , has_media, id_region);
                        }*/
                        //retrofitWebServices.postOpportunities(id_division, id_oportunity, brands, description, lat, lng, state, city, has_media, id_region);
                    }else{
                        appController.setIdOpportunity(0);
                        stop();
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onFailError(int id, int status, String message) {
        switch (id){
            case PUT_MEDIA_OPPORTUNITY:
                switch (status){
                    case 500:
                    case -1:
                        loadOnFailMedia();
                        break;
                    case 1001:
                        stop();
                        Log.i("SESION_INVALID","FAIL_SESION");
                        break;
                    default:
                        appController.setIdOpportunity(0);
                        Log.i("MEDIA_FAIL_UPLOAD","TRUE");
                        deleteOpportunity();
                        stop();
                        break;
                }
                break;
            case POST_OPPORTUNITY:
                switch (status){
                    case 500:
                    case -1:
                        stop();
                        break;
                    case 1001:
                        stop();
                        Log.i("SESION_INVALID","FAIL_SESION");
                    default:
                        appController.setIdOpportunity(0);
                        Log.i("POST_FAIL_UPLOAD","TRUE");
                        deleteOpportunity();
                        stop();
                        break;
                }
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

    public void deleteOpportunity(){
        final String key_hash = hash_opportunity.get(cont_opor_local);
        final RealmResults<OflinePostOpportunity> result1 = realm.where(OflinePostOpportunity.class).equalTo("key_hash", key_hash).findAll();
        if (result1.size()>0) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    result1.deleteAllFromRealm();
                }
            });
        }
        final RealmResults<OflineListMedia> result = realm.where(OflineListMedia.class).equalTo("key_hash", key_hash).findAll();
        if (result.size()>0) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    result.deleteAllFromRealm();
                }
            });
        }
    }

    public void loadOnFailMedia(){
        stopCountDownTimer();
        Toast.makeText(context
                , "No pudimos subir su archivos multimedia,es posible que no tenga una conexion a internet"
                , Toast.LENGTH_LONG).show();
        if (itemsMedias.size()>0){
            if (cont_error_media<3) {
                executeCountDownTimerMedia();
                cont_error_media++;
            }else{
                itemsMedias.clear();
                checkSyncOpportunity();
            }
        } else{
            if (cont_error_media < 3) {
                final String key_hash = hash_opportunity.get(cont_opor_local);
                final RealmResults<OflineListMedia> result1 = realm.where(OflineListMedia.class).equalTo("key_hash", key_hash).findAll();
                if (result1.size() > 0) {
                    Log.i("ADD_MEDIA_FAILS","ADD MEDIAS");
                    itemsMedias.addAll(result1);
                    executeCountDownTimerMedia();
                } else {
                    Log.i("STOP_FAIL_ERROR_LOCAL","True");
                    stop();
                    stopCountDownTimer();
                }
                cont_error_media++;
            }else{
                itemsMedias.clear();
                checkSyncOpportunity();
            }
        }
    }

    public void stopCountDownTimer(){
        if (countDownTimer!=null){
            countDownTimer.cancel();
        }
    }

    public void executeCountDownTimerMedia(){
        countDownTimer = new CountDownTimer(3000, 500) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (ValidateNetwork.getNetwork(context)) {
                    if (itemsMedias.size()>0) {
                        checkUploadMedia(itemsMedias);
                    }else{
                        Log.i("ITEMS_MEDIA","FALSE");
                    }
                }else{
                    Log.i("NOT_NETWORK","False");
                }
            }

            @Override
            public void onFinish() {

            }
        };
    }

    public void checkUploadMedia(List<OflineListMedia> result1 ){
        boolean status_media_id=false;
        for (int i=0;i<result1.size();i++){
            if (result1.get(con_media_load).getId_opportunity()>0){
                status_media_id=true;
                int id_opor=result1.get(con_media_load).getId_opportunity();
                List<MediasFilesEntity> mediasFilesEntities = convertMediaOfline(result1.get(con_media_load));
                if (mediasFilesEntities.size()>0) {
                    if (mediasConvertFiles(mediasFilesEntities)){
                        Log.i(LOG_TAG,"SEND_MEDIA_"+id_opor);
                        mediaLoad(id_opor,mediasFilesEntities);
                        stopCountDownTimer();
                    }else{
                        Log.i("NOT_CONVERT_FILES","false");
                    }
                }else{
                    Log.i("NOT_FILES","No hay archivos");
                }
                break;
            }else{
                status_media_id=false;
            }
        }
        if (!status_media_id){
            itemsMedias.clear();
            checkSyncOpportunity();
        }
        stopCountDownTimer();
    }

    public void newUpload(List<OflinePostOpportunity> items){
        try{
            int id_division=items.get(cont_opor_local).getId_division();
            int id_oportunity=items.get(cont_opor_local).getId_oportunity();
            int id_region=items.get(cont_opor_local).getId_region();
            int has_media=items.get(cont_opor_local).getHas_media();
            String brands=items.get(cont_opor_local).getBrands();
            String state=""+items.get(cont_opor_local).getState();
            String description=items.get(cont_opor_local).getDescription();
            String city="";
            String text_option_title=items.get(cont_opor_local).getExtra_field_title();
            String text_option_value=items.get(cont_opor_local).getExtra_field_value();
            double lat=items.get(cont_opor_local).getLat();
            double lng=items.get(cont_opor_local).getLng();
            String key_hash=items.get(cont_opor_local).getKey_hash();
            String address= items.get(cont_opor_local).getAddress();
            String version= BuildConfig.VERSION_NAME;
            if (text_option_title!=null && text_option_value!=null && (itemOpportunityLoca.get(cont_opor_local).isRequire_location()) || itemOpportunityLoca.get(cont_opor_local).isContainer_location()) {
                retrofitWebServices.postOpportunitiesLocal(key_hash,address,version,id_division,id_oportunity,brands,description,lat,lng,state,city
                        ,has_media,id_region,text_option_title,text_option_value);
            }else if (text_option_title!=null && text_option_value!=null && !itemOpportunityLoca.get(cont_opor_local).isRequire_location()) {
                retrofitWebServices.postOpportunitiesLocal(key_hash,version,id_division,id_oportunity,brands,description
                        ,has_media,text_option_title,text_option_value);
            }else if (text_option_title!=null && (itemOpportunityLoca.get(cont_opor_local).isRequire_location()) || itemOpportunityLoca.get(cont_opor_local).isContainer_location()){
                retrofitWebServices.postOpportunitiesLocal(key_hash,address,version,id_division,id_oportunity,brands,description,lat,lng,state,city
                        ,has_media,id_region,text_option_title);
            }else if (text_option_title!=null && !itemOpportunityLoca.get(cont_opor_local).isRequire_location()){
                retrofitWebServices.postOpportunitiesLocal(key_hash,version,id_division,id_oportunity,brands,description
                        ,has_media,text_option_title);
            }else if (itemOpportunityLoca.get(cont_opor_local).isRequire_location() || itemOpportunityLoca.get(cont_opor_local).isContainer_location()){
                retrofitWebServices.postOpportunitiesLocal(key_hash,address,version,id_division, id_oportunity, brands, description, lat, lng, state, city
                        , has_media, id_region);
            }else{
                retrofitWebServices.postOpportunitiesLocal(key_hash,version,id_division, id_oportunity, brands, description, has_media);
            }
            /*if (text_option_title!=null && text_option_value!=null){
                retrofitWebServices.postOpportunitiesLocal(key_hash,address,version,id_division,id_oportunity,brands,description,lat,lng,state,city
                        ,has_media,id_region,text_option_title,text_option_value);
            }else if (text_option_title!=null){
                retrofitWebServices.postOpportunitiesLocal(key_hash,address,version,id_division,id_oportunity,brands,description,lat,lng,state,city
                        ,has_media,id_region,text_option_title);
            }else {
                retrofitWebServices.postOpportunitiesLocal(key_hash,address,version,id_division, id_oportunity, brands, description, lat, lng, state, city
                        , has_media, id_region);
            }*/
        }catch (Exception e){
            Log.e(LOG_TAG,"CONVERT TO JSON:"+e.getLocalizedMessage());
        }
    }

    public List<MediasFilesEntity> mediasFilesStatus(RealmResults<OflineListMedia> persons){
        List<MediasFilesEntity> mediasFiles = new ArrayList<>();
        for (OflineListMedia oflineMedias : persons) {
            for (OflineMediaOpportunity mediasOpor : oflineMedias.getOflineMediaOpportunities()) {
                try {
                    MediasFilesEntity mediasFilesEntity = new MediasFilesEntity();
                    File file = new File(mediasOpor.getPath());
                    mediasFilesEntity.setExtension(mediasOpor.getExtension());
                    mediasFilesEntity.setFile(file);
                    mediasFilesEntity.setType(mediasOpor.getType());
                    mediasFiles.add(mediasFilesEntity);
                } catch (Exception e) {
                    Log.i(LOG_TAG, "convertFile");
                }
            }
        }
        return mediasFiles;
    }

    public List<MediasFilesEntity> convertMediaOfline(OflineListMedia persons){
        List<MediasFilesEntity> mediasFiles = new ArrayList<>();
        for (OflineMediaOpportunity mediasOpor : persons.getOflineMediaOpportunities()) {
            try {
                MediasFilesEntity mediasFilesEntity = new MediasFilesEntity();
                File file = new File(mediasOpor.getPath());
                mediasFilesEntity.setExtension(mediasOpor.getExtension());
                mediasFilesEntity.setFile(file);
                mediasFilesEntity.setType(mediasOpor.getType());
                mediasFiles.add(mediasFilesEntity);
            } catch (Exception e) {
                Log.i(LOG_TAG, "convertFile");
            }
        }

        return mediasFiles;
    }

    public boolean mediasConvertFiles(List<MediasFilesEntity> mediasFiles){
        boolean status_files=false;
        for (int i=0;i<mediasFiles.size();i++){
            try {
                File file = mediasFiles.get(i).getFile();
                if (file.exists()){
                    status_files=true;
                }else{
                    mediasFiles.remove(i);
                }
            }catch (Exception e){
                Log.i(LOG_TAG,"no existe el archivo");
            }
        }
        return status_files;
    }
}
