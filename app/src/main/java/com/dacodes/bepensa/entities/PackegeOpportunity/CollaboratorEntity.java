package com.dacodes.bepensa.entities.PackegeOpportunity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CollaboratorEntity  implements Serializable {
    /**
     * uuid : 77ad5ce7-f25d-4ffb-b623-52dcc6f4ee9a
     * first_name : AARON
     * last_name : AGUILAR MEZA
     * email : aron@example.com
     * username : 8653
     * avatar_url : https://bepensa-media.s3.amazonaws.com/media/empty.png
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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}