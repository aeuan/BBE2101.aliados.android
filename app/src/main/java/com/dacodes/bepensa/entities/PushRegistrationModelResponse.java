package com.dacodes.bepensa.entities;

import com.google.gson.annotations.SerializedName;

public class PushRegistrationModelResponse {

    /**
     * id : 4
     * name : Can be anything
     * active : true
     * date_created : 2018-11-06T16:19:09.143411-06:00
     * device_id : 55673323424
     * registration_id : e0-6XaonRB0:APA91bGFTJDNbVjy-0KBsKu-TCVYW0-6rRRb_D4i_Ty8pI_N7YEb0UEsYeLTgKl9bRaLiUtmWCxrNIM6OUXuL-ZreP483SOQGNoI9lkb0BgYzfBW0GEVkMnwYb2_-77if2pTdPguuNEU
     * type : android
     * user : 314c899f-4642-43d1-ad36-1296723172c6
     */

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("active")
    private boolean active;
    @SerializedName("date_created")
    private String dateCreated;
    @SerializedName("device_id")
    private String deviceId;
    @SerializedName("registration_id")
    private String registrationId;
    @SerializedName("type")
    private String type;
    @SerializedName("user")
    private String user;

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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
