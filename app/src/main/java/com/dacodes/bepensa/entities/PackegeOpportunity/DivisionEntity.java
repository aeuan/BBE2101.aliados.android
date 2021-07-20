package com.dacodes.bepensa.entities.PackegeOpportunity;

import com.dacodes.bepensa.entities.DataStates;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DivisionEntity implements Serializable {
    /**
     * id : 1
     * name : BEPENSA SPIRITS
     * hash : 14a994b2ebf1cd00f19953295b7bc4e9
     * created_at : 2018-10-11T02:45:10.331335-05:00
     * updated_at : 2018-10-11T02:45:10.331382-05:00
     */

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("hash")
    private String hash;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("allow_reports")
    private boolean allow_reports;
    @SerializedName("image")
    private String image;
    @SerializedName("allow_login")
    private boolean allow_login;
    @SerializedName("brands_title")
    private String brands_title;
    @SerializedName("states")
    private List<DataStates> states = new ArrayList<>();

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isAllow_reports() {
        return allow_reports;
    }

    public void setAllow_reports(boolean allow_reports) {
        this.allow_reports = allow_reports;
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

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
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

    public boolean isAllow_login() {
        return allow_login;
    }

    public void setAllow_login(boolean allow_login) {
        this.allow_login = allow_login;
    }

    public String getBrands_title() {
        return brands_title;
    }

    public void setBrands_title(String brands_title) {
        this.brands_title = brands_title;
    }

    public List<DataStates> getStates() {
        return states;
    }

    public void setStates(List<DataStates> states) {
        this.states = states;
    }
}