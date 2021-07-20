package com.dacodes.bepensa.fragments;

import android.content.Context;
import android.os.AsyncTask;
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

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.MediasFilesEntity;
import com.dacodes.bepensa.entities.ProfileEntity;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.dacodes.bepensa.api.RetrofitWebServices.PUT_PROFILE_USER;
import static com.dacodes.bepensa.api.RetrofitWebServices.PUT_VALIDATE_CONTACT;

public class FragmentEditContact extends Fragment implements RetrofitWebServices.DefaultResponseListeners{
    String LOG_TAG =FragmentEditContact.class.getSimpleName();
    AppController app;
    Context mContext;
    RetrofitWebServices retrofitWebServices;
    @BindView(R.id.sms_et)EditText sms_et;
    @BindView(R.id.send_sms_bt)Button send_sms_bt;
    @BindView(R.id.end_bt)Button end_bt;
    MultipartBody.Part avatar_url = null;
    String contact;
    String email;
    File file;
    String type;
    MediasFilesEntity mediasFilesEntity;
    RequestBody smsBody;
    RequestBody emailBody;
    RequestBody phoneBody;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app=(AppController)getActivity().getApplication();
        mContext = getActivity();
        retrofitWebServices = new RetrofitWebServices(app,LOG_TAG,mContext,this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.register_fragment_three_layout, container, false);
        ButterKnife.bind(this,rootView);
        end_bt.setText("Finalizar");
        Bundle bundle = getArguments();
        if (bundle!=null){
            type=bundle.getString("type");
            switch (type){
                case "1":
                    contact=bundle.getString("contact");
                    break;
                case "2":
                    Log.e("contacto","contact");
                    contact=bundle.getString("contact");
                    email=bundle.getString("email");
                    Log.e("contacto","contact"+contact+"email"+email);
                    break;
                case "3":
                    contact=bundle.getString("contact");
                    mediasFilesEntity = (MediasFilesEntity) bundle.getSerializable("file");
                    break;
                case "4":
                    contact=bundle.getString("contact");
                    email=bundle.getString("email");
                    mediasFilesEntity = (MediasFilesEntity) bundle.getSerializable("file");
                    break;
            }
        }

        new CountDownTimer( 120000,1000){

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                send_sms_bt.setEnabled(true);
                send_sms_bt.setBackgroundColor(ContextCompat.getColor(mContext,R.color.colorLoaderBackground));
            }
        }.start();

        send_sms_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
                send_sms_bt.setEnabled(false);
            }
        });

        return rootView;
    }

    public void validate(){
        retrofitWebServices = new RetrofitWebServices(app,LOG_TAG,mContext,this);
        retrofitWebServices.ValidateContact("+52"+contact);
    }

    @OnClick(R.id.end_bt)
    public void send(){
        if(validateInput()){
            switch (type){
                case "1":
                    phoneBody =RequestBody.create(MediaType.parse("text/plain"),"+52"+contact);
                    smsBody=RequestBody.create(MediaType.parse("text/plain"),sms_et.getText().toString());
                    retrofitWebServices.putProfile(phoneBody,null,smsBody,null);
                    break;
                case "2":
                    Log.e("conta","+52"+contact);
                    phoneBody =RequestBody.create(MediaType.parse("text/plain"),"+52"+contact);
                    smsBody=RequestBody.create(MediaType.parse("text/plain"),sms_et.getText().toString());
                    emailBody = RequestBody.create(MediaType.parse("text/plain"), email);
                    retrofitWebServices.putProfile(phoneBody,emailBody,smsBody,null);
                    Log.e("sms",sms_et.getText().toString());
                    break;
                case "3":
                    phoneBody =RequestBody.create(MediaType.parse("text/plain"),"+52"+contact);
                    smsBody=RequestBody.create(MediaType.parse("text/plain"),sms_et.getText().toString());
                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), mediasFilesEntity.getFile());
                    avatar_url = MultipartBody.Part.createFormData("avatar_url", mediasFilesEntity.getFile().getName(), requestFile);
                    new Upload().execute();
                    break;
                case "4":
                    phoneBody =RequestBody.create(MediaType.parse("text/plain"),"+52"+contact);
                    smsBody=RequestBody.create(MediaType.parse("text/plain"),sms_et.getText().toString());
                    emailBody = RequestBody.create(MediaType.parse("text/plain"), email);
                    RequestBody requestFile2 = RequestBody.create(MediaType.parse("multipart/form-data"), mediasFilesEntity.getFile());
                    avatar_url = MultipartBody.Part.createFormData("avatar_url", mediasFilesEntity.getFile().getName(), requestFile2);
                    new Upload().execute();
                    break;
            }
        }
    }

    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id) {
            case PUT_PROFILE_USER:
                ProfileEntity profileEntity = (ProfileEntity) object;
                app.setEmail(profileEntity.getEmail());
                app.setPhone(profileEntity.getContact());
                app.setUsername(profileEntity.getUsername());
                app.setImageUrl(profileEntity.getAvatarUrl());
                app.setDivision(profileEntity.getDivision().getName());
                getActivity().onBackPressed();
                break;
            case PUT_VALIDATE_CONTACT:
                Log.e("validate","contacto");
                break;
        }

    }

    @Override
    public void onFailError(int id, int status, String message) {
        switch (id){
            case PUT_VALIDATE_CONTACT:
                switch (status){
                    case 400:
                        Toast.makeText(mContext,""+message,Toast.LENGTH_LONG).show();
                        break;
                }
                break;
            case PUT_PROFILE_USER:
                switch (status){
                    case 400:
                        Toast.makeText(mContext,""+message,Toast.LENGTH_LONG).show();
                        break;
                }
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

    private boolean validateInput(){
        sms_et.setError(null);
        if(TextUtils.isEmpty(sms_et.getText().toString())){
            sms_et.setError("El código es obligatorio");
            return false;
        }
        return true;
    }


    private class Upload extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            switch (type){
                case "3":
                    retrofitWebServices.putProfile(phoneBody,null,smsBody,avatar_url);
                    break;
                case "4":
                    retrofitWebServices.putProfile(phoneBody,emailBody,smsBody,avatar_url);
                    break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(mContext,"Datos actualizados",Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
        }
    }
}
