package com.dacodes.bepensa.entities.promotion;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PromotionsEntity {
    @SerializedName("pagination")
    private PromotionPaginationEntity pagination;

    @SerializedName("data")
    private ArrayList<PromotionDataEntity> data;

    public void setPagination(PromotionPaginationEntity pagination){
        this.pagination = pagination;
    }

    public void setData(ArrayList<PromotionDataEntity> data){
        this.data = data;
    }

    public PromotionPaginationEntity getPagination() {
        return pagination;
    }

    public ArrayList<PromotionDataEntity> getData() {
        return data;
    }
}
