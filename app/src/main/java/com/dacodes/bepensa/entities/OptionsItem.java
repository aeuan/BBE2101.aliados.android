package com.dacodes.bepensa.entities;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OptionsItem implements Serializable {
	@SerializedName("ordered")
	private int ordered;
	@SerializedName("name")
	private String name;
	@SerializedName("id")
	private int id;

	public void setOrdered(int ordered){
		this.ordered = ordered;
	}

	public int getOrdered(){
		return ordered;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}
}