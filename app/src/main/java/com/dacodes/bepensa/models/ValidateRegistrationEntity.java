package com.dacodes.bepensa.models;

import com.google.gson.annotations.SerializedName;

public class ValidateRegistrationEntity {

    /**
     * success : ok
     */

    @SerializedName("success")
    private String success;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }
}
