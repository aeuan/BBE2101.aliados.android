package com.dacodes.bepensa.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.DeviceId;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.activities.Contact;
import com.dacodes.bepensa.activities.LoginActivity;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.ProfileUserEntity;
import com.dacodes.bepensa.utils.AlertDIalogLogout;
import com.dacodes.bepensa.utils.DialogEditPassword;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.dacodes.bepensa.api.RetrofitWebServices.DELETE_DEVICE;
import static com.dacodes.bepensa.api.RetrofitWebServices.GET_STATUS_PROFILE;
import static com.dacodes.bepensa.api.RetrofitWebServices.POST_PASSWORD_EDIT;
import static com.dacodes.bepensa.api.RetrofitWebServices.PUT_RANKING_PRIVACY;
import static com.dacodes.bepensa.api.RetrofitWebServices.REGISTER_PUSH;

/**
 * Created by Eric on 09/03/17.
 *
 */

public class ConfigurationFragment extends Fragment implements RetrofitWebServices.DefaultResponseListeners
        ,CompoundButton.OnCheckedChangeListener,AlertDIalogLogout.logout,AlertDIalogLogout.opportunity,AlertDIalogLogout.survey{
    private Context mContext;
    AppController app;
    RetrofitWebServices retrofitWebServices;
    private static final String LOG_TAG = LoginActivity.class.getSimpleName();
    @BindView(R.id.notification) SwitchCompat notificacion;
    @BindView(R.id.md_visible) SwitchCompat md_visible;
    @BindView(R.id.reset_parword)Button password_reset;
    @BindView(R.id.number_version_tv)TextView number_version_tv;
    @BindView(R.id.name_version_tv)TextView name_version_tv;
    Realm realm;
    int status_survey=0;
    int status_opportunities=0;
    int status_logout=0;
    private Activity mActivity;


    @OnClick({R.id.terms_bt,R.id.terms_btn})
    public void terms_bt(View view) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.bepensa.com/aviso-de-privacidad/"));
            startActivity(i);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @OnClick(R.id.log_out_bt)
    public void log_out_bt(View view) {
        //logoutAction();
        logout();

    }

    public void logout(){
        AlertDIalogLogout alertDIalogLogout=new AlertDIalogLogout(getActivity(),app,this,this,this);
        alertDIalogLogout.checkDataBase();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @OnClick(R.id.tv_contact)
    public void tv_contact(View view){
        Intent intent = new Intent(mContext,Contact.class);
        startActivity(intent);
    }

    @OnClick(R.id.reset_parword)
    public void passwordReset(){
        DialogFragment();
    }

    public void DialogFragment(){
        DialogEditPassword editNameDialogFragment = new DialogEditPassword();
        editNameDialogFragment.show(getChildFragmentManager(), "password");
    }

    private void logoutAction(){
        app.clearPreferences();
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mActivity.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        app = (AppController) getActivity().getApplication();
        mActivity=getActivity();
        retrofitWebServices = new RetrofitWebServices(app,LOG_TAG,mContext,this);
        realm =Realm.getDefaultInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.configuration_layout, container, false);
        ButterKnife.bind(this, view);
        mContext = getActivity().getApplicationContext();
        notificacion.setChecked(app.isNotification());
        notificacion.setOnCheckedChangeListener(this);
        md_visible.setOnCheckedChangeListener(this);
        //retrofitWebServices.getProfile();
        md_visible.setChecked(app.getRankingPrivacy());

        if (versionCode()>0){
            number_version_tv.setText("Versión "+versionCode());
        }

        if (versionName()!=null){
            name_version_tv.setText("Compilacion "+versionName());
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mActivity.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id) {
            case REGISTER_PUSH:
                app.setRegisterForPush(true);
                app.setNotifications(true);
                notificacion.setChecked(true);
                break;
            case POST_PASSWORD_EDIT:
                Toast.makeText(mContext,"Te enviamos un SMS de confirmacion",Toast.LENGTH_LONG).show();
                break;
            case DELETE_DEVICE:
                retrofitWebServices.logoutAction();
                break;
            case PUT_RANKING_PRIVACY:

                break;
            case GET_STATUS_PROFILE:
                ProfileUserEntity profileUserEntity =(ProfileUserEntity)object;
                if (profileUserEntity.isHidden_ranking()){
                    app.setRankingPrivacy(false);
                    md_visible.setChecked(false);
                }else{
                    app.setRankingPrivacy(true);
                    md_visible.setChecked(true);
                }
                break;
        }
    }

    @Override
    public void onFailError(int id, int status, String message) {
        switch (id){
            case DELETE_DEVICE:
                logoutAction();
                break;
            case REGISTER_PUSH:
                sesionTerminate(status);
                app.setRegisterForPush(true);
                app.setNotifications(true);
                notificacion.setChecked(app.isNotification());
                break;
            case PUT_RANKING_PRIVACY:
                sesionTerminate(status);
                app.setRankingPrivacy(true);
                md_visible.setChecked(app.getRankingPrivacy());
                break;
            case GET_STATUS_PROFILE:
                sesionTerminate(status);
                md_visible.setChecked(false);
                break;
        }

    }

    @Override
    public void onWebServiceStart() {

    }

    @Override
    public void onWebServiceEnd() {

    }

    @Override
    public void onExpireToken() {

    }

    public void sesionTerminate(int status){
        switch (status){
            case 1001:
                AlertDIalogLogout alertDIalogLogout = new AlertDIalogLogout(getActivity(),app,this,this,this);
                alertDIalogLogout.sesionTerminate();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isCheked) {
        switch (compoundButton.getId()) {
            case R.id.notification:
                if (isCheked) {
                    new Thread(() -> {
                        FirebaseInstanceId.getInstance().getToken();
                        if (app.getDeviceToken().contentEquals("")) {
                            String token = FirebaseInstanceId.getInstance().getToken();
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                            preferences.edit().putString("deviceID", token).apply();
                        }
                        if (app.isRegisterForPush()) {
                            retrofitWebServices.registerPushServices(app.getAccessToken(),app.getAndroid_id(), app.getDeviceToken(), "android", "model");
                        }
                    }).start();
                } else {
                    new Thread(() -> {
                        try {
                            FirebaseInstanceId.getInstance().deleteInstanceId();
                            app.setNotifications(false);
                        } catch (IOException e) {
                            Log.e(LOG_TAG, "" + e.getLocalizedMessage());
                        }
                    }).start();
                }
                break;
            case R.id.md_visible:
                if (isCheked) {
                    app.setRankingPrivacy(true);
                    md_visible.setChecked(true);
                    retrofitWebServices.putRankingPrivacy(0);
                }else {
                    app.setRankingPrivacy(false);
                    md_visible.setChecked(false);
                    retrofitWebServices.putRankingPrivacy(1);
                }
                break;

        }
    }


    @Override
    public void dialogSurvey(int status) {
        this.status_survey=status;
        compareDialog();
    }

    @Override
    public void dialogOpportunity(int status) {
        this.status_opportunities=status;
        compareDialog();
    }

    @Override
    public void dialogLogout(int status) {
        this.status_logout=status;
        compareDialog();
    }

    public void compareDialog(){
        RequestBody emailBody;
        emailBody = RequestBody.create(MediaType.parse("text/plain"), app.getAccessToken());
        DeviceId deviceId= new DeviceId();
        deviceId.setToken(app.getAccessToken());
        if (status_logout==1 && status_opportunities==1 &&status_survey==1){
            retrofitWebServices.deleteDevice(app.getAccessToken(),app.getAndroid_id(),deviceId);
        }
    }

    public String versionName(){
        String versonName=null;
        PackageInfo pinfo;
        try {
            pinfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            versonName = pinfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versonName;
    }

    public int versionCode(){
        int versonCode=0;
        PackageInfo pinfo;
        try {
            pinfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            versonCode = pinfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versonCode;
    }
}
