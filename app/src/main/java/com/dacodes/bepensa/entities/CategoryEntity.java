package com.dacodes.bepensa.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Eric on 15/03/17.
 *
 */

public class CategoryEntity implements Serializable{

    @SerializedName("id")
    private int id;
    @SerializedName("image")
    private String image;
    @SerializedName("name")
    private String name;
    @SerializedName("created")
    private String created;
    @SerializedName("modified")
    private String modified;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }
}
