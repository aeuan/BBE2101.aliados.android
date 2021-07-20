package com.dacodes.bepensa.entities.PackegeOpportunity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TypeEntity implements Serializable {
    /**
     * id : 1
     * name : Reporte de neveras contaminadas
     * division : {"id":2,"name":"BEPENSA BEBIDAS"}
     */

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("division")
    private DivisionEntity division;

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

    public DivisionEntity getDivision() {
        return division;
    }

    public void setDivision(DivisionEntity division) {
        this.division = division;
    }


}