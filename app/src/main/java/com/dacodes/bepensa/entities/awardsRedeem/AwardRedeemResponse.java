package com.dacodes.bepensa.entities.awardsRedeem;

import com.dacodes.bepensa.entities.PaginationEntity;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AwardRedeemResponse {
    @SerializedName("pagination")
    private PaginationEntity pagination;

    @SerializedName("data")
    private ArrayList<AwardRedeemData> data;

    public void setPagination(PaginationEntity pagination){
        this.pagination = pagination;
    }

    public PaginationEntity getPagination(){
        return pagination;
    }

    public void setData(ArrayList<AwardRedeemData> data){
        this.data = data;
    }

    public ArrayList<AwardRedeemData> getData(){
        return data;
    }
}
