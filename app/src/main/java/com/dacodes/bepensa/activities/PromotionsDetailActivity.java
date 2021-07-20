package com.dacodes.bepensa.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.DetailPromotionsEntity;
import com.dacodes.bepensa.entities.promotion.PromotionDataEntity;
import com.dacodes.bepensa.fragments.BottomSheetMarkers;
import com.dacodes.bepensa.models.Markers;
import com.dacodes.bepensa.utils.DM;
import com.dacodes.bepensa.utils.DateFormatter;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PromotionsDetailActivity extends AppCompatActivity implements RetrofitWebServices.DefaultResponseListeners {

    PromotionDataEntity promotionDataEntity;
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.picture)ImageView picture;
    @BindView(R.id.tv_title)TextView tv_title;
    @BindView(R.id.tv_description)TextView tv_description;
    @BindView(R.id.tv_date)TextView tv_date;
    @BindView(R.id.progressBar)ProgressBar progressBar;
    @BindView(R.id.image_map)ImageView image_map;

    Context mContext;
    AppController appController;
    RetrofitWebServices retrofitWebServices;

    Locale locale = new Locale("es", "ES");
    @OnClick(R.id.picture)
    public void fullImage(){
        try {
            if (promotionDataEntity.getImage()!=null) {
                List<String> images = new ArrayList<>();
                images.add(promotionDataEntity.getImage());
                new ImageViewer.Builder(this, images)
                        .setStartPosition(0)
                        .hideStatusBar(false)
                        .setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorTranslucentBlack))
                        .show();
            }
        }catch (Exception e){

        }
    }


    @OnClick(R.id.image_map)
    public void map(){
        loadMapDetail(promotionDataEntity);
    }

    public void loadMapDetail(PromotionDataEntity promotionDataEntity){
        if (promotionDataEntity.getMarkers().size() >=2){
            BottomSheetMarkers bottomSheetMarkers = BottomSheetMarkers.getInstance(promotionDataEntity);
            bottomSheetMarkers.show(getSupportFragmentManager(),"Markers");
        }else if (promotionDataEntity.getMarkers().size()>0){
            try {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=&daddr=" + promotionDataEntity.getMarkers().get(0).getLat() + "," + promotionDataEntity.getMarkers().get(0).getLng()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                try {
                    String uri = "http://maps.google.com/maps?saddr=&daddr=" + promotionDataEntity.getMarkers().get(0).getLat()  + "," + promotionDataEntity.getMarkers().get(0).getLng();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);
                }catch (ActivityNotFoundException ex){
                    Toast.makeText(this,"Por favor instale google maps en su teléfono",Toast.LENGTH_LONG).show();
                }
            }
        }
        else {
            if (promotionDataEntity != null && promotionDataEntity.getLat() != null && promotionDataEntity.getLng() != null) {
                try {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?saddr=&daddr=" + promotionDataEntity.getLat() + "," + promotionDataEntity.getLng()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    try {
                        String uri = "http://maps.google.com/maps?saddr=&daddr=" + promotionDataEntity.getLat() + "," + promotionDataEntity.getLng();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(intent);
                    }catch (ActivityNotFoundException ex){
                        Toast.makeText(this,"Por favor instale google maps en su teléfono",Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotions_detail);
        ButterKnife.bind(this);
        mContext=getApplicationContext();
        appController=(AppController)getApplication();
        retrofitWebServices = new RetrofitWebServices(appController,"DetallePromotions",mContext,this);
        toolbar.setTitle("DETALLE DEL BENEFICIO");
        toolbar.setTitleTextColor(ContextCompat.getColor(mContext, R.color.colorOrange));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar.setVisibility(View.VISIBLE);
        int promotionId = getIntent().getIntExtra("promotionId", 0);
        if (promotionId>0){
            retrofitWebServices.getPromotionsDetail(String.valueOf(promotionId),appController.getAccessToken());
        }else {
            progressBar.setVisibility(View.GONE);
            promotionDataEntity = (PromotionDataEntity) getIntent().getSerializableExtra("beneficio");
            loadDetalle(promotionDataEntity);
        }
    }

    private void loadDetalle(PromotionDataEntity promotionDataEntity){
        tv_title.setText(promotionDataEntity.getName());
        tv_date.setText("Valido hasta el "+DateFormatter
                .formatDate("yyyy-MM-dd",
                        "dd 'de' MMMM 'de' yyy",promotionDataEntity.getStarts(),
                        locale));
        tv_description.setText(promotionDataEntity.getText());
        if (promotionDataEntity.getImage()!=null) {
            Glide.with(mContext)
                    .load(promotionDataEntity.getImage())
                    .apply(new RequestOptions().error(R.drawable.no_media_placeholder))
                    .into(picture);
        }else{
            Glide.with(mContext)
                    .load(R.drawable.logo)
                    .into(picture);
        }

        String url_marker = "";
        String center_map= "";
        String static_url ="";
        if (promotionDataEntity.getMarkers().size()>0){

            for (Markers marker: promotionDataEntity.getMarkers()){
                url_marker = url_marker + "&markers=color:red%7Clabel:" + marker.getLat().substring(0, 1) + "%7C"
                        + marker.getLat() + "," + marker.getLng();
            }
            static_url = "https://maps.googleapis.com/maps/api/staticmap?"
                    +"&size=600x300&maptype=roadmap"+url_marker;
        }else {
            url_marker = url_marker + "&markers=color:red%7Clabel:" + promotionDataEntity.getName().substring(0, 1) + "%7C"
                    + promotionDataEntity.getLat() + "," + promotionDataEntity.getLng();
            static_url = "https://maps.googleapis.com/maps/api/staticmap?center="+promotionDataEntity.getLat()+","+promotionDataEntity.getLng()+
                    "&zoom=10&size=600x300&maptype=roadmap"+url_marker;
        }

        static_url =static_url+"&key="+getString(R.string.google_maps_key);

        RequestManager requestManager = Glide.with(this)
                .applyDefaultRequestOptions(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .applyDefaultRequestOptions(RequestOptions.placeholderOf(R.drawable.placeholder_image));
        requestManager
                .applyDefaultRequestOptions(RequestOptions.skipMemoryCacheOf(true));

        requestManager.load(static_url)
                .into(image_map);
        /*Glide.with(mContext)
                .load(static_url)
                .apply(new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE))
                .into(image_map);*/

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(DM.getDisplayWidth(mContext),DM.getDisplayWidth(mContext));
        picture.setLayoutParams(params);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }


    @Override
    public void onResponse(int id, String message, Object object) {
        DetailPromotionsEntity responseModel= (DetailPromotionsEntity) object;
        PromotionDataEntity promotionDataEntity1=new PromotionDataEntity();
        promotionDataEntity1.setName(responseModel.getName());
        promotionDataEntity1.setCreatedAt(responseModel.getCreatedAt());
        promotionDataEntity1.setEnds(responseModel.getEnds());
        promotionDataEntity1.setId(responseModel.getId());
        promotionDataEntity1.setLat(responseModel.getLat());
        promotionDataEntity1.setLng(responseModel.getLng());
        promotionDataEntity1.setImage(responseModel.getImage());
        promotionDataEntity1.setText(responseModel.getText());
        promotionDataEntity1.setStarts(responseModel.getStarts());
        promotionDataEntity1.setStatus(responseModel.getStatus());
        progressBar.setVisibility(View.GONE);
        promotionDataEntity=promotionDataEntity1;
        loadDetalle(promotionDataEntity);
    }

    @Override
    public void onFailError(int id, int status, String message) {

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
