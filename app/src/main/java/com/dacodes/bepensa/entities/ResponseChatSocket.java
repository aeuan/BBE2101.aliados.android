package com.dacodes.bepensa.entities;

import com.google.gson.annotations.SerializedName;

public class ResponseChatSocket{

	@SerializedName("type")
	private String type;
	@SerializedName("opportunity")
	private DataChatSocket dataChatSocket;

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	public DataChatSocket getDataChatSocket() {
		return dataChatSocket;
	}

	public void setDataChatSocket(DataChatSocket dataChatSocket) {
		this.dataChatSocket = dataChatSocket;
	}
}