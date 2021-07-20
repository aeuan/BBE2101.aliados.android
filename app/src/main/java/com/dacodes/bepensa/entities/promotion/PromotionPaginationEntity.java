package com.dacodes.bepensa.entities.promotion;

import com.google.gson.annotations.SerializedName;

public class PromotionPaginationEntity {
    @SerializedName("per_page")
    private int perPage;

    @SerializedName("total_rows")
    private int totalRows;

    @SerializedName("links")
    private PromotionLinkEntity links;

    @SerializedName("current_page")
    private int currentPage;

    public void setPerPage(int perPage){
        this.perPage = perPage;
    }

    public void setTotalRows(int totalRows){
        this.totalRows = totalRows;
    }

    public void setLinks(PromotionLinkEntity links){
        this.links = links;
    }

    public void setCurrentPage(int currentPage){
        this.currentPage = currentPage;
    }

    public int getPerPage() {
        return perPage;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public PromotionLinkEntity getLinks() {
        return links;
    }

    public int getCurrentPage() {
        return currentPage;
    }
}
