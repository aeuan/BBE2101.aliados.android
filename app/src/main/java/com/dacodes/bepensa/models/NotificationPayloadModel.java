package com.dacodes.bepensa.models;

import com.dacodes.bepensa.entities.NotificationPayloadEntity;
import com.google.gson.annotations.SerializedName;

public class NotificationPayloadModel {


    /**
     * notification : {"body":"Prueba 7 de sockets y push notifications","title":"Abner Grajales"}
     * chat_notice : {"opportunity_id":31}
     */

    @SerializedName("notification")
    private NotificationPayloadEntity notification;
    @SerializedName("chat_notice")
    private ChatNoticeBean chatNotice;
    @SerializedName("opportunity")
    private IntentBean opportunity;
    @SerializedName("event")
    private IntentBean event;
    @SerializedName("release")
    private IntentBean release;

    public NotificationPayloadEntity getNotification() {
        return notification;
    }

    public void setNotification(NotificationPayloadEntity notification) {
        this.notification = notification;
    }

    public ChatNoticeBean getChatNotice() {
        return chatNotice;
    }

    public void setChatNotice(ChatNoticeBean chatNotice) {
        this.chatNotice = chatNotice;
    }

    public IntentBean getOpportunity() {
        return opportunity;
    }

    public void setOpportunity(IntentBean opportunity) {
        this.opportunity = opportunity;
    }

    public IntentBean getEvent() {
        return event;
    }

    public void setEvent(IntentBean event) {
        this.event = event;
    }

    public IntentBean getRelease() {
        return release;
    }

    public void setRelease(IntentBean release) {
        this.release = release;
    }


    public static class ChatNoticeBean {
        /**
         * opportunity_id : 31
         */

        @SerializedName("opportunity_id")
        private int opportunityId;

        public int getOpportunityId() {
            return opportunityId;
        }

        public void setOpportunityId(int opportunityId) {
            this.opportunityId = opportunityId;
        }
    }

    public static class EventNoticeBean {
        /**
         * opportunity_id : 31
         */

        @SerializedName("id")
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public static class OpportunityNoticeBean {
        /**
         * opportunity_id : 31
         */

        @SerializedName("id")
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public static class RelaseNoticeBean {
        /**
         * opportunity_id : 31
         */

        @SerializedName("id")
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public static class SurveyNoticeBean {
        /**
         * opportunity_id : 31
         */

        @SerializedName("id")
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public static class PromotionNoticeBean {
        /**
         * opportunity_id : 31
         */

        @SerializedName("id")
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
    public static class IntentBean {
        /**
         * id : 31
         */

        @SerializedName("id")
        private int id;

        public int getId() {
            return id;
        }

        /*public void setId(int id) {
            this.id = id;
        }*/
    }



}
