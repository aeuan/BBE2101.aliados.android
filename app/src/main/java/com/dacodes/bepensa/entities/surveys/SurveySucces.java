package com.dacodes.bepensa.entities.surveys;

import com.google.gson.annotations.SerializedName;

public class SurveySucces {

    @SerializedName("success")
    private String success;

    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
