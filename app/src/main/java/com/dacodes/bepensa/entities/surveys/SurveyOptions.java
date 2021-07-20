package com.dacodes.bepensa.entities.surveys;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SurveyOptions {
    @SerializedName ("options")
    private List<Integer> options;

    @SerializedName("id")
    private int id;

    @SerializedName("type")
    private int type;

    @SerializedName("text")
    private String text;

    public void setOptions(List<Integer> options){
        this.options = options;
    }

    public List<Integer> getOptions(){
        return options;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public void setType(int type){
        this.type = type;
    }

    public int getType(){
        return type;
    }

    public void setText(String text){
        this.text = text;
    }

    public String getText(){
        return text;
    }
}
