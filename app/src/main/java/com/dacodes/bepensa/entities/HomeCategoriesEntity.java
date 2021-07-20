package com.dacodes.bepensa.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Eric on 16/03/17.
 *
 */

public class HomeCategoriesEntity {
    @SerializedName("id")
    private int id;
    @SerializedName("parent_id")
    private int parentId;
    @SerializedName("lft")
    private int lft;
    @SerializedName("rght")
    private int rght;
    @SerializedName("name")
    private String name;
    @SerializedName("key_name")
    private String keyName;
    @SerializedName("created")
    private String created;
    @SerializedName("modified")
    private String modified;
    @SerializedName("restaurants")
    private List<RestaurantsEntity> restaurants;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getLft() {
        return lft;
    }

    public void setLft(int lft) {
        this.lft = lft;
    }

    public int getRght() {
        return rght;
    }

    public void setRght(int rght) {
        this.rght = rght;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
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

    public List<RestaurantsEntity> getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(List<RestaurantsEntity> restaurants) {
        this.restaurants = restaurants;
    }

}
