package com.dacodes.bepensa.models;

import com.google.gson.annotations.SerializedName;

public class PointsResponseModel {

    /**
     * rank : 5
     * points : 3
     * opportunities_count : 39
     */

    @SerializedName("rank")
    private int rank;
    @SerializedName("points")
    private int points;
    @SerializedName("opportunities_count")
    private int opportunitiesCount;
    @SerializedName("points_available")
    private int points_available;

    @SerializedName("points_surveys")
    private int points_surveys;

    @SerializedName("points_opportunities")
    private int points_opportunities;

    public int getPoints_surveys() {
        return points_surveys;
    }

    public void setPoints_surveys(int points_surveys) {
        this.points_surveys = points_surveys;
    }

    public int getPoints_opportunities() {
        return points_opportunities;
    }

    public void setPoints_opportunities(int points_opportunities) {
        this.points_opportunities = points_opportunities;
    }

    public int getPoints_available() {
        return points_available;
    }

    public void setPoints_available(int points_available) {
        this.points_available = points_available;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getOpportunitiesCount() {
        return opportunitiesCount;
    }

    public void setOpportunitiesCount(int opportunitiesCount) {
        this.opportunitiesCount = opportunitiesCount;
    }
}
