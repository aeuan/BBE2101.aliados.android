package com.dacodes.bepensa.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.entities.DivisionOpportunity;
import com.dacodes.bepensa.entities.MediasFilesEntity;
import com.dacodes.bepensa.entities.PackegeOpportunity.BrandEntity;
import com.dacodes.bepensa.entities.PackegeOpportunity.DivisionEntity;
import com.dacodes.bepensa.entities.PackegeOpportunity.MediaEntity;
import com.dacodes.bepensa.fragments.NewOportunityStart;
import com.dacodes.bepensa.fragments.OportunityDivision;
import com.dacodes.bepensa.fragments.OportunityLocation;
import com.dacodes.bepensa.fragments.OportunityMarca;
import com.dacodes.bepensa.models.MediaItem;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewOportunity extends AppCompatActivity {

    private Context context;
    private AppController appController;

    @BindView(R.id.main_toolbar)
    Toolbar toolbar;
    @BindView(R.id.fragment_container)
    FrameLayout fragment_contaner;

    private Fragment fragment;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private int currentFragment;

    public static final int NEW_OPORTUNITY_START = 1;
    public static final int OPORTUNITY_DIVISION = 2;
    public static final int OPORTUNITY_BRANDS = 3;
    public static final int OPORTUNITY_LOCATION = 4;
    public static final int OPORTUNITY_TYPE = 5;
    public static final int NEW_OPORTUNITY_START_LOCATION=6;

    private ArrayList<BrandEntity> brands = new ArrayList<>();
    private DivisionOpportunity divisionOpportunity;
    private DivisionEntity divisionEntity;
    private int opportunityPoints = 0;
    private String address;
    private String state;
    private String city;
    public int region;
    private LatLng location;
    private int indexSelected;
    private int indexSelectedState;

    private String description;
    private List<MediaItem> mediaItems = new ArrayList<>();
    private String brandsItems = "";
    private List<MediaEntity> medias = new ArrayList<>();
    private List<MediasFilesEntity> mediasFilesEntities = new ArrayList<>();

    private  int status_location;

    public int getStatus_location() {
        return status_location;
    }

    public void setStatus_location(int status_location) {
        this.status_location = status_location;
    }

    //MEDIA FILES TO SEND
    public List<MediasFilesEntity> getMediasFilesEntities() {
        return mediasFilesEntities;
    }

    public void setMediasFilesEntities(List<MediasFilesEntity> mediasFilesEntities) {
        this.mediasFilesEntities = mediasFilesEntities;
    }

    public int getIndexSelectedState() {
        return indexSelectedState;
    }

    public void setIndexSelectedState(int indexSelectedState) {
        this.indexSelectedState = indexSelectedState;
    }

    public int getIndexSelected() {
        return indexSelected;
    }

    public void setIndexSelected(int indexSelected) {
        this.indexSelected = indexSelected;
    }

    public Context getContext() {
        return context;
    }

    public int getRegion(){
        return region;
    }

    public void setRegion(int region){
        this.region=region;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<MediaEntity> getMedias() {
        return medias;
    }

    public void setMedias(List<MediaEntity> medias) {
        this.medias = medias;
    }

    public ArrayList<BrandEntity> getBrands() {
        return brands;
    }

    public void setBrands(ArrayList<BrandEntity> brands) {
        this.brands = brands;
    }

    public DivisionOpportunity getDivisionOpportunity() {
        return divisionOpportunity;
    }

    public void setDivisionOpportunity(DivisionOpportunity divisionOpportunity) {
        this.divisionOpportunity = divisionOpportunity;
    }

    public DivisionEntity getDivisionEntity() {
        return divisionEntity;
    }

    public void setDivisionEntity(DivisionEntity divisionEntity) {
        this.divisionEntity = divisionEntity;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<MediaItem> getMediaItems() {
        return mediaItems;
    }

    public void setMediaItems(List<MediaItem> mediaItems) {
        this.mediaItems = mediaItems;
    }

    public String getBrandsItems() {
        return brandsItems;
    }

    public void setBrandsItems(String brandsItems) {
        this.brandsItems = brandsItems;
    }


    //Nuevos
    public int id_division = 0;
    public String division_name;
    public int id_opportunity;
    public String opportunity_name;

    public int getId_division() {
        return id_division;
    }

    public void setId_division(int id_division) {
        this.id_division = id_division;
    }

    public int getId_opportunity() {
        return id_opportunity;
    }

    public void setId_opportunity(int id_opportunity) {
        this.id_opportunity = id_opportunity;
    }

    public String getDivision_name() {
        return division_name;
    }

    public void setDivision_name(String division_name) {
        this.division_name = division_name;
    }

    public String getOpportunity_name() {
        return opportunity_name;
    }

    public void setOpportunity_name(String opportunity_name) {
        this.opportunity_name = opportunity_name;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_oportunity);
        ButterKnife.bind(this);
        context = getApplicationContext();
        appController = (AppController) getApplication();
        manager = getSupportFragmentManager();
        setSupportActionBar(toolbar);
        toolbar.setTitle("NUEVA OPORTUNIDAD");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(ContextCompat.getColor(getContext(), R.color.colorOrange));
        if(getIntent().getExtras()!=null){

        }else {
            fragmentChanger(NEW_OPORTUNITY_START);
        }
        brands.add(new BrandEntity(0, "Agregar nuevo", ""));

        medias.add(new MediaEntity(1, "https://img.clasf.mx/2016/06/07/Enfriador-De-2-Puertas-Marca-Torrey-Inoxidable-E-20160607031411.jpg", 1));
        medias.add(new MediaEntity(1, "https://img.clasf.mx/2016/06/07/Enfriador-De-2-Puertas-Marca-Torrey-Inoxidable-E-20160607031411.jpg", 1));
        medias.add(new MediaEntity(1, "https://img.clasf.mx/2016/06/07/Enfriador-De-2-Puertas-Marca-Torrey-Inoxidable-E-20160607031411.jpg", 1));
        medias.add(new MediaEntity(1, "https://img.clasf.mx/2016/06/07/Enfriador-De-2-Puertas-Marca-Torrey-Inoxidable-E-20160607031411.jpg", 1));

        checkLocationPermission();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                switch (currentFragment){
                    case NEW_OPORTUNITY_START:
                        onBackPressed();
                        break;
                    case OPORTUNITY_DIVISION:
                    case OPORTUNITY_TYPE:
                    case OPORTUNITY_BRANDS:
                        fragmentChanger(NEW_OPORTUNITY_START);
                        break;
                    case OPORTUNITY_LOCATION:
                        if (getState()!=null)
                            fragmentChanger(NEW_OPORTUNITY_START);
                        else
                            fragmentChanger(NEW_OPORTUNITY_START_LOCATION);
                        break;
                    case NEW_OPORTUNITY_START_LOCATION:
                        onBackPressed();
                        break;
                }

                break;
        }
        return true;
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    public void fragmentChanger(int fragment){
        currentFragment = fragment;
        switch (fragment){
            case NEW_OPORTUNITY_START:
                toolbar.setTitle("NUEVA OPORTUNIDAD");
                this.fragment = NewOportunityStart.newInstance();
                Bundle bundle = new Bundle();
                bundle.putInt("id_location",1);
                this.fragment.setArguments(bundle);
                break;
            case OPORTUNITY_DIVISION:
                this.fragment = OportunityDivision.newInstance(1);
                toolbar.setTitle("DIVISIÓN");
                break;
            case OPORTUNITY_TYPE:
                this.fragment = OportunityDivision.newInstance(2);
                toolbar.setTitle("TIPOS DE OPORTUNIDAD");
                break;
            case OPORTUNITY_BRANDS:
                this.fragment = OportunityMarca.newInstance();
                toolbar.setTitle("MARCAS");
                break;
            case OPORTUNITY_LOCATION:
                this.fragment = OportunityLocation.newInstance();
                toolbar.setTitle("UBICACIÓN");
                break;
            case NEW_OPORTUNITY_START_LOCATION:
                this.fragment = NewOportunityStart.newInstance();
                Bundle bundle2 = new Bundle();
                bundle2.putInt("id_location",2);
                this.fragment.setArguments(bundle2);
                break;
        }
        transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_container, this.fragment);
        transaction.commit();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.no_anim, R.anim.exit_to_right);
    }

    public int getOpportunityPoints() {
        return opportunityPoints;
    }

    public void setOpportunityPoints(int opportunityPoints) {
        this.opportunityPoints = opportunityPoints;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(fragment!=null){
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

}
