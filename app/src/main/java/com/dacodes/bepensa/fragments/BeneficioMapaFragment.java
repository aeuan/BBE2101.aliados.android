package com.dacodes.bepensa.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.promotion.PromotionDataEntity;
import com.dacodes.bepensa.entities.promotion.PromotionsEntity;
import com.dacodes.bepensa.utils.BottomSheetMarkerPromotion;
import com.dacodes.bepensa.utils.ValidateNetwork;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

import static android.view.View.VISIBLE;
import static com.dacodes.bepensa.api.RetrofitWebServices.GET_PROMOTIONS;

public class BeneficioMapaFragment extends Fragment implements OnMapReadyCallback
        , RetrofitWebServices.DefaultResponseListeners, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener {

    private GoogleMap mGoogleMap;

    @BindView(R.id.textView3)TextView textView;
    @BindView(R.id.btSave)Button btSave;
    @BindView(R.id.no_network)ImageView no_network;
    @BindView(R.id.constraint_map)ConstraintLayout constraint_map;
    RetrofitWebServices retrofitWebServices;
    AppController app;
    private Context mContext;

    ArrayList<PromotionDataEntity> items = new ArrayList<>();
    String LOG_TAG = BeneficioMapaFragment.class.getSimpleName();
    boolean statusMarker = false;
    private Activity mActivity;
    String query_filters="";
    String query_search="";
    private Call<PromotionsEntity> callPromotion;
    private FusedLocationProviderClient mFusedLocationClient;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private LocationRequest mLocationRequest;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity=getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_oportunity_location, container, false);
        ButterKnife.bind(this, view);
        btSave.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);

        app = (AppController) getActivity().getApplication();
        mContext = getActivity().getApplicationContext();
        SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.card);

        mMapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        query_filters=getResources().getString(R.string.base_url)+getResources().getString(R.string.api_get_promotions)+"?categories=";
        retrofitWebServices = new RetrofitWebServices(app, LOG_TAG, mContext, this);
        if (ValidateNetwork.getNetwork(getActivity())) {
            no_network.setVisibility(View.GONE);
            constraint_map.setVisibility(VISIBLE);
            if (getArguments() != null){
                String temp = getArguments().getString("search");
                if (temp != null)
                    getAllSearch(temp);
                else
                    getAllPromotions();
            }else
                getAllPromotions();
        }else{
            constraint_map.setVisibility(View.GONE);
            no_network.setVisibility(VISIBLE);
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        mGoogleMap.clear();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGoogleMap != null) {
            mGoogleMap.clear();
            statusMarker = false;
        }
        items.clear();
    }


    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public void onFailError(int id, int status, String message) {
        Log.e("onFailError","cancel");
    }

    @Override
    public void onWebServiceStart() {
        Log.e("onWebServiceStart","cancel");
    }

    @Override
    public void onWebServiceEnd() {

    }

    @Override
    public void onExpireToken() {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        PromotionDataEntity promotionDataEntity = (PromotionDataEntity) marker.getTag();
        Bundle bundle= new Bundle();
        bundle.putSerializable("beneficio",promotionDataEntity);
        BottomSheetMarkerPromotion bottomSheetMarkerPromotion = new BottomSheetMarkerPromotion();
        bottomSheetMarkerPromotion.setArguments(bundle);
        bottomSheetMarkerPromotion.show(getChildFragmentManager(), "Marker");
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.setOnMyLocationButtonClickListener(this);
        mGoogleMap.setOnMyLocationClickListener(this);
        initGps();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mGoogleMap.setMyLocationEnabled(true);
                    }
                }
                return;
            }
        }
    }

    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id) {
            case GET_PROMOTIONS:
                PromotionsEntity responseModel = (PromotionsEntity) object;
                items.addAll(responseModel.getData());
                if (!TextUtils.isEmpty(responseModel.getPagination().getLinks().getNext()))
                    retrofitWebServices.getPromotions(responseModel.getPagination().getLinks().getNext(),app.getAccessToken());
                else {
                    mGoogleMap.clear();
                    paintMarker();
                }
                break;
        }
    }

    public void initGps() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mGoogleMap.setMyLocationEnabled(true);
            } else
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    private void getAllPromotions(){
        if (callPromotion != null && callPromotion.isExecuted())
            callPromotion.cancel();
        if (app.getFiltersPromotions()!=null)
            callPromotion = retrofitWebServices.getPromotions(query_filters+app.getFiltersPromotions(),app.getAccessToken());
        else
            callPromotion = retrofitWebServices.getPromotions(null, app.getAccessToken());
    }

    public void getAllSearch(String name){
        if (mGoogleMap != null)
            mGoogleMap.clear();
        items.clear();
        query_search=getResources().getString(R.string.base_url)+getResources().getString(R.string.api_get_promotions)+"?name=";
        if (callPromotion != null){
            if (callPromotion.isExecuted())
                callPromotion.cancel();
        }
        callPromotion = retrofitWebServices.getPromotions(query_search + name, app.getAccessToken());
    }

    public void paintMarker(){
        for (int i=0;i<items.size();i++){
            try{
                if (items.get(i).getMarkers().size()>0) {
                    for (int j = 0; j < items.get(i).getMarkers().size(); j++) {
                        Double lat = Double.parseDouble(items.get(i).getMarkers().get(j).getLat());
                        Double lgn = Double.parseDouble(items.get(i).getMarkers().get(j).getLng());
                        String title = items.get(i).getMarkers().get(j).getAddress();
                        addMarker(lat, lgn, title, items.get(i));
                    }
                }else{
                    Double lat = Double.parseDouble(items.get(i).getLat());
                    Double lgn = Double.parseDouble(items.get(i).getLng());
                    String title = items.get(i).getName();
                    addMarker(lat, lgn, title, items.get(i));
                }
            }catch (Exception e){
                Log.e("MarkerError",""+e.getLocalizedMessage());
            }
        }
        statusMarker=true;
    }

    public void addMarker(Double lat,Double lgn,String title, PromotionDataEntity promotionDataEntity){
        int height = 81;
        int width = 61;
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.maker_bepensa);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
       Marker marker= mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lgn))
                .anchor(0.2f, 0.2f)
                .title(title)
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
       marker.setTag(promotionDataEntity);
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                Location location = locationList.get(locationList.size() - 1);
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                CameraPosition position = new CameraPosition.Builder()
                        .target(latLng)
                        .zoom(10.0f)
                        .build();
                mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
            }
        }

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
        }
    };

}