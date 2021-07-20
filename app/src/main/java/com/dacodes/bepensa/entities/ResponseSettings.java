package com.dacodes.bepensa.entities;

import com.google.gson.annotations.SerializedName;

public class ResponseSettings{

	@SerializedName("whatsApp")
	private String whatsApp;
	@SerializedName("google_form_android")
	private String googleFormAndroid;

	public void setWhatsApp(String whatsApp){
		this.whatsApp = whatsApp;
	}

	public String getWhatsApp(){
		return whatsApp;
	}

	public String getGoogleFormAndroid() {
		return googleFormAndroid;
	}

	public void setGoogleFormAndroid(String googleFormAndroid) {
		this.googleFormAndroid = googleFormAndroid;
	}
}