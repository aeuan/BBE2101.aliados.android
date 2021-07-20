package com.dacodes.bepensa;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.dacodes.bepensa.activities.ActivityOpportunity;
import com.dacodes.bepensa.entities.DataStates;
import com.dacodes.bepensa.entities.LocationEntity;
import com.dacodes.bepensa.entities.PackegeOpportunity.DivisionEntity;
import com.dacodes.bepensa.utils.DecodeLocation;
import com.google.android.gms.common.api.Status;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentOpportunityMap extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,GoogleMap.OnMapClickListener, PlaceSelectionListener {


    public FragmentOpportunityMap() {
        // Required empty public constructor
    }

    @BindView(R.id.btSave)
    Button btSave;

    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private SupportMapFragment mMapFragment;
    private GoogleMap mGoogleMap;
    private Marker mCurrLocationMarker;
    private FusedLocationProviderClient mFusedLocationClient;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private Context mContext;
    private Activity mActivity;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        mActivity = getActivity();
        ((ActivityOpportunity) mActivity).setStatusLocation(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_opportunity_map, container, false);
        ButterKnife.bind(this, view);
        //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (mMapFragment == null) {
            mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.card);
        }
        if (!Places.isInitialized()) {
            Places.initialize(mContext, getString(R.string.google_maps_key));
        }

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        assert autocompleteFragment != null;
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS));
        autocompleteFragment.setHint("¿Cuál es tu ubicación?");

        autocompleteFragment.setOnPlaceSelectedListener(this);

        mMapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        return view;
    }

    @Override
    public void onPlaceSelected(@NonNull Place place) {
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        LatLng latLng = place.getLatLng();
        CameraPosition position = new CameraPosition.Builder()
                .target(latLng)
                .zoom(14.0f)
                .build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
        addMarker(latLng);
    }

    @Override
    public void onError(Status status) {

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
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mGoogleMap.setOnMyLocationButtonClickListener(this);
        mGoogleMap.setOnMyLocationClickListener(this);
        mGoogleMap.setOnMapClickListener(this);
        ((ActivityOpportunity) getActivity()).enabledGpsMaps();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        CameraPosition position = new CameraPosition.Builder()
                .target(latLng)
                .zoom(14.0f)
                .build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
        addMarker(latLng);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @OnClick(R.id.btSave)
    public void save() {
        ((ActivityOpportunity) mActivity).fragmentChanger(ActivityOpportunity.NEW_OPPORTUNITY);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mGoogleMap.setMyLocationEnabled(true);
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setCancelable(false)
                            .setMessage("Para poder obtener tu ubicación es necesario que aceptes los permisos")
                            .setPositiveButton("Aceptar", (dialog, which) ->
                                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_REQUEST_LOCATION)
                            ).setNegativeButton("Cancelar",(dialog, which) -> dialog.dismiss()).create().show();
                }
                return;
            }
        }
    }

    private void addMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
        LocationEntity locationEntity = DecodeLocation.getDirection(mContext, latLng);
        DivisionEntity divisionEntity = ((ActivityOpportunity)getActivity()).getDivisionEntity();
        if (divisionEntity.getStates().size()==0){
            showAlertDivision();
        }else{
            boolean status_division= false;
            for (DataStates states: divisionEntity.getStates()){
                if (states.getId() == locationEntity.getRegion()) {
                    status_division = true;
                    break;
                }
            }
            if (!status_division)
                showAlertDivision();
            else{
                if (locationEntity != null) {
                    ((ActivityOpportunity) mActivity).setLocationEntity(locationEntity);
                } else {
                    ((ActivityOpportunity) mActivity).setLocationEntity(null);
                }
                btSave.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showAlertDivision(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false)
                .setMessage("La ubicación seleccionada no aplica para la división seleccionada")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((ActivityOpportunity) mActivity).setLocationEntity(null);
                        btSave.setVisibility(View.INVISIBLE);
                        dialog.dismiss();
                    }
                }).create().show();
    }

    public void initGps() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mGoogleMap.setMyLocationEnabled(true);
            } else
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                Location location = locationList.get(locationList.size() - 1);
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                CameraPosition position = new CameraPosition.Builder()
                        .target(latLng)
                        .zoom(14.0f)
                        .build();
                addMarker(latLng);
                mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
            }
        }

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
        }
    };
}
