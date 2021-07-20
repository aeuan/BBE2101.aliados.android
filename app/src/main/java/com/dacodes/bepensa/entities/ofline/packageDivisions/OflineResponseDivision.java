package com.dacodes.bepensa.entities.ofline.packageDivisions;

import io.realm.RealmList;
import io.realm.RealmObject;

public class OflineResponseDivision extends RealmObject {

    RealmList<OflineDivisionEntity> divisionEntities;

    public RealmList<OflineDivisionEntity> getDivisionEntities() {
        return divisionEntities;
    }

    public void setDivisionEntities(RealmList<OflineDivisionEntity> divisionEntities) {
        this.divisionEntities = divisionEntities;
    }
}
