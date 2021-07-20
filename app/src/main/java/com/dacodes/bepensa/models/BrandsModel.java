package com.dacodes.bepensa.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BrandsModel {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("division")
    @Expose
    private Division division;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("logo")
    @Expose
    private String logo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Division getDivision() {
        return division;
    }

    public void setDivision(Division division) {
        this.division = division;
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
}
