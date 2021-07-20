package com.dacodes.bepensa.entities.PackegeOpportunity;

import com.dacodes.bepensa.entities.OpportunityType;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class BrandEntity  implements Serializable {

    public BrandEntity(int id, String name, String logo) {
        this.id = id;
        this.name = name;
        this.logo = logo;
    }

    /**
     * id : 1
     * name : Coca Cola
     * logo : https://bepensa-media.s3.amazonaws.com/media/brands/c8b45cd854e04b158e8aaa108e064b77.png
     */



    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("logo")
    private String logo;
    @SerializedName("extra_field_title")
    private String extra_field_title;
    @SerializedName("required_location")
    private boolean required_location;
    @SerializedName("required_media")
    private boolean required_media;
    @SerializedName("division")
    DivisionEntity divisionEntity;
    @SerializedName("opportunity_types")
    List<OpportunityType> opportunity_types;

    private int type;

    public BrandEntity() {

    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public BrandEntity(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public DivisionEntity getDivisionEntity() {
        return divisionEntity;
    }

    public void setDivisionEntity(DivisionEntity divisionEntity) {
        this.divisionEntity = divisionEntity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public List<OpportunityType> getOpportunity_types() {
        return opportunity_types;
    }

    public void setOpportunity_types(List<OpportunityType> opportunity_types) {
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
