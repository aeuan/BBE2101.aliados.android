package com.dacodes.bepensa.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.activities.MainActivity;
import com.dacodes.bepensa.activities.PrincipalTest;
import com.dacodes.bepensa.api.RetrofitWebServices;
import com.dacodes.bepensa.entities.ofline.packageSurvey.OflineSurveyEntity;
import com.dacodes.bepensa.entities.surveys.SurveyOptions;
import com.dacodes.bepensa.entities.surveys.SurveyResult;
import com.dacodes.bepensa.models.PointsResponseModel;
import com.dacodes.bepensa.utils.ValidateNetwork;
import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.dacodes.bepensa.api.RetrofitWebServices.GET_POINTS;
import static com.dacodes.bepensa.api.RetrofitWebServices.PUT_SURVEY_QUESTION;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTotalQuestion extends Fragment implements RetrofitWebServices.DefaultResponseListeners{
    private static final String LOG_TAG = FragmentTotalQuestion.class.getSimpleName();
    @BindView(R.id.total_tv)TextView total_tv;
    @BindView(R.id.btn_ready)Button btn_ready;
    @BindView(R.id.progress_bar)ProgressBar progressBar;
    @BindView(R.id.success)RelativeLayout success;

    public FragmentTotalQuestion() {
        // Required empty public constructor
    }

    ArrayList<SurveyOptions> item;
    SurveyResult surveyResult;
    AppController app;
    private Context mContext;
    RetrofitWebServices retrofitWebServices;
    Realm realm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_total_question, container, false);
        ButterKnife.bind(this,view);
        surveyResult = new SurveyResult();
        app = (AppController)getActivity().getApplication();
        mContext = getActivity().getApplicationContext();
        retrofitWebServices = new RetrofitWebServices(app,LOG_TAG,mContext,this);
        progressBar.setVisibility(View.GONE);
        realm= Realm.getDefaultInstance();
        sendResulQuestion();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @OnClick(R.id.btn_ready)
    public void ready(){
        if (((PrincipalTest)getActivity()).getBoletin()>=1){
            Intent intent = new Intent(getActivity(),MainActivity.class);
            intent.putExtra("intentTypeBack",6);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.no_anim, R.anim.exit_down);
        }else{
            if (((PrincipalTest)getActivity()).getEncuesta()>=1){
                Intent intent = new Intent(getActivity(),MainActivity.class);
                intent.putExtra("intentTypeBack",6);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.no_anim, R.anim.exit_down);
            }else{
                Intent intent = new Intent(getActivity(),MainActivity.class);
                intent.putExtra("intentTypeBack",1);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.no_anim, R.anim.exit_down);
            }
        }
    }

    public void surveyData(){
        surveyResult = new SurveyResult();
        surveyResult.setSurvey_id(((PrincipalTest)getActivity()).getSurvey_id());
        item=((PrincipalTest)getActivity()).getItems();
        surveyResult.setItems(item);
        sendData(surveyResult);
    }

    public void sendData(final SurveyResult surveyResult){
        if (ValidateNetwork.getNetwork(mContext)) {
            retrofitWebServices.putSurveyQuestion(surveyResult);
        }else{
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<OflineSurveyEntity> realmResults = realm.where(OflineSurveyEntity.class).findAll();
                    if (realmResults.size()>0){
                        boolean status_res=false;
                        final int id_survey=((PrincipalTest)getActivity()).getSurvey_id();
                        for (int i=0;i<realmResults.size();i++){
                            if (realmResults.get(i).getId()==id_survey){
                                Gson gson = new Gson();
                                final String res = gson.toJson(surveyResult);
                                status_res=true;
                                realmResults.get(i).setHas_sync(true);
                                realmResults.get(i).setResult(res);
                                realmResults.get(i).setHas_sync_load(true);
                                if (((PrincipalTest)getActivity()).getBoletin()>=1){
                                    Intent intent = new Intent(getActivity(),MainActivity.class);
                                    intent.putExtra("intentTypeBack",6);
                                    startActivity(intent);
                                    getActivity().overridePendingTransition(R.anim.no_anim, R.anim.exit_down);
                                }else{
                                    if (((PrincipalTest)getActivity()).getEncuesta()>=1){
                                        Intent intent = new Intent(getActivity(),MainActivity.class);
                                        intent.putExtra("intentTypeBack",6);
                                        startActivity(intent);
                                        getActivity().overridePendingTransition(R.anim.no_anim, R.anim.exit_down);
                                    }else{
                                        Intent intent = new Intent(getActivity(),MainActivity.class);
                                        intent.putExtra("intentTypeBack",1);
                                        startActivity(intent);
                                        getActivity().overridePendingTransition(R.anim.no_anim, R.anim.exit_down);
                                    }
                                }
                                break;
                            }
                        }
                        if (!status_res){
                            Gson gson = new Gson();
                            String res = gson.toJson(surveyResult);
                            insertData(id_survey,res,true);
                        }
                    }



                    /*
                    RealmResults<OflineSurveyEntity> persons = realm.where(OflineSurveyEntity.class)
                            .equalTo("id", id_survey).findAll();
                    persons.get(0).setHas_sync(true);
                    persons.get(0).setResult(res);
                    persons.get(0).setHas_sync_load(true);
                    */
                }
            });







        }
    }

    public void insertData(final int id,final String data,final boolean status){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                OflineSurveyEntity object = bgRealm.createObject(OflineSurveyEntity.class);
                object.setResult(data);
                object.setHas_sync(status);
                object.setId(id);
                object.setHas_sync_load(false);
                //object.setUsername(Integer.parseInt(app.getUsername()));
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.e(LOG_TAG,"Success Realm");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.e(LOG_TAG,"onError Realm");
            }
        });
    }

    public void sendResulQuestion(){
        total_tv.setText(""+((PrincipalTest)getActivity()).getSurvey_points() +"\npuntos");
    }

    @Override
    public void onResponse(int id, String message, Object object) {
        switch (id) {
            case PUT_SURVEY_QUESTION:
                deleteItem();
                if (((PrincipalTest)getActivity()).getBoletin()>=1){
                    Intent intent = new Intent(getActivity(),MainActivity.class);
                    intent.putExtra("intentTypeBack",6);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.no_anim, R.anim.exit_down);
                }else{
                    if (((PrincipalTest)getActivity()).getEncuesta()>=1){
                        Intent intent = new Intent(getActivity(),MainActivity.class);
                        intent.putExtra("intentTypeBack",6);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.no_anim, R.anim.exit_down);
                    }else{
                        Intent intent = new Intent(getActivity(),MainActivity.class);
                        intent.putExtra("intentTypeBack",1);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.no_anim, R.anim.exit_down);
                    }
                }
                /*
                SurveySucces surveySucces= (SurveySucces) object;
                if (surveySucces.getSuccess().equals("ok"))
                    sendResulQuestion();*/
                break;
            case GET_POINTS:
                PointsResponseModel responseModel= (PointsResponseModel) object;
                app.setPoints(responseModel.getPoints());
                app.setRankingPosition(String.valueOf(responseModel.getRank()));
                app.setPointAvailable1(responseModel.getPoints_available());
                break;
        }
    }

    @Override
    public void onFailError(int id, int status, String message) {
        Log.e("error","id"+id+"status"+status+"messagge");
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

    public void deleteItem(){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                int id=((PrincipalTest)getActivity()).getSurvey_id();
                RealmResults<OflineSurveyEntity> resul = realm.where(OflineSurveyEntity.class).equalTo("id",id).findAll();
                if (resul.size()>0){
                    resul.deleteAllFromRealm();
                }else{
                    Log.i(LOG_TAG,"Not found");
                }
            }
        });
    }

    public void alertAccess(String message){
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(message).setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                })
                .show();
    }


}
