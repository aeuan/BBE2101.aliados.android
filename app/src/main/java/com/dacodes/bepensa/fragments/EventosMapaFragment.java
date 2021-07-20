package com.dacodes.bepensa.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.EventosEntity;
import com.dacodes.bepensa.models.EventosResponseModel;
import com.dacodes.bepensa.utils.BottomSheetMarker;
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
import static com.dacodes.bepensa.api.RetrofitWebServices.GET_EVENTOS;

public class EventosMapaFragment extends Fragment implements OnMapReadyCallback
        , RetrofitWebServices.DefaultResponseListeners, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener {

    private Context context;
    private GoogleMap mGoogleMap;


    @BindView(R.id.textView3)TextView textView;
    @BindView(R.id.btSave)Button btSave;

    RetrofitWebServices retrofitWebServices;
    private final int REQUEST_FINE_LOCATION = 1;
    AppController app;
    private Context mContext;
    ArrayList<EventosEntity> items = new ArrayList<>();
    String LOG_TAG = BeneficioMapaFragment.class.getSimpleName();
    boolean statusMarker = false;
    @BindView(R.id.no_network)ImageView no_network;
    @BindView(R.id.constraint_map)ConstraintLayout constraint_map;

    private FusedLocationProviderClient mFusedLocationClient;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private LocationRequest mLocationRequest;
    private Call<EventosResponseModel> callEvents;
    private String query_search="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_oportunity_location, container, false);
        ButterKnife.bind(this, view);
        btSave.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);

        app = (AppController) getActivity().getApplication();
        mContext = getActivity().getApplicationContext();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.card);
        mMapFragment.getMapAsync(this);
        retrofitWebServices = new RetrofitWebServices(app, LOG_TAG, mContext, this);
        query_search=getResources().getString(R.string.base_url)+getResources().getString(R.string.api_get_events)+"?name=";

        if (ValidateNetwork.getNetwork(getActivity())) {
            no_network.setVisibility(View.GONE);
            constraint_map.setVisibility(VISIBLE);
            if (getArguments() != null){
                if (getArguments().getString("data") != null)
                    callEvents = retrofitWebServices.getEventos(query_search+""+getArguments().getString("data"),app.getAccessToken());
                else
                    callEvents = retrofitWebServices.getEventos(null, app.getAccessToken());
            }else
                callEvents = retrofitWebServices.getEventos(null, app.getAccessToken());
        }else{
            constraint_map.setVisibility(View.GONE);
            no_network.setVisibility(VISIBLE);
        }
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGoogleMap != null) {
            mGoogleMap.clear();
            statusMarker = false;
        }
    }

    @Override
    public void onDestroyView() {
        if (mGoogleMap != null)
            mGoogleMap.clear();
        super.onDestroyView();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

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
            case GET_EVENTOS:
                EventosResponseModel responseModel = (EventosResponseModel) object;
                this.items.addAll(responseModel.getData());
                paintMarker();
                break;
        }
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
        EventosEntity eventosEntity = (EventosEntity) marker.getTag();
        Bundle bundle= new Bundle();
        bundle.putSerializable("evento",eventosEntity);
        BottomSheetMarker bottomSheetMarker = new BottomSheetMarker();
        bottomSheetMarker.setArguments(bundle);
        bottomSheetMarker.show(getChildFragmentManager(), "Marker");
        return false;
    }

    public void validateConnectionNetworkSearch(Bundle bundle){
        if (callEvents != null){
            if (callEvents.isExecuted())
                callEvents.cancel();
        }
        if (mGoogleMap != null)
            mGoogleMap.clear();
        items.clear();
        callEvents = retrofitWebServices.getEventos(query_search+""+bundle.getString("data"),app.getAccessToken());
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

    public void paintMarker(){
        for (int i=0;i<items.size();i++){
            try{
                Double lat=Double.parseDouble(items.get(i).getLat());
                Double lgn =Double.parseDouble(items.get(i).getLng());
                String title=items.get(i).getName();
                addMarker(lat,lgn,title,items.get(i));
            }catch (Exception e){ }
        }
        statusMarker=true;
    }

    public void addMarker(Double lat,Double lgn,String title, EventosEntity eventosEntity){
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
        marker.setTag(eventosEntity);
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