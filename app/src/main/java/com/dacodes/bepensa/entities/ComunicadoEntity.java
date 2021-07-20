package com.dacodes.bepensa.entities;

import com.dacodes.bepensa.utils.FindDateDifference;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ComunicadoEntity {
    /**
     * id : 11
     * name : Día inhábil
     * text : El próximo lunes 19 de septiembre no se laborará por motivo del aniversario de la Revolución Mexicana
     * starts : 2018-11-19T09:00:49-06:00
     * ends : 2018-11-19T09:00:49-06:00
     * image : https://bepensa-media.s3.amazonaws.com/media/releases/None/7d43b375e3e24ee987432e17efc33ad7.jpg
     * status : 1
     * created_at : 2018-11-13T00:39:10.588514-06:00
     */

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("text")
    private String text;
    @SerializedName("starts")
    private String starts;
    @SerializedName("ends")
    private String ends;
    @SerializedName("image")
    private String image;
    @SerializedName("status")
    private int status;
    @SerializedName("created_at")
    private String createdAt;

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getStarts() {
        return starts;
    }

    public void setStarts(String starts) {
        this.starts = starts;
    }

    public String getEnds() {
        return ends;
    }

    public void setEnds(String ends) {
        this.ends = ends;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }


    public String getFormattedCreatedAt() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
        Date date = null;
        FindDateDifference findDateDifference = null;
        String tiempo;
        try {
            String upToNCharacters = createdAt.substring(0, Math.min(createdAt.length(), 19));
            date = df.parse(upToNCharacters);
            TimeZone tz = TimeZone.getDefault();
            SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH);
            destFormat.setTimeZone(tz);
            String startDate = destFormat.format(date);
            date = destFormat.parse(startDate);
            Date currentDate = new Date();
            findDateDifference = new FindDateDifference(date, currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(findDateDifference!=null) {
            tiempo = findDateDifference.calculateTimeSinceDate();
        }else{
            tiempo = createdAt;
        }
        return tiempo;
    }

}
