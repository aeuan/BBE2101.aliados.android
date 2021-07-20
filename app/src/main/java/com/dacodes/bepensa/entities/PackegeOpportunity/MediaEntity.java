package com.dacodes.bepensa.entities.PackegeOpportunity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MediaEntity implements Serializable {
    /**
     * id : 1
     * archive : https://bepensa-media.s3.amazonaws.com/media/images/55184a3a7a1c41fa8287f7d2891611ec.jpg
     * type : 1
     */

    @SerializedName("id")
    private int id;
    @SerializedName("archive")
    private String archive;
    @SerializedName("type")
    private int type;
    @SerializedName("thumbnail")
    private String thumbnail;
    private String mediaLocal;

    public MediaEntity(int id, String archive, int type) {
        this.id = id;
        this.archive = archive;
        this.type = type;
    }

    public MediaEntity() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArchive() {
        return archive;
    }

    public void setArchive(String archive) {
        this.archive = archive;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getMediaLocal() {
        return mediaLocal;
    }

    public void setMediaLocal(String mediaLocal) {
        this.mediaLocal = mediaLocal;
    }
}