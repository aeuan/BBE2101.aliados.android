package com.dacodes.bepensa.models;

import com.dacodes.bepensa.entities.Chat.ChatEntity;
import com.dacodes.bepensa.entities.PaginationEntity;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChatResponseModel {

    /**
     * data : [{"id":103,"user":{"uuid":"fd06b0cc-8081-476a-9ef3-9482eed2687d","first_name":"Abner","last_name":"Grajales","email":"be_93edit@example.com","username":"administrador","role":1,"division":{"id":2,"name":"BEPENSA BEBIDAS","status":1},"avatar_url":"https://bepensa-media.s3.amazonaws.com/media/user-placeholder.jpg"},"text":"mandando desde el panel","created_at":"2018-11-13T02:41:36.577403-06:00"},{"id":102,"user":{"uuid":"fd06b0cc-8081-476a-9ef3-9482eed2687d","first_name":"Abner","last_name":"Grajales","email":"be_93edit@example.com","username":"administrador","role":1,"division":{"id":2,"name":"BEPENSA BEBIDAS","status":1},"avatar_url":"https://bepensa-media.s3.amazonaws.com/media/user-placeholder.jpg"},"text":"probando el chat","created_at":"2018-11-13T02:41:27.305516-06:00"},{"id":101,"user":{"uuid":"4b7e1981-77c1-4e4e-a7cc-d5b67e7b2b45","first_name":"ZULEMY ASTRID","last_name":"BARCELO ZALDIVAR","email":"jorge@correo.com","username":"32040","role":3,"division":{"id":2,"name":"BEPENSA BEBIDAS","status":1},"avatar_url":"https://bepensa-media.s3.amazonaws.com/media/user-placeholder.jpg"},"text":"Test","created_at":"2018-11-13T02:40:45.451571-06:00"},{"id":100,"user":{"uuid":"4b7e1981-77c1-4e4e-a7cc-d5b67e7b2b45","first_name":"ZULEMY ASTRID","last_name":"BARCELO ZALDIVAR","email":"jorge@correo.com","username":"32040","role":3,"division":{"id":2,"name":"BEPENSA BEBIDAS","status":1},"avatar_url":"https://bepensa-media.s3.amazonaws.com/media/user-placeholder.jpg"},"text":"Es una prueba ","created_at":"2018-11-13T02:40:14.943999-06:00"},{"id":99,"user":{"uuid":"4b7e1981-77c1-4e4e-a7cc-d5b67e7b2b45","first_name":"ZULEMY ASTRID","last_name":"BARCELO ZALDIVAR","email":"jorge@correo.com","username":"32040","role":3,"division":{"id":2,"name":"BEPENSA BEBIDAS","status":1},"avatar_url":"https://bepensa-media.s3.amazonaws.com/media/user-placeholder.jpg"},"text":"Va","created_at":"2018-11-13T02:39:43.174083-06:00"},{"id":98,"user":{"uuid":"4b7e1981-77c1-4e4e-a7cc-d5b67e7b2b45","first_name":"ZULEMY ASTRID","last_name":"BARCELO ZALDIVAR","email":"jorge@correo.com","username":"32040","role":3,"division":{"id":2,"name":"BEPENSA BEBIDAS","status":1},"avatar_url":"https://bepensa-media.s3.amazonaws.com/media/user-placeholder.jpg"},"text":"Ok","created_at":"2018-11-13T02:39:34.787199-06:00"},{"id":97,"user":{"uuid":"4b7e1981-77c1-4e4e-a7cc-d5b67e7b2b45","first_name":"ZULEMY ASTRID","last_name":"BARCELO ZALDIVAR","email":"jorge@correo.com","username":"32040","role":3,"division":{"id":2,"name":"BEPENSA BEBIDAS","status":1},"avatar_url":"https://bepensa-media.s3.amazonaws.com/media/user-placeholder.jpg"},"text":"Test","created_at":"2018-11-13T02:39:28.961189-06:00"},{"id":96,"user":{"uuid":"fd06b0cc-8081-476a-9ef3-9482eed2687d","first_name":"Abner","last_name":"Grajales","email":"be_93edit@example.com","username":"administrador","role":1,"division":{"id":2,"name":"BEPENSA BEBIDAS","status":1},"avatar_url":"https://bepensa-media.s3.amazonaws.com/media/user-placeholder.jpg"},"text":"Prueba","created_at":"2018-11-13T02:38:40.748134-06:00"}]
     * pagination : {"total_rows":8,"per_page":10,"current_page":1,"links":{"first":"https://bepensa.api.dacodes.mx/v1/opportunities/conversations/84?page=1","last":"https://bepensa.api.dacodes.mx/v1/opportunities/conversations/84?page=1","next":"","prev":""}}
     */

    @SerializedName("pagination")
    private PaginationEntity pagination;
    @SerializedName("data")
    private List<ChatEntity> data;

    public PaginationEntity getPagination() {
        return pagination;
    }

    public void setPagination(PaginationEntity pagination) {
        this.pagination = pagination;
    }

    public List<ChatEntity> getData() {
        return data;
    }

    public void setData(List<ChatEntity> data) {
        this.data = data;
    }

}
