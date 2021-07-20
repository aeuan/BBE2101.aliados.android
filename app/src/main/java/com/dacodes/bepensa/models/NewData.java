package com.dacodes.bepensa.models;

import com.google.gson.annotations.SerializedName;

public class NewData{

	@SerializedName("new_surveys")
	private int newSurveys;

	@SerializedName("new_releases")
	private int newReleases;

	@SerializedName("unread_messages")
	private int unreadMessages;

	@SerializedName("new_events")
	private int newEvents;

	@SerializedName("new_promotions")
	private int newPromotions;

	public void setNewSurveys(int newSurveys){
		this.newSurveys = newSurveys;
	}

	public int getNewSurveys(){
		return newSurveys;
	}

	public void setNewReleases(int newReleases){
		this.newReleases = newReleases;
	}

	public int getNewReleases(){
		return newReleases;
	}

	public void setUnreadMessages(int unreadMessages){
		this.unreadMessages = unreadMessages;
	}

	public int getUnreadMessages(){
		return unreadMessages;
	}

	public void setNewEvents(int newEvents){
		this.newEvents = newEvents;
	}

	public int getNewEvents(){
		return newEvents;
	}

	public void setNewPromotions(int newPromotions){
		this.newPromotions = newPromotions;
	}

	public int getNewPromotions(){
		return newPromotions;
	}
}