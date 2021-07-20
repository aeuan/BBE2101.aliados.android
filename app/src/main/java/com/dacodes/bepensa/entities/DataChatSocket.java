package com.dacodes.bepensa.entities;

import com.google.gson.annotations.SerializedName;

public class DataChatSocket{

	@SerializedName("sender_uud")
	private String senderUud;

	@SerializedName("has_unread_sender")
	private int hasUnreadSender;

	@SerializedName("has_unread_collaborator")
	private int hasUnreadCollaborator;

	@SerializedName("id")
	private int id;

	@SerializedName("collaborator_uuid")
	private String collaboratorUuid;

	@SerializedName("status")
	private int status;

	public void setSenderUud(String senderUud){
		this.senderUud = senderUud;
	}

	public String getSenderUud(){
		return senderUud;
	}

	public void setHasUnreadSender(int hasUnreadSender){
		this.hasUnreadSender = hasUnreadSender;
	}

	public int getHasUnreadSender(){
		return hasUnreadSender;
	}

	public void setHasUnreadCollaborator(int hasUnreadCollaborator){
		this.hasUnreadCollaborator = hasUnreadCollaborator;
	}

	public int getHasUnreadCollaborator(){
		return hasUnreadCollaborator;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setCollaboratorUuid(String collaboratorUuid){
		this.collaboratorUuid = collaboratorUuid;
	}

	public String getCollaboratorUuid(){
		return collaboratorUuid;
	}

	public void setStatus(int status){
		this.status = status;
	}

	public int getStatus(){
		return status;
	}
}