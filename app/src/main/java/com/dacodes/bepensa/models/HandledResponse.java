package com.dacodes.bepensa.models;

public class HandledResponse {

    private boolean isNoAnError;
    private int code;
    private int erroCode;
    private String message;

    public HandledResponse() {
    }

    public HandledResponse(boolean isNoAnError, int code, int erroCode, String message) {
        this.isNoAnError = isNoAnError;
        this.code = code;
        this.erroCode = erroCode;
        this.message = message;
    }

    public HandledResponse(boolean isNoAnError, int code, String message) {
        this.isNoAnError = isNoAnError;
        this.code = code;
        this.message = message;
    }

    public boolean isNoAnError() {
        return isNoAnError;
    }

    public void setNoAnError(boolean noAnError) {
        isNoAnError = noAnError;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getErroCode() {
        return erroCode;
    }

    public void setErroCode(int erroCode) {
        this.erroCode = erroCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
