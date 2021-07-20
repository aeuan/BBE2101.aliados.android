package com.dacodes.bepensa.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MediaErrorModel {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    private List<String> message = null;
    @SerializedName("code")
    private Integer code;
    @SerializedName("errors")
    @Expose
    private List<Error> errors = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public List<String> getMessage() {
        return message;
    }

    public void setMessage(List<String> message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }

    public class Error{
        @SerializedName("message")
        @Expose
        private String message;
        @SerializedName("code")
        @Expose
        private Integer code;
        @SerializedName("content_type")
        @Expose
        private String contentType;
        @SerializedName("enables")
        @Expose
        private List<String> enables = null;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public List<String> getEnables() {
            return enables;
        }

        public void setEnables(List<String> enables) {
            this.enables = enables;
        }

    }
}
