package com.dacodes.bepensa.entities;

import com.google.gson.annotations.SerializedName;

public class AwardType {
    /**
     * id : 1
     * name : Gama baja
     * min_points : 0
     * max_points : 100
     */

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("min_points")
    private int minPoints;
    @SerializedName("max_points")
    private int maxPoints;

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

    public int getMinPoints() {
        return minPoints;
    }

    public void setMinPoints(int minPoints) {
        this.minPoints = minPoints;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    public void setMaxPoints(int maxPoints) {
        this.maxPoints = maxPoints;
    }
}
