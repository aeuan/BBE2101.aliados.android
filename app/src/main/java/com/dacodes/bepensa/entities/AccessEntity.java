package com.dacodes.bepensa.entities;

import com.google.gson.annotations.SerializedName;

public class AccessEntity {
    /**
     * avatar_url : https://bepensa-media.s3.amazonaws.com/media/empty.png
     */

    @SerializedName("avatar_url")
    private String avatarUrl;
    @SerializedName("uuid")
    private String uuid;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
