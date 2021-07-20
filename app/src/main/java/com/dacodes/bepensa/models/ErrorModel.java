package com.dacodes.bepensa.models;

import com.google.gson.annotations.SerializedName;

public class ErrorModel {

    /**
     * message : Acceso no válido
     * code : 1009
     */

    @SerializedName("message")
    private String message;
    @SerializedName("code")
    private int code;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
