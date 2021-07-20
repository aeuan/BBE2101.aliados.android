package com.dacodes.bepensa.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.activities.EditPassword;
import com.dacodes.bepensa.activities.LoginActivity;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.RecoverPassword;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.dacodes.bepensa.api.RetrofitWebServices.POST_PASSWORD_EDIT;
import static com.dacodes.bepensa.api.RetrofitWebServices.PUT_RESET_PASSWORD;

public class FragmentResetPasswordTwo extends Fragment implements RetrofitWebServices.DefaultResponseListeners {

    String LOG_TAG=FragmentResetPasswordTwo.class.getSimpleName();
    RetrofitWebServices retrofitWebServices;
    AppController appController;
    Context mContext;
    private Activity mActivity;


    @BindView(R.id.password_et)EditText password_et;
    @BindView(R.id.confirm_password_et)EditText confirm_password_et;
    @BindView(R.id.end_bt)Button end_bt;
    @BindView(R.id.sms_code_et)EditText sms_code_et;
    @BindView(R.id.send_sms_bt)Button send_sms_bt;
    CountDownTimer countDownTimer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        mActivity=getActivity();
        appController =(AppController)getActivity().getApplication();
        retrofitWebServices = new RetrofitWebServices(appController,LOG_TAG,mContext,this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.dialog_edit_password, container, false);
        ButterKnife.bind(this,view);
        sms_code_et.setVisibility(View.GONE);
        send_sms_bt.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer!=null)
            countDownTimer.cancel();
    }

    @OnClick(R.id.end_bt)
    public void click(){
        if (validateInput()){
            RecoverPassword recoverPassword=((EditPassword)mActivity).getRecoverPassword();
            recoverPassword.setNewpassword(password_et.getText().toString());
            retrofitWebServices.putResetPasswordV2(recoverPassword);
            /*
            retrofitWebServices.putResetPassword(username
                    ,id_division,password_et.getText().toString(),Integer.parseInt(sms_code_et.getText().toString()));*/
        }
    }

    private boolean validateInput(){

        if(TextUtils.isEmpty(password_et.getText().toString())){
            password_et.setError("Campo vacío");
            return false;
        }

        if(TextUtils.isEmpty(confirm_password_et.getText().toString())){
            confirm_password_et.setError("Campo vacío");
            return false;
        }

        if(!confirm_password_et.getText().toString().contentEquals(password_et.getText().toString())){
            confirm_password_et.setError("Las contraseñas no coinciden");
            return false;
        }
/*
        if(TextUtils.isEmpty(sms_code_et.getText().toString())){
            sms_code_et.setError("Campo vacío");
            return false;
        }*/

        return true;
    }


    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id){
            case PUT_RESET_PASSWORD:
                /*
                SurveySucces surveySucces =(SurveySucces)object;
                if (surveySucces.getSuccess().equals("ok")){*/
                    Toast.makeText(mContext,"Tú contraseña ha sido actualizada con éxito",Toast.LENGTH_LONG).show();
                    if (getActivity()!=null){
                        Intent intent = new Intent(mContext,LoginActivity.class);
                        getActivity().startActivity(intent);
                        getActivity().finish();
                    }
               // }
                break;
            case POST_PASSWORD_EDIT:
                Toast.makeText(mContext,"Se ha enviado un código al número celular registrado en la cuenta, por favor ingresa una nueva contraseña y el código recibido",Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onFailError(int id, int status, String message) {
        switch (status){
            case 1043:
                Toast.makeText(mContext,message,Toast.LENGTH_LONG).show();
                break;
        }
        Log.e("Error","mensaje"+message+"status"+status);
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
}
