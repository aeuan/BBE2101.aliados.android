package com.dacodes.bepensa.entities;

import com.google.gson.annotations.SerializedName;

public class ProfileUserEntity {

    @SerializedName("hidden_ranking")
    boolean hidden_ranking;

    public boolean isHidden_ranking() {
        return hidden_ranking;
    }

    public void setHidden_ranking(boolean hidden_ranking) {
        this.hidden_ranking = hidden_ranking;
    }
}
