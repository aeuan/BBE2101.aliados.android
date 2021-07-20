package com.dacodes.bepensa.entities;

import com.google.gson.annotations.SerializedName;

/**
 * created by Carlos Chin Ku
 * email:efrainck94@gmail.com
 */
public class ThemeNotification {

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("is_active")
    private boolean isActive;

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
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
