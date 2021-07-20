package com.dacodes.bepensa.entities;

import com.dacodes.bepensa.utils.FindDateDifference;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class NotificacionesEntity {
    /**
     * id : 143
     * to_user : {"uuid":"314c899f-4642-43d1-ad36-1296723172c6","first_name":"SEBASTIAN","last_name":"COCOM CHAN","email":"manuel@gmail.com","username":"5646","role":3,"division":{"id":2,"name":"BEPENSA BEBIDAS","status":1},"avatar_url":"https://bepensa-media.s3.amazonaws.com/media/user-placeholder.jpg"}
     * from_user : {"uuid":"fd06b0cc-8081-476a-9ef3-9482eed2687d","first_name":"Abner","last_name":"Grajales","email":"be_93edit@example.com","username":"administrador","role":1,"division":{"id":2,"name":"BEPENSA BEBIDAS","status":1},"avatar_url":"https://bepensa-media.s3.amazonaws.com/media/user-placeholder.jpg"}
     * notice_key : NEW_EVENT
     * title : Nuevo evento
     * message : This a text of the event
     * status : 1
     * created_at : 2018-11-12T19:55:48.953581-06:00
     * updated_at : 2018-11-12T19:55:48.953593-06:00
     */

    @SerializedName("id")
    private int id;
    @SerializedName("notice_key")
    private String noticeKey;
    @SerializedName("title")
    private String title;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private int status;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("theme_content")
    private ThemeContentNotification themeContentNotification;

    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNoticeKey() {
        return noticeKey;
    }

    public void setNoticeKey(String noticeKey) {
        this.noticeKey = noticeKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ThemeContentNotification getThemeContentNotification() {
        return themeContentNotification;
    }

    public void setThemeContentNotification(ThemeContentNotification themeContentNotification) {
        this.themeContentNotification = themeContentNotification;
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
