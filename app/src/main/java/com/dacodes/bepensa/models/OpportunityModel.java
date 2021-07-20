package com.dacodes.bepensa.models;

import com.dacodes.bepensa.entities.PackegeOpportunity.OpportunityEntity;
import com.dacodes.bepensa.entities.PaginationEntity;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class OpportunityModel {

    /**
     * data : [{"id":1,"collaborator":{"uuid":"77ad5ce7-f25d-4ffb-b623-52dcc6f4ee9a","first_name":"AARON","last_name":"AGUILAR MEZA","email":"aron@example.com","username":"8653","avatar_url":"https://bepensa-media.s3.amazonaws.com/media/empty.png"},"media":[{"id":1,"archive":"https://bepensa-media.s3.amazonaws.com/media/images/55184a3a7a1c41fa8287f7d2891611ec.jpg","type":1},{"id":2,"archive":"https://bepensa-media.s3.amazonaws.com/media/videos/b6bdf5eb220f4e9083f0fb2ed0fa1a1a.mp4","type":2}],"type":{"id":1,"name":"Reporte de neveras contaminadas","division":{"id":2,"name":"BEPENSA BEBIDAS"}},"description":"Description here","lat":"20.970090","lng":"-89.631920","points":10,"created_at":"2018-10-12T09:26:50.270431-05:00","updated_at":"2018-10-12T09:26:50.270468-05:00","division":{"id":1,"name":"BEPENSA SPIRITS","hash":"14a994b2ebf1cd00f19953295b7bc4e9","created_at":"2018-10-11T02:45:10.331335-05:00","updated_at":"2018-10-11T02:45:10.331382-05:00"},"brands":[{"id":1,"name":"Coca Cola","logo":"https://bepensa-media.s3.amazonaws.com/media/brands/c8b45cd854e04b158e8aaa108e064b77.png"}]}]
     * pagination : {"total_rows":1,"per_page":10,"current_page":1,"links":{"first":"https://bepensa.api.dacodes.mx/v1/opportunities/myrecords?page=1","last":"https://bepensa.api.dacodes.mx/v1/opportunities/myrecords?page=1","next":null,"prev":null}}
     */

    @SerializedName("pagination")
    private PaginationEntity pagination;
    @SerializedName("data")
    private ArrayList<OpportunityEntity> data;

    public PaginationEntity getPagination() {
        return pagination;
    }

    public void setPagination(PaginationEntity pagination) {
        this.pagination = pagination;
    }

    public ArrayList<OpportunityEntity> getData() {
        return data;
    }

    public void setData(ArrayList<OpportunityEntity> data) {
        this.data = data;
    }

}
