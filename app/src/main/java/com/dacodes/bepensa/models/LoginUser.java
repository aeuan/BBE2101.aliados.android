package com.dacodes.bepensa.models;

import com.google.gson.annotations.SerializedName;

public class LoginUser {

    @SerializedName("username")
    public int username;

    @SerializedName("division")
    public int division;

    @SerializedName("password")
    public String password;

    @SerializedName("close_all_conections")
    public int close_all_conections;

    public int getUsername() {
        return username;
    }

    public void setUsername(int username) {
        this.username = username;
    }

    public int getDivision() {
        return division;
    }

    public void setDivision(int division) {
        this.division = division;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getClose_all_conections() {
        return close_all_conections;
    }

    public void setClose_all_conections(int close_all_conections) {
        this.close_all_conections = close_all_conections;
    }
}
