package com.dacodes.bepensa.models;

import com.dacodes.bepensa.entities.ComunicadoEntity;
import com.dacodes.bepensa.entities.PaginationEntity;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ComunicadosResponseModel {

    /**
     * data : [{"id":11,"name":"Día inhábil","text":"El próximo lunes 19 de septiembre no se laborará por motivo del aniversario de la Revolución Mexicana","starts":"2018-11-19T09:00:49-06:00","ends":"2018-11-19T09:00:49-06:00","image":"https://bepensa-media.s3.amazonaws.com/media/releases/None/7d43b375e3e24ee987432e17efc33ad7.jpg","status":1,"created_at":"2018-11-13T00:39:10.588514-06:00"}]
     * pagination : {"total_rows":1,"per_page":10,"current_page":1,"links":{"first":"http://bepensa.api.dacodes.mx/v1/collaborators/releases?page=1","last":"http://bepensa.api.dacodes.mx/v1/collaborators/releases?page=1","next":"","prev":""}}
     */

    @SerializedName("pagination")
    private PaginationEntity pagination;
    @SerializedName("data")
    private ArrayList<ComunicadoEntity> data;

    public PaginationEntity getPagination() {
        return pagination;
    }

    public void setPagination(PaginationEntity pagination) {
        this.pagination = pagination;
    }

    public ArrayList<ComunicadoEntity> getData() {
        return data;
    }

    public void setData(ArrayList<ComunicadoEntity> data) {
        this.data = data;
    }


}
