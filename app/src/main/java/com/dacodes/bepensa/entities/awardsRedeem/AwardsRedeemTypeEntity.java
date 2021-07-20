package com.dacodes.bepensa.entities.awardsRedeem;

import com.google.gson.annotations.SerializedName;

public class AwardsRedeemTypeEntity {
    @SerializedName("max_points")
    private int maxPoints;

    @SerializedName("min_points")
    private int minPoints;

    @SerializedName("name")
    private String name;

    @SerializedName("id")
    private int id;

    public void setMaxPoints(int maxPoints){
        this.maxPoints = maxPoints;
    }

    public int getMaxPoints(){
        return maxPoints;
    }

    public void setMinPoints(int minPoints){
        this.minPoints = minPoints;
    }

    public int getMinPoints(){
        return minPoints;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }
}
