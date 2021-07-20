package com.dacodes.bepensa.models;

import com.dacodes.bepensa.entities.EventosEntity;
import com.dacodes.bepensa.entities.PaginationEntity;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class EventosResponseModel {


    /**
     * data : [{"id":3,"name":"Evento de prueba","text":"This a text of the event","starts":"2018-12-19T08:15:00-06:00","ends":"2018-12-19T08:15:00-06:00","image":"https://bepensa-media.s3.amazonaws.com/media/events/3/ec28f2bfb62845afbc7c6fec960ccc7f.png","lat":"20.9927921","lng":"-89.58009179999999","status":1,"created_at":"2018-11-09T13:57:32.665849-06:00"},{"id":2,"name":"Nuevo evento","text":"This a text of the event","starts":"2018-12-01T10:00:00-06:00","ends":"2018-12-20T12:00:00-06:00","image":"https://bepensa-media.s3.amazonaws.com/media/empty.png","lat":"0.000000","lng":"0.000000","status":1,"created_at":"2018-11-09T13:56:18.113105-06:00"}]
     * pagination : {"total_rows":2,"per_page":10,"current_page":1,"links":{"first":"http://bepensa.api.dacodes.mx/v1/collaborators/events?page=1","last":"http://bepensa.api.dacodes.mx/v1/collaborators/events?page=1","next":"","prev":""}}
     */

    @SerializedName("pagination")
    private PaginationEntity pagination;
    @SerializedName("data")
    private ArrayList<EventosEntity> data;

    public PaginationEntity getPagination() {
        return pagination;
    }

    public void setPagination(PaginationEntity pagination) {
        this.pagination = pagination;
    }

    public ArrayList<EventosEntity> getData() {
        return data;
    }

    public void setData(ArrayList<EventosEntity> data) {
        this.data = data;
    }


}
