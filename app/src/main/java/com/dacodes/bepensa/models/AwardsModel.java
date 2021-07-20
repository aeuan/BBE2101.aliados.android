package com.dacodes.bepensa.models;

import com.dacodes.bepensa.entities.AwardType;
import com.google.gson.annotations.SerializedName;

public class AwardsModel {

    /**
     * id : 1
     * name : Bolígrafo
     * points : 10
     * description :
     * availability : 100
     * image : https://bepensa-media.s3.amazonaws.com/media/awards/e74ff7fd43054b34b8bca89f68db0076.jpg
     * created_at : 2018-10-17T14:49:08.444586-05:00
     * updated_at : 2018-10-17T14:49:08.444641-05:00
     * type : {"id":1,"name":"Gama baja","min_points":0,"max_points":100}
     */

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("points")
    private int points;
    @SerializedName("description")
    private String description;
    @SerializedName("availability")
    private int availability;
    @SerializedName("image")
    private String image;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("type")
    private AwardType type;
    @SerializedName("active")
    private boolean active;


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

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public AwardType getType() {
        return type;
    }

    public void setType(AwardType type) {
        this.type = type;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
