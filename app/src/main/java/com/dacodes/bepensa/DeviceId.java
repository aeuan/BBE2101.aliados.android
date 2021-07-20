package com.dacodes.bepensa;

import com.google.gson.annotations.SerializedName;

public class DeviceId {
    @SerializedName("token")
    String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
