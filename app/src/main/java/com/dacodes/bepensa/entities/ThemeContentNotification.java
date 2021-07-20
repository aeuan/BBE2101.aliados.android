package com.dacodes.bepensa.entities;

import com.google.gson.annotations.SerializedName;

/**
 * created by Carlos Chin Ku
 * email:efrainck94@gmail.com
 */
public class ThemeContentNotification {

    @SerializedName("link_title")
    private String linkTitle;
    @SerializedName("link_url")
    private String linkUrl;
    @SerializedName("image")
    private String image;
    @SerializedName("file")
    private String file;
    @SerializedName("theme")
    private ThemeNotification theme;

    public String getLinkTitle() {
        return linkTitle;
    }

    public void setLinkTitle(String linkTitle) {
        this.linkTitle = linkTitle;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public ThemeNotification getTheme() {
        return theme;
    }

    public void setTheme(ThemeNotification theme) {
        this.theme = theme;
    }
}
