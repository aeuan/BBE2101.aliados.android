package com.dacodes.bepensa.entities;

import com.dacodes.bepensa.entities.PackegeOpportunity.DivisionEntity;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProfileEntity implements Serializable {

    /**
     * uuid : 4035ca1a-45e1-4c59-977a-7dc1ee03972f
     * full_name : null
     * email : sc@example.com
     * username : 6677
     * role : 3
     * token : {"hash":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6IjQwMzVjYTFhLTQ1ZTEtNGM1OS05NzdhLTdkYzFlZTAzOTcyZiIsImRpc3BsYXlfbmFtZSI6bnVsbCwicm9sZSI6Mywic3RhdHVzIjoyLCJlbWFpbCI6InNjQGV4YW1wbGUuY29tIiwiZXhwIjoxNTQ0MjAzNTkxfQ.COKYL1HmXFZBZngKQvObSaItcyYjGZz7oBfZ8tO1pvU"}
     * avatar_url :
     */

    @SerializedName("uuid")
    private String uuid;
    @SerializedName("first_name")
    private String first_name;
    @SerializedName("last_name")
    private String last_name;
    @SerializedName("email")
    private String email;
    @SerializedName("username")
    private String username;
    @SerializedName("role")
    private int role;
    @SerializedName("token")
    private TokenEntity token;
    @SerializedName("avatar_url")
    private String avatarUrl;
    @SerializedName("division")
    private DivisionEntity division;
    @SerializedName("access")
    private AccessEntity access;
    private int points;
    private String fullName;
    @SerializedName("contact")
    private String contact;
    @SerializedName("points_available")
    private int points_available;
    @SerializedName("rank")
    private int rank;
    @SerializedName("hidden_ranking")
    private boolean hidden_ranking;
    @SerializedName("imss")
    private String imss;

    public String getImss() {
        return imss;
    }

    public void setImss(String imss) {
        this.imss = imss;
    }

    public boolean isHidden_ranking() {
        return hidden_ranking;
    }

    public void setHidden_ranking(boolean hidden_ranking) {
        this.hidden_ranking = hidden_ranking;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getPoints_available() {
        return points_available;
    }

    public void setPoints_available(int points_available) {
        this.points_available = points_available;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        if (first_name != null) {
            return first_name;
        } else {
            return "Nombre";
        }
    }

    public String getLast_name() {
        if (last_name != null) {
            return last_name;
        } else {
            return "Apellido";
        }
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

    public TokenEntity getToken() {
        return token;
    }

    public void setToken(TokenEntity token) {
        this.token = token;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getPoints() {
        if (points > 0) {
            return points;
        } else {
            return 0;
        }
    }

    public void setPoints(int points) {
        this.points = points;
    }


    public DivisionEntity getDivision() {
        return division;
    }

    public void setDivision(DivisionEntity division) {
        this.division = division;
    }

    public AccessEntity getAccess() {
        return access;
    }

    public void setAccess(AccessEntity access) {
        this.access = access;
    }

}
