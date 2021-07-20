package com.dacodes.bepensa.entities.feed;

import com.dacodes.bepensa.entities.EventosEntity;
import com.dacodes.bepensa.entities.promotion.PromotionDataEntity;
import com.dacodes.bepensa.entities.surveys.result.SurveyDataEntity;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FeedResponseEntity {

    @SerializedName("events_top")
    private List<EventosEntity> events_top;
    @SerializedName("promotions")
    private List<PromotionDataEntity> promotions;
    @SerializedName("events_near")
    private List<EventosEntity> events_near;
    @SerializedName("surveys")
    private List<SurveyDataEntity> surveys;

    private boolean new_events;
    private boolean new_promotions;
    private boolean new_surveys;

    public List<EventosEntity> getEvents_top() {
        return events_top;
    }

    public void setEvents_top(List<EventosEntity> events_top) {
        this.events_top = events_top;
    }

    public List<PromotionDataEntity> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<PromotionDataEntity> promotions) {
        this.promotions = promotions;
    }

    public List<EventosEntity> getEvents_near() {
        return events_near;
    }

    public void setEvents_near(List<EventosEntity> events_near) {
        this.events_near = events_near;
    }

    public List<SurveyDataEntity> getSurveys() {
        return surveys;
    }

    public void setSurveys(List<SurveyDataEntity> surveys) {
        this.surveys = surveys;
    }

    public boolean isNew_events() {
        return new_events;
    }

    public void setNew_events(boolean new_events) {
        this.new_events = new_events;
    }

    public boolean isNew_promotions() {
        return new_promotions;
    }

    public void setNew_promotions(boolean new_promotions) {
        this.new_promotions = new_promotions;
    }

    public boolean isNew_surveys() {
        return new_surveys;
    }

    public void setNew_surveys(boolean new_surveys) {
        this.new_surveys = new_surveys;
    }
}
