package com.dacodes.bepensa.fragments;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.UploadMedias;
import com.dacodes.bepensa.activities.ChatActivity;
import com.dacodes.bepensa.activities.UploadPendingMedia;
import com.dacodes.bepensa.adapters.ViewPagerAdapter;
import com.dacodes.bepensa.entities.PackegeOpportunity.MediaEntity;
import com.dacodes.bepensa.models.OpportunityDetailModel;
import com.efrain.dcretrofit.DCListeners;
import com.efrain.dcretrofit.DCResponse;
import com.efrain.dcretrofit.DCResponseInterface;
import com.efrain.dcretrofit.DCService;

import java.util.List;
import java.util.Timer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.dacodes.bepensa.api.RetrofitWebServices.GET_OPPORTUNITY_DETAIL;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDetailOportunityNetwork extends Fragment
        implements DCListeners.retrofitResponse {


    @BindView(R.id.indicator) CircleIndicator indicator;
    @BindView(R.id.viewpager) ViewPager viewpager;
    @BindView(R.id.logo_iv) ImageView logo_iv;
    @BindView(R.id.date_tv) TextView date_tv;
    @BindView(R.id.opportunity_title_tv) TextView opportunity_title_tv;
    @BindView(R.id.opportunity_id_tv) TextView opportunity_id_tv;
    @BindView(R.id.opportunity_description_tv) TextView opportunity_description_tv;
    @BindView(R.id.division_static_tv) TextView division_static_tv;
    @BindView(R.id.division_tv) TextView division_tv;
    @BindView(R.id.static_map_iv) ImageView static_map_iv;
    @BindView(R.id.btUploadPendingMedia) Button btUploadPendingMedia;
    @BindView(R.id.ivPlaceHolder) ImageView ivPlaceHolder;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.prMedias) ProgressBar prMedias;
    @BindView(R.id.contenido) LinearLayout contenido;
    @BindView(R.id.vChat)ImageView vChat;
    @BindView(R.id.ivChat)TextView ivChat;
    @BindView(R.id.tvPuntos)TextView tvPuntos;
    @BindView(R.id.tvStatus)TextView tvStatus;

    private static final int UPLOAD_MEDIA = 102;
    private int id = 0;
    private boolean goToChat;
    private RecyclerView.Adapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private int pageIndex = 0;
    private int NUM_PAGES = 0;
    private Timer myTimer = new Timer();
    private boolean isTimerCancelled = false;
    private static final String LOG_TAG = FragmentDetailOportunityNetwork.class.getSimpleName();
    CountDownTimer countDownTimer;

    Context mContext;
   // RetrofitWebServices retrofitWebServices;
    AppController app;
    int id_oportunity=0;
    String lat_map="";
    String lng_map="";
    int files_media=0;
    int networkLocal=0;
    ViewPagerAdapter pagerAdapter;
    private uploadMedias upMedias;
    private Activity mActivity;
    //private Call<OpportunityDetailModel> call;


    public FragmentDetailOportunityNetwork() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mContext=getContext();
        app = (AppController) getActivity().getApplicationContext();
        mContext=getContext();
        mActivity=getActivity();
        assert getArguments() != null;
        goToChat = getArguments().getBoolean("goToChat", false);
       // retrofitWebServices = new RetrofitWebServices(app, LOG_TAG, mContext, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_detail_oportunity_network, container, false);
        ButterKnife.bind(this,view);

        //networkAvailable();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        /*if (call != null && call.isExecuted())
            call.cancel();*/
    }

    @OnClick(R.id.btUploadPendingMedia)
    public void submit(View view){
        switch (view.getId()){
            case R.id.btUploadPendingMedia:
                Bundle bundle = this.getArguments();
                if (bundle != null) {
                    int ids = bundle.getInt("id", 0);
                    Intent intent = new Intent(mContext, UploadPendingMedia.class);
                    intent.putExtra("OPPORTUNITY_ID", ids);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    getActivity().finish();
                    //getActivity().overridePendingTransition(R.anim.enter_up, R.anim.no_anim);
                }
                break;
        }
    }

    @OnClick(R.id.static_map_iv)
    public void map(){
        loadMapDetail(lat_map,lng_map);
    }

    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id) {
            case GET_OPPORTUNITY_DETAIL:
                OpportunityDetailModel response = (OpportunityDetailModel) object;
                progressBar.setVisibility(GONE);
                    contenido.setVisibility(VISIBLE);
                    progressBar.setVisibility(GONE);
                    if(response.getMedia().size()==0){//IF ISN`T MEDIA
                        btUploadPendingMedia.setVisibility(VISIBLE);
                        ivPlaceHolder.setVisibility(VISIBLE);
                        if (isMyServiceRunning(UploadMedias.class)){
                            if (app.getIdOpportunityMedias()>0) {
                                contTimer();
                                btUploadPendingMedia.setText("Subiendo archivos");
                                prMedias.setVisibility(VISIBLE);
                            }
                        }else{
                            Glide.with(mContext)
                                    .load(R.drawable.logo)
                                    .apply(new RequestOptions().fitCenter())
                                    .into(ivPlaceHolder);
                            prMedias.setVisibility(View.INVISIBLE);
                        }
                    }else {
                        btUploadPendingMedia.setVisibility(GONE);
                        ivPlaceHolder.setVisibility(GONE);
                    }
                showUI(response);
                break;
        }
    }

    @Override
    public void onFailure(int i, int i1, String s) {
        progressBar.setVisibility(GONE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof uploadMedias){
            upMedias=(uploadMedias)context;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopCurrentTime();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (goToChat)
            ivChat.setVisibility(GONE);
        networkAvailable();
        /*if (call != null && call.isExecuted())
            call.cancel();*/
    }

    @OnClick({ R.id.ivChat,R.id.vChat })
    public void chatClick(View view) {
        ivChat.setVisibility(GONE);
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("opportunityId", id);
        startActivity(intent);
    }

    private void contTimer(){
        countDownTimer=new CountDownTimer(120000,2500) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (isMyServiceRunning(UploadMedias.class)){

                }else{
                    try {
                        stopMediaService();
                    }catch (Exception e){

                    }
                    if (upMedias!=null)
                        upMedias.uploadMedias(true);
                    try {
                        app.setIdOpportunityMedias(0);
                        app.setLocalMedias(null);
                        stopCurrentTime();
                    }catch (Exception e){
                        app.setLocalMedias(null);
                        app.setIdOpportunityMedias(0);
                    }
                }
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    public void stopCurrentTime(){
        if (countDownTimer!=null){
            countDownTimer.cancel();
        }
    }

    private void stopMediaService(){
        if (isMyServiceRunning(UploadMedias.class)) {
            Intent intent = new Intent(mContext, UploadMedias.class);
            mActivity.stopService(intent);
        }
    }


    public interface uploadMedias{
        void uploadMedias(boolean status);
    }

    public void networkAvailable(){
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            id = bundle.getInt("id", 0);
            goToChat = bundle.getBoolean("goToChat", false);
            files_media=bundle.getInt("files_media",0);
            networkLocal=bundle.getInt("networkLocal",0);
            DCService.Build(this)
                    .setCode(GET_OPPORTUNITY_DETAIL)
                    .setCallResponse(new DCResponse<OpportunityDetailModel>() {
                        @Override
                        public Call<OpportunityDetailModel> getResponse(DCResponseInterface dcResponseInterface) {
                            return app.getWebServices().getOpportunityDetailModel(app.getAccessToken(),String.valueOf(id));
                        }
                    }).init();
            //call = retrofitWebServices.getOpportunityDetail(String.valueOf(id));

            id_oportunity=id;
            if(goToChat){
                ivChat.setVisibility(GONE);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("opportunityId", id);
                startActivity(intent);
            }
        }
    }

    public void loadMapDetail(String lat ,String lng){
        try {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=&daddr="+lat+","+lng));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_LAUNCHER );
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            startActivity(intent);
        }catch (ActivityNotFoundException e){
            Toast.makeText(mContext,"Es posible que no tenga una aplicación para ver la ubibicacion",Toast.LENGTH_LONG).show();
        }catch (Exception e){

        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void showUI(OpportunityDetailModel opportunityDetailModel) {
        if(opportunityDetailModel.getBrands().size()>0){
            Glide.with(mContext)
                    .load(opportunityDetailModel.getBrands().get(0).getLogo())
                    .apply(RequestOptions.circleCropTransform())
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
        tvPuntos.setText("Puntos: "+opportunityDetailModel.getPoints());
        tvStatus.setText(""+getStatusOportunity(opportunityDetailModel.getStatus()));
        if (opportunityDetailModel.isHas_pending_chat()) {
            ivChat.setVisibility(VISIBLE);
            ivChat.setText(""+opportunityDetailModel.getUnread_messages());
            //chat_available.setVisibility(VISIBLE);
        }else {
            ivChat.setVisibility(GONE);
            //chat_available.setVisibility(GONE);
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
        viewpager.setOnTouchListener((v, event) -> false);
        isTimerCancelled = false;
        myTimer.cancel();
        myTimer.purge();
        //setUpViewPagerTimer();
    }

    private String getStatusOportunity(int status){
        String myStatus="Pendiente";
        switch (status){
            case 1:
            case 2:
                myStatus="Enviada";
                break;
            case 5:
                myStatus="En seguimiento";
                break;
            case 4:
            case 3:
            case 6:
            case 7:
            case 8:
                myStatus="Cerrada";
                break;
        }
        return myStatus;
    }

}
