package com.dacodes.bepensa.entities;

import com.google.gson.annotations.SerializedName;

public class LoginUser {
    @SerializedName("username")
    private String username;
    @SerializedName("password")
    private String password;
    @SerializedName("division")
    private int division;
    @SerializedName("close_all_conections")
    private int close_all_conections;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDivision() {
        return division;
    }

    public void setDivision(int division) {
        this.division = division;
    }

    public int getClose_all_conections() {
        return close_all_conections;
    }

    public void setClose_all_conections(int close_all_conections) {
        this.close_all_conections = close_all_conections;
    }
}
