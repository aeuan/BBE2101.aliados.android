package com.dacodes.bepensa.utils;

public class FailError {
    private Throwable t;
    private int code;
    private String message;

    public FailError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Throwable getT() {
        return t;
    }

    public void setT(Throwable t) {
        this.t = t;
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
