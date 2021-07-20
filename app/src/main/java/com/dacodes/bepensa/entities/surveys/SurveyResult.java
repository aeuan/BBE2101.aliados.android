package com.dacodes.bepensa.entities.surveys;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SurveyResult {
    @SerializedName("items")
    private ArrayList<SurveyOptions> items;

    @SerializedName("survey_id")
    private int survey_id;

    public void setItems(ArrayList<SurveyOptions> items){
        this.items = items;
    }

    public ArrayList<SurveyOptions> getItems(){
        return items;
    }

    public int getSurvey_id() {
        return survey_id;
    }

    public void setSurvey_id(int survey_id) {
        this.survey_id = survey_id;
    }
}
