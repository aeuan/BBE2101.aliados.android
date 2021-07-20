package com.dacodes.bepensa.models;

import com.dacodes.bepensa.entities.PackegeOpportunity.BrandEntity;

import java.util.List;

public class DivisionTemporal {
    int id_division;
    String name_division;
    List<BrandEntity> brands;

    public DivisionTemporal(int id_division, String name_division, List<BrandEntity> brands) {
        this.id_division = id_division;
        this.name_division = name_division;
        this.brands = brands;
    }

    public int getId_division() {
        return id_division;
    }

    public void setId_division(int id_division) {
        this.id_division = id_division;
    }

    public String getName_division() {
        return name_division;
    }

    public void setName_division(String name_division) {
        this.name_division = name_division;
    }

    public List<BrandEntity> getBrands() {
        return brands;
    }

    public void setBrands(List<BrandEntity> brands) {
        this.brands = brands;
    }
}
