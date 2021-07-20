package com.dacodes.bepensa;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.Toast;

import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.MediasFilesEntity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.dacodes.bepensa.api.RetrofitWebServices.PUT_MEDIA_OPPORTUNITY;

public class UploadMedias extends Service implements RetrofitWebServices.DefaultResponseListeners  {

    String LOG_TAG  = UploadMedias.class.getSimpleName();
    private List<MediasFilesEntity> mediasFilesEntities;
    private MultipartBody.Part media_image0 = null;
    private MultipartBody.Part media_image1 = null;
    private MultipartBody.Part media_image2 = null;
    private MultipartBody.Part media_video0 = null;
    private int id_oportunity;
    RetrofitWebServices retrofitWebServices;
    AppController appController;
    boolean status;
    Context context;
    public UploadMedias() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        appController = (AppController)getApplication();
        context = getApplicationContext();
        retrofitWebServices = new RetrofitWebServices(appController, LOG_TAG, context, this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent!=null) {
            if (intent.getIntExtra("id",0)>0){
                if (intent.getSerializableExtra("media")!=null){
                    mediasFilesEntities = (List<MediasFilesEntity>) intent.getSerializableExtra("media");
                    id_oportunity = intent.getIntExtra("id",0);
                    if (id_oportunity>0)
                        mediaLoad(mediasFilesEntities,id_oportunity);
                    else if (appController.getIdOpportunityMedias()>0){
                        mediaLoad(mediasFilesEntities,appController.getIdOpportunityMedias());
                    }else{
                        stop();
                    }
                }else{
                    validateMediasLocal();
                }
            }else{
                validateMediasLocal();
            }
        }else{
            stop();
        }
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
            case PUT_MEDIA_OPPORTUNITY:
                try {
                    Thread.sleep(1000);
                    stop();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onFailError(int id, int status, String message) {
        switch (status){
            case 500:
            case -1:
                try {
                    Thread.sleep(3000);
                    if (!TextUtils.isEmpty(message))
                        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(context,"No pudimos cargar los archivos multimedia,es posibe que no tenga una conexió a internet",Toast.LENGTH_LONG).show();
                }catch (Exception e){

                }
                break;
            case 400:
                if (!TextUtils.isEmpty(message))
                    try {
                        Thread.sleep(3000);
                        Toast.makeText(context,""+message,Toast.LENGTH_LONG).show();
                    }catch (Exception e){

                    }
                break;
        }
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

    private void validateMediasLocal(){
        if (appController.getLocalMedias()!=null){
            String object=appController.getLocalMedias();
            String []objecMedias=object.split(">>");
            if (objecMedias.length>1){
                for (int i=0;i<objecMedias.length-1;i++) {
                    try {
                        Gson gson = new Gson();
                        MediasFilesEntity media = gson.fromJson(objecMedias[i],MediasFilesEntity.class);
                        if (mediasFilesEntities==null)
                            mediasFilesEntities=new ArrayList<>();
                        mediasFilesEntities.add(media);
                    } catch (Exception e) {

                    }
                }
            }else{
                try {
                    Gson gson=new Gson();
                    MediasFilesEntity media=gson.fromJson(object,MediasFilesEntity.class);
                    if (mediasFilesEntities==null)
                        mediasFilesEntities=new ArrayList<>();
                    mediasFilesEntities.add(media);
                }catch (Exception e){

                }
            }
            if (appController.getIdOpportunityMedias()>0) {
                mediaLoad(mediasFilesEntities,appController.getIdOpportunityMedias());
            }else{
                stop();
            }
        }else{
            stop();
        }
    }

    public void stop(){
        Intent intent = new Intent(context, UploadMedias.class);
        stopService(intent);
        appController.setIdOpportunityMedias(0);
        appController.setLocalMedias(null);
    }

    public void mediaLoad(List<MediasFilesEntity> mediasFilesEntityy,int id_oportunity){
        for(MediasFilesEntity mediasFilesEntity : mediasFilesEntityy){
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
        retrofitWebServices.putMediaOpportunity(id_oportunity,media_image0,media_image1,media_image2,media_video0);
    }
}
