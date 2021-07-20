package com.dacodes.bepensa.entities.ofline.packageSurvey;

import io.realm.RealmObject;

public class OflineSurveyEntity extends RealmObject {

    String data;
    int id;
    boolean has_sync;
    String result;
    boolean has_sync_load;
    String id_colaborator;

    public String getId_colaborator() {
        return id_colaborator;
    }

    public void setId_colaborator(String id_colaborator) {
        this.id_colaborator = id_colaborator;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isHas_sync() {
        return has_sync;
    }

    public void setHas_sync(boolean has_sync) {
        this.has_sync = has_sync;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public boolean isHas_sync_load() {
        return has_sync_load;
    }

    public void setHas_sync_load(boolean has_sync_load) {
        this.has_sync_load = has_sync_load;
    }


}
