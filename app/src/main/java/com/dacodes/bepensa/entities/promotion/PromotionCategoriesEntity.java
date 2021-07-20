package com.dacodes.bepensa.entities.promotion;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PromotionCategoriesEntity implements Serializable {
    @SerializedName("name")
    private String name;

    @SerializedName("id")
    private int id;

    public void setName(String name){
        this.name = name;
    }

    public void setId(int id){
        this.id = id;
    }
}
