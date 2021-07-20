package com.dacodes.bepensa.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.dacodes.bepensa.R;
import com.dacodes.bepensa.entities.LocationEntity;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;

public class DecodeLocation {

    public static LocationEntity getDirection(Context mContext,LatLng latLng){
        LocationEntity locationEntity=null;
        Address locationAdrress=getAddress(mContext,latLng);
        if (locationAdrress!=null){
            boolean status=false;
            locationEntity=new LocationEntity();
            if (locationAdrress.getAddressLine(0)!=null){
                locationEntity.setAddress(locationAdrress.getAddressLine(0));
               // status=true;
            }
            if (locationAdrress.getLocality()!=null){
                locationEntity.setCity(locationAdrress.getLocality());
                //status=true;
            }

            if (locationAdrress.getAdminArea()!=null){
                locationEntity.setState(locationAdrress.getAdminArea());
                int temp=getRegion(mContext,locationAdrress.getAdminArea());
                locationEntity.setRegion(temp);
                //status=true;
            }

            if (locationAdrress.getPostalCode()!=null){
                locationEntity.setPostalCode(locationAdrress.getPostalCode());
                //status=true;
            }

            if (locationAdrress.getCountryName()!=null){
                locationEntity.setCountryName(locationAdrress.getCountryName());
                //status=true;
            }

            //if (locationEntity.getRegion()>0){
                locationEntity.setLat(latLng.latitude);
                locationEntity.setLgn(latLng.longitude);
            /*}else{
                locationEntity=null;
            }*/
        }else{
            locationEntity=new LocationEntity();
            locationEntity.setLat(latLng.latitude);
            locationEntity.setLgn(latLng.longitude);
        }

        return locationEntity;
    }

    private static Address getAddress(Context mContext, LatLng latLng){
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        List<Address> address;
        try {
            address = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            if (address.size()>0)
                return address.get(0);
            else
                return null;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private  static int getRegion(Context context,String state){
        String[] states= context.getResources().getStringArray(R.array.name_location);
        String stateUnaccent= RemoveAccent.unaccent(state);
       /* LocationEntity locationEntity=((ActivityOpportunity)mActivity).getLocationEntity();
        if (locationEntity==null) {
            locationEntity = new LocationEntity();
            ((ActivityOpportunity)mActivity).setLocationEntity(locationEntity);
        }*/
        for (int i=0;i<states.length;i++){
            String localState=RemoveAccent.unaccent(states[i]);
            if (localState.equalsIgnoreCase(stateUnaccent)){
                int temp=i+1;
              //  ((ActivityOpportunity)mActivity).getLocationEntity().setRegion(i+1);
                return temp;
            }
        }

        return 0;
    }

}
