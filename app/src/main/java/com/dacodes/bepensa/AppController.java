package com.dacodes.bepensa;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;

import androidx.multidex.MultiDex;

import com.dacodes.bepensa.api.WebServices;
import com.dacodes.bepensa.utils.RealmMigrations;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.provider.Settings.Secure.ANDROID_ID;

public class AppController extends Application {

    private static AppController mInstance;
    public static Context getContext() {
        return mInstance;
    }

    private SharedPreferences preferences;
    private Retrofit retrofit;
    private String android_id;
    int deviceId;
    private WebServices webServices;

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        Fresco.initialize(this);
        mInstance = this;
        final String apiBaseUrl = getString(R.string.base_url);
        android_id = Settings.Secure.getString(getContext().getContentResolver(), ANDROID_ID);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(80, TimeUnit.SECONDS)
                .connectTimeout(80, TimeUnit.SECONDS)
                .build();

        webServices = retofitHeader().create(WebServices.class);

        retrofit = new Retrofit.Builder()
                .baseUrl(apiBaseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("bepensa.realm")
                .schemaVersion(6)
                .migration(new RealmMigrations())
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        Realm.getInstance(config);
    }

    private Retrofit retofitHeader(){
        OkHttpClient.Builder httpClient= new OkHttpClient.Builder();
        httpClient.connectTimeout(12, TimeUnit.SECONDS);
        //httpClient.addInterceptor(new Interceptor(this));
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(getString(R.string.base_url))
                .client(httpClient.build())
                .build();
    }

    public WebServices getWebServices() {
        return webServices;
    }

    @Override
    public void onTerminate() {
        Realm.getDefaultInstance().close();
        super.onTerminate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getAndroid_id() {
        return android_id;
    }

    public String getDeviceToken() {
        return preferences.getString("deviceID","");
    }


    public Retrofit getRetrofit() {
        return retrofit;
    }

    public void setIdBrands(String accessToken){
        preferences.edit().putString("idBrands",accessToken).apply();
    }

    public String getIdBrands(){
        return preferences.getString("idBrands", null);
    }

    public void setAccessToken(String accessToken){
        preferences.edit().putString("AccessToken",accessToken).apply();
    }

    public String getAccessToken(){
        return preferences.getString("AccessToken", "");
    }

    public void setLocalMedias(String dir){
        preferences.edit().putString("dirMedias",dir).apply();
    }

    public String getLocalMedias(){
        return preferences.getString("dirMedias", null);
    }

    public void setFilterThemes(String dir){
        preferences.edit().putString("filterThemes",dir).apply();
    }

    public String getFilterThemes(){
        return preferences.getString("filterThemes", null);
    }

    public void setFiltersPromotions(String userId){
        preferences.edit().putString("filters",userId).apply();
    }

    public String getFiltersPromotions(){
        return preferences.getString("filters", null);
    }

    public void setUserId(String userId){
        preferences.edit().putString("uuid",userId).apply();
    }

    public String getUserId(){
        return preferences.getString("uuid", "default");
    }

    public void setUsername(String userId){
        preferences.edit().putString("username",userId).apply();
    }

    public String getUsername(){
        return preferences.getString("username", "default");
    }


    public void setEmail(String email){
        preferences.edit().putString("email",email).apply();
    }

    public void setDivision(String division){
        preferences.edit().putString("division",division).apply();
    }

    public void setPhone(String phone){
        preferences.edit().putString("phone",phone).apply();
    }

    public void setPointAvailable(int phone){
        preferences.edit().putInt("pointAvailable",phone).apply();
    }

    public int getPointAvailable(){return  preferences.getInt("pointAvailable1",0);}

    public void setUpdateRealm(boolean version){
        preferences.edit().putBoolean("realmVersion",version).apply();
    }

    public boolean getRealmVersion(){return  preferences.getBoolean("realmVersion",true);}

    public void setPointAvailable1(int phone){
        preferences.edit().putInt("pointAvailable",phone).apply();
    }

    public void setRefresh(int refresh){
        preferences.edit().putInt("actualizacion",refresh).apply();
    }

    public int getRefresh(){return  preferences.getInt("actualizacion",0);}


    public int getPointAvailable1(){return  preferences.getInt("pointAvailable",0);}

    public void setRank(int rank){
        preferences.edit().putInt("rank",rank).apply();
    }

    public int getRank(){return  preferences.getInt("rank",0);}

    public void setIdDivision(int division){
        preferences.edit().putInt("idDivision",division).apply();
    }
    public int getIdDivision(){return  preferences.getInt("idDivision",0);}

    public void setIdOpportunity(int division){
        preferences.edit().putInt("idOpportunity",division).apply();
    }
    public int getIdOpportunity(){return  preferences.getInt("idOpportunity",0);}

    public void setIdOpportunityMedias(int division){
        preferences.edit().putInt("idOpportunityMedias",division).apply();
    }
    public int getIdOpportunityMedias(){return  preferences.getInt("idOpportunityMedias",0);}

    public String getEmail(){return  preferences.getString("email","default");}

    public String getDivision(){return  preferences.getString("division","default");}

    public String getPhone(){return  preferences.getString("phone","default");}

    public void setLoggedIn(boolean isLoggedIn){
        preferences.edit().putBoolean("IsLoggedIn",isLoggedIn).apply();
    }

    public boolean getLoggedIn(){
        return preferences.getBoolean("IsLoggedIn", false);
    }

    public void setImageDivision(String imageUrl){
        preferences.edit().putString("imageDivision",imageUrl).apply();
    }

    public String getImageDivision(){
        return preferences.getString("imageDivision", "");
    }

    public void setImageUrl(String imageUrl){
        preferences.edit().putString("ImageUrl",imageUrl).apply();
    }

    public String getImageUrl(){
        return preferences.getString("ImageUrl", "");
    }

    public String getName(){
        return preferences.getString("Name", "");
    }

    public void setName(String name){
        preferences.edit().putString("Name",name).apply();
    }


    public String getLastName(){
        return preferences.getString("LastName", "");
    }

    public void setLastName(String lastName){
        preferences.edit().putString("LastName",lastName).apply();
    }

    public void setRankingPrivacy(boolean name){
        preferences.edit().putBoolean("privacy",name).apply();
    }

    public boolean getRankingPrivacy(){
        return preferences.getBoolean("privacy", true);
    }

    public void setPoints(int points){
        preferences.edit().putInt("points",points).apply();
    }

    public int getPoints(){
        return preferences.getInt("points", 0);
    }
    
    public void setRankingPosition(String rankingPosition){
        preferences.edit().putString("rankingPosition", rankingPosition).apply();
    }

    public String getRankingPosition(){
        return preferences.getString("rankingPosition", "");
    }

    public String getFullName(){
        String name = preferences.getString("Name", "");
        String lastName = preferences.getString("LastName", "");
        return name + " " + lastName;
    }

    public void clearPreferences(){
        preferences.edit().clear().apply();
    }

    public boolean isRegisterForPush() {
        return preferences.getBoolean("isRegisterForPush",false);
    }

    public void setRegisterForPush(boolean isRegisterForPush) {
        preferences.edit().putBoolean("isRegisterForPush", isRegisterForPush).apply();
    }

    public boolean isNotification() {
        return preferences.getBoolean("isNotification",true);
    }

    public void setNotifications(boolean isRegisterForPush) {
        preferences.edit().putBoolean("isNotification", isRegisterForPush).apply();
    }


}
