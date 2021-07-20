package com.dacodes.bepensa.entities.ofline.postOpportunity;

import io.realm.RealmObject;

public class OflinePostOpportunity extends RealmObject {

    int id_oportunity;
    int id_division;
    String description;
    double lat;
    double lng;
    String city;
    String state;
    int has_media;
    int id_region;
    String brands;
    String key_hash;
    boolean has_sync;
    String id_colaborator;
    String extra_field_title;
    String extra_field_value;
    String address;
    boolean require_location;
    boolean container_location;

    public String getId_colaborator() {
        return id_colaborator;
    }

    public void setId_colaborator(String id_colaborator) {
        this.id_colaborator = id_colaborator;
    }

    public boolean isHas_sync() {
        return has_sync;
    }

    public void setHas_sync(boolean has_sync) {
        this.has_sync = has_sync;
    }

    public String getKey_hash() {
        return key_hash;
    }

    public void setKey_hash(String key_hash) {
        this.key_hash = key_hash;
    }

    public int getId_oportunity() {
        return id_oportunity;
    }

    public void setId_oportunity(int id_oportunity) {
        this.id_oportunity = id_oportunity;
    }

    public int getId_division() {
        return id_division;
    }

    public void setId_division(int id_division) {
        this.id_division = id_division;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getHas_media() {
        return has_media;
    }

    public void setHas_media(int has_media) {
        this.has_media = has_media;
    }

    public int getId_region() {
        return id_region;
    }

    public void setId_region(int id_region) {
        this.id_region = id_region;
    }

    public String getBrands() {
        return brands;
    }

    public void setBrands(String brands) {
        this.brands = brands;
    }

    public String getExtra_field_title() {
        return extra_field_title;
    }

    public void setExtra_field_title(String extra_field_title) {
        this.extra_field_title = extra_field_title;
    }

    public String getExtra_field_value() {
        return extra_field_value;
    }

    public void setExtra_field_value(String extra_field_value) {
        this.extra_field_value = extra_field_value;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isRequire_location() {
        return require_location;
    }

    public void setRequire_location(boolean require_location) {
        this.require_location = require_location;
    }

    public boolean isContainer_location() {
        return container_location;
    }

    public void setContainer_location(boolean container_location) {
        this.container_location = container_location;
    }
}
