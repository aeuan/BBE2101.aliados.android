package com.dacodes.bepensa.entities.awardsRedeem;


import com.dacodes.bepensa.models.Division;
import com.google.gson.annotations.SerializedName;

public class Collaborator{

	@SerializedName("division")
	private Division division;

	@SerializedName("avatar_url")
	private String avatarUrl;

	@SerializedName("last_conection")
	private String lastConection;

	@SerializedName("last_name")
	private String lastName;

	@SerializedName("rank")
	private int rank;

	@SerializedName("points_available")
	private int pointsAvailable;

	@SerializedName("uuid")
	private String uuid;

	@SerializedName("first_name")
	private String firstName;

	@SerializedName("email")
	private String email;

	@SerializedName("username")
	private String username;

	@SerializedName("points")
	private int points;

	public void setDivision(Division division){
		this.division = division;
	}

	public Division getDivision(){
		return division;
	}

	public void setAvatarUrl(String avatarUrl){
		this.avatarUrl = avatarUrl;
	}

	public String getAvatarUrl(){
		return avatarUrl;
	}

	public void setLastConection(String lastConection){
		this.lastConection = lastConection;
	}

	public String getLastConection(){
		return lastConection;
	}

	public void setLastName(String lastName){
		this.lastName = lastName;
	}

	public String getLastName(){
		return lastName;
	}

	public void setRank(int rank){
		this.rank = rank;
	}

	public int getRank(){
		return rank;
	}

	public void setPointsAvailable(int pointsAvailable){
		this.pointsAvailable = pointsAvailable;
	}

	public int getPointsAvailable(){
		return pointsAvailable;
	}

	public void setUuid(String uuid){
		this.uuid = uuid;
	}

	public String getUuid(){
		return uuid;
	}

	public void setFirstName(String firstName){
		this.firstName = firstName;
	}

	public String getFirstName(){
		return firstName;
	}

	public void setEmail(String email){
		this.email = email;
	}

	public String getEmail(){
		return email;
	}

	public void setUsername(String username){
		this.username = username;
	}

	public String getUsername(){
		return username;
	}

	public void setPoints(int points){
		this.points = points;
	}

	public int getPoints(){
		return points;
	}
}