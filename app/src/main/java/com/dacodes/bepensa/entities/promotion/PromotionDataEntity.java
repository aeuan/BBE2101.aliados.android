package com.dacodes.bepensa.entities.promotion;
import com.dacodes.bepensa.models.Markers;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PromotionDataEntity implements Serializable {
    @SerializedName("image")
    private String image;

    @SerializedName("lng")
    private String lng;

    @SerializedName("ends")
    private String ends;

    @SerializedName("name")
    private String name;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("id")
    private int id;

    @SerializedName("categories")
    private List<PromotionCategoriesEntity> categories;

    @SerializedName("text")
    private String text;

    @SerializedName("starts")
    private String starts;

    @SerializedName("lat")
    private String lat;

    @SerializedName("status")
    private int status;

    @SerializedName("markers")
    private List<Markers> markers = new ArrayList<>();

    public void setImage(String image){
        this.image = image;
    }

    public void setLng(String lng){
        this.lng = lng;
    }

    public void setEnds(String ends){
        this.ends = ends;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setCreatedAt(String createdAt){
        this.createdAt = createdAt;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setCategories(List<PromotionCategoriesEntity> categories){
        this.categories = categories;
    }

    public void setText(String text){
        this.text = text;
    }

    public void setStarts(String starts){
        this.starts = starts;
    }

    public void setLat(String lat){
        this.lat = lat;
    }

    public void setStatus(int status){
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public String getLng() {
        return lng;
    }

    public String getEnds() {
        return ends;
    }

    public String getName() {
        return name;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public int getId() {
        return id;
    }

    public List<PromotionCategoriesEntity> getCategories() {
        return categories;
    }

    public String getText() {
        return text;
    }

    public String getStarts() {
        return starts;
    }

    public String getLat() {
        return lat;
    }

    public int getStatus() {
        return status;
    }

    public List<Markers> getMarkers() {
        return markers;
    }

    public void setMarkers(List<Markers> markers) {
        this.markers = markers;
    }
}
