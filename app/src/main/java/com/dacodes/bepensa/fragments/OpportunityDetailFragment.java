package com.dacodes.bepensa.fragments;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.activities.ChatActivity;
import com.dacodes.bepensa.activities.OpportunityDetailActivity;
import com.dacodes.bepensa.activities.UploadPendingMedia;
import com.dacodes.bepensa.adapters.BrandsTagsAdapter;
import com.dacodes.bepensa.adapters.ViewPagerAdapter;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.PackegeOpportunity.MediaEntity;
import com.dacodes.bepensa.entities.ofline.packageBrands.OflineBrandsEntity;
import com.dacodes.bepensa.entities.ofline.packageDivisions.OflineDivisionEntity;
import com.dacodes.bepensa.entities.ofline.packageOpportunity.OflineOpportunity;
import com.dacodes.bepensa.entities.ofline.postOpportunity.OflineListMedia;
import com.dacodes.bepensa.models.OpportunityDetailModel;
import com.dacodes.bepensa.service.UploadMediaFilesOpportunity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import me.relex.circleindicator.CircleIndicator;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.dacodes.bepensa.api.RetrofitWebServices.GET_OPPORTUNITY_DETAIL;


public class OpportunityDetailFragment extends Fragment implements RetrofitWebServices.DefaultResponseListeners
        , BrandsTagsAdapter.AddNewBrand {
    @BindView(R.id.indicator)
    CircleIndicator indicator;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.logo_iv)
    ImageView logo_iv;
    @BindView(R.id.date_tv)
    TextView date_tv;
    @BindView(R.id.opportunity_title_tv)
    TextView opportunity_title_tv;
    @BindView(R.id.opportunity_id_tv)
    TextView opportunity_id_tv;
    @BindView(R.id.opportunity_description_tv)
    TextView opportunity_description_tv;
    @BindView(R.id.division_static_tv)
    TextView division_static_tv;
    @BindView(R.id.division_tv)
    TextView division_tv;
    @BindView(R.id.static_map_iv)
    ImageView static_map_iv;
    /*@BindView(R.id.comentario_static_tv)
    TextView comentario_static_tv;
    @BindView(R.id.comentario_number_static_tv)
    TextView comentario_number_static_tv;
    @BindView(R.id.arrow_bt)
    ImageButton arrow_bt;*/
    @BindView(R.id.rvMarcas)
    RecyclerView rvMarcas;
    @BindView(R.id.btUploadPendingMedia)
    Button btUploadPendingMedia;
    @BindView(R.id.ivPlaceHolder)
    ImageView ivPlaceHolder;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.chat_available)View chat_available;
    CountDownTimer countDownTimer;
    //@BindView(R.id.comentario_constraint_layout)ConstraintLayout comentario_constraint_layout;
    @BindView(R.id.contenido)LinearLayout contenido;


    List<MediaEntity> mediaEntityList = new ArrayList<>();


    @OnClick(R.id.btUploadPendingMedia)
    public void submit(View view){
        switch (view.getId()){
            case R.id.btUploadPendingMedia:
                Bundle bundle = this.getArguments();
                if (bundle != null) {
                    int ids = bundle.getInt("id", 0);
                    Intent intent = new Intent(mContext, UploadPendingMedia.class);
                    intent.putExtra("OPPORTUNITY_ID", ids);
                    startActivityForResult(intent, UPLOAD_MEDIA);
                    getActivity().overridePendingTransition(R.anim.enter_up, R.anim.no_anim);
                }
                break;
        }
    }
    private static final int UPLOAD_MEDIA = 102;
    private int id = 0;
    private boolean goToChat;
    private RecyclerView.Adapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private int pageIndex = 0;
    private int NUM_PAGES = 0;
    private Timer myTimer = new Timer();
    private boolean isTimerCancelled = false;
    private static final String LOG_TAG = OpportunityDetailFragment.class.getSimpleName();

    public OpportunityDetailFragment() {
        // Required empty public constructor
    }
    Context mContext;
    RetrofitWebServices retrofitWebServices;
    AppController app;
    int id_oportunity=0;
    String lat_map="";
    String lng_map="";
    int files_media=0;
    int networkLocal=0;
    OpportunityDetailModel opportunityDetailModel;
    Realm realm;
    ViewPagerAdapter pagerAdapter;
    CountDownTimer countDownTimerOpportunity;

    @OnClick(R.id.static_map_iv)
    public void map(){
        loadMapDetail(lat_map,lng_map);
    }


    public void loadMapDetail(String lat ,String lng){
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=&daddr="+lat+","+lng));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_LAUNCHER );
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mContext=getContext();
        app = (AppController) getActivity().getApplicationContext();
        retrofitWebServices = new RetrofitWebServices(app, LOG_TAG, mContext, this);
        realm = Realm.getDefaultInstance();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.opportunity_detail_layout, container, false);
        ButterKnife.bind(this, view);
        Bundle bundle =getArguments();
        if (bundle!=null){
            networkLocal=bundle.getInt("networkLocal",0);
            if (networkLocal==1){
                networkAvailable();
            }else{
                if (networkLocal==2) {
                    //comentario_constraint_layout.setVisibility(View.GONE);
                    static_map_iv.setVisibility(View.GONE);
                    contenido.setVisibility(VISIBLE);
                    progressBar.setVisibility(GONE);
                    opportunityDetailModel = (OpportunityDetailModel) bundle.getSerializable("dataNetworLocal");
                    opportunity_description_tv.setText("" + opportunityDetailModel.getDescription());
                    int id_opor = opportunityDetailModel.getType().getId();
                    int id_div = opportunityDetailModel.getDivision().getId();
                    RealmResults<OflineOpportunity> oflineOpportunities = realm.where(OflineOpportunity.class).equalTo("oflineOpportunityTypes.id", id_opor).findAll();
                    if (oflineOpportunities.size() > 0) {
                        opportunity_title_tv.setText("" + oflineOpportunities.get(0).getOflineOpportunityTypes().get(0).getName());
                    }

                    RealmResults<OflineDivisionEntity> oflineDivisionEntities = realm.where(OflineDivisionEntity.class).equalTo("id", id_div).findAll();
                    if (oflineDivisionEntities.size() > 0) {
                        division_tv.setText("" + oflineDivisionEntities.get(0).getName());
                    }

                    RealmResults<OflineBrandsEntity> oflineBrandsEntityRealmResults = realm.where(OflineBrandsEntity.class).equalTo("divisionEntity.id", id_div).findAll();
                    if (oflineBrandsEntityRealmResults.size() > 0) {
                        Glide.with(mContext)
                                .load(oflineBrandsEntityRealmResults.get(0).getLogo())
                                .apply(new RequestOptions().placeholder(mContext.getResources().getDrawable(R.drawable.no_media_placeholder))
                                        .error(mContext.getResources().getDrawable(R.drawable.no_media_placeholder)))
                                .into(logo_iv);
                    }
                    mediaEntityList.addAll(opportunityDetailModel.getMedia());
                    populateViewPager(mediaEntityList);
                    opportunity_id_tv.setVisibility(GONE);
                    date_tv.setVisibility(GONE);
                    btUploadPendingMedia.setVisibility(GONE);
                    ivPlaceHolder.setVisibility(VISIBLE);
                    progressBar.setVisibility(GONE);
                    Glide.with(getContext()).load(R.drawable.logo).into(ivPlaceHolder);
                }else{
                    Log.i("NETWORK_LOCAL",""+networkLocal);
                }
            }
        }

        //arrow_bt.setColorFilter(ContextCompat.getColor(mContext, R.color.colorOrange), android.graphics.PorterDuff.Mode.MULTIPLY);
        return view;
    }

    public void networkAvailable(){
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            id = bundle.getInt("id", 0);
            goToChat = bundle.getBoolean("goToChat", false);
            files_media=bundle.getInt("files_media",0);
            retrofitWebServices.getOpportunityDetail(String.valueOf(id));

            id_oportunity=id;
            if(goToChat){
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("opportunityId", id);
                startActivity(intent);
            }
            RealmResults<OflineListMedia> result = realm.where(OflineListMedia.class).equalTo("id_opportunity", id).findAll();
            if (result.size()>0) {
                Log.i("LOAD_SERVICE","SUCCESS");
                for (OflineListMedia medias : result) {
                    if (medias.getId_opportunity() == id) {
                        if (app.getIdOpportunity() == id) {
                            btUploadPendingMedia.setVisibility(VISIBLE);
                            ivPlaceHolder.setVisibility(VISIBLE);
                            btUploadPendingMedia.setOnClickListener(null);
                            btUploadPendingMedia.setText("Subiendo archivos..");
                            startCountDownTimer(id, false, 0);
                            break;
                        }else{
                            Log.i("Not Id","False"+app.getIdOpportunity());
                        }
                    }else{
                        Log.i("Not","False");
                    }
                }
            }else{
                Log.i("LOAD_SERVICE","FALSE");
            }
            if (files_media==2){
                btUploadPendingMedia.setVisibility(VISIBLE);
                ivPlaceHolder.setVisibility(VISIBLE);
                btUploadPendingMedia.setOnClickListener(null);
                btUploadPendingMedia.setText("Subiendo archivos..");
            }
        }
    }


    public void startCountDownTimer(final int id,final boolean chat,final int files_media){
        countDownTimerOpportunity = new CountDownTimer(120000,5000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (app.getIdOpportunity()==id){
                    Log.i("noRestart","false");
                }else{
                    try {
                        Thread.sleep(2000);
                        restarFragment(id,goToChat,files_media,1);
                    }catch (Exception e){
                        Log.i("Exception",""+e.getLocalizedMessage());
                    }
                }
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    public void stopCoutDownTimer(){
        if (countDownTimerOpportunity!=null){
            countDownTimerOpportunity.cancel();
        }
    }

    public void restarFragment(int id,boolean chat,int file,int network){
        ((OpportunityDetailActivity)getActivity()).detailOpprtunity(id,chat,file,network);
    }

    /*@OnClick({ R.id.comentario_constraint_layout, R.id.comentario_static_tv,
            R.id.comentario_number_static_tv, R.id.arrow_bt})
    public void chatClick(View view) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("opportunityId", id);
        startActivity(intent);
    }*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext=context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer!=null)
            countDownTimer.cancel();
        if (pagerAdapter!=null){
            pagerAdapter.releaseVideoView();
        }
        if (countDownTimerOpportunity!=null){
            countDownTimerOpportunity.cancel();
        }
        realm.close();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id) {
            case GET_OPPORTUNITY_DETAIL:
                OpportunityDetailModel response = (OpportunityDetailModel) object;
                progressBar.setVisibility(GONE);
                if(response.isHas_media() || response.getMedia()!=null || response.getMedia().size()>0){
                    contenido.setVisibility(VISIBLE);
                    progressBar.setVisibility(GONE);
                    if(response.getMedia().size()==0){//IF ISN`T MEDIA
                        if (files_media==2){
                            btUploadPendingMedia.setVisibility(VISIBLE);
                            ivPlaceHolder.setVisibility(VISIBLE);
                            btUploadPendingMedia.setOnClickListener(null);
                            btUploadPendingMedia.setText("Subiendo archivos..");
                        }else {
                            if (isMyServiceRunning(UploadMediaFilesOpportunity.class)){
                                checkPreferences(response.getId());
                            }else{
                                checkPreferences(response.getId());
                            }
                        }
                    }else {
                        btUploadPendingMedia.setVisibility(GONE);
                        ivPlaceHolder.setVisibility(GONE);
                    }
                }else{
                    contenido.setVisibility(VISIBLE);
                    ivPlaceHolder.setVisibility(VISIBLE);
                    btUploadPendingMedia.setVisibility(VISIBLE);
                    progressBar.setVisibility(GONE);
                    app.setIdOpportunityMedias(0);
                }
                showUI(response);
                break;
        }
    }

    @Override
    public void onFailError(int id, int status, String message) {
        progressBar.setVisibility(GONE);

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

    public void checkPreferences(int id){
        if (app.getIdOpportunityMedias()==id){
            btUploadPendingMedia.setVisibility(VISIBLE);
            ivPlaceHolder.setVisibility(VISIBLE);
            btUploadPendingMedia.setOnClickListener(null);
            btUploadPendingMedia.setText("Subiendo archivos..");
        }else{
            app.setIdOpportunityMedias(0);
            ivPlaceHolder.setVisibility(VISIBLE);
            btUploadPendingMedia.setVisibility(VISIBLE);
            Glide.with(mContext)
                    .load(R.drawable.logo)
                    .apply(new RequestOptions().fitCenter())
                    .into(ivPlaceHolder);
        }
    }

    private void showUI(OpportunityDetailModel opportunityDetailModel) {
        if(opportunityDetailModel.getBrands().size()>0){
            Glide.with(mContext).load(opportunityDetailModel.getBrands().get(0).getLogo()).apply(RequestOptions.circleCropTransform())
                    .into(logo_iv);
        }else{
            logo_iv.setVisibility(GONE);
        }
        String static_url = "https://maps.googleapis.com/maps/api/staticmap?center="
                + opportunityDetailModel.getLat() + "," + opportunityDetailModel.getLng() +
                "&zoom=13&size="+ String.valueOf(800)+"x" + String.valueOf(300) +"&maptype=roadmap%20&markers=color:orange%7C"
                + opportunityDetailModel.getLat() + "," + opportunityDetailModel.getLng() + "&key="
                + getString(R.string.google_maps_key);
        Glide.with(mContext).load(static_url).apply(new RequestOptions().centerCrop()).into(static_map_iv);
        date_tv.setText(opportunityDetailModel.getFormattedCreatedAt());
        opportunity_title_tv.setText(opportunityDetailModel.getType().getName());
        opportunity_id_tv.setText("Folio: " + opportunityDetailModel.getId());
        opportunity_description_tv.setText(opportunityDetailModel.getDescription());
        division_tv.setText(opportunityDetailModel.getDivision().getName());
        // comentario_number_static_tv.setText("4");
        populateViewPager(opportunityDetailModel.getMedia());

        lat_map=opportunityDetailModel.getLat();
        lng_map=opportunityDetailModel.getLng();

        rvMarcas.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayout.HORIZONTAL, false);
        adapter = new BrandsTagsAdapter(mContext, opportunityDetailModel.getBrands(), this, false);
        rvMarcas.setLayoutManager(linearLayoutManager);
        rvMarcas.setAdapter(adapter);
        rvMarcas.setVisibility(View.GONE);
        if (opportunityDetailModel.isHas_pending_chat()) {
            chat_available.setVisibility(VISIBLE);
        }else {
            chat_available.setVisibility(GONE);
        }
    }

    //ViewPager con featured restaurants
    private void populateViewPager(List<MediaEntity> imageEntities) {
        pagerAdapter = new ViewPagerAdapter(getActivity(),mContext,imageEntities,networkLocal);
        NUM_PAGES = imageEntities.size();
        if (NUM_PAGES > 0) {
         //   restaurant_iv.setVisibility(View.INVISIBLE);
        }
        viewpager.setAdapter(pagerAdapter);
        indicator.setViewPager(viewpager);
        viewpager.setCurrentItem(0);
        viewpager.setOffscreenPageLimit(NUM_PAGES);
        viewpager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        isTimerCancelled = false;
        myTimer.cancel();
        myTimer.purge();
        //setUpViewPagerTimer();
    }


    @Override
    public void onAddBrand() {

    }

    @Override
    public void onDeleteBrand(int position) {

    }
}
