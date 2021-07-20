package com.dacodes.bepensa.models;

import com.dacodes.bepensa.entities.PaginationEntity;
import com.dacodes.bepensa.entities.surveys.result.SurveyDataEntity;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SurveyResponseModel {

    @SerializedName("pagination")
    private PaginationEntity pagination;

    @SerializedName("data")
    private ArrayList<SurveyDataEntity> data;

    public void setPagination(PaginationEntity pagination){
        this.pagination = pagination;
    }

    public PaginationEntity getPagination(){
        return pagination;
    }

    public void setData(ArrayList<SurveyDataEntity> data){
        this.data = data;
    }

    public ArrayList<SurveyDataEntity> getData(){
        return data;
    }
}
