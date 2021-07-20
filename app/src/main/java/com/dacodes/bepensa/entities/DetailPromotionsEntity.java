package com.dacodes.bepensa.entities;

import com.google.gson.annotations.SerializedName;
public class DetailPromotionsEntity{

	@SerializedName("image")
	private String image;

	@SerializedName("lng")
	private String lng;

	@SerializedName("updated_at")
	private String updatedAt;

	@SerializedName("ends")
	private String ends;

	@SerializedName("name")
	private String name;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("id")
	private int id;

	@SerializedName("text")
	private String text;

	@SerializedName("starts")
	private String starts;

	@SerializedName("lat")
	private String lat;

	@SerializedName("status")
	private int status;

	public void setImage(String image){
		this.image = image;
	}

	public String getImage(){
		return image;
	}

	public void setLng(String lng){
		this.lng = lng;
	}

	public String getLng(){
		return lng;
	}

	public void setUpdatedAt(String updatedAt){
		this.updatedAt = updatedAt;
	}

	public String getUpdatedAt(){
		return updatedAt;
	}

	public void setEnds(String ends){
		this.ends = ends;
	}

	public String getEnds(){
		return ends;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setText(String text){
		this.text = text;
	}

	public String getText(){
		return text;
	}

	public void setStarts(String starts){
		this.starts = starts;
	}

	public String getStarts(){
		return starts;
	}

	public void setLat(String lat){
		this.lat = lat;
	}

	public String getLat(){
		return lat;
	}

	public void setStatus(int status){
		this.status = status;
	}

	public int getStatus(){
		return status;
	}
}