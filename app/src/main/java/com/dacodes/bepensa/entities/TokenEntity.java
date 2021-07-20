package com.dacodes.bepensa.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TokenEntity implements Serializable {
    /**
     * hash : eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6IjQwMzVjYTFhLTQ1ZTEtNGM1OS05NzdhLTdkYzFlZTAzOTcyZiIsImRpc3BsYXlfbmFtZSI6bnVsbCwicm9sZSI6Mywic3RhdHVzIjoyLCJlbWFpbCI6InNjQGV4YW1wbGUuY29tIiwiZXhwIjoxNTQ0MjAzNTkxfQ.COKYL1HmXFZBZngKQvObSaItcyYjGZz7oBfZ8tO1pvU
     */

    @SerializedName("hash")
    private String hash;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
