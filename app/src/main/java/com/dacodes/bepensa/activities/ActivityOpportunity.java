package com.dacodes.bepensa.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dacodes.bepensa.FragmentMarcas;
import com.dacodes.bepensa.FragmentOpportunityMap;
import com.dacodes.bepensa.FragmentTipoOportunidad;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.entities.LocationEntity;
import com.dacodes.bepensa.entities.MediasFilesEntity;
import com.dacodes.bepensa.entities.OpportunityType;
import com.dacodes.bepensa.entities.PackegeOpportunity.BrandEntity;
import com.dacodes.bepensa.entities.PackegeOpportunity.DivisionEntity;
import com.dacodes.bepensa.fragments.FragmentNewOpportunity;
import com.dacodes.bepensa.utils.DecodeLocation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActivityOpportunity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks{

    @BindView(R.id.toolbar) Toolbar toolbar;

    public static final int NEW_OPPORTUNITY = 1;
    public static final int OPORTUNITY_BRANDS = 3;
    public static final int OPORTUNITY_LOCATION = 4;
    public static final int OPORTUNITY_TYPE = 5;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private int ON_BACK_PRESSED=0;
    private FragmentTransaction transaction;
    private FragmentManager manager;
    private List<BrandEntity> brandEntities=new ArrayList<>();
    private List<MediasFilesEntity> mediasFilesEntities=new ArrayList<>();
    private DivisionEntity divisionEntity;
    private OpportunityType opportunityType;
    private String description;
    private String titleOption;
    private String valueOption;
    private LocationEntity locationEntity;
    private boolean statusLocation=false;
    private boolean statusState=false;
    private int idMarca=0;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleApiClient googleApiClient;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private static final int PERMISSIONS_ACCESS_FINE = 200;
    private int TEMP_REGION = 0;
    private boolean MAP_GPS=false;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opportunity);
        ButterKnife.bind(this);
        mContext=getApplicationContext();
        manager=getSupportFragmentManager();
        transaction=manager.beginTransaction();
        toolbar.setTitle("NUEVA OPORTUNIDAD");
        toolbar.setTitleTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /*if (checkVersionSdk()){
            checkLocationPermission();
        }*/
        int id=getIntent().getIntExtra("id",1);
        fragmentChanger(id);
        brandEntities.add(new BrandEntity("Agregar Nuevo",1));
        buildGoogleAPiClient();
        buildLocationRequest();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onBackPressed() {
        if (ON_BACK_PRESSED==0 || ON_BACK_PRESSED==1)
            super.onBackPressed();
        else
            fragmentChanger(NEW_OPPORTUNITY);
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
    public void onConnected(@Nullable Bundle bundle) {
        LocationSettingsRequest.Builder locationSettingsRequest = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).setAlwaysShow(true);

        SettingsClient settingsClient =  LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task =settingsClient.checkLocationSettings(locationSettingsRequest.build());

        task.addOnSuccessListener(locationSettingsResponse -> {
            FragmentOpportunityMap mapFragment = null;
            for (Fragment fragment: getSupportFragmentManager().getFragments()){
                if (fragment instanceof FragmentOpportunityMap)
                    mapFragment = (FragmentOpportunityMap) fragment;
            }
            if (mapFragment != null && MAP_GPS)
                mapFragment.initGps();
            else
                validateServiceLocation();
        });

        task.addOnFailureListener(e -> {
            if (e instanceof ResolvableApiException){
                try {
                    ((ResolvableApiException) e).startResolutionForResult(ActivityOpportunity.this,REQUEST_CHECK_SETTINGS);
                }catch (IntentSender.SendIntentException e1){

                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_ACCESS_FINE){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                validateServiceLocation();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS){
            switch (resultCode){
                case RESULT_OK:
                    FragmentOpportunityMap mapFragment = null;
                    for (Fragment fragment: getSupportFragmentManager().getFragments()){
                        if (fragment instanceof FragmentOpportunityMap)
                            mapFragment = (FragmentOpportunityMap) fragment;
                    }
                    if (mapFragment != null && MAP_GPS)
                        mapFragment.initGps();
                    else
                        validateServiceLocation();
                    break;
                case RESULT_CANCELED:
                    /*if (MAP_GPS){
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setCancelable(false)
                                .setMessage("Para poder continuar es necesario tener activo el gps")
                                .setPositiveButton("Aceptar", (dialog, which) -> enabledGpsMaps())
                                .create().show();
                    }*/
                    break;
            }
        }
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

    public void enabledGpsMaps(){
        MAP_GPS = true;
        buildGoogleAPiClient();
        buildLocationRequest();
    }

    private boolean versionSdk(){
        return Build.VERSION.SDK_INT>=Build.VERSION_CODES.M;
    }

    private void validateServiceLocation(){
        if (versionSdk()){
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_ACCESS_FINE);
            else
                fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,Looper.myLooper());
        }else
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,Looper.myLooper());
    }

    private  LocationCallback locationCallback = new LocationCallback(){
        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
        }

        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            List<Location> locations = locationResult.getLocations();
            if (locations.size() > 0){
                LatLng latLng = new LatLng(locations.get(0).getLatitude(),locations.get(0).getLongitude());
                LocationEntity locationEntity= DecodeLocation.getDirection(mContext,latLng);
                TEMP_REGION = locationEntity.getRegion();
                /*setLocationEntity(locationEntity);
                if (locationEntity != null)
                    Log.e("location",""+locationEntity.getRegion());*/
            }
        }
    };

    public void fragmentChanger(int id){
        switch (id){
            case OPORTUNITY_BRANDS:
                ON_BACK_PRESSED=2;
                replaceFragment(new FragmentMarcas());
                break;
            case OPORTUNITY_LOCATION:
                ON_BACK_PRESSED=3;
                replaceFragment(new FragmentOpportunityMap());
                break;
            case OPORTUNITY_TYPE:
                ON_BACK_PRESSED=4;
                replaceFragment(new FragmentTipoOportunidad());
                break;
            default:
                ON_BACK_PRESSED=1;
                replaceFragment(new FragmentNewOpportunity());
                break;
        }
        titleToolbar(id);
    }


    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contenedor,fragment).commit();
    }

    private void titleToolbar(int position){
        switch (position){
            case NEW_OPPORTUNITY:
                toolbar.setTitle("NUEVA OPORTUNIDAD");
                toolbar.setTitleTextColor(ContextCompat.getColor(mContext,R.color.colorPrimary));
                break;
            case OPORTUNITY_BRANDS:
                toolbar.setTitle("MARCA");
                toolbar.setTitleTextColor(ContextCompat.getColor(mContext,R.color.colorPrimary));
                break;
            case OPORTUNITY_LOCATION:
                toolbar.setTitle("UBICACION");
                toolbar.setTitleTextColor(ContextCompat.getColor(mContext,R.color.colorPrimary));
                break;
            case OPORTUNITY_TYPE:
                toolbar.setTitle("TIPO DE OPORTUNIDAD");
                toolbar.setTitleTextColor(ContextCompat.getColor(mContext,R.color.colorPrimary));
                break;
        }
    }

    public boolean checkVersionSdk(){
        return Build.VERSION.SDK_INT>=Build.VERSION_CODES.M;
    }

    public List<BrandEntity> getBrandEntities() {
        return brandEntities;
    }

    public void setBrandEntities(List<BrandEntity> brandEntities) {
        this.brandEntities = brandEntities;
    }

    public DivisionEntity getDivisionEntity() {
        return divisionEntity;
    }

    public void setDivisionEntity(DivisionEntity divisionEntity) {
        this.divisionEntity = divisionEntity;
    }

    public OpportunityType getOpportunityType() {
        return opportunityType;
    }

    public void setOpportunityType(OpportunityType opportunityType) {
        this.opportunityType = opportunityType;
    }

    public LocationEntity getLocationEntity() {
        return locationEntity;
    }

    public void setLocationEntity(LocationEntity locationEntity) {
        this.locationEntity = locationEntity;
    }

    public boolean isStatusLocation() {
        return statusLocation;
    }

    public void setStatusLocation(boolean statusLocation) {
        this.statusLocation = statusLocation;
    }

    public boolean isStatusState() {
        return statusState;
    }

    public void setStatusState(boolean statusState) {
        this.statusState = statusState;
    }

    public void setMediasFilesEntities(List<MediasFilesEntity> mediasFilesEntities) {
        this.mediasFilesEntities = mediasFilesEntities;
    }

    public List<MediasFilesEntity> getMediasFilesEntities() {
        return mediasFilesEntities;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitleOption() {
        return titleOption;
    }

    public void setTitleOption(String titleOption) {
        this.titleOption = titleOption;
    }

    public String getValueOption() {
        return valueOption;
    }

    public void setValueOption(String valueOption) {
        this.valueOption = valueOption;
    }

    public int getIdMarca() {
        return idMarca;
    }

    public void setIdMarca(int idMarca) {
        this.idMarca = idMarca;
    }

    public int getTEMP_REGION() {
        return TEMP_REGION;
    }
}
