package com.dacodes.bepensa.entities;

public class DivisionOpportunity {

    int division_id;
    String division_name;
    int opportunity_id;
    String opportunity_name;
    double points;


    public DivisionOpportunity(int division_id, String division_name, int opportunity_id,
                               String opportunity_name, double points) {
        this.division_id = division_id;
        this.division_name = division_name;
        this.opportunity_id = opportunity_id;
        this.opportunity_name = opportunity_name;
        this.points = points;
    }

    public int getDivision_id() {
        return division_id;
    }

    public void setDivision_id(int division_id) {
        this.division_id = division_id;
    }

    public String getDivision_name() {
        return division_name;
    }

    public void setDivision_name(String division_name) {
        this.division_name = division_name;
    }

    public int getOpportunity_id() {
        return opportunity_id;
    }

    public void setOpportunity_id(int opportunity_id) {
        this.opportunity_id = opportunity_id;
    }

    public String getOpportunity_name() {
        return opportunity_name;
    }

    public void setOpportunity_name(String opportunity_name) {
        this.opportunity_name = opportunity_name;
    }

    public double getPoints() {
        return points;
    }

    public void setPoints(double points) {
        this.points = points;
    }


}
