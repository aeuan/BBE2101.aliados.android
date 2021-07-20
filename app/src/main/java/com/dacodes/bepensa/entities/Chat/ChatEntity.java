package com.dacodes.bepensa.entities.Chat;

import com.google.gson.annotations.SerializedName;

public class ChatEntity {
    /**
     * id : 103
     * user : {"uuid":"fd06b0cc-8081-476a-9ef3-9482eed2687d","first_name":"Abner","last_name":"Grajales","email":"be_93edit@example.com","username":"administrador","role":1,"division":{"id":2,"name":"BEPENSA BEBIDAS","status":1},"avatar_url":"https://bepensa-media.s3.amazonaws.com/media/user-placeholder.jpg"}
     * text : mandando desde el panel
     * created_at : 2018-11-13T02:41:36.577403-06:00
     */

    @SerializedName("id")
    private int id;
    @SerializedName("user")
    private UserEntity user;
    @SerializedName("text")
    private String text;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("type")
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private int type_model;

    public int getType_model() {
        return type_model;
    }

    public void setType_model(int type_model) {
        this.type_model = type_model;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

}
