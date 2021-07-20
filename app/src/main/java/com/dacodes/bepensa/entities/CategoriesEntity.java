package com.dacodes.bepensa.entities;

import com.google.gson.annotations.SerializedName;

public class CategoriesEntity{

	@SerializedName("name")
	private String name;

	@SerializedName("id")
	private int id;

	private boolean checked;
	private boolean enable;

	public CategoriesEntity() {
	}

	public CategoriesEntity(String name, int id, boolean checked) {
		this.name = name;
		this.id = id;
		this.checked = checked;
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

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}
}