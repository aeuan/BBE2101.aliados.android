package com.dacodes.bepensa.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PaginationEntity implements Serializable {
    /**
     * total_rows : 1
     * per_page : 10
     * current_page : 1
     * links : {"first":"https://bepensa.api.dacodes.mx/v1/opportunities/myrecords?page=1","last":"https://bepensa.api.dacodes.mx/v1/opportunities/myrecords?page=1","next":null,"prev":null}
     */

    @SerializedName("total_rows")
    private int totalRows;
    @SerializedName("per_page")
    private int perPage;
    @SerializedName("current_page")
    private int currentPage;
    @SerializedName("links")
    private LinksBean links;

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public int getPerPage() {
        return perPage;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public LinksBean getLinks() {
        return links;
    }

    public void setLinks(LinksBean links) {
        this.links = links;
    }

    public static class LinksBean implements Serializable {
        /**
         * first : https://bepensa.api.dacodes.mx/v1/opportunities/myrecords?page=1
         * last : https://bepensa.api.dacodes.mx/v1/opportunities/myrecords?page=1
         * next : null
         * prev : null
         */

        @SerializedName("first")
        private String first;
        @SerializedName("last")
        private String last;
        @SerializedName("next")
        private Object next;
        @SerializedName("prev")
        private Object prev;

        public String getFirst() {
            return first;
        }

        public void setFirst(String first) {
            this.first = first;
        }

        public String getLast() {
            return last;
        }

        public void setLast(String last) {
            this.last = last;
        }

        public Object getNext() {
            return next;
        }

        public void setNext(Object next) {
            this.next = next;
        }

        public Object getPrev() {
            return prev;
        }

        public void setPrev(Object prev) {
            this.prev = prev;
        }
    }
}