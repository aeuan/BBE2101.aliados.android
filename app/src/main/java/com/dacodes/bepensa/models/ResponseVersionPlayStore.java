package com.dacodes.bepensa.models;
import com.google.gson.annotations.SerializedName;

public class ResponseVersionPlayStore{

	@SerializedName("version")
	private String version;

	public void setVersion(String version){
		this.version = version;
	}

	public String getVersion(){
		return version;
	}
}