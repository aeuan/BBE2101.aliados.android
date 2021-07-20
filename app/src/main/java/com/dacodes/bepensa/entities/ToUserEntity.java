package com.dacodes.bepensa.entities;

import com.dacodes.bepensa.entities.PackegeOpportunity.DivisionEntity;
import com.google.gson.annotations.SerializedName;

public class ToUserEntity {
    /**
     * uuid : 314c899f-4642-43d1-ad36-1296723172c6
     * first_name : SEBASTIAN
     * last_name : COCOM CHAN
     * email : manuel@gmail.com
     * username : 5646
     * role : 3
     * division : {"id":2,"name":"BEPENSA BEBIDAS","status":1}
     * avatar_url : https://bepensa-media.s3.amazonaws.com/media/user-placeholder.jpg
     */

    @SerializedName("uuid")
    private String uuid;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("email")
    private String email;
    @SerializedName("username")
    private String username;
    @SerializedName("role")
    private int role;
    @SerializedName("division")
    private DivisionEntity division;
    @SerializedName("avatar_url")
    private String avatarUrl;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public DivisionEntity getDivision() {
        return division;
    }

    public void setDivision(DivisionEntity division) {
        this.division = division;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

}
