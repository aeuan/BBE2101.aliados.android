package com.dacodes.bepensa.fragments;


import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.DeviceId;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.utils.AlertDIalogLogout;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.dacodes.bepensa.api.RetrofitWebServices.DELETE_DEVICE;
import static com.dacodes.bepensa.api.RetrofitWebServices.POST_SUGGESTIONS;


/**
 * A simple {@link Fragment} subclass.
 */
public class CommentsSuggestions extends Fragment implements RetrofitWebServices.DefaultResponseListeners
        ,AlertDIalogLogout.logout,AlertDIalogLogout.opportunity,AlertDIalogLogout.survey{

    String TAG=CommentsSuggestions.class.getSimpleName();
    AppController appController;
    Context mContext;
    RetrofitWebServices retrofitWebServices;


    int status_survey=0;
    int status_opportunity=0;
    int status_logout=0;

    public CommentsSuggestions() {
        // Required empty public constructor
    }

    @BindView(R.id.end_bt)Button end_bt;
    @BindView(R.id.edComments)EditText edComments;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appController = (AppController)getActivity().getApplication();
        mContext = getContext();
        retrofitWebServices = new RetrofitWebServices(appController,TAG,mContext,this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_comments_suggestions, container, false);
        ButterKnife.bind(this,view);

        end_bt.setOnClickListener(view1 -> {
            if (validateInput()) {
                retrofitWebServices.postSuggestions(appController.getAccessToken(), edComments.getText().toString());
            }
        });

        return view;
    }

    private boolean validateInput(){
        edComments.setError(null);

        if(TextUtils.isEmpty(edComments.getText().toString())){
            edComments.setError("");
            return false;
        }

        return true;
    }

    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id){
            case POST_SUGGESTIONS:
                Toast.makeText(mContext,"Gracias por compartir tu comentario con nosotros",Toast.LENGTH_LONG).show();
                edComments.setText(null);
                break;
            case DELETE_DEVICE:
                retrofitWebServices.logoutAction();
                break;
        }

    }

    @Override
    public void onFailError(int id, int status, String message) {
        switch (status){
            case 1001:
                AlertDIalogLogout alertDIalogLogout = new AlertDIalogLogout(getActivity(),appController,this,this,this);
                alertDIalogLogout.sesionTerminate();
                break;
        }
        switch (id){
            case DELETE_DEVICE:
                retrofitWebServices.logoutAction();
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

    @Override
    public void dialogSurvey(int status) {
        this.status_survey=status;
        compareDialog();
    }

    @Override
    public void dialogOpportunity(int status) {
        this.status_opportunity=status;
        compareDialog();
    }

    @Override
    public void dialogLogout(int status) {
        this.status_logout=status;
        compareDialog();
    }

    public void compareDialog(){
        DeviceId deviceId= new DeviceId();
        deviceId.setToken(appController.getAccessToken());
        if (status_logout==1 && status_opportunity==1 &&status_survey==1){
            retrofitWebServices.deleteDevice(appController.getAccessToken(),appController.getAndroid_id(),deviceId);
        }
    }
}
