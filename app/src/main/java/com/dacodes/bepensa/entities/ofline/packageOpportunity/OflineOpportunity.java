package com.dacodes.bepensa.entities.ofline.packageOpportunity;

import io.realm.RealmList;
import io.realm.RealmObject;

public class OflineOpportunity extends RealmObject {

    int id;
    String name;
    RealmList<OflineOpportunityType> oflineOpportunityTypes;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RealmList<OflineOpportunityType> getOflineOpportunityTypes() {
        return oflineOpportunityTypes;
    }

    public void setOflineOpportunityTypes(RealmList<OflineOpportunityType> oflineOpportunityTypes) {
        this.oflineOpportunityTypes = oflineOpportunityTypes;
    }
}
