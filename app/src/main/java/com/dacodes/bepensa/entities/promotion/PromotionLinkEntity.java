package com.dacodes.bepensa.entities.promotion;

import com.google.gson.annotations.SerializedName;

public class PromotionLinkEntity {
    @SerializedName("next")
    private String next;

    @SerializedName("last")
    private String last;

    @SerializedName("prev")
    private String prev;

    @SerializedName("first")
    private String first;

    public void setNext(String next){
        this.next = next;
    }

    public void setLast(String last){
        this.last = last;
    }

    public void setPrev(String prev){
        this.prev = prev;
    }

    public void setFirst(String first){
        this.first = first;
    }

    public String getNext() {
        return next;
    }

    public String getLast() {
        return last;
    }

    public String getPrev() {
        return prev;
    }

    public String getFirst() {
        return first;
    }
}
