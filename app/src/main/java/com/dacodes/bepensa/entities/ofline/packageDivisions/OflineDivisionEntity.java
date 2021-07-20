package com.dacodes.bepensa.entities.ofline.packageDivisions;

import io.realm.RealmObject;

public class OflineDivisionEntity extends RealmObject {

    private int id;
    private String name;
    private boolean allow_reports;
    private String brands_title;
    private String states;

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

    public String getBrands_title() {
        return brands_title;
    }

    public void setBrands_title(String brands_title) {
        this.brands_title = brands_title;
    }

    public String getStates() {
        return states;
    }

    public void setStates(String states) {
        this.states = states;
    }
}
