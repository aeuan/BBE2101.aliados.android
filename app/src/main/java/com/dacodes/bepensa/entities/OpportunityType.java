package com.dacodes.bepensa.entities;

import com.dacodes.bepensa.entities.PackegeOpportunity.DivisionEntity;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OpportunityType {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("points")
    @Expose
    private double points;
    @SerializedName("division")
    DivisionEntity division;
    private String help_text;
    @SerializedName("help_text")

    public DivisionEntity getDivision() {
        return division;
    }

    public void setDivision(DivisionEntity division) {
        this.division = division;
    }

    public double getPoints() {
        return points;
    }

    public void setPoints(double points) {
        this.points = points;
    }

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

    public String getHelp_text() {
        return help_text;
    }

    public void setHelp_text(String help_text) {
        this.help_text = help_text;
    }
}
