package com.dacodes.bepensa.entities.surveys;

import com.dacodes.bepensa.entities.OptionsItem;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SurveyItemsEntity implements Serializable {
    @SerializedName("options")
    private List<OptionsItem> options;
    @SerializedName("name")
    private String name;
    @SerializedName("id")
    private int id;
    @SerializedName("type")
    private int type;

    public void setOptions(List<OptionsItem> options){
        this.options = options;
    }

    public List<OptionsItem> getOptions(){
        return options;
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

    public void setType(int type){
        this.type = type;
    }

    public int getType(){
        return type;
    }
}
