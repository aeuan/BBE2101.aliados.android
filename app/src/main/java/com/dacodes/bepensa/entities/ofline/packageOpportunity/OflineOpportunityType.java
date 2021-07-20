package com.dacodes.bepensa.entities.ofline.packageOpportunity;

import com.dacodes.bepensa.entities.ofline.packageDivisions.OflineDivisionEntity;

import io.realm.RealmObject;

public class OflineOpportunityType extends RealmObject {

    public int id;
    public String name;
    public double points;
    OflineDivisionEntity divisionEntity;

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

    public double getPoints() {
        return points;
    }

    public void setPoints(double points) {
        this.points = points;
    }

    public OflineDivisionEntity getDivisionEntity() {
        return divisionEntity;
    }

    public void setDivisionEntity(OflineDivisionEntity divisionEntity) {
        this.divisionEntity = divisionEntity;
    }
}
