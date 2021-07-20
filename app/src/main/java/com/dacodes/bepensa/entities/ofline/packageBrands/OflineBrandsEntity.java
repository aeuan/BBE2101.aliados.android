package com.dacodes.bepensa.entities.ofline.packageBrands;

import com.dacodes.bepensa.entities.ofline.packageDivisions.OflineDivisionEntity;

import io.realm.RealmObject;

public class OflineBrandsEntity extends RealmObject {

    int id;
    OflineDivisionEntity divisionEntity;
    String name;
    String logo;
    String extra_field_title;
    private String opportunity_types;
    private boolean required_location;
    private boolean required_media;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public OflineDivisionEntity getDivisionEntity() {
        return divisionEntity;
    }

    public void setDivisionEntity(OflineDivisionEntity divisionEntity) {
        this.divisionEntity = divisionEntity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getExtra_field_title() {
        return extra_field_title;
    }

    public void setExtra_field_title(String extra_field_title) {
        this.extra_field_title = extra_field_title;
    }

    public String getOpportunity_types() {
        return opportunity_types;
    }

    public void setOpportunity_types(String opportunity_types) {
        this.opportunity_types = opportunity_types;
    }

    public boolean isRequired_location() {
        return required_location;
    }

    public void setRequired_location(boolean required_location) {
        this.required_location = required_location;
    }

    public boolean isRequired_media() {
        return required_media;
    }

    public void setRequired_media(boolean required_media) {
        this.required_media = required_media;
    }
}
