package com.dacodes.bepensa.utils;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.api.RetrofitWebServices;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.dacodes.bepensa.api.RetrofitWebServices.PUT_PASSWORD_EDIT;

public class DialogEditPassword extends DialogFragment implements RetrofitWebServices.DefaultResponseListeners {

    String LOG_TAG=DialogEditPassword.class.getSimpleName();
    @BindView(R.id.password_et)EditText password_et;
    @BindView(R.id.confirm_password_et)EditText confirm_password_et;
    @BindView(R.id.end_bt)Button end_bt;
    Context mContext;
    AppController appController;
    RetrofitWebServices retrofitWebServices;

    public DialogEditPassword() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.dialog_edit_password, container);
        ButterKnife.bind(this,view);
        mContext=getContext();
        appController =(AppController)getActivity().getApplication();
        retrofitWebServices = new RetrofitWebServices(appController,LOG_TAG,mContext,this);
        return view;
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

        return true;
    }

    @OnClick(R.id.end_bt)
    public void click(){
        if (validateInput()){
            retrofitWebServices.putNewPassword(Integer.parseInt(appController.getUsername())
                    ,appController.getIdDivision(),password_et.getText().toString());
        }
    }

    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id){
            case PUT_PASSWORD_EDIT:
                dismiss();
                Toast.makeText(mContext,"Tú contraseña ha sido actualizada con éxito",Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onFailError(int id, int status, String message) {

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
