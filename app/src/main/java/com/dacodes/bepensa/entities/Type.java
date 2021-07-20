package com.dacodes.bepensa.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Type implements Serializable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("points")
    @Expose
    private Integer points;
    @SerializedName("points_extra")
    @Expose
    private Integer pointsExtra;
    @SerializedName("division")
    @Expose
    private DivisionShort division;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getPointsExtra() {
        return pointsExtra;
    }

    public void setPointsExtra(Integer pointsExtra) {
        this.pointsExtra = pointsExtra;
    }

    public DivisionShort getDivision() {
        return division;
    }

    public void setDivision(DivisionShort division) {
        this.division = division;
    }

}
