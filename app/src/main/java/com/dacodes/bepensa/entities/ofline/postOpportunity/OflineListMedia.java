package com.dacodes.bepensa.entities.ofline.postOpportunity;

import io.realm.RealmList;
import io.realm.RealmObject;

public class OflineListMedia extends RealmObject {
    int id;
    RealmList<OflineMediaOpportunity> oflineMediaOpportunities;
    boolean has_sync;
    String key_hash;
    int id_opportunity;
    String id_colaborator;

    public String getId_colaborator() {
        return id_colaborator;
    }

    public void setId_colaborator(String id_colaborator) {
        this.id_colaborator = id_colaborator;
    }

    public int getId_opportunity() {
        return id_opportunity;
    }

    public void setId_opportunity(int id_opportunity) {
        this.id_opportunity = id_opportunity;
    }

    public String getKey_hash() {
        return key_hash;
    }

    public void setKey_hash(String key_hash) {
        this.key_hash = key_hash;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public RealmList<OflineMediaOpportunity> getOflineMediaOpportunities() {
        return oflineMediaOpportunities;
    }

    public void setOflineMediaOpportunities(RealmList<OflineMediaOpportunity> oflineMediaOpportunities) {
        this.oflineMediaOpportunities = oflineMediaOpportunities;
    }

    public boolean isHas_sync() {
        return has_sync;
    }

    public void setHas_sync(boolean has_sync) {
        this.has_sync = has_sync;
    }
}
