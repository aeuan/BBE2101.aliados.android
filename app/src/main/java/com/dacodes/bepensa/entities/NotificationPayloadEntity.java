package com.dacodes.bepensa.entities;

import com.google.gson.annotations.SerializedName;

public class NotificationPayloadEntity {
    /**
     * body : Prueba 7 de sockets y push notifications
     * title : Abner Grajales
     */

    @SerializedName("body")
    private String body;
    @SerializedName("title")
    private String title;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
