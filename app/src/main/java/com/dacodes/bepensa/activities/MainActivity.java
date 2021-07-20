package com.dacodes.bepensa.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.BuildConfig;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.adapters.MenuAdapter;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.ProfileEntity;
import com.dacodes.bepensa.entities.ofline.packageSurvey.OflineSurveyEntity;
import com.dacodes.bepensa.entities.ofline.postOpportunity.OflineListMedia;
import com.dacodes.bepensa.entities.ofline.postOpportunity.OflineMediaOpportunity;
import com.dacodes.bepensa.entities.ofline.postOpportunity.OflinePostOpportunity;
import com.dacodes.bepensa.fragments.BeneficioFragment;
import com.dacodes.bepensa.fragments.CommentsSuggestions;
import com.dacodes.bepensa.fragments.ComunicadosFragment;
import com.dacodes.bepensa.fragments.ConfigurationFragment;
import com.dacodes.bepensa.fragments.EventosFragment;
import com.dacodes.bepensa.fragments.FeedFragment;
import com.dacodes.bepensa.fragments.FragmentTest;
import com.dacodes.bepensa.fragments.HomeFragment;
import com.dacodes.bepensa.fragments.NotificacionesFragment;
import com.dacodes.bepensa.fragments.PerfilFragment;
import com.dacodes.bepensa.fragments.PremiosFragment;
import com.dacodes.bepensa.fragments.RankingFragment;
import com.dacodes.bepensa.models.PointsResponseModel;
import com.dacodes.bepensa.models.ResponseVersionPlayStore;
import com.dacodes.bepensa.service.DownloadJsonBrands;
import com.dacodes.bepensa.service.DownloadJsonDivision;
import com.dacodes.bepensa.service.DownloadJsonOppotunityType;
import com.dacodes.bepensa.service.DownloadJsonSurvey;
import com.dacodes.bepensa.service.UploadMediaOportunity;
import com.dacodes.bepensa.service.UploadSurveyOfline;
import com.dacodes.bepensa.utils.UpdateApp;
import com.dacodes.bepensa.utils.ValidateNetwork;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;
import static com.dacodes.bepensa.api.RetrofitWebServices.GET_POINTS;
import static com.dacodes.bepensa.api.RetrofitWebServices.GET_VERSION_PLAY_STORE;
import static com.dacodes.bepensa.api.RetrofitWebServices.REGISTER_PUSH;
import static com.dacodes.bepensa.push_notifications.MyFirebaseMessagingService.CHAT_INTENT;
import static com.dacodes.bepensa.push_notifications.MyFirebaseMessagingService.EVENT_DETAIL_INTENT;
import static com.dacodes.bepensa.push_notifications.MyFirebaseMessagingService.NOTIFICATION_INTENT;
import static com.dacodes.bepensa.push_notifications.MyFirebaseMessagingService.OPPORTUNITY_DETAIL_INTENT;
import static com.dacodes.bepensa.push_notifications.MyFirebaseMessagingService.PROMOTION_DETAIL_INTENT;
import static com.dacodes.bepensa.push_notifications.MyFirebaseMessagingService.RELEASE_DETAIL_INTENT;
import static com.dacodes.bepensa.push_notifications.MyFirebaseMessagingService.SURVEY_DETAIL_INTENT;

public class MainActivity extends BaseActivity implements MenuAdapter.CustomMenuClickListener,
        PerfilFragment.OnFragmentInteractionListener, HomeFragment.OnFragmentInteractionListener
        , PremiosFragment.OnFragmentPremiosListener,BeneficioFragment.searchData,
        RetrofitWebServices.DefaultResponseListeners,GoogleApiClient.ConnectionCallbacks{

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.scrollMenu)
    RecyclerView recyclerView;
    @BindView(R.id.layout_drawer)LinearLayout layout_drawer;
    AppController app;
    boolean isSelected = false;
    MenuAdapter menuAdapter;
    Fragment visibleFragment;
    ProfileEntity profileEntity;
    RetrofitWebServices rws;
    View ranking_view;
    View iconProfie;
    View pointPromotions;
    private Context mContext;
    Realm realm;
    boolean has_sync=false;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    ArrayList<OflineSurveyEntity> itemLocal=new ArrayList<>();
    int cont_item=0;
    int cont_opor_local=0;
    ArrayList<OflinePostOpportunity> itemOpportunityLoca=new ArrayList<>();
    private int TAG_POSITION=1;

    @BindView(R.id.ivProfilePicture)ImageView ivProfilePicture;
    @BindView(R.id.tvNombre)TextView tvNombre;
    @BindView(R.id.id_tv)TextView id_tv;
    @BindView(R.id.points_tv)TextView points_tv;
    @BindView(R.id.points_available)TextView points_available;

    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private static final int PERMISSIONS_ACCESS_FINE = 200;

    private AppEventsLogger logger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
        app = (AppController) getApplication();
        setSupportActionBar(toolbar);
        realm=Realm.getDefaultInstance();
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        ranking_view = mInflater.inflate(R.layout.toolbar_ranking_layout, null);
        iconProfie = mInflater.inflate(R.layout.icon_profile,null);
        pointPromotions = mInflater.inflate(R.layout.points_promotions,null);
        toolbar.setTitleTextColor(ContextCompat.getColor(mContext, R.color.colorOrange));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rws = new RetrofitWebServices(app, LOG_TAG, mContext, this);
        rws.getPoints();
        if (app.getDeviceToken().contentEquals("")) {
            String token = FirebaseInstanceId.getInstance().getToken();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            preferences.edit().putString("deviceID", token).apply();
        }

        if (!app.isRegisterForPush()) {
            try {
                String name="Marca:"+Build.BRAND +",Modelo:"+Build.MODEL+",Android:"+Build.VERSION.RELEASE;
                rws.registerPushServices(app.getAccessToken(),app.getAndroid_id(), app.getDeviceToken(), "android", ""+name);
                app.setNotifications(true);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        if (getIntent() != null){
            String filters = getIntent().getStringExtra("filters");
            app.setFiltersPromotions(filters);
        }

        downloadDatas();
        SlideMenu();
        Bundle bundle = new Bundle();
        profileEntity = (ProfileEntity) getIntent().getSerializableExtra("profile");
        bundle.putSerializable("profile", profileEntity);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(bundle);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        visibleFragment = fragment;
        transaction.add(R.id.content, visibleFragment);
        transaction.commit();
        TAG_POSITION=1;
        Intent intent = getIntent();
        int intentType = intent.getIntExtra("intentType", 0);
        int intentTypeBack=intent.getIntExtra("intentTypeBack",0);
        showIntent(intentType);
        showIntentBack(intentTypeBack);
        logger=AppEventsLogger.newLogger(this);
    }

    public void logSentFriendRequestEvent () {
        logger.logEvent("sentFriendRequest");
    }

    private void showMessageUpdate(){
        UpdateApp updateApp=new UpdateApp();
        updateApp.setCancelable(false);
        updateApp.show(getSupportFragmentManager(),"UpdateApp");
    }

    @OnClick({R.id.ivProfilePicture,R.id.tvNombre,R.id.id_tv,R.id.points_available,R.id.points_tv})
    public void viewProfile(){
        loadProfile();
    }

    public void loadProfile(){
        PerfilFragment fragment = new PerfilFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        visibleFragment = fragment;
        transaction.replace(R.id.content, visibleFragment);
        transaction.commit();
    }

    public void loadHeaderUser(){
        String photo = app.getImageUrl();
        if(!TextUtils.isEmpty(photo)){
            Glide.with(mContext).asBitmap()
                    .load(photo)
                    .apply(new RequestOptions().centerCrop().circleCrop())
                    .transition(withCrossFade())
                    .into(ivProfilePicture);
        }else{
            Glide.with(mContext).load(app.getImageUrl())
                    .apply(new RequestOptions().placeholder(R.drawable.no_media_placeholder))
                    .apply(new RequestOptions().circleCrop())
                    .into(ivProfilePicture);
        }
        tvNombre.setText(""+app.getName()+" "+app.getLastName());
        id_tv.setText("ID: " +app.getUsername());
        try {
            points_tv.setText(""+app.getPoints()+" puntos totales");
        }catch (Exception e){
            e.printStackTrace();
            rws.getPoints();
        }

        try {
            if(app.getPointAvailable1()>0){
                points_available.setText(""+app.getPointAvailable1()+" puntos disponibles");
            }else{
                points_available.setText(""+app.getPointAvailable1()+"");
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = manager.getActiveNetworkInfo();
            onNetworkChange(ni);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        getVersionPlay();
        //showMessageUpdate();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        unregisterReceiver(networkStateReceiver);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if(TAG_POSITION==1){
            super.onBackPressed();
        }else{
            itemSelected(0,MenuAdapter.OPORTUNIDADES_ID);
        }
    }

    private void onNetworkChange(NetworkInfo networkInfo) {
        if (networkInfo != null && networkInfo.isConnected()) {
            checkSync();
        }else {
            Toast.makeText(mContext,"Sin acceso a internet",Toast.LENGTH_LONG).show();
        }
    }

    public void checkSync(){
        String id_colaborator=app.getUsername();
        RealmResults<OflineSurveyEntity> persons = realm.where(OflineSurveyEntity.class)
                .equalTo("has_sync", true).equalTo("id_colaborator",id_colaborator).findAll();
        if (persons.size()>0){
            this.itemLocal.addAll(persons);
            syncDataLocal(itemLocal);
        }else{
            checkSuncMediaFails();
        }
    }

    public void downloadDatas(){
        if (!isMyServiceRunning(DownloadJsonSurvey.class)
                && !isMyServiceRunning(DownloadJsonDivision.class)
                && !isMyServiceRunning(DownloadJsonBrands.class) &&!isMyServiceRunning(DownloadJsonOppotunityType.class)){

            if (ValidateNetwork.getNetwork(mContext)){
                Intent intent = new Intent(mContext,DownloadJsonSurvey.class);
                startService(intent);
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void checkSyncOpportunity(){
        String id_colaborator=app.getUsername();
        RealmResults<OflinePostOpportunity> persons = realm.where(OflinePostOpportunity.class)
                .equalTo("has_sync", true).equalTo("id_colaborator",id_colaborator).findAll();
        if (persons.size()>0){
            this.itemOpportunityLoca.addAll(persons);
            syncOpportunityLocal(itemOpportunityLoca);
        }
    }

    public void checkSuncMediaFails(){
        String id_colaborator=app.getUsername();
        final RealmResults<OflineListMedia> result1 = realm.where(OflineListMedia.class)
                .equalTo("has_sync", true).equalTo("id_colaborator",id_colaborator).findAll();
        if (result1.size() > 0) {
            if (isMyServiceRunning(UploadMediaOportunity.class)) {
                Log.i("SERVICE_START","Ejecutando");
            }else {
                Intent intent = new Intent(mContext, UploadMediaOportunity.class);
                startService(intent);
            }
        } else {
            checkSyncOpportunity();
        }
    }
    private void showIntentBack(int type){
        switch (type){
            case 6:
                itemSelected(5, MenuAdapter.TERMS_CONDITIONS_ID);
                break;
            case 2:
                itemSelected(1, MenuAdapter.MI_BOLETIN);
                break;
            case 1:
                itemSelected(0, MenuAdapter.OPORTUNIDADES_ID);
                break;
            case 34:
                itemSelected(3,MenuAdapter.PROMOCIONES_ID);
                break;
            case 35:
                itemSelected(0,MenuAdapter.OPORTUNIDADES_ID);
                break;
            default:
                    break;

        }
    }

    private void showIntent(int intentType) {
        int opportunityId, eventId;
        switch (intentType) {
            case CHAT_INTENT: {
                opportunityId = getIntent().getIntExtra("opportunityId", 0);
                Intent intent = new Intent(this, ActivityDetailOportunity.class);
                intent.putExtra("id", opportunityId);
                intent.putExtra("goToChat", true);
                intent.putExtra("networkLocal", 1);
                startActivity(intent);
                break;
            } case OPPORTUNITY_DETAIL_INTENT: {
                itemSelected(0, MenuAdapter.OPORTUNIDADES_ID);
                opportunityId = getIntent().getIntExtra("opportunityId", 0);
                Intent intent = new Intent(this, ActivityDetailOportunity.class);
                intent.putExtra("id", opportunityId);
                intent.putExtra("networkLocal", 1);
                intent.putExtra("screen", OPPORTUNITY_DETAIL_INTENT);
                startActivity(intent);
                break;
            }case EVENT_DETAIL_INTENT: {
                toolbar.setTitle("EVENTOS");
                itemSelected(4, MenuAdapter.EVENTOS_ID);
                eventId = getIntent().getIntExtra("eventId", 0);
                Intent intent = new Intent(this, EventoDetailActivity.class);
                intent.putExtra("eventId", eventId);
                intent.putExtra("screen", EVENT_DETAIL_INTENT);
                startActivity(intent);
                break;
            }case PROMOTION_DETAIL_INTENT: {
                toolbar.setTitle("BENEFICIOS");
                itemSelected(3, MenuAdapter.PROMOCIONES_ID);
                eventId = getIntent().getIntExtra("promotionId", 0);
                Intent intent = new Intent(this, PromotionsDetailActivity.class);
                intent.putExtra("promotionId", eventId);
                intent.putExtra("screen", EVENT_DETAIL_INTENT);
                startActivity(intent);
                break;
            }case SURVEY_DETAIL_INTENT: {
                itemSelected(5, MenuAdapter.TERMS_CONDITIONS_ID);
                eventId = getIntent().getIntExtra("surveyId", 0);
                Intent intent = new Intent(this, PrincipalTest.class);
                intent.putExtra("surveyId", eventId);
                intent.putExtra("screen", SURVEY_DETAIL_INTENT);
                startActivity(intent);
                break;
            } case RELEASE_DETAIL_INTENT: {
                toolbar.setTitle("EVENTOS");
                itemSelected(2, MenuAdapter.COMUNICADOS_ID);
                break;
            }case NOTIFICATION_INTENT: {
                toolbar.setTitle("NOTIFICACIONES");
                itemSelected(8, MenuAdapter.NOTIFICACIONES_ID);
                break;

            }default:
                break;
        }
    }

    public ProfileEntity getProfileEntity() {
        return this.profileEntity;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void SlideMenu() {
        loadHeaderUser();
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        //drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.closeDrawer(layout_drawer);
        menuAdapter = new MenuAdapter(mContext, this);
       // menuAdapter.addHeaderImage(app.getImageUrl(), app.getFullName(), app.getUsername(), app.getPoints(),app.getPointAvailable());
        menuAdapter.addTextIconItem(R.drawable.menu_oportunidades, "Oportunidades", MenuAdapter.OPORTUNIDADES_ID, true);
        menuAdapter.addTextIconItem(R.drawable.boletin_icon, "Mi Boletín", MenuAdapter.MI_BOLETIN, false);
        menuAdapter.addTextIconItem(R.drawable.menu_comunicados, "Comunicados", MenuAdapter.COMUNICADOS_ID, false);
        menuAdapter.addTextIconItem(R.drawable.menu_beneficios,"Beneficios", MenuAdapter.PROMOCIONES_ID,false);
        menuAdapter.addTextIconItem(R.drawable.menu_eventos, "Eventos", MenuAdapter.EVENTOS_ID, false);
        menuAdapter.addTextIconItem(R.drawable.menu_encuestas,"Encuestas", MenuAdapter.TERMS_CONDITIONS_ID, false);
        menuAdapter.addTextIconItem(R.drawable.menu_ranking, "Ranking", MenuAdapter.RANKING_ID, false);
        menuAdapter.addTextIconItem(R.drawable.menu_beneficios, "Premios", MenuAdapter.AWARDS_ID, false);
        menuAdapter.addTextIconItem(R.drawable.menu_notificaciones, "Notificaciones", MenuAdapter.NOTIFICACIONES_ID, false);
        menuAdapter.addTextIconItem(R.drawable.menu_ranking, "Comentarios y sugerencias", MenuAdapter.COMMENTS_SUGGESTIONS, false);
        menuAdapter.addTextIconItem(R.drawable.menu_configuraciones, "Configuraciones", MenuAdapter.CONFIGURACION_ID, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(menuAdapter);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_opened, R.string.drawer_closed) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                hideKeyboard(MainActivity.this);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
    }

    public void refreshPoint(){
        removePoint();
        SlideMenu();
        promotionAvailable();
    }

    public void refreshTotalPoint(){
        SlideMenu();
    }

    public void removeRanking() {
        toolbar.removeView(ranking_view);
    }

    public void removeProfile(){
        toolbar.removeView(iconProfie);
    }

    public void removePoint(){
        toolbar.removeView(pointPromotions);
    }
    public void addIcon(){
        ImageView logo = iconProfie.findViewById(R.id.logo);

        try{
            toolbar.addView(iconProfie, new Toolbar.LayoutParams(Gravity.RIGHT));
            menuAdapter.notifyDataSetChanged();
            logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext,EditProfile.class);
                    startActivity(intent);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void promotionAvailable(){
        TextView point = pointPromotions.findViewById(R.id.pts_tv);
        CircleImageView imageLogo = pointPromotions.findViewById(R.id.imageLogo);
        try{
            toolbar.addView(pointPromotions, new Toolbar.LayoutParams(Gravity.RIGHT));
            point.setText(""+app.getPointAvailable1()+" pts");
            Glide.with(this)
                    .load(app.getImageUrl())
                    .apply(new RequestOptions().centerCrop().centerCrop())
                    .into(imageLogo);
            menuAdapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void viewRanking() {
        TextView ranking_tv = ranking_view.findViewById(R.id.ranking_tv);
        TextView pts_tv = ranking_view.findViewById(R.id.pts_tv);
        ImageView imageLogo = ranking_view.findViewById(R.id.imageLogo);
        RelativeLayout ln=ranking_view.findViewById(R.id.ln);

        try {
            toolbar.addView(ranking_view, new Toolbar.LayoutParams(Gravity.RIGHT));
            Glide.with(mContext).load(app.getImageUrl())
                    .apply(new RequestOptions().centerCrop().centerCrop())
                    .into(imageLogo);

            menuAdapter.getItems().get(0).setPoints(""+app.getPoints());
            pts_tv.setText(app.getPoints() + " pts");
            ranking_tv.setText(app.getRankingPosition());
            menuAdapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void viewRowStick() {
        removeRanking();
        TextView ranking_tv = ranking_view.findViewById(R.id.ranking_tv);
        TextView pts_tv = ranking_view.findViewById(R.id.pts_tv);
        ImageView imageLogo = ranking_view.findViewById(R.id.imageLogo);
        RelativeLayout ln=ranking_view.findViewById(R.id.ln);
        ln.setVisibility(View.GONE);
        try {
            toolbar.addView(ranking_view, new Toolbar.LayoutParams(Gravity.RIGHT));
            Glide.with(mContext).load(app.getImageUrl())
                    .apply(new RequestOptions().centerCrop().centerCrop())
                    .into(imageLogo);

            menuAdapter.getItems().get(0).setPoints(""+app.getPoints());
            pts_tv.setText(app.getPoints() + "pts");
            ranking_tv.setText(app.getRankingPosition());
            menuAdapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onCustomMenuItemClick(int position, int type, int id) {
        itemSelected(position, id);
        drawerLayout.closeDrawer(layout_drawer);
    }

    public void showNotificationFragment(FragmentTransaction transaction, FragmentManager manager){
        toolbar.setTitle("NOTIFICACIONES");
        transaction = manager.beginTransaction();
        visibleFragment = new NotificacionesFragment();
        transaction.replace(R.id.content, visibleFragment);
        transaction.commit();
    }

    public void itemSelected(int position, int id) {
        Intent intentFilter = getIntent();
        int backOpportunity=0;
        if (intentFilter!=null){
            backOpportunity=intentFilter.getIntExtra("backOpportunity",0);
        }

        FragmentManager manager = getSupportFragmentManager();
        Intent intent = null;
        FragmentTransaction transaction = null;
        menuAdapter.selectRow(position);
        switch (id) {
            case MenuAdapter.OPORTUNIDADES_ID:
                toolbar.setTitle("GENERAL");
                transaction = manager.beginTransaction();
                visibleFragment = new HomeFragment();
                if (backOpportunity>0){
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("backOpportunity",1);
                    visibleFragment.setArguments(bundle);
                }
                transaction.replace(R.id.content, visibleFragment);
                transaction.commit();
                removeRanking();
                removePoint();
                removeProfile();
                TAG_POSITION=1;
                break;

            case MenuAdapter.FEED_ID:
                toolbar.setTitle("FEED");
                transaction = manager.beginTransaction();
                visibleFragment = new FeedFragment();
                transaction.replace(R.id.content, visibleFragment);
                transaction.commit();
                removeRanking();
                removePoint();
                removeProfile();
                TAG_POSITION=14;
                break;

            case MenuAdapter.COMUNICADOS_ID:
                removeRanking();
                transaction = manager.beginTransaction();
                visibleFragment = new ComunicadosFragment();
                transaction.replace(R.id.content, visibleFragment);
                transaction.commit();
                toolbar.setTitle("COMUNICADOS");
                removeRanking();
                removePoint();
                removeProfile();
                TAG_POSITION=13;
                break;

            case MenuAdapter.PROMOCIONES_ID:
                initializeLocation();
                visibleFragment = new BeneficioFragment();
                toolbar.setTitle("BENEFICIOS");
                transaction = manager.beginTransaction();
                transaction.replace(R.id.content, visibleFragment);
                transaction.commit();
                removeRanking();
                removePoint();
                removeProfile();
                TAG_POSITION=12;
                break;

            case MenuAdapter.RANKING_ID:
                toolbar.setTitle("RANKING");
                transaction = manager.beginTransaction();
                visibleFragment = new RankingFragment();
                transaction.replace(R.id.content, visibleFragment);
                transaction.commit();
                removeRanking();
                removePoint();
                removeProfile();
                TAG_POSITION=11;
                break;

            case MenuAdapter.COMMENTS_SUGGESTIONS:
                toolbar.setTitle("COMENTARIOS Y SUGERENCIAS");
                transaction = manager.beginTransaction();
                visibleFragment = new CommentsSuggestions();
                transaction.replace(R.id.content, visibleFragment);
                transaction.commit();
                removeRanking();
                removePoint();
                removeProfile();
                TAG_POSITION=10;
                break;

            case MenuAdapter.AWARDS_ID:
                toolbar.setTitle("PREMIOS");
                transaction = manager.beginTransaction();
                visibleFragment = new PremiosFragment();
                transaction.replace(R.id.content, visibleFragment);
                transaction.commit();
                removeRanking();
                promotionAvailable();
                removeProfile();
                TAG_POSITION=8;
                break;

            case MenuAdapter.CONFIGURACION_ID:
                toolbar.setTitle("CONFIGURACIONES");
                transaction = manager.beginTransaction();
                visibleFragment = new ConfigurationFragment();
                transaction.replace(R.id.content, visibleFragment);
                transaction.commit();
                removeRanking();
                removePoint();
                removeProfile();
                TAG_POSITION=9;
                break;

            case MenuAdapter.HEADER_ID:
                toolbar.setTitle("PERFIL");
                transaction = manager.beginTransaction();
                visibleFragment = new PerfilFragment();
                transaction.replace(R.id.content,  visibleFragment);
                transaction.commit();
                removeRanking();
                removePoint();
                addIcon();
                TAG_POSITION=3;
                break;

            case MenuAdapter.MI_BOLETIN:
                toolbar.setTitle("MI BOLETÍN");
                transaction = manager.beginTransaction();
                visibleFragment = new FeedFragment();
                transaction.replace(R.id.content,  visibleFragment);
                transaction.commit();
                removeRanking();
                removePoint();
                removeProfile();
                TAG_POSITION=4;
                break;
            case MenuAdapter.EVENTOS_ID:
                initializeLocation();
                transaction = manager.beginTransaction();
                visibleFragment = new EventosFragment();
                transaction.replace(R.id.content, visibleFragment);
                transaction.commit();
                toolbar.setTitle("EVENTOS");
                removeRanking();
                removePoint();
                removeProfile();
                TAG_POSITION=5;
                break;

            case MenuAdapter.NOTIFICACIONES_ID:
                showNotificationFragment(transaction, manager);
                toolbar.setTitle("NOTIFICACIONES");
                removeRanking();
                removePoint();
                removeProfile();
                TAG_POSITION=6;
                break;

            case MenuAdapter.TERMS_CONDITIONS_ID:
                Log.v(LOG_TAG, "TERMS_CONDITIONS_ID");
                toolbar.setTitle("ENCUESTAS");
                transaction = manager.beginTransaction();
                visibleFragment = new FragmentTest();
                transaction.replace(R.id.content, visibleFragment);
                transaction.commit();
                removeRanking();
                removePoint();
                removeProfile();
                TAG_POSITION=7;
                break;
        }
    }

    public void setToolbar(int pos){
        switch (pos){
            case 2:
                getSupportActionBar().setTitle("MI BOLETÍN");
                break;
            case 6:
                getSupportActionBar().setTitle("ENCUESTAS");
                break;
            case 3:
                getSupportActionBar().setTitle("COMUNICADO");
                break;
            case 1:
                getSupportActionBar().setTitle("MIS OPORTUNIDADES");
                break;
            case 8:
                getSupportActionBar().setTitle("PERFIL");
                break;
            case 10:
                getSupportActionBar().setTitle("NOTIFICACIONES");
                break;
        }

    }

    @Override
    public void onFragmentInteraction(String newUrl, String name) {
        menuAdapter.getItems().get(0).setImage(newUrl);
        menuAdapter.getItems().get(0).setTextField1(name);
        menuAdapter.notifyDataSetChanged();
        itemSelected(0, MenuAdapter.OPORTUNIDADES_ID);
    }

    @Override
    public void onFragmentInteraction() {
        //itemSelected(MenuAdapter.CURRICULUM_ID);
    }

    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id) {
            case GET_POINTS:
                try {
                    PointsResponseModel responseModel = (PointsResponseModel) object;
                    points_tv.setText(""+responseModel.getPoints()+" puntos totales");
                    if(app.getPointAvailable1()>0){
                        points_available.setText(""+responseModel.getPoints_available()+" puntos disponibles");
                    }else{
                        points_available.setText(""+responseModel.getPoints_available()+"");
                    }
                    app.setPoints(responseModel.getPoints());
                    app.setRankingPosition(String.valueOf(responseModel.getRank()));
                    app.setPointAvailable1(responseModel.getPoints_available());
                    loadHeaderUser();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case REGISTER_PUSH:
                Log.e("push","registrado");
                app.setRegisterForPush(true);
                break;
            case GET_VERSION_PLAY_STORE:
                ResponseVersionPlayStore play=(ResponseVersionPlayStore)object;
                compareToVersion(BuildConfig.VERSION_NAME.replaceAll("[^0-9]","")
                        ,play.getVersion().replaceAll("[^0-9]",""));
                break;
        }
    }


    @Override
    public void onFailError(int id, int status, String message) {
            switch (id){
                case REGISTER_PUSH:
                    app.setRegisterForPush(true);
                    app.setDeviceId(0);
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

    private void compareToVersion(String local, String playStore){
        try {
            if (Integer.parseInt(local)<Integer.parseInt(playStore)){
                showMessageUpdate();
            }else{
                Log.v("VersionPlay","Local:"+local+"Play"+playStore);
            }
        }catch (NumberFormatException e){
            if (!local.equals(playStore)){
                showMessageUpdate();
            }
        }catch (Exception e){

        }
    }

    private void getVersionPlay(){
        rws.getVersionPlay(app.getAccessToken(),"android");
    }

    public void syncDataLocal(final ArrayList<OflineSurveyEntity> items){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false)
                .setMessage("Se ha detectado que existen encuestas sin publicar al servidor. ¿Desea publicarlos ahora?")
                .setPositiveButton("Borrar", (dialog, which) -> {
                    dialog.dismiss();
                    verifiLocalSurvey();
                })
                .setNeutralButton("Mas tarde", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("Si", (dialogInterface, i) -> {
                    Intent intent = new Intent(mContext,UploadSurveyOfline.class);
                    startService(intent);
                    dialogInterface.dismiss();
                });
        if (!isFinishing()){
            builder.show();
        }
    }

    public void syncOpportunityLocal(final ArrayList<OflinePostOpportunity> items){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Se ha detectado que existen oportunidades sin publicar al servidor. ¿Desea publicarlos ahora?")
                .setPositiveButton("Borrar", (dialog, which) -> {
                    dialog.dismiss();
                    verifiLocalOpportunity();
                })
                .setNeutralButton("Mas tarde", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("Si", (dialogInterface, i)
                -> {
                    dialogInterface.dismiss();
                    Intent intent = new Intent(mContext,UploadMediaOportunity.class);
                    startService(intent);

                });
        if (!isFinishing()){
            builder.show();
        }
    }

    public void verifiLocalOpportunity(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false)
                .setMessage("Los datos de la oportunidad se borrarán, en caso de que quieras enviarlos," +
                        " deberás reportarla nuevamente")
                /*.setPositiveButton("No", (dialog, which) -> {
                    dialog.dismiss();
                })*/
                .setNegativeButton("Aceptar", (dialogInterface, i)
                        -> {
                    dialogInterface.dismiss();
                    checkDataBaseOpportunity();
                });
        if (!isFinishing()){
            builder.show();
        }
    }

    public void verifiLocalSurvey(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false)
                .setMessage("Los datos de la encuesta se borrarán, en caso de que quieras enviarlos, " +
                "deberás de contestarla nuevamente")
                .setPositiveButton("Aceptar", (dialogInterface, i)
                        -> {
                    dialogInterface.dismiss();
                    checkDataBaseSurvey();
                });
        if (!isFinishing()){
            builder.show();
        }
    }


    public void checkDataBaseOpportunity(){
        RealmResults<OflinePostOpportunity> result1 = realm.where(OflinePostOpportunity.class).findAll();
        RealmResults<OflineListMedia> result4 = realm.where(OflineListMedia.class).findAll();
        RealmResults<OflineMediaOpportunity> result6 = realm.where(OflineMediaOpportunity.class).findAll();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                result1.deleteAllFromRealm();
                result4.deleteAllFromRealm();
                result6.deleteAllFromRealm();
            }
        });
    }

    public void checkDataBaseSurvey(){
        RealmResults<OflineSurveyEntity> result12 = realm.where(OflineSurveyEntity.class).equalTo("has_sync_load",true).findAll();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    result12.deleteAllFromRealm();
                }
            });
    }

    @Override
    public void dataValues(String values) {
        Log.e("data",""+values);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void initializeLocation(){
        buildGoogleAPiClient();
        buildLocationRequest();
    }

    private void buildGoogleAPiClient(){
        googleApiClient= new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();
        googleApiClient.connect();
    }

    private void buildLocationRequest(){
        locationRequest=LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationSettingsRequest.Builder locationSettingsRequest = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).setAlwaysShow(true);

        SettingsClient settingsClient =  LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task =settingsClient.checkLocationSettings(locationSettingsRequest.build());

        task.addOnSuccessListener(locationSettingsResponse -> {
            validateServiceLocation();
        });

        task.addOnFailureListener(e -> {
            if (e instanceof ResolvableApiException){
                try {
                    ((ResolvableApiException) e).startResolutionForResult(MainActivity.this,REQUEST_CHECK_SETTINGS);
                }catch (IntentSender.SendIntentException e1){

                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS){
            if (resultCode == RESULT_OK)
                validateServiceLocation();
        }
    }

    private void validateServiceLocation(){
        if (versionSdk()){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_ACCESS_FINE);
        }
    }

    private boolean versionSdk(){
        return Build.VERSION.SDK_INT>=Build.VERSION_CODES.M;
    }
}
