package com.dacodes.bepensa.entities.awardsRedeem;

import com.google.gson.annotations.SerializedName;

public class AwardRedeemData {
    @SerializedName("award")
    private Award award;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("is_changed")
    private boolean isChanged;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("id")
    private int id;

    @SerializedName("collaborator")
    private Collaborator collaborator;

    @SerializedName("points")
    private int points;

    public void setAward(Award award){
        this.award = award;
    }

    public Award getAward(){
        return award;
    }

    public void setUpdatedAt(String updatedAt){
        this.updatedAt = updatedAt;
    }

    public String getUpdatedAt(){
        return updatedAt;
    }

    public void setIsChanged(boolean isChanged){
        this.isChanged = isChanged;
    }

    public boolean isIsChanged(){
        return isChanged;
    }

    public void setCreatedAt(String createdAt){
        this.createdAt = createdAt;
    }

    public String getCreatedAt(){
        return createdAt;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public void setCollaborator(Collaborator collaborator){
        this.collaborator = collaborator;
    }

    public Collaborator getCollaborator(){
        return collaborator;
    }

    public void setPoints(int points){
        this.points = points;
    }

    public int getPoints(){
        return points;
    }
}
