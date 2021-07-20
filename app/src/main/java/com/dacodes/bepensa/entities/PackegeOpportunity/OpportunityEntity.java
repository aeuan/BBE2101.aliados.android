package com.dacodes.bepensa.entities.PackegeOpportunity;

import com.dacodes.bepensa.utils.FindDateDifference;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class OpportunityEntity implements Serializable {
    /**
     * id : 1
     * collaborator : {"uuid":"77ad5ce7-f25d-4ffb-b623-52dcc6f4ee9a","first_name":"AARON","last_name":"AGUILAR MEZA","email":"aron@example.com","username":"8653","avatar_url":"https://bepensa-media.s3.amazonaws.com/media/empty.png"}
     * media : [{"id":1,"archive":"https://bepensa-media.s3.amazonaws.com/media/images/55184a3a7a1c41fa8287f7d2891611ec.jpg","type":1},{"id":2,"archive":"https://bepensa-media.s3.amazonaws.com/media/videos/b6bdf5eb220f4e9083f0fb2ed0fa1a1a.mp4","type":2}]
     * type : {"id":1,"name":"Reporte de neveras contaminadas","division":{"id":2,"name":"BEPENSA BEBIDAS"}}
     * description : Description here
     * lat : 20.970090
     * lng : -89.631920
     * points : 10
     * created_at : 2018-10-12T09:26:50.270431-05:00
     * updated_at : 2018-10-12T09:26:50.270468-05:00
     * division : {"id":1,"name":"BEPENSA SPIRITS","hash":"14a994b2ebf1cd00f19953295b7bc4e9","created_at":"2018-10-11T02:45:10.331335-05:00","updated_at":"2018-10-11T02:45:10.331382-05:00"}
     * brands : [{"id":1,"name":"Coca Cola","logo":"https://bepensa-media.s3.amazonaws.com/media/brands/c8b45cd854e04b158e8aaa108e064b77.png"}]
     */

    @SerializedName("id")
    private int id;
    @SerializedName("collaborator")
    private CollaboratorEntity collaborator;
    @SerializedName("type")
    private TypeEntity type;
    @SerializedName("description")
    private String description;
    @SerializedName("lat")
    private String lat;
    @SerializedName("lng")
    private String lng;
    @SerializedName("points")
    private int points;
    @SerializedName("unread_messages")
    private int unread_messages;
    @SerializedName("status")
    private int status;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("division")
    private DivisionEntity division;
    @SerializedName("media")
    private List<MediaEntity> media;
    @SerializedName("brands")
    private List<BrandEntity> brands;

    @SerializedName("has_pending_chat")
    private boolean has_pending_chat;

    public boolean isHas_pending_chat() {
        return has_pending_chat;
    }

    public void setHas_pending_chat(boolean has_pending_chat) {
        this.has_pending_chat = has_pending_chat;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CollaboratorEntity getCollaborator() {
        return collaborator;
    }

    public void setCollaborator(CollaboratorEntity collaborator) {
        this.collaborator = collaborator;
    }

    public TypeEntity getType() {
        return type;
    }

    public void setType(TypeEntity type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getFormattedCreatedAt() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
        Date date = null;
        FindDateDifference findDateDifference = null;
        String tiempo;
        try {
            String upToNCharacters = createdAt.substring(0, Math.min(createdAt.length(), 19));
            date = df.parse(upToNCharacters);
            TimeZone tz = TimeZone.getDefault();
            SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH);
            destFormat.setTimeZone(tz);
            String startDate = destFormat.format(date);
            date = destFormat.parse(startDate);
            Date currentDate = new Date();
            findDateDifference = new FindDateDifference(date, currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(findDateDifference!=null) {
            tiempo = findDateDifference.calculateTimeSinceDate();
        }else{
            tiempo = createdAt;
        }
        return tiempo;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public DivisionEntity getDivision() {
        return division;
    }

    public void setDivision(DivisionEntity division) {
        this.division = division;
    }

    public List<MediaEntity> getMedia() {
        return media;
    }

    public void setMedia(List<MediaEntity> media) {
        this.media = media;
    }

    public List<BrandEntity> getBrands() {
        return brands;
    }

    public void setBrands(List<BrandEntity> brands) {
        this.brands = brands;
    }

    public int getUnread_messages() {
        return unread_messages;
    }

    public void setUnread_messages(int unread_messages) {
        this.unread_messages = unread_messages;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
