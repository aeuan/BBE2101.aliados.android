package com.dacodes.bepensa.entities;

import java.io.File;
import java.io.Serializable;

public class MediasFilesEntity implements Serializable {

    private int type;
    private String extension;
    private File file;

    public MediasFilesEntity(int type, String extension, File file) {
        this.type = type;
        this.extension = extension;
        this.file = file;
    }

    public MediasFilesEntity() {

    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
