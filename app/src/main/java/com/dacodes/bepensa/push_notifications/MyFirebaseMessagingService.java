package com.dacodes.bepensa.push_notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.dacodes.bepensa.R;
import com.dacodes.bepensa.activities.MainActivity;
import com.dacodes.bepensa.entities.PushNotificationEntity;
import com.dacodes.bepensa.models.NotificationPayloadModel;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Map;

import static android.os.Build.VERSION_CODES.O;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String LOG_TAG = MyFirebaseMessagingService.class.getSimpleName();
    public static final int CHAT_INTENT = 1;
    public static final int OPPORTUNITY_DETAIL_INTENT = 2;
    public static final int EVENT_DETAIL_INTENT = 3;
    public static final int RELEASE_DETAIL_INTENT = 4;
    public static final int NOTIFICATION_INTENT = 5;
    public static final int SURVEY_DETAIL_INTENT= 6;
    public static final int PROMOTION_DETAIL_INTENT = 7;


    NotificationManager notificationManager;
    private NotificationManager mNotificationManager;
    private NotificationChannel mChannel;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            setupChannels();
        }
        if (remoteMessage.getData().size() > 0) {
            sendNotification(remoteMessage.getData());
        }
    }

    @RequiresApi(api = O)
    private void setupChannels(){
        NotificationChannel notificationChannel = notificationManager.getNotificationChannel(getString(R.string.channel_one_body));
        if(notificationChannel==null){
            CharSequence adminChannelName = getString(R.string.channel_one_body);
            String adminChannelDescription = getString(R.string.channel_one_body);

            NotificationChannel adminChannel;
            adminChannel = new NotificationChannel(getString(R.string.channel_one_body),
                    adminChannelName, NotificationManager.IMPORTANCE_HIGH);
            adminChannel.setDescription(adminChannelDescription);
            adminChannel.enableLights(true);
            adminChannel.setLightColor(Color.RED);
            adminChannel.enableVibration(true);
            adminChannel.setShowBadge(true);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(adminChannel);
            }
        }else{
            Log.d("Channel", notificationChannel.getName().toString());
        }
    }

    private void sendNotification(Map<String, String> data) {
        PendingIntent pendingIntent = createPendingIntent(this, data, false);
        String message = createNotificationMessage(data);
        String title = createNotificationTitle(data);
        Log.e("datos",""+data.toString());

        String channelId = getString(R.string.channel_one_body);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.icon_pr)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS|Notification.DEFAULT_VIBRATE)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setShowBadge(true);
            notificationManager.createNotificationChannel(channel);
        }

        try {
            notificationManager.notify(0, notificationBuilder.build());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String createNotificationMessage(Map<String, String > data){
        String notification = data.get("notification");
        Gson gson = new Gson();
        PushNotificationEntity pushNotificationEntity = gson.fromJson(notification, PushNotificationEntity.class);
        return pushNotificationEntity.getBody();
    }

    private String createNotificationTitle(Map<String, String > data){
        String notification = data.get("notification");
        Gson gson = new Gson();
        PushNotificationEntity pushNotificationEntity = gson.fromJson(notification, PushNotificationEntity.class);
        return pushNotificationEntity.getTitle();
    }

    public static NotificationPayloadModel.ChatNoticeBean getChatNotificationPayload(Map<String, String > data){
        String notification = data.get("chat_notice");
        Gson gson = new Gson();
        return gson.fromJson(notification, NotificationPayloadModel.ChatNoticeBean.class);
    }

    public static NotificationPayloadModel.OpportunityNoticeBean getOpportunityNotificationPayload(Map<String, String > data){
        String notification = data.get("opportunity");
        Gson gson = new Gson();
        return gson.fromJson(notification, NotificationPayloadModel.OpportunityNoticeBean.class);
    }

    public static NotificationPayloadModel.RelaseNoticeBean getRelaseNotificationPayload(Map<String, String > data){
        String notification = data.get("release");
        Gson gson = new Gson();
        return gson.fromJson(notification, NotificationPayloadModel.RelaseNoticeBean.class);
    }

    public static NotificationPayloadModel.SurveyNoticeBean getSurveyNotificationPayload(Map<String, String > data){
        String notification = data.get("survey");
        Gson gson = new Gson();
        return gson.fromJson(notification, NotificationPayloadModel.SurveyNoticeBean.class);
    }

    public static NotificationPayloadModel.PromotionNoticeBean getPromotionNotificationPayload(Map<String, String > data){
        String notification = data.get("promotion");
        Gson gson = new Gson();
        return gson.fromJson(notification, NotificationPayloadModel.PromotionNoticeBean.class);
    }

    public static NotificationPayloadModel.EventNoticeBean getEventNotificationPayload(Map<String, String > data){
        String notification = data.get("event");
        Gson gson = new Gson();
        return gson.fromJson(notification, NotificationPayloadModel.EventNoticeBean.class);
    }

    public static NotificationPayloadModel.IntentBean getNotificationPayload(Map<String, String > data, String key){
        String notification = data.get(key);
        Gson gson = new Gson();
        return gson.fromJson(notification, NotificationPayloadModel.IntentBean.class);
    }

    public PendingIntent createPendingIntent(Context mContext, Map<String, String> data, boolean current){
        Intent intent = null;
        PendingIntent pendingIntent;
        NotificationPayloadModel.ChatNoticeBean chatNoticeBean;
        NotificationPayloadModel.EventNoticeBean eventNoticeBean;
        NotificationPayloadModel.OpportunityNoticeBean opportunityNoticeBean;
        NotificationPayloadModel.PromotionNoticeBean promotionNoticeBean;
        NotificationPayloadModel.SurveyNoticeBean surveyNoticeBean;
        NotificationPayloadModel.RelaseNoticeBean relaseNoticeBean;

        //# Push del chat de una oportunidad, Deberá llevar a la conversación de la oportunidad
        if(data.containsKey("chat_notice")){
            /*val i = Intent(Home.PUSH_NOTIFICATION)
            if (remoteMessage?.data!=null)
            i.putExtra(ORDER_REQUEST,remoteMessage.data["order_id"]!!.toInt*/
            chatNoticeBean = getChatNotificationPayload(data);
            intent = new Intent(mContext, MainActivity.class);
            intent.putExtra("intentType", CHAT_INTENT);
            Log.e("chat_notice","detalle comunicado");
            try {
                intent.putExtra("opportunityId", chatNoticeBean.getOpportunityId());
                /*Intent broadcast = new Intent(BuildConfig.APPLICATION_ID+"chat_notice");
                broadcast.putExtra("id_chat",chatNoticeBean.getOpportunityId());
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);*/
            }catch (Exception e){
              e.printStackTrace();
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        //# Push al cerrar una oportunidad, Deberá llevar al detalle de la oportunidad
        else if (data.containsKey("opportunity")){
            opportunityNoticeBean = getOpportunityNotificationPayload(data);
            intent = new Intent(mContext, MainActivity.class);
            intent.putExtra("intentType", OPPORTUNITY_DETAIL_INTENT);
            Log.e("oportunity","detalle comunicado");
            try {
                intent.putExtra("opportunityId", opportunityNoticeBean.getId());
            }catch (Exception e){
                e.printStackTrace();
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        //# Push cuando me invitan a un evento, Deberá mostrar el detalle del evento
        else if (data.containsKey("event")){
            eventNoticeBean = getEventNotificationPayload(data);
            intent = new Intent(mContext, MainActivity.class);
            intent.putExtra("intentType", EVENT_DETAIL_INTENT);
            Log.e("event","detalle comunicado");
            try {
                intent.putExtra("eventId", eventNoticeBean.getId());
            }catch (Exception e){
                e.printStackTrace();
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        //# Push cuando recibo un comunicado, deberá mostrar el detalle del comunicado
        else if (data.containsKey("release")){
            Log.e("comunicado","detalle comunicado");
            relaseNoticeBean = getRelaseNotificationPayload(data);
            intent = new Intent(mContext, MainActivity.class);
            intent.putExtra("intentType", RELEASE_DETAIL_INTENT);
            try {
                intent.putExtra("releaseId", relaseNoticeBean.getId());
            }catch (Exception e){
                e.printStackTrace();
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        //#Push cuando se recibe una nueva encuesta,debera ir al detalle de la encuesta
        else if (data.containsKey("survey")){
            surveyNoticeBean = getSurveyNotificationPayload(data);
            intent = new Intent(mContext, MainActivity.class);
            intent.putExtra("intentType", SURVEY_DETAIL_INTENT);
            Log.e("survey","detalle comunicado");
            try {
                intent.putExtra("surveyId", surveyNoticeBean.getId());
            }catch (Exception e){
                e.printStackTrace();
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        //#Push cuando se recibe una nueva promocion,debera ir al detalle de la promocion
        else if (data.containsKey("promotion")){
            promotionNoticeBean = getPromotionNotificationPayload(data);
            intent = new Intent(mContext, MainActivity.class);
            intent.putExtra("intentType", PROMOTION_DETAIL_INTENT);
            Log.e("promotion","detalle comunicado");
            try {
                intent.putExtra("promotionId", promotionNoticeBean.getId());
            }catch (Exception e){
                e.printStackTrace();
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        //Pantalla de notificaciones
        else{
            Log.e("notification","detalle comunicado");
            intent = new Intent(mContext, MainActivity.class);
            intent.putExtra("intentType", NOTIFICATION_INTENT);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        pendingIntent = PendingIntent.getActivity(mContext, 0 , intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return pendingIntent;
    }
}