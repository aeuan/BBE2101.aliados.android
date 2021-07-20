package com.dacodes.bepensa.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.models.NotificationPayloadModel;
import com.dacodes.bepensa.push_notifications.MyFirebaseMessagingService;
import com.dacodes.bepensa.service.DownloadJsonSurvey;
import com.dacodes.bepensa.utils.ValidateNetwork;

import java.util.HashMap;

import butterknife.ButterKnife;

import static com.dacodes.bepensa.push_notifications.MyFirebaseMessagingService.CHAT_INTENT;
import static com.dacodes.bepensa.push_notifications.MyFirebaseMessagingService.EVENT_DETAIL_INTENT;
import static com.dacodes.bepensa.push_notifications.MyFirebaseMessagingService.NOTIFICATION_INTENT;
import static com.dacodes.bepensa.push_notifications.MyFirebaseMessagingService.OPPORTUNITY_DETAIL_INTENT;
import static com.dacodes.bepensa.push_notifications.MyFirebaseMessagingService.PROMOTION_DETAIL_INTENT;
import static com.dacodes.bepensa.push_notifications.MyFirebaseMessagingService.RELEASE_DETAIL_INTENT;
import static com.dacodes.bepensa.push_notifications.MyFirebaseMessagingService.SURVEY_DETAIL_INTENT;

public class SplashScreen extends AppCompatActivity {

    private static final String LOG_TAG = SplashScreen.class.getSimpleName();
    /**
     * Duration of wait
     **/

    private Context mContext;
    AppController app;
    private String TAG = SplashScreen.class.getSimpleName();
    //RetrofitWebServices rws;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        //overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
        app = (AppController) getApplication();

        if(ValidateNetwork.getNetwork(mContext) && app.getLoggedIn()){
            Intent jsonSurvey =new Intent(this, DownloadJsonSurvey.class);
            startService(jsonSurvey);
        }

        if (app.getRefresh()==0){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            preferences.edit().remove("pointAvailable").commit();
            app.setRefresh(1);
        }

        if (app.getLoggedIn()) {
            Intent logInIntent = new Intent(mContext, MainActivity.class);
            startActivity(logInIntent);
            finish();
        } else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }

      /*  Intent service = new Intent(mContext,DownloadJsonFiles.class);
        startService(service);
*/
        if (getIntent().getExtras() != null) {
            HashMap<String, String> data = new HashMap<>();

            for (String keyNotifications : getIntent().getExtras().keySet()) {
                if(keyNotifications.contentEquals("notification")) {
                    for (String key : getIntent().getExtras().keySet()) {
                        if(key.contentEquals("chat_notice")){
                            data.put("chat_notice", getIntent().getExtras().getString("chat_notice"));
                            Intent intent = new Intent(this, MainActivity.class);
                            NotificationPayloadModel.ChatNoticeBean chatNoticeBean =
                                    MyFirebaseMessagingService.getChatNotificationPayload(data);
                            intent.putExtra("opportunityId", chatNoticeBean.getOpportunityId());
                            intent.putExtra("intentType", CHAT_INTENT);
                            intent.putExtra("networkLocal",1);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                           break;
                        } else if(key.contentEquals("opportunity")) {
                            data.put("opportunity", getIntent().getExtras().getString("opportunity"));
                            Intent intent = new Intent(this, MainActivity.class);
                            NotificationPayloadModel.IntentBean intentBean =
                                    MyFirebaseMessagingService.getNotificationPayload(data, "opportunity");
                            intent.putExtra("opportunityId", intentBean.getId());
                            intent.putExtra("intentType", OPPORTUNITY_DETAIL_INTENT);
                            intent.putExtra("networkLocal",1);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            break;
                        }else if(key.contentEquals("event")) {
                            data.put("event", getIntent().getExtras().getString("event"));
                            Intent intent = new Intent(this, MainActivity.class);
                            NotificationPayloadModel.EventNoticeBean intentBean =
                                    MyFirebaseMessagingService.getEventNotificationPayload(data);
                            intent.putExtra("eventId", intentBean.getId());
                            intent.putExtra("intentType", EVENT_DETAIL_INTENT);
                            Log.e("id",""+intentBean.getId());
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            break;
                        }else if(key.contentEquals("release")) {
                            data.put("release", getIntent().getExtras().getString("release"));
                            Intent intent = new Intent(this, MainActivity.class);
                            NotificationPayloadModel.IntentBean intentBean =
                                    MyFirebaseMessagingService.getNotificationPayload(data, "release");
                            intent.putExtra("releaseId", intentBean.getId());
                            intent.putExtra("intentType", RELEASE_DETAIL_INTENT);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            break;
                        }else if(key.contentEquals("survey")) {
                            data.put("survey", getIntent().getExtras().getString("survey"));
                            Intent intent = new Intent(this, MainActivity.class);
                            NotificationPayloadModel.IntentBean intentBean =
                                    MyFirebaseMessagingService.getNotificationPayload(data,"survey");
                            intent.putExtra("surveyId", intentBean.getId());
                            intent.putExtra("intentType", SURVEY_DETAIL_INTENT);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            break;
                        }else if(key.contentEquals("promotion")) {
                            data.put("promotion", getIntent().getExtras().getString("promotion"));
                            Intent intent = new Intent(this, MainActivity.class);
                            NotificationPayloadModel.IntentBean intentBean =
                                    MyFirebaseMessagingService.getNotificationPayload(data, "promotion");
                            intent.putExtra("promotionId", intentBean.getId());
                            intent.putExtra("intentType", PROMOTION_DETAIL_INTENT);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            break;
                        }else{
                            Intent intent = new Intent(this, MainActivity.class);
                            intent.putExtra("intentType", NOTIFICATION_INTENT);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                          //  break;
                        }
                }
            }

            }
            }
    }


}