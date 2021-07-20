package com.dacodes.bepensa.models;

import com.dacodes.bepensa.entities.PackegeOpportunity.CollaboratorEntity;
import com.dacodes.bepensa.entities.Type;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class NewOpportunityResponse implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("collaborator")
    @Expose
    private CollaboratorEntity collaborator;
    @SerializedName("media")
    @Expose
    private List<Object> media = null;
    @SerializedName("division")
    @Expose
    private Division division;
    @SerializedName("brands")
    @Expose
    private List<Object> brands = null;
    @SerializedName("type")
    @Expose
    private Type type;
    @SerializedName("responsable")
    @Expose
    private String responsable;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lng")
    @Expose
    private String lng;
    @SerializedName("points")
    @Expose
    private Integer points;
    @SerializedName("has_media")
    @Expose
    private Boolean hasMedia;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CollaboratorEntity getCollaborator() {
        return collaborator;
    }

    public void setCollaborator(CollaboratorEntity collaborator) {
        this.collaborator = collaborator;
    }

    public List<Object> getMedia() {
        return media;
    }

    public void setMedia(List<Object> media) {
        this.media = media;
    }

    public Division getDivision() {
        return division;
    }

    public void setDivision(Division division) {
        this.division = division;
    }

    public List<Object> getBrands() {
        return brands;
    }

    public void setBrands(List<Object> brands) {
        this.brands = brands;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Boolean getHasMedia() {
        return hasMedia;
    }

    public void setHasMedia(Boolean hasMedia) {
        this.hasMedia = hasMedia;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
