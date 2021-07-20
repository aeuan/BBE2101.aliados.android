package com.dacodes.bepensa.entities.ofline.postOpportunity;

import io.realm.RealmObject;

public class OflineMediaOpportunity extends RealmObject {
    int type;
    String path;
    String extension;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
