package com.dacodes.bepensa.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asksira.bsimagepicker.BSImagePicker;
import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.UploadMedias;
import com.dacodes.bepensa.adapters.MediasAdapter;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.MediasFilesEntity;
import com.dacodes.bepensa.fragments.DialogMedias;
import com.dacodes.bepensa.utils.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.tapadoo.alerter.Alerter;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class UploadPendingMedia extends AppCompatActivity implements
        RetrofitWebServices.DefaultResponseListeners,
        MediasAdapter.DeleteItem, BSImagePicker.OnSingleImageSelectedListener, DialogMedias.pickerMedias {

    private Context context;
    private AppController appController;
    private String LOG_TAG = UploadPendingMedia.class.getSimpleName();
    private RetrofitWebServices retrofitWebServices;

    @BindView(R.id.recyclerViewMedias) RecyclerView rvMedias;
    @BindView(R.id.cross_iv) FloatingActionButton cross_iv;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.main_toolbar) Toolbar toolbar;

    private String[] permissions = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int VIDEO_CAPTURE = 101;
    private static final int MULTIPLE_PERMISSIONS_REQUEST_CODE_VIDEO = 100;

    private List<MediasFilesEntity> mediasFilesEntities = new ArrayList<>();
    private static final int PICK_IMAGE_ID = 10;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 2;
    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 5;
    private static final int REQUEST_TAKE_GALLERY_VIDEO = 3;
    private RecyclerView.Adapter adapterMedias;
    private LinearLayoutManager linearLayoutManager;

    private int opportunity_id = 0;

    @OnClick({R.id.cross_iv, R.id.btSave, R.id.select_images})
    public void submit(View view){
        switch (view.getId()){
            case R.id.cross_iv:
            case R.id.select_images:
                new DialogMedias().setActivity(this).setListenerMedias(this).init();
                break;
            case R.id.btSave:
                if(mediasFilesEntities.size()>0) {
                    if (getIntent().getExtras()!=null){
                        int idOp=getIntent().getExtras().getInt("OPPORTUNITY_ID",0);
                        if (idOp>0){
                            serviceMedias(mediasFilesEntities,idOp);
                        }
                    }
                }else{
                    Toast.makeText(context,"Agregar una foto o video",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pending_media);
        ButterKnife.bind(this);
        context = getApplicationContext();
        appController = (AppController) getApplication();
        retrofitWebServices = new RetrofitWebServices(appController, LOG_TAG, context, this);

        setSupportActionBar(toolbar);
        toolbar.setTitle("SUBIR MULTIMEDIA");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent().getExtras()!=null){
            opportunity_id = getIntent().getExtras().getInt("OPPORTUNITY_ID");
        }

        setupRecyclerViewMedias();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id){
            case RetrofitWebServices.PUT_MEDIA_OPPORTUNITY:
                Toast.makeText(context, "Archivos multimedia cargados exitosamente", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(this, ActivityDetailOportunity.class);
                intent.putExtra("id",opportunity_id);
                intent.putExtra("networkLocal",1);
                startActivity(intent);
                finish();
                /*Intent returnIntent = new Intent();
                returnIntent.putExtra("refresh", true);
                setResult(RESULT_OK,returnIntent);
                finish();*/
                break;
        }
    }

    @Override
    public void onFailError(int id, int status, String message) {
        Log.v(LOG_TAG, message);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWebServiceStart() {
        progressBar.setVisibility(VISIBLE);
    }

    @Override
    public void onWebServiceEnd() {
        progressBar.setVisibility(GONE);
    }

    @Override
    public void onExpireToken() {

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK) {
            switch (requestCode) {
                case VIDEO_CAPTURE:
                    Uri contentURI = data.getData();
                    String selectedVideoPath = getPatchDirectory(this,contentURI,"VIDEO");
                    if (selectedVideoPath != null && !TextUtils.isEmpty(selectedVideoPath)) {
                        File videoFile = new File(selectedVideoPath);
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        retriever.setDataSource(selectedVideoPath);
                        int duration = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                        retriever.release();
                        long fileSizeInBytes = videoFile.length();
                        long fileSizeInKB = fileSizeInBytes / 1024;
                        long fileSizeInMB = fileSizeInKB / 1024;
                        if (fileSizeInMB > 100) {
                            showMessage("El archivo que intentas subir es demasiado grande");
                        } else if (duration > 15900) {
                            showMessage("El video no puede durar mas de 15 segundos");
                        } else {
                            cross_iv.setVisibility(GONE);
                            mediasFilesEntities.add(new MediasFilesEntity(2, "mp4", videoFile));
                            adapterMedias.notifyDataSetChanged();
                        }
                    } else {
                        Log.e("null", "nulo");
                    }
                    Log.e("Video", "aqui");
                    break;
            }
        }
    }

    @Override
    public void onDeleteItem(int position) {
        mediasFilesEntities.remove(position);
        if( mediasFilesEntities.size() == 0){
            cross_iv.setVisibility(VISIBLE);
        }
        adapterMedias.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        Intent intent2=new Intent(this, ActivityDetailOportunity.class);
        intent2.putExtra("id",opportunity_id);
        intent2.putExtra("networkLocal",1);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent2);
        finish();
    }

    @Override
    public void onSingleImageSelected(Uri uri, String tag) {
        if (uri!=null){
            File originFile=new File(getPatchDirectory(this,uri,"IMAGE"));
            File compressedImageFile=null;
            try {
                compressedImageFile =
                        new Compressor(this).compressToFile(originFile);
                long bytes = compressedImageFile.length();
                long KB = bytes / 1024;
                long MB = KB / 1024;
            }catch (Exception e){
                compressedImageFile =new File(getPatchDirectory(this,uri,"IMAGE"));
            }
            //File file =new File(getRealPathFromURI(mContext,uri,"IMAGE"));
            if (compressedImageFile!=null) {
                cross_iv.setVisibility(GONE);
                mediasFilesEntities.add(new MediasFilesEntity(1, "jpg", compressedImageFile));
            }
            adapterMedias.notifyDataSetChanged();
        }else{
            Log.e("Uri","NUll");
        }
    }

    @Override
    public void idMedia(int position) {
        if (position==2){
            if (mediasFilesEntities.size()>0){
                int conMedias=0;
                for (MediasFilesEntity medias:mediasFilesEntities){
                    if (medias.getType()==2)
                        conMedias++;
                }
                if (conMedias>=1){
                    showMessage("Un solo video permitido");
                }else
                    openVideoCapture();
            }else
                openVideoCapture();
        }else{
            if (mediasFilesEntities.size()>0){
                int conImage=0;
                for (MediasFilesEntity medias:mediasFilesEntities){
                    if (medias.getType()==1)
                        conImage++;
                }
                if (conImage>=3)
                    showMessage("Límite de imágenes excedido (3)");
                else
                    new BSImagePicker.Builder("com.dacodes.bepensa.utils.provider").build().show(getSupportFragmentManager(),"Medias");
            }else
                new BSImagePicker.Builder("com.dacodes.bepensa.utils.provider").build().show(getSupportFragmentManager(),"Medias");
        }
    }

    private static String getPatchDirectory(Context context,Uri uri,String type){
        String imagePath = "";
        if (uri.toString().contains("content:")) {
            imagePath = getRealPathFromURI(context,uri,type);
        } else if (uri.toString().contains("file:")) {
            imagePath = uri.getPath();
        } else {
            imagePath = null;
        }
        return imagePath;
    }

    public static String getRealPathFromURI(Context context,Uri contentURI,String type) {
        String result  = null;
        try {
            Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
            if (cursor == null) { // Source is Dropbox or other similar local file path
                result = contentURI.getPath();
                Log.d("TAG", "result******************" + result);
            } else {
                cursor.moveToFirst();
                int idx = 0;
                if(type.equalsIgnoreCase("IMAGE")){
                    idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                }else if(type.equalsIgnoreCase("VIDEO")){
                    idx = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
                }else if(type.equalsIgnoreCase("AUDIO")){
                    idx = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
                }
                result = cursor.getString(idx);
                Log.d("TAG", "result*************else*****" + result);
                cursor.close();
            }
        } catch (Exception e){
            Log.e("TAG", "Exception ",e);
        }
        return result;
    }

    public void setupRecyclerViewMedias(){
        rvMedias.setVisibility(VISIBLE);
        rvMedias.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(context, LinearLayout.HORIZONTAL, false);
        adapterMedias = new MediasAdapter(mediasFilesEntities,context, this, this);
        rvMedias.setLayoutManager(linearLayoutManager);
        rvMedias.setAdapter(adapterMedias);
    }

    private void serviceMedias(List<MediasFilesEntity> mediasFilesEntities,int id){
        if (mediasFilesEntities.size()>0){
            String object="";
            Gson gson=new Gson();
            if (mediasFilesEntities.size()>1) {
                for (MediasFilesEntity medias : mediasFilesEntities) {
                    String media = gson.toJson(medias);
                    object = object + media + ">>";
                }
            }else
                object=gson.toJson(mediasFilesEntities.get(0));
            if (!TextUtils.isEmpty(object))
                appController.setLocalMedias(object);
        }
        appController.setIdOpportunityMedias(id);
        Intent intent=new Intent(this, UploadMedias.class);
        intent.putExtra("id",id);
        intent.putExtra("medias",(Serializable) mediasFilesEntities);
        startService(intent);
        try {
            Thread.sleep(600);
            Intent intent2=new Intent(this, ActivityDetailOportunity.class);
            intent2.putExtra("id",id);
            intent2.putExtra("networkLocal",1);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent2);
            finish();
        }catch (Exception e){

        }
    }

    private void showMessage(String mesaje){
        Alerter.create(this)
                .setDuration(2000)
                .setBackgroundColorRes(R.color.colorPrimary)
                .setText(mesaje)
                .enableSwipeToDismiss()
                .show();
    }

    private void openVideoCapture(){
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, permissions[1]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, permissions[2]) != PackageManager.PERMISSION_GRANTED) {
            //Si alguno de los permisos no esta concedido lo solicita
            ActivityCompat.requestPermissions(this, permissions, MULTIPLE_PERMISSIONS_REQUEST_CODE_VIDEO);
        } else {
            Intent intent = ImagePicker.getPickVideoIntent(this);
            startActivityForResult(intent, VIDEO_CAPTURE);
        }
    }
}
