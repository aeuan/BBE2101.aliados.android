package com.dacodes.bepensa.entities.surveys.result;

import com.dacodes.bepensa.entities.surveys.SurveyItemsEntity;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SurveyDataEntity implements Serializable {
    @SerializedName("image")
    private String image;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("name")
    private String name;
    @SerializedName("description")
    private String description;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("id")
    private int id;
    @SerializedName("items")
    private List<SurveyItemsEntity> items;
    @SerializedName("has_answered")
    private boolean hasAnswered;
    @SerializedName("points")
    private int points;
    @SerializedName("status")
    private int status;

    public void setImage(String image){
        this.image = image;
    }

    public String getImage(){
        return image;
    }

    public void setUpdatedAt(String updatedAt){
        this.updatedAt = updatedAt;
    }

    public String getUpdatedAt(){
        return updatedAt;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }

    public void setCreatedAt(String createdAt){
        this.createdAt = createdAt;
    }

    public String getCreatedAt(){
        return createdAt;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public void setItems(List<SurveyItemsEntity> items){
        this.items = items;
    }

    public List<SurveyItemsEntity> getItems(){
        return items;
    }

    public void setHasAnswered(boolean hasAnswered){
        this.hasAnswered = hasAnswered;
    }

    public boolean isHasAnswered(){
        return hasAnswered;
    }

    public void setPoints(int points){
        this.points = points;
    }

    public int getPoints(){
        return points;
    }

    public void setStatus(int status){
        this.status = status;
    }

    public int getStatus(){
        return status;
    }
}
