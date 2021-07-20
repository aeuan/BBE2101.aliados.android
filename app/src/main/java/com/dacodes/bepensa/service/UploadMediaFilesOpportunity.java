package com.dacodes.bepensa.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.MediasFilesEntity;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.dacodes.bepensa.api.RetrofitWebServices.PUT_MEDIA_OPPORTUNITY;

public class UploadMediaFilesOpportunity extends Service implements RetrofitWebServices.DefaultResponseListeners {
    String LOG_TAG  = UploadMediaOportunity.class.getSimpleName();
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

    private final IBinder mBinder = new LocalBinder();

    public UploadMediaFilesOpportunity() {
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appController = (AppController)getApplication();
        context = getApplicationContext();
        retrofitWebServices = new RetrofitWebServices(appController, LOG_TAG, context, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            setStatus(false);
            mediasFilesEntities= (List<MediasFilesEntity>) intent.getSerializableExtra("media");
            id_oportunity = Integer.parseInt(intent.getStringExtra("id"));
            mediaLoad(mediasFilesEntities);
        }catch (Exception e){
            stop();
            Log.i(LOG_TAG,""+e.getLocalizedMessage());
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        try {
            setStatus(false);
            mediasFilesEntities= (List<MediasFilesEntity>) intent.getSerializableExtra("media");
            id_oportunity = Integer.parseInt(intent.getStringExtra("id"));
            mediaLoad(mediasFilesEntities);
        }catch (Exception e){
            Log.e("medias",""+e.getLocalizedMessage());
            Log.i(LOG_TAG,""+e.getLocalizedMessage());
        }
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public UploadMediaFilesOpportunity getService() {
            // Return this instance of LocalService so clients can call public methods
            return UploadMediaFilesOpportunity.this;
        }
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

    public void mediaLoad(List<MediasFilesEntity> mediasFilesEntityy){
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

    public void stop(){
        Intent intent = new Intent(context,UploadMediaFilesOpportunity.class);
        stopService(intent);
        appController.setIdOpportunityMedias(0);
    }
    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id){
            case PUT_MEDIA_OPPORTUNITY:
                Log.e(LOG_TAG,"Files->Success");
                setStatus(true);
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
        switch (id){
            case PUT_MEDIA_OPPORTUNITY:
                switch (status){
                    case 400:
                        try {
                            Thread.sleep(3000);
                            Toast.makeText(context,"Tuvimos un problema al con los archivos multimedia,porfavor intente de nuevo",Toast.LENGTH_LONG).show();
                        }catch (Exception e){

                        }
                        break;
                }
                break;
        }
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
        }
        Log.e("medias","id::"+id+"status::"+status+"message::"+message);
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
}

