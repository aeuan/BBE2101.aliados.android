package com.dacodes.bepensa.api;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.DeviceId;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.activities.LoginActivity;
import com.dacodes.bepensa.entities.CategoriesEntity;
import com.dacodes.bepensa.entities.DetailPromotionsEntity;
import com.dacodes.bepensa.entities.EventosEntity;
import com.dacodes.bepensa.entities.LoginUser;
import com.dacodes.bepensa.entities.Opportunity;
import com.dacodes.bepensa.entities.PackegeOpportunity.BrandEntity;
import com.dacodes.bepensa.entities.PackegeOpportunity.DivisionEntity;
import com.dacodes.bepensa.entities.ProfileEntity;
import com.dacodes.bepensa.entities.ProfileUserEntity;
import com.dacodes.bepensa.entities.PushRegistrationModelResponse;
import com.dacodes.bepensa.entities.RecoverPassword;
import com.dacodes.bepensa.entities.ResponseSettings;
import com.dacodes.bepensa.entities.SuggestionsEntity;
import com.dacodes.bepensa.entities.awardsRedeem.AwardRedeemResponse;
import com.dacodes.bepensa.entities.feed.FeedResponseEntity;
import com.dacodes.bepensa.entities.promotion.PromotionsEntity;
import com.dacodes.bepensa.entities.surveys.SurveyResult;
import com.dacodes.bepensa.entities.surveys.SurveySucces;
import com.dacodes.bepensa.entities.surveys.result.SurveyDataEntity;
import com.dacodes.bepensa.models.AwardsModel;
import com.dacodes.bepensa.models.ChatResponseModel;
import com.dacodes.bepensa.models.ComunicadosResponseModel;
import com.dacodes.bepensa.models.ErrorModel;
import com.dacodes.bepensa.models.EventosResponseModel;
import com.dacodes.bepensa.models.HandledResponse;
import com.dacodes.bepensa.models.MediaError;
import com.dacodes.bepensa.models.MediaErrorModel;
import com.dacodes.bepensa.models.NewOpportunityResponse;
import com.dacodes.bepensa.models.NotificacionesResponseModel;
import com.dacodes.bepensa.models.OpportunityDetailModel;
import com.dacodes.bepensa.models.OpportunityModel;
import com.dacodes.bepensa.models.PointsResponseModel;
import com.dacodes.bepensa.models.RedeemResponse;
import com.dacodes.bepensa.models.ResponseVersionPlayStore;
import com.dacodes.bepensa.models.SurveyResponseModel;
import com.dacodes.bepensa.models.ValidateRegistrationEntity;
import com.dacodes.bepensa.utils.FailError;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import io.realm.Realm;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 */


public class RetrofitWebServices {
    private AppController app;
    private WebServices service;
    private String logTag;
    private Context mContext;
    private DefaultResponseListeners defaultResponseListeners;

    //Request identifiers
    public static final int LOGIN = 1;
    public static final int GET_DIVISIONS = 2;
    public static final int GET_OPPORTUNITIES = 3;
    public static final int GET_RANKING = 4;
    public static final int GET_BRANDS = 6;
    public static final int GET_OPPORTUNITIES_TYPE =7;
    public static final int GET_OPPORTUNITY_DETAIL = 5;
    public static final int POST_OPPORTUNITY = 10;
    public static final int VALIDATE_USER = 8;
    public static final int REGISTER_USER = 9;
    public static final int GET_AWARDS = 11;
    public static final int CLAIM_AWARD = 12;
    public static final int PUT_MEDIA_OPPORTUNITY = 13;
    public static final int REGISTER_PUSH = 14;
    public static final int VERIFY_PHONE = 15;
    public static final int GET_EVENTOS = 16;
    public static final int GET_NOTIFICACIONES = 17;
    public static final int GET_COMUNICADOS = 18;
    public static final int GET_CHAT = 19;
    public static final int SEND_CHAT_MESSAGE = 20;
    public static final int GET_PROMOTIONS=22;
    public static final int GET_SURVEY=23;
    public static final int GET_SURVEY_DETAIL=40;
    public static final int PUT_NOTIFICATION = 24;
    public static final int GET_POINTS = 25;
    public static final int UNREGISTER_PUSH = 26;
    public static final int GET_EVENT_DETAIL = 27;
    public static final int PUT_PROFILE_USER=29;
    public static final int PUT_PROFILE_EMAIL=30;
    public static final int PUT_SURVEY_QUESTION=30;
    public static final int PUT_VALIDATE_CONTACT=35;
    public static final int GET_AWARDS_REDEEM=37;
    public static final int GET_FEED= 39;
    public static final int POST_PASSWORD_EDIT= 42;
    public static final int PUT_PASSWORD_EDIT= 43;
    public static final int PUT_RESET_PASSWORD= 44;
    public static final int GET_CATEGORIES_FILTER=50;
    public static final int DELETE_DEVICE=51;
    public static final int PUT_RANKING_PRIVACY=52;
    public static final int GET_STATUS_PROFILE=53;
    public static final int POST_SUGGESTIONS=54;
    public static final int GET_DETAIL_PROMOTIONS=56;
    public static final int GET_VERSION_PLAY_STORE=100;
    public static final int GET_FILTER_THEMES_NOTIFICATIONS = 101;
    public static final int GET_CONTACT_WHATS = 200;


    boolean cancel=false;
    public interface DefaultResponseListeners {
        void onResponse(int id, String message, Object object);
        void onFailError(int id, int status, String message);
        void onWebServiceStart();
        void onWebServiceEnd();
        void onExpireToken();
    }

    Realm realm;

    public RetrofitWebServices(AppController app, String logTag, Context context,
                               DefaultResponseListeners defaultResponseListeners) {
        this.app = app;
        Retrofit retrofit = app.getRetrofit();
        this.service = retrofit.create(WebServices.class);
        this.logTag = logTag;
        this.mContext = context;
        this.defaultResponseListeners = defaultResponseListeners;
        realm = Realm.getDefaultInstance();
    }

    public void loginUser(LoginUser loginUser) {
        defaultResponseListeners.onWebServiceStart();
        Call<ProfileEntity> call = service.loginUser(loginUser);
        call.enqueue(new Callback<ProfileEntity>() {
            @Override
            public void onResponse(Call<ProfileEntity> call, Response<ProfileEntity> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                        defaultResponseListeners.onResponse(LOGIN, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(LOGIN, errorModel.getCode(), errorModel.getMessage());
                        break;
                    case 400:
                        ErrorModel errorModel2 = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(LOGIN, errorModel2.getCode(), errorModel2.getMessage());
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(LOGIN, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(LOGIN, response.code(), parseError(response.errorBody()));
                        break;
                }
            }

            @Override
            public void onFailure(Call<ProfileEntity> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(LOGIN, -1, ""+failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    public void login(LoginUser loginUser){
        Call<ProfileEntity> call=service.loginUser(loginUser);
        call.enqueue(new Callback<ProfileEntity>() {
            @Override
            public void onResponse(Call<ProfileEntity> call, Response<ProfileEntity> response) {
                onResponseMethod(LOGIN,call,response);
            }

            @Override
            public void onFailure(Call<ProfileEntity> call, Throwable t) {
                onFailMethod(call,LOGIN,t);
            }
        });
    }

    public void getDivisions() {
        defaultResponseListeners.onWebServiceStart();
        Call<List<DivisionEntity>> call = service.getDivisions();
        call.enqueue(new Callback<List<DivisionEntity>>() {
            @Override
            public void onResponse(Call<List<DivisionEntity>> call, Response<List<DivisionEntity>> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                        defaultResponseListeners.onResponse(GET_DIVISIONS, response.raw().message(),response.body());
                        break;
                    case 401:
                        defaultResponseListeners.onFailError(GET_DIVISIONS, response.code(), parseError(response.errorBody()));
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(GET_DIVISIONS, response.code(), parseError(response.errorBody()));
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(GET_DIVISIONS, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(GET_DIVISIONS, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<DivisionEntity>> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(GET_DIVISIONS, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    public void getPromotionsDetail(String id,String token) {
        Call<DetailPromotionsEntity> call;
            call = service.getPromotionsDetail(mContext.getString(R.string.base_url)+"v1/promotions/"+id,token);
        call.enqueue(new Callback<DetailPromotionsEntity>() {
            @Override
            public void onResponse(Call<DetailPromotionsEntity> call, Response<DetailPromotionsEntity> response) {
                switch (response.code()) {
                    case 200:
                        defaultResponseListeners.onResponse(GET_DETAIL_PROMOTIONS, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(GET_DETAIL_PROMOTIONS, errorModel.getCode(), errorModel.getMessage());
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(GET_DETAIL_PROMOTIONS, response.code(), parseError(response.errorBody()));
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(GET_DETAIL_PROMOTIONS, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(GET_DETAIL_PROMOTIONS, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<DetailPromotionsEntity> call, Throwable t) {
                if(!cancel){
                    defaultResponseListeners.onFailError(GET_DETAIL_PROMOTIONS, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    public Call<PromotionsEntity> getPromotions(String query,String token) {
        Call<PromotionsEntity> call;
        if(query!=null){
            call = service.getPromotions(query,token);
            //call = service.getPromotions(mContext.getString(R.string.base_url)+ mContext.getString(R.string.api_get_promotions)+"?name="+query,token);
        }else {
            call = service.getPromotions(mContext.getString(R.string.base_url)+ mContext.getString(R.string.api_get_promotions),token);
        }

        call.enqueue(new Callback<PromotionsEntity>() {
            @Override
            public void onResponse(Call<PromotionsEntity> call, Response<PromotionsEntity> response) {
                switch (response.code()) {
                    case 200:
                        defaultResponseListeners.onResponse(GET_PROMOTIONS, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(GET_PROMOTIONS, errorModel.getCode(), errorModel.getMessage());
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(GET_PROMOTIONS, response.code(), parseError(response.errorBody()));
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(GET_PROMOTIONS, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(GET_PROMOTIONS, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<PromotionsEntity> call, Throwable t) {
                if(!cancel){
                    defaultResponseListeners.onFailError(GET_EVENTOS, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
        return call;
    }


    public void getAwardsRedeem(String query,String token) {
        Call<AwardRedeemResponse> call;
        if(query!=null){
            call = service.getAwardsRedeem(query,token);
        }else {
            call = service.getAwardsRedeem(mContext.getString(R.string.base_url)+ mContext.getString(R.string.api_get_awards_redeem)+"?is_changed=False",token);
        }

        call.enqueue(new Callback<AwardRedeemResponse>() {
            @Override
            public void onResponse(Call<AwardRedeemResponse> call, Response<AwardRedeemResponse> response) {
                switch (response.code()) {
                    case 200:
                        defaultResponseListeners.onResponse(GET_AWARDS_REDEEM, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(GET_AWARDS_REDEEM,errorModel.getCode(),errorModel.getMessage() );
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(GET_AWARDS_REDEEM, response.code(), parseError(response.errorBody()));
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(GET_AWARDS_REDEEM, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(GET_AWARDS_REDEEM, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<AwardRedeemResponse> call, Throwable t) {
                if(!cancel){
                    defaultResponseListeners.onFailError(GET_AWARDS_REDEEM, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }


    public void getSurvey(String query,String token) {
        Call<SurveyResponseModel> call;

        if(query!=null){
            call =service.getSurveys(query,token);
           // call = service.getSurveys(mContext.getString(R.string.base_url)+ mContext.getString(R.string.api_get_survey)+"?name="+query,token);
        }else {
            call = service.getSurveys(mContext.getString(R.string.base_url)+ mContext.getString(R.string.api_get_survey),token);
        }

        call.enqueue(new Callback<SurveyResponseModel>() {
            @Override
            public void onResponse(Call<SurveyResponseModel> call, Response<SurveyResponseModel> response) {
                switch (response.code()) {
                    case 200:
                        defaultResponseListeners.onResponse(GET_SURVEY, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(GET_SURVEY,errorModel.getCode(),errorModel.getMessage());
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(GET_SURVEY, response.code(), parseError(response.errorBody()));
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(GET_SURVEY, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(GET_SURVEY, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<SurveyResponseModel> call, Throwable t) {
                if(!cancel){
                    defaultResponseListeners.onFailError(GET_SURVEY, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }


    public void getFeed(String query,String token) {
        Call<FeedResponseEntity> call;
        if(query!=null){
            call = service.getFeed(query,token);
        }else {
            call = service.getFeed(mContext.getString(R.string.base_url)+ mContext.getString(R.string.api_get_feed),token);
        }
        call.enqueue(new Callback<FeedResponseEntity>() {
            @Override
            public void onResponse(Call<FeedResponseEntity> call, Response<FeedResponseEntity> response) {
                switch (response.code()) {
                    case 200:
                        defaultResponseListeners.onResponse(GET_FEED, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(GET_FEED,errorModel.getCode(),errorModel.getMessage());
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(GET_FEED, response.code(), parseError(response.errorBody()));
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(GET_FEED, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(GET_FEED, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<FeedResponseEntity> call, Throwable t) {
                if(!cancel){
                    defaultResponseListeners.onFailError(GET_FEED, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }


    public void getOpportunities(String query, String token) {
        defaultResponseListeners.onWebServiceStart();
        Call<OpportunityModel> call;
        if(query!=null){
            call = service.getOpportunities(query,token);
        }else {
            call = service.getOpportunities(mContext.getString(R.string.base_url)+"v1/opportunities/myrecords",token);
        }
        call.enqueue(new Callback<OpportunityModel>() {
            @Override
            public void onResponse(Call<OpportunityModel> call, Response<OpportunityModel> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                        defaultResponseListeners.onResponse(GET_OPPORTUNITIES, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(GET_OPPORTUNITIES,errorModel.getCode(),errorModel.getMessage());
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(GET_OPPORTUNITIES, response.code(), parseError(response.errorBody()));
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(GET_OPPORTUNITIES, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(GET_OPPORTUNITIES, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<OpportunityModel> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(GET_OPPORTUNITIES, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    public void getProfile() {
        Call<ProfileUserEntity> call;
            call = service.getProfile(mContext.getString(R.string.base_url)+"v1/collaborators/profile",app.getAccessToken());
        call.enqueue(new Callback<ProfileUserEntity>() {
            @Override
            public void onResponse(Call<ProfileUserEntity> call, Response<ProfileUserEntity> response) {
                switch (response.code()) {
                    case 200:
                        defaultResponseListeners.onResponse(GET_STATUS_PROFILE, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(GET_STATUS_PROFILE,errorModel.getCode(),errorModel.getMessage());
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(GET_STATUS_PROFILE, response.code(), parseError(response.errorBody()));
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(GET_STATUS_PROFILE, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(GET_STATUS_PROFILE, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<ProfileUserEntity> call, Throwable t) {
                if(!cancel){
                    defaultResponseListeners.onFailError(GET_OPPORTUNITIES, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }
    public void getRanking(String token) {
        defaultResponseListeners.onWebServiceStart();
        Call<List<ProfileEntity>> call = service.getRanking(token);
        call.enqueue(new Callback<List<ProfileEntity>>() {
            @Override
            public void onResponse(Call<List<ProfileEntity>> call, Response<List<ProfileEntity>> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                        defaultResponseListeners.onResponse(GET_RANKING, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(GET_RANKING,errorModel.getCode(),errorModel.getMessage());
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(GET_RANKING, response.code(), parseError(response.errorBody()));
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(GET_RANKING, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(GET_RANKING, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<ProfileEntity>> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(GET_RANKING, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    public void getBrand(String token) {
        defaultResponseListeners.onWebServiceStart();
        Call<List<BrandEntity>> call = service.getBrand(mContext.getString(R.string.base_url)+"v1/brands",token);
        call.enqueue(new Callback<List<BrandEntity>>() {
            @Override
            public void onResponse(Call<List<BrandEntity>> call, Response<List<BrandEntity>> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                        defaultResponseListeners.onResponse(GET_BRANDS, response.raw().message(),response.body());
                        break;
                    case 401:
                        defaultResponseListeners.onFailError(GET_BRANDS, response.code(), parseError(response.errorBody()));
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(GET_BRANDS, response.code(), parseError(response.errorBody()));
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(GET_BRANDS, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(GET_BRANDS, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<BrandEntity>> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(GET_BRANDS, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    public Call<List<BrandEntity>> getBrands(String token, int id_division) {
        defaultResponseListeners.onWebServiceStart();
        Call<List<BrandEntity>> call = service.getBrands(token, id_division);
        call.enqueue(new Callback<List<BrandEntity>>() {
            @Override
            public void onResponse(Call<List<BrandEntity>> call, Response<List<BrandEntity>> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                        defaultResponseListeners.onResponse(GET_BRANDS, response.raw().message(),response.body());
                        break;
                    case 401:
                            ErrorModel errorModel = parseErrorBody(response.errorBody());
                            defaultResponseListeners.onFailError(GET_BRANDS,errorModel.getCode(),errorModel.getMessage());
                            //defaultResponseListeners.onFailError(GET_BRANDS, response.code(), parseError(response.errorBody()));
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(GET_BRANDS, response.code(), parseError(response.errorBody()));
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(GET_BRANDS, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(GET_BRANDS, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<BrandEntity>> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(GET_BRANDS, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });

        return call;
    }

    public Call<List<BrandEntity>> getOpportunitiesType(String token, int id_division) {
        defaultResponseListeners.onWebServiceStart();
        Call<List<BrandEntity>> call = service.getOportunitiesTypes(token, id_division);
        call.enqueue(new Callback<List<BrandEntity>>() {
            @Override
            public void onResponse(Call<List<BrandEntity>> call, Response<List<BrandEntity>> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                        defaultResponseListeners.onResponse(GET_OPPORTUNITIES_TYPE, response.raw().message(),response.body());
                        break;
                    case 401:
                            ErrorModel errorModel = parseErrorBody(response.errorBody());
                            defaultResponseListeners.onFailError(GET_OPPORTUNITIES_TYPE,errorModel.getCode(),errorModel.getMessage());
                            //defaultResponseListeners.onFailError(GET_OPPORTUNITIES_TYPE, response.code(), parseError(response.errorBody()));
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(GET_OPPORTUNITIES_TYPE, response.code(), parseError(response.errorBody()));
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(GET_OPPORTUNITIES_TYPE, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(GET_OPPORTUNITIES_TYPE, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<BrandEntity>> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(GET_OPPORTUNITIES_TYPE, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
        return call;
    }

    public void getCategories(String token) {
        defaultResponseListeners.onWebServiceStart();
        Call<List<CategoriesEntity>> call = service.getCategories(token);
        call.enqueue(new Callback<List<CategoriesEntity>>() {
            @Override
            public void onResponse(Call<List<CategoriesEntity>> call, Response<List<CategoriesEntity>> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                        defaultResponseListeners.onResponse(GET_CATEGORIES_FILTER, response.raw().message(),response.body());
                        break;
                    case 401:
                        defaultResponseListeners.onFailError(GET_CATEGORIES_FILTER, response.code(), parseError(response.errorBody()));
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(GET_CATEGORIES_FILTER, response.code(), parseError(response.errorBody()));
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(GET_CATEGORIES_FILTER, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(GET_CATEGORIES_FILTER, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<CategoriesEntity>> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(GET_CATEGORIES_FILTER, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    public void getOpportunitiesTypes(String token) {
        defaultResponseListeners.onWebServiceStart();
        Call<List<Opportunity>> call = service.getOportunitiesType(mContext.getString(R.string.base_url)+"v1/opportunities/types",token);
        call.enqueue(new Callback<List<Opportunity>>() {
            @Override
            public void onResponse(Call<List<Opportunity>> call, Response<List<Opportunity>> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                        defaultResponseListeners.onResponse(GET_OPPORTUNITIES_TYPE, response.raw().message(),response.body());
                        break;
                    case 401:
                        defaultResponseListeners.onFailError(GET_OPPORTUNITIES_TYPE, response.code(), parseError(response.errorBody()));
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(GET_OPPORTUNITIES_TYPE, response.code(), parseError(response.errorBody()));
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(GET_OPPORTUNITIES_TYPE, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(GET_OPPORTUNITIES_TYPE, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Opportunity>> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(GET_OPPORTUNITIES_TYPE, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }


    public Call<OpportunityDetailModel> getOpportunityDetail(String opportunityId) {
        defaultResponseListeners.onWebServiceStart();
        Call<OpportunityDetailModel> call = service.getOpportunityDetailModel(app.getAccessToken(), opportunityId);
        call.enqueue(new Callback<OpportunityDetailModel>() {
            @Override
            public void onResponse(Call<OpportunityDetailModel> call, Response<OpportunityDetailModel> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                        defaultResponseListeners.onResponse(GET_OPPORTUNITY_DETAIL, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(GET_OPPORTUNITY_DETAIL,errorModel.getCode(),errorModel.getMessage());
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(GET_OPPORTUNITY_DETAIL, response.code(), parseError(response.errorBody()));
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(GET_OPPORTUNITY_DETAIL, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(GET_OPPORTUNITY_DETAIL, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<OpportunityDetailModel> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(GET_OPPORTUNITY_DETAIL, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });

        return call;
    }

    public void getSurveyDetail(int opportunityId) {
        defaultResponseListeners.onWebServiceStart();
        String url=mContext.getString(R.string.base_url)+"v1/collaborators/surveys/"+opportunityId;
        Call<SurveyDataEntity> call = service.getSurveyDetailModel(app.getAccessToken(), url);
        call.enqueue(new Callback<SurveyDataEntity>() {
            @Override
            public void onResponse(Call<SurveyDataEntity> call, Response<SurveyDataEntity> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                        defaultResponseListeners.onResponse(GET_SURVEY_DETAIL, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(GET_SURVEY_DETAIL,errorModel.getCode(),errorModel.getMessage());
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(GET_SURVEY_DETAIL, response.code(), parseError(response.errorBody()));
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(GET_SURVEY_DETAIL, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(GET_SURVEY_DETAIL, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<SurveyDataEntity> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(GET_SURVEY_DETAIL, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }


    public void getEventDetail(String eventId) {
        defaultResponseListeners.onWebServiceStart();
        String urlEvent=""+mContext.getString(R.string.base_url)+"v1/events/"+eventId;
        Call<EventosEntity> call = service.getEventDetail(app.getAccessToken(),urlEvent);
       // call = service.getPromotionsDetail(mContext.getString(R.string.base_url)+"v1/promotions/"+id,token);
        call.enqueue(new Callback<EventosEntity>() {
            @Override
            public void onResponse(Call<EventosEntity> call, Response<EventosEntity> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                        defaultResponseListeners.onResponse(GET_EVENT_DETAIL, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(GET_EVENT_DETAIL,errorModel.getCode(),errorModel.getMessage());
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(GET_EVENT_DETAIL, response.code(), parseError(response.errorBody()));
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(GET_EVENT_DETAIL, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(GET_EVENT_DETAIL, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<EventosEntity> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(GET_EVENT_DETAIL, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }


    public void postOpportunities(String address,String version,int division, int type, String brands_items, String description, Double lat, Double lng, String state,
                                  String city, int has_media,int region) {
        defaultResponseListeners.onWebServiceStart();
        Call<NewOpportunityResponse> call = service.postOpportunity(app.getAccessToken(), address,version,division, type,
                brands_items, description, lat, lng, state, city, has_media,region);
        call.enqueue(new Callback<NewOpportunityResponse>() {
            @Override
            public void onResponse(Call<NewOpportunityResponse> call, Response<NewOpportunityResponse> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                    case 201:
                        defaultResponseListeners.onResponse(POST_OPPORTUNITY, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY,errorModel.getCode(),errorModel.getMessage());
                        break;
                    case 400:
                        String message="";
                        int code=0;
                        Pair<String,Integer> parsedJson = parseError400(getErrorBody(response.errorBody()));
                        message = parsedJson.first;
                        code = parsedJson.second;
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY,code,message);
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<NewOpportunityResponse> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(POST_OPPORTUNITY, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    public void postOpportunities(String address,String version,int division, int type, String brands_items, String description, Double lat, Double lng, String state,
                                  String city, int has_media,int region,String extra_field_title,String extra_field_value) {
        defaultResponseListeners.onWebServiceStart();
        Call<NewOpportunityResponse> call = service.postOpportunity(app.getAccessToken(),address,version, division, type,
                brands_items, description, lat, lng, state, city, has_media,region,extra_field_title,extra_field_value);
        call.enqueue(new Callback<NewOpportunityResponse>() {
            @Override
            public void onResponse(Call<NewOpportunityResponse> call, Response<NewOpportunityResponse> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                    case 201:
                        defaultResponseListeners.onResponse(POST_OPPORTUNITY, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY,errorModel.getCode(),errorModel.getMessage());
                        break;
                    case 400:
                        String message="";
                        int code=0;
                        Pair<String,Integer> parsedJson = parseError400(getErrorBody(response.errorBody()));
                        message = parsedJson.first;
                        code = parsedJson.second;
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY,code,message);
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<NewOpportunityResponse> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(POST_OPPORTUNITY, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    public void postOpportunities(String address,String version,int division, int type, String brands_items, String description, Double lat, Double lng, String state,
                                  String city, int has_media,int region,String extra_field_title) {
        defaultResponseListeners.onWebServiceStart();
        Call<NewOpportunityResponse> call = service.postOpportunity(app.getAccessToken(), address,version,division, type,
                brands_items, description, lat, lng, state, city, has_media,region,extra_field_title);
        call.enqueue(new Callback<NewOpportunityResponse>() {
            @Override
            public void onResponse(Call<NewOpportunityResponse> call, Response<NewOpportunityResponse> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                    case 201:
                        defaultResponseListeners.onResponse(POST_OPPORTUNITY, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY,errorModel.getCode(),errorModel.getMessage());
                        break;
                    case 400:
                        String message="";
                        int code=0;
                        Pair<String,Integer> parsedJson = parseError400(getErrorBody(response.errorBody()));
                        message = parsedJson.first;
                        code = parsedJson.second;
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY,code,message);
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<NewOpportunityResponse> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(POST_OPPORTUNITY, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    public void postOpportunities(String address,String version,int division, String brands_items, String description, Double lat, Double lng, String state,
                                  String city, int has_media,int region) {
        defaultResponseListeners.onWebServiceStart();
        Call<NewOpportunityResponse> call = service.postOpportunity(app.getAccessToken(),address,version, division,
                brands_items, description, lat, lng, state, city, has_media,region);
        call.enqueue(new Callback<NewOpportunityResponse>() {
            @Override
            public void onResponse(Call<NewOpportunityResponse> call, Response<NewOpportunityResponse> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                    case 201:
                        defaultResponseListeners.onResponse(POST_OPPORTUNITY, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY,errorModel.getCode(),errorModel.getMessage());
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY, response.code(), response.message());
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<NewOpportunityResponse> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(POST_OPPORTUNITY, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    public void postOpportunities(String address,String version,int division, String brands_items, String description, Double lat, Double lng, String state,
                                  String city, int has_media,int region,String extra_field_title,String extra_field_value) {
        defaultResponseListeners.onWebServiceStart();
        Call<NewOpportunityResponse> call = service.postOpportunity(app.getAccessToken(),address,version, division,
                brands_items, description, lat, lng, state, city, has_media,region,extra_field_title,extra_field_value);
        call.enqueue(new Callback<NewOpportunityResponse>() {
            @Override
            public void onResponse(Call<NewOpportunityResponse> call, Response<NewOpportunityResponse> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                    case 201:
                        defaultResponseListeners.onResponse(POST_OPPORTUNITY, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY,errorModel.getCode(),errorModel.getMessage());
                        break;
                    case 400:
                        String message="";
                        int code=0;
                        Pair<String,Integer> parsedJson = parseError400(getErrorBody(response.errorBody()));
                        message = parsedJson.first;
                        code = parsedJson.second;
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY,code,message);
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<NewOpportunityResponse> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(POST_OPPORTUNITY, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    public void postOpportunities(String address,String version,int division, String brands_items, String description, Double lat, Double lng, String state,
                                  String city, int has_media,int region,String extra_field_title) {
        defaultResponseListeners.onWebServiceStart();
        Call<NewOpportunityResponse> call = service.postOpportunity(app.getAccessToken(),address,version, division,
                brands_items, description, lat, lng, state, city, has_media,region,extra_field_title);
        call.enqueue(new Callback<NewOpportunityResponse>() {
            @Override
            public void onResponse(Call<NewOpportunityResponse> call, Response<NewOpportunityResponse> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                    case 201:
                        defaultResponseListeners.onResponse(POST_OPPORTUNITY, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY,errorModel.getCode(),errorModel.getMessage());
                        break;
                    case 400:
                        String message="";
                        int code=0;
                        Pair<String,Integer> parsedJson = parseError400(getErrorBody(response.errorBody()));
                        message = parsedJson.first;
                        code = parsedJson.second;
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY,code,message);
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<NewOpportunityResponse> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(POST_OPPORTUNITY, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    /**
     *Oportunidades ofline
     */

    public void postOpportunitiesLocal(String key_hash,String address,String version,int division, int type, String brands_items, String description, Double lat, Double lng, String state,
                                  String city, int has_media,int region) {
        defaultResponseListeners.onWebServiceStart();
        Call<NewOpportunityResponse> call = service.postOpportunityLocal(app.getAccessToken(),key_hash,address,version, division, type,
                brands_items, description, lat, lng, state, city, has_media,region);
        call.enqueue(new Callback<NewOpportunityResponse>() {
            @Override
            public void onResponse(Call<NewOpportunityResponse> call, Response<NewOpportunityResponse> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                    case 201:
                        defaultResponseListeners.onResponse(POST_OPPORTUNITY, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY,errorModel.getCode(),errorModel.getMessage());
                        break;
                    case 400:
                        String message="";
                        int code=0;
                        Pair<String,Integer> parsedJson = parseError400(getErrorBody(response.errorBody()));
                        message = parsedJson.first;
                        code = parsedJson.second;
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY,code,message);
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<NewOpportunityResponse> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(POST_OPPORTUNITY, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    public void postOpportunitiesLocal(String key_hash,String version,int division, int type, String brands_items, String description,int has_media) {
        defaultResponseListeners.onWebServiceStart();
        Call<NewOpportunityResponse> call = service.postOpportunityLocal(app.getAccessToken(),key_hash,version, division, type,
                brands_items, description, has_media);
        call.enqueue(new Callback<NewOpportunityResponse>() {
            @Override
            public void onResponse(Call<NewOpportunityResponse> call, Response<NewOpportunityResponse> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                    case 201:
                        defaultResponseListeners.onResponse(POST_OPPORTUNITY, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY,errorModel.getCode(),errorModel.getMessage());
                        break;
                    case 400:
                        String message="";
                        int code=0;
                        Pair<String,Integer> parsedJson = parseError400(getErrorBody(response.errorBody()));
                        message = parsedJson.first;
                        code = parsedJson.second;
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY,code,message);
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<NewOpportunityResponse> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(POST_OPPORTUNITY, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    public void postOpportunitiesLocal(String key_hash,String address,String version,int division, int type, String brands_items, String description, Double lat, Double lng, String state,
                                  String city, int has_media,int region,String extra_field_title,String extra_field_value) {
        defaultResponseListeners.onWebServiceStart();
        Call<NewOpportunityResponse> call = service.postOpportunityLocal(app.getAccessToken(),key_hash,address,version, division, type,
                brands_items, description, lat, lng, state, city, has_media,region,extra_field_title,extra_field_value);
        call.enqueue(new Callback<NewOpportunityResponse>() {
            @Override
            public void onResponse(Call<NewOpportunityResponse> call, Response<NewOpportunityResponse> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                    case 201:
                        defaultResponseListeners.onResponse(POST_OPPORTUNITY, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY,errorModel.getCode(),errorModel.getMessage());
                        break;
                    case 400:
                        String message="";
                        int code=0;
                        Pair<String,Integer> parsedJson = parseError400(getErrorBody(response.errorBody()));
                        message = parsedJson.first;
                        code = parsedJson.second;
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY,code,message);
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<NewOpportunityResponse> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(POST_OPPORTUNITY, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    public void postOpportunitiesLocal(String key_hash,String version,int division, int type, String brands_items, String description, int has_media,String extra_field_title,String extra_field_value) {
        defaultResponseListeners.onWebServiceStart();
        Call<NewOpportunityResponse> call = service.postOpportunityLocal(app.getAccessToken(),key_hash,version, division, type,
                brands_items, description, has_media,extra_field_title,extra_field_value);
        call.enqueue(new Callback<NewOpportunityResponse>() {
            @Override
            public void onResponse(Call<NewOpportunityResponse> call, Response<NewOpportunityResponse> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                    case 201:
                        defaultResponseListeners.onResponse(POST_OPPORTUNITY, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY,errorModel.getCode(),errorModel.getMessage());
                        break;
                    case 400:
                        String message="";
                        int code=0;
                        Pair<String,Integer> parsedJson = parseError400(getErrorBody(response.errorBody()));
                        message = parsedJson.first;
                        code = parsedJson.second;
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY,code,message);
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<NewOpportunityResponse> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(POST_OPPORTUNITY, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    public void postOpportunitiesLocal(String key_hash,String address,String version,int division, int type, String brands_items, String description, Double lat, Double lng, String state,
                                  String city, int has_media,int region,String extra_field_title) {
        defaultResponseListeners.onWebServiceStart();
        Call<NewOpportunityResponse> call = service.postOpportunityLocal(app.getAccessToken(),key_hash,address,version, division, type,
                brands_items, description, lat, lng, state, city, has_media,region,extra_field_title);
        call.enqueue(new Callback<NewOpportunityResponse>() {
            @Override
            public void onResponse(Call<NewOpportunityResponse> call, Response<NewOpportunityResponse> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                    case 201:
                        defaultResponseListeners.onResponse(POST_OPPORTUNITY, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY,errorModel.getCode(),errorModel.getMessage());
                        break;
                    case 400:
                        String message="";
                        int code=0;
                        Pair<String,Integer> parsedJson = parseError400(getErrorBody(response.errorBody()));
                        message = parsedJson.first;
                        code = parsedJson.second;
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY,code,message);
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<NewOpportunityResponse> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(POST_OPPORTUNITY, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    public void postOpportunitiesLocal(String key_hash,String version,int division, int type, String brands_items, String description, int has_media,String extra_field_title) {
        defaultResponseListeners.onWebServiceStart();
        Call<NewOpportunityResponse> call = service.postOpportunityLocal(app.getAccessToken(),key_hash,version, division, type,
                brands_items, description, has_media,extra_field_title);
        call.enqueue(new Callback<NewOpportunityResponse>() {
            @Override
            public void onResponse(Call<NewOpportunityResponse> call, Response<NewOpportunityResponse> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                    case 201:
                        defaultResponseListeners.onResponse(POST_OPPORTUNITY, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY,errorModel.getCode(),errorModel.getMessage());
                        break;
                    case 400:
                        String message="";
                        int code=0;
                        Pair<String,Integer> parsedJson = parseError400(getErrorBody(response.errorBody()));
                        message = parsedJson.first;
                        code = parsedJson.second;
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY,code,message);
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<NewOpportunityResponse> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(POST_OPPORTUNITY, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    public void postOpportunitiesLocal(String key_hash,String address,String version,int division, String brands_items, String description, Double lat, Double lng, String state,
                                  String city, int has_media,int region) {
        defaultResponseListeners.onWebServiceStart();
        Call<NewOpportunityResponse> call = service.postOpportunityLocal(app.getAccessToken(),key_hash,address,version, division,
                brands_items, description, lat, lng, state, city, has_media,region);
        call.enqueue(new Callback<NewOpportunityResponse>() {
            @Override
            public void onResponse(Call<NewOpportunityResponse> call, Response<NewOpportunityResponse> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                    case 201:
                        defaultResponseListeners.onResponse(POST_OPPORTUNITY, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY,errorModel.getCode(),errorModel.getMessage());
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY, response.code(), response.message());
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<NewOpportunityResponse> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(POST_OPPORTUNITY, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    public void postOpportunitiesLocal(String key_hash,String address,String version,int division, String brands_items, String description, Double lat, Double lng, String state,
                                  String city, int has_media,int region,String extra_field_title,String extra_field_value) {
        defaultResponseListeners.onWebServiceStart();
        Call<NewOpportunityResponse> call = service.postOpportunityLocal(app.getAccessToken(),key_hash,address,version, division,
                brands_items, description, lat, lng, state, city, has_media,region,extra_field_title,extra_field_value);
        call.enqueue(new Callback<NewOpportunityResponse>() {
            @Override
            public void onResponse(Call<NewOpportunityResponse> call, Response<NewOpportunityResponse> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                    case 201:
                        defaultResponseListeners.onResponse(POST_OPPORTUNITY, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY,errorModel.getCode(),errorModel.getMessage());
                        break;
                    case 400:
                        String message="";
                        int code=0;
                        Pair<String,Integer> parsedJson = parseError400(getErrorBody(response.errorBody()));
                        message = parsedJson.first;
                        code = parsedJson.second;
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY,code,message);
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<NewOpportunityResponse> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(POST_OPPORTUNITY, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    public void postOpportunitiesLocal(String key_hash,String address,String version,int division, String brands_items, String description, Double lat, Double lng, String state,
                                  String city, int has_media,int region,String extra_field_title) {
        defaultResponseListeners.onWebServiceStart();
        Call<NewOpportunityResponse> call = service.postOpportunityLocal(app.getAccessToken(),key_hash,address,version, division,
                brands_items, description, lat, lng, state, city, has_media,region,extra_field_title);
        call.enqueue(new Callback<NewOpportunityResponse>() {
            @Override
            public void onResponse(Call<NewOpportunityResponse> call, Response<NewOpportunityResponse> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                    case 201:
                        defaultResponseListeners.onResponse(POST_OPPORTUNITY, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY,errorModel.getCode(),errorModel.getMessage());
                        break;
                    case 400:
                        String message="";
                        int code=0;
                        Pair<String,Integer> parsedJson = parseError400(getErrorBody(response.errorBody()));
                        message = parsedJson.first;
                        code = parsedJson.second;
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY,code,message);
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(POST_OPPORTUNITY, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<NewOpportunityResponse> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(POST_OPPORTUNITY, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    public void putMediaOpportunity(int new_opportunity_id, MultipartBody.Part media_image0, MultipartBody.Part media_image1, MultipartBody.Part media_image2, MultipartBody.Part media_video0) {
        defaultResponseListeners.onWebServiceStart();
        Call<Void> call = service.putMediaOpportunity(app.getAccessToken(), new_opportunity_id, media_image0, media_image1, media_image2, media_video0);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                    case 201:
                        defaultResponseListeners.onResponse(PUT_MEDIA_OPPORTUNITY, response.raw().message(),response.body());
                        break;
                    case 401:
                        defaultResponseListeners.onFailError(PUT_MEDIA_OPPORTUNITY, response.code(), parseError(response.errorBody()));
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(PUT_MEDIA_OPPORTUNITY, response.code(),parseErrorMedia(response.errorBody()));
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(PUT_MEDIA_OPPORTUNITY, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(PUT_MEDIA_OPPORTUNITY, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(PUT_MEDIA_OPPORTUNITY, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }


    public void putProfile(RequestBody contact, final RequestBody email, RequestBody sms_code, MultipartBody.Part media_image0) {
        Call<ProfileEntity> call = service.putProfile(app.getAccessToken(),contact,email,sms_code,media_image0);
        call.enqueue(new Callback<ProfileEntity>() {
            @Override
            public void onResponse(Call<ProfileEntity> call, Response<ProfileEntity> response) {
                switch (response.code()) {
                    case 200:
                    case 201:
                        defaultResponseListeners.onResponse(PUT_PROFILE_USER, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(PUT_PROFILE_USER,errorModel.getCode(),errorModel.getMessage());
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(PUT_PROFILE_USER, response.code(),"El código SMS no coincide");
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(PUT_PROFILE_USER, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(PUT_PROFILE_USER, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<ProfileEntity> call, Throwable t) {
                if(!cancel){
                    defaultResponseListeners.onFailError(PUT_PROFILE_USER, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    public void putNewPassword(int username,int division,String password) {
        defaultResponseListeners.onWebServiceStart();
        Call<SurveySucces> call = service.putNewPassword(app.getAccessToken(),username,division,password);
        call.enqueue(new Callback<SurveySucces>() {
            @Override
            public void onResponse(Call<SurveySucces> call, Response<SurveySucces> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                    case 201:
                        defaultResponseListeners.onResponse(PUT_PASSWORD_EDIT, response.raw().message(),response.body());
                        break;
                    case 401:
                        defaultResponseListeners.onFailError(PUT_PASSWORD_EDIT, response.code(), parseError(response.errorBody()));
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(PUT_PASSWORD_EDIT, response.code(),parseError(response.errorBody()));
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(PUT_PASSWORD_EDIT, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(PUT_PASSWORD_EDIT, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<SurveySucces> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(PUT_PASSWORD_EDIT, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }


    public void putResetPassword(int username,int division,String password,int code) {
        defaultResponseListeners.onWebServiceStart();
        Call<SurveySucces> call = service.putResetPassword(app.getAccessToken(),username,division,password,code);
        call.enqueue(new Callback<SurveySucces>() {
            @Override
            public void onResponse(Call<SurveySucces> call, Response<SurveySucces> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                    case 201:
                        defaultResponseListeners.onResponse(PUT_RESET_PASSWORD, response.raw().message(),response.body());
                        break;
                    case 401:
                        defaultResponseListeners.onFailError(PUT_RESET_PASSWORD, response.code(), parseError(response.errorBody()));
                        break;
                    case 400:
                        ErrorModel errorModel2 = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(PUT_RESET_PASSWORD, errorModel2.getCode(),errorModel2.getMessage());
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(PUT_RESET_PASSWORD, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(PUT_RESET_PASSWORD, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<SurveySucces> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(PUT_RESET_PASSWORD, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    public void putResetPasswordV2(RecoverPassword recoverPassword) {
        defaultResponseListeners.onWebServiceStart();
        Call<Void> call = service.putResetPasswordV2(app.getAccessToken(),recoverPassword);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                    case 201:
                        defaultResponseListeners.onResponse(PUT_RESET_PASSWORD, response.raw().message(),response.body());
                        break;
                    case 401:
                        defaultResponseListeners.onFailError(PUT_RESET_PASSWORD, response.code(), parseError(response.errorBody()));
                        break;
                    case 400:
                        ErrorModel errorModel2 = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(PUT_RESET_PASSWORD, errorModel2.getCode(),errorModel2.getMessage());
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(PUT_RESET_PASSWORD, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(PUT_RESET_PASSWORD, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(PUT_RESET_PASSWORD, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    public void ValidateContact(String contact) {
        defaultResponseListeners.onWebServiceStart();
        Call<Void> call = service.ValidateContact(app.getAccessToken(),contact);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                    case 201:
                        defaultResponseListeners.onResponse(PUT_VALIDATE_CONTACT, response.raw().message(),response.body());
                        break;
                    case 401:
                        defaultResponseListeners.onFailError(PUT_VALIDATE_CONTACT, response.code(), parseError(response.errorBody()));
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(PUT_VALIDATE_CONTACT, response.code(), parseErrorMedia(response.errorBody()));
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(PUT_VALIDATE_CONTACT, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(PUT_VALIDATE_CONTACT, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(PUT_VALIDATE_CONTACT, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    public void putNotification(int notification_id,String status) {
        defaultResponseListeners.onWebServiceStart();
        Call<Void> call = service.putNotification(app.getAccessToken(), notification_id,status);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                    case 201:
                        defaultResponseListeners.onResponse(PUT_NOTIFICATION, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(PUT_NOTIFICATION,errorModel.getCode(),errorModel.getMessage());
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(PUT_NOTIFICATION, response.code(),  response.message());
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(PUT_NOTIFICATION, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(PUT_NOTIFICATION, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(PUT_NOTIFICATION, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    public void putRankingPrivacy(int status) {
        defaultResponseListeners.onWebServiceStart();
        Call<Void> call = service.putRankingPrivacy(app.getAccessToken(),status);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                    case 201:
                        defaultResponseListeners.onResponse(PUT_RANKING_PRIVACY, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(PUT_RANKING_PRIVACY,errorModel.getCode(),errorModel.getMessage());
                        break;
                    case 400:
                        ErrorModel errorMode2 = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(PUT_RANKING_PRIVACY,errorMode2.getCode(),errorMode2.getMessage());
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(PUT_RANKING_PRIVACY, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(PUT_RANKING_PRIVACY, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(PUT_RANKING_PRIVACY, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }


    public void putSurveyQuestion(SurveyResult surveyResult) {
        defaultResponseListeners.onWebServiceStart();
        Call<SurveySucces> call = service.putSurveyQuestion(app.getAccessToken(),surveyResult);
        call.enqueue(new Callback<SurveySucces>() {
            @Override
            public void onResponse(Call<SurveySucces> call, Response<SurveySucces> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                    case 201:
                        defaultResponseListeners.onResponse(PUT_SURVEY_QUESTION, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(PUT_SURVEY_QUESTION,errorModel.getCode(),errorModel.getMessage());
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(PUT_SURVEY_QUESTION, response.code(),  response.message());
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(PUT_SURVEY_QUESTION, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(PUT_SURVEY_QUESTION, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<SurveySucces> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(PUT_SURVEY_QUESTION, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    public void validateUser(String username, String division, String password) {
        defaultResponseListeners.onWebServiceStart();
        Call<ValidateRegistrationEntity> call = service.validateUser(username, division, password);
        call.enqueue(new Callback<ValidateRegistrationEntity>() {
            @Override
            public void onResponse(Call<ValidateRegistrationEntity> call, Response<ValidateRegistrationEntity> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                        defaultResponseListeners.onResponse(VALIDATE_USER, response.raw().message(),response.body());
                        break;
                    case 401:
                        defaultResponseListeners.onFailError(VALIDATE_USER, response.code(), parseError(response.errorBody()));
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(VALIDATE_USER, response.code(), parseError(response.errorBody()));
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(VALIDATE_USER, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(VALIDATE_USER, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<ValidateRegistrationEntity> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(VALIDATE_USER, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    public void registerUser(String username, String email, String contact, String password,
                             String division, String smsCode) {
        defaultResponseListeners.onWebServiceStart();
        Call<ProfileEntity> call = service.registerUser(username, email, contact, password, division, Integer.valueOf(smsCode));
        call.enqueue(new Callback<ProfileEntity>() {
            @Override
            public void onResponse(Call<ProfileEntity> call, Response<ProfileEntity> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                        defaultResponseListeners.onResponse(REGISTER_USER, response.raw().message(),response.body());
                        break;
                    case 201:
                        defaultResponseListeners.onResponse(REGISTER_USER, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(REGISTER_USER, errorModel.getCode(), errorModel.getMessage());
                        break;
                    case 400:
                        ErrorModel errorModel1 = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(REGISTER_USER, errorModel1.getCode(),errorModel1.getMessage());
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(REGISTER_USER, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(REGISTER_USER, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<ProfileEntity> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(REGISTER_USER, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    public void getAwards(String token) {
        defaultResponseListeners.onWebServiceStart();
        Call<List<AwardsModel>> call = service.getAwards(token);
        call.enqueue(new Callback<List<AwardsModel>>() {
            @Override
            public void onResponse(Call<List<AwardsModel>> call, Response<List<AwardsModel>> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                        defaultResponseListeners.onResponse(GET_AWARDS, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(GET_AWARDS,errorModel.getCode(),errorModel.getMessage());
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(GET_AWARDS, response.code(), parseError(response.errorBody()));
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(GET_AWARDS, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(GET_AWARDS, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<AwardsModel>> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(GET_AWARDS, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }


    public void postPassword(String token, String username,int division,String birthday) {
        defaultResponseListeners.onWebServiceStart();
        Call<SurveyResult> call = service.postPassword(token, username,division,birthday);
        call.enqueue(new Callback<SurveyResult>() {
            @Override
            public void onResponse(Call<SurveyResult>  call, Response<SurveyResult> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                        defaultResponseListeners.onResponse(POST_PASSWORD_EDIT, response.raw().message(),response.body());
                        break;
                    case 401:
                        defaultResponseListeners.onFailError(POST_PASSWORD_EDIT, response.code(), parseError(response.errorBody()));
                        break;
                    case 400:
                        ErrorModel errorModel2 = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(POST_PASSWORD_EDIT, errorModel2.getCode(),errorModel2.getMessage());
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(POST_PASSWORD_EDIT, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(POST_PASSWORD_EDIT, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<SurveyResult> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(POST_PASSWORD_EDIT, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    public void postSuggestions(String token, String message) {
        defaultResponseListeners.onWebServiceStart();
        Call<SuggestionsEntity> call = service.postSuggestions(token,message);
        call.enqueue(new Callback<SuggestionsEntity>() {
            @Override
            public void onResponse(Call<SuggestionsEntity>  call, Response<SuggestionsEntity> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                        defaultResponseListeners.onResponse(POST_SUGGESTIONS, response.raw().message(),response.body());
                        break;
                    case 401:
                        defaultResponseListeners.onFailError(POST_SUGGESTIONS, response.code(), parseError(response.errorBody()));
                        break;
                    case 400:
                        ErrorModel errorModel2 = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(POST_SUGGESTIONS, errorModel2.getCode(),errorModel2.getMessage());
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(POST_SUGGESTIONS, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(POST_SUGGESTIONS, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<SuggestionsEntity> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(POST_SUGGESTIONS, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    public void claimAwards(String token, int award_id) {
        defaultResponseListeners.onWebServiceStart();
        Call<RedeemResponse> call = service.postAwards(token, award_id);
        call.enqueue(new Callback<RedeemResponse>() {
            @Override
            public void onResponse(Call<RedeemResponse>  call, Response<RedeemResponse> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                        defaultResponseListeners.onResponse(CLAIM_AWARD, response.raw().message(),response.body());
                        break;
                    case 401:
                        defaultResponseListeners.onFailError(CLAIM_AWARD, response.code(), parseError(response.errorBody()));
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(CLAIM_AWARD, response.code(), parseError(response.errorBody()));
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(CLAIM_AWARD, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(CLAIM_AWARD, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<RedeemResponse> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(CLAIM_AWARD, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }


    /**
     * Endpoint para registrar push notifications
     * @param device_id device id
     * @param registration_id registra
     * @param type tipo de sistema operativo
     * @param name modelo del dispositivo
     */
    public void registerPushServices(String token, String device_id, String registration_id, String type, String name) {
        Call<PushRegistrationModelResponse> call = service.registerPushServices(token, device_id, registration_id, type, name);
        call.enqueue(new Callback<PushRegistrationModelResponse>() {
            @Override
            public void onResponse(Call<PushRegistrationModelResponse>  call, Response<PushRegistrationModelResponse> response) {
                switch (response.code()) {
                    case 200:
                    case 201:
                        defaultResponseListeners.onResponse(REGISTER_PUSH, response.raw().message(),response.body());
                        break;
                    case 401:
                        defaultResponseListeners.onFailError(REGISTER_PUSH, response.code(), parseError(response.errorBody()));
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(REGISTER_PUSH, response.code(), parseError(response.errorBody()));
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(REGISTER_PUSH, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(REGISTER_PUSH, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<PushRegistrationModelResponse> call, Throwable t) {
                if(!cancel){
                    defaultResponseListeners.onFailError(REGISTER_PUSH, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }


    public void deleteDevice(String token,String device,DeviceId deviceId) {
        Call<SurveyResult> call = service.deleteDevice(token,device,deviceId);
        call.enqueue(new Callback<SurveyResult>() {
            @Override
            public void onResponse(Call<SurveyResult>  call, Response<SurveyResult> response) {
                switch (response.code()) {
                    case 200:
                    case 201:
                        defaultResponseListeners.onResponse(DELETE_DEVICE, response.raw().message(),response.body());
                        break;
                    case 401:
                        defaultResponseListeners.onFailError(DELETE_DEVICE, response.code(), parseError(response.errorBody()));
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(DELETE_DEVICE, response.code(), parseError(response.errorBody()));
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(DELETE_DEVICE, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(DELETE_DEVICE, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<SurveyResult> call, Throwable t) {
                if(!cancel){
                    defaultResponseListeners.onFailError(DELETE_DEVICE, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    /**
     * Endpoint para quitar registro de push notifications
     * @param device_id device id
     */
    public void unregisterPushServices(String token, String device_id) {
        Call<PushRegistrationModelResponse> call = service.unregisterPushServices(token, device_id);
        call.enqueue(new Callback<PushRegistrationModelResponse>() {
            @Override
            public void onResponse(Call<PushRegistrationModelResponse>  call, Response<PushRegistrationModelResponse> response) {
                switch (response.code()) {
                    case 200:
                        defaultResponseListeners.onResponse(UNREGISTER_PUSH, response.raw().message(),response.body());
                        break;
                    case 401:
                        defaultResponseListeners.onFailError(UNREGISTER_PUSH, response.code(), parseError(response.errorBody()));
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(UNREGISTER_PUSH, response.code(), parseError(response.errorBody()));
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(UNREGISTER_PUSH, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(UNREGISTER_PUSH, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<PushRegistrationModelResponse> call, Throwable t) {
                if(!cancel){
                    defaultResponseListeners.onFailError(UNREGISTER_PUSH, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    public void verifyPhoneNumber(String employee_number, String phone,int division,String imss) {
        Call<ValidateRegistrationEntity> call = service.verifyPhoneNumber(employee_number, phone,division,imss);
        call.enqueue(new Callback<ValidateRegistrationEntity>() {
            @Override
            public void onResponse(Call<ValidateRegistrationEntity>  call, Response<ValidateRegistrationEntity> response) {
                switch (response.code()) {
                    case 200:
                        defaultResponseListeners.onResponse(VERIFY_PHONE, response.raw().message(),response.body());
                        break;
                    case 401:
                        defaultResponseListeners.onFailError(VERIFY_PHONE, response.code(), parseError(response.errorBody()));
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(VERIFY_PHONE, response.code(), parseError(response.errorBody()));
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(VERIFY_PHONE, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(VERIFY_PHONE, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<ValidateRegistrationEntity> call, Throwable t) {
                if(!cancel){
                    defaultResponseListeners.onFailError(VERIFY_PHONE, -1,failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }


    /**
     * modelo de puntos y posicion en el ranking de un colaborador
     */
    public void getPoints() {
        defaultResponseListeners.onWebServiceStart();
        Call<PointsResponseModel> call = service.getPoints(app.getAccessToken());
        call.enqueue(new Callback<PointsResponseModel>() {
            @Override
            public void onResponse(Call<PointsResponseModel> call, Response<PointsResponseModel> response) {
                defaultResponseListeners.onWebServiceEnd();
                switch (response.code()) {
                    case 200:
                    case 201:
                        defaultResponseListeners.onResponse(GET_POINTS, response.raw().message(),response.body());
                        break;
                    case 401:
                        defaultResponseListeners.onFailError(GET_POINTS, response.code(), response.message());
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(GET_POINTS, response.code(),  response.message());
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(GET_POINTS, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(GET_POINTS, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<PointsResponseModel> call, Throwable t) {
                defaultResponseListeners.onWebServiceEnd();
                if(!cancel){
                    defaultResponseListeners.onFailError(GET_POINTS, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    public Call<EventosResponseModel> getEventos(String query, String token) {
        Call<EventosResponseModel> call;
        if(query!=null){
            call=service.getEventos(query,app.getAccessToken());
        }else {
            call = service.getEventos(mContext.getString(R.string.base_url)+ mContext.getString(R.string.api_get_events),token);
        }
        call.enqueue(new Callback<EventosResponseModel>() {
            @Override
            public void onResponse(Call<EventosResponseModel> call, Response<EventosResponseModel> response) {
                switch (response.code()) {
                    case 200:
                        defaultResponseListeners.onResponse(GET_EVENTOS, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(GET_EVENTOS,errorModel.getCode(),errorModel.getMessage());
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(GET_EVENTOS, response.code(), parseError(response.errorBody()));
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(GET_EVENTOS, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(GET_EVENTOS, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<EventosResponseModel> call, Throwable t) {
                if(!cancel){
                    defaultResponseListeners.onFailError(GET_EVENTOS, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
        return call;
    }

    public void getNotificaciones(String query, String token) {
        Call<NotificacionesResponseModel> call;
        if(query!=null){
            call = service.getNotificaciones(query,token);
        }else {
            call = service.getNotificaciones(mContext.getString(R.string.base_url)+ mContext.getString(R.string.api_get_notificaciones),token);
        }
        call.enqueue(new Callback<NotificacionesResponseModel>() {
            @Override
            public void onResponse(Call<NotificacionesResponseModel> call, Response<NotificacionesResponseModel> response) {
                switch (response.code()) {
                    case 200:
                        defaultResponseListeners.onResponse(GET_NOTIFICACIONES, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(GET_NOTIFICACIONES,errorModel.getCode(),errorModel.getMessage());
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(GET_NOTIFICACIONES, response.code(), parseError(response.errorBody()));
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(GET_NOTIFICACIONES, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(GET_NOTIFICACIONES, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<NotificacionesResponseModel> call, Throwable t) {
                if(!cancel){
                    defaultResponseListeners.onFailError(GET_NOTIFICACIONES, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    public void getNotificaciones(String query, String token,String themes) {
        Call<NotificacionesResponseModel> call;
        if(query!=null){
            call = service.getNotificaciones(query,token,themes);
        }else {
            call = service.getNotificaciones(mContext.getString(R.string.base_url)+ mContext.getString(R.string.api_get_notificaciones),token,themes);
        }
        call.enqueue(new Callback<NotificacionesResponseModel>() {
            @Override
            public void onResponse(Call<NotificacionesResponseModel> call, Response<NotificacionesResponseModel> response) {
                switch (response.code()) {
                    case 200:
                        defaultResponseListeners.onResponse(GET_NOTIFICACIONES, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(GET_NOTIFICACIONES,errorModel.getCode(),errorModel.getMessage());
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(GET_NOTIFICACIONES, response.code(), parseError(response.errorBody()));
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(GET_NOTIFICACIONES, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(GET_NOTIFICACIONES, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<NotificacionesResponseModel> call, Throwable t) {
                if(!cancel){
                    defaultResponseListeners.onFailError(GET_NOTIFICACIONES, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }


    public void getComunicados(String query, String token) {
        Call<ComunicadosResponseModel> call;
        if(query!=null){
            call = service.getComunicados(query,token);
            //call = service.getComunicados(mContext.getString(R.string.base_url)+ mContext.getString(R.string.api_get_comunicados)+"?name="+query,token);
        }else {
            call = service.getComunicados(mContext.getString(R.string.base_url)+ mContext.getString(R.string.api_get_comunicados),token);
        }
        call.enqueue(new Callback<ComunicadosResponseModel>() {
            @Override
            public void onResponse(Call<ComunicadosResponseModel> call, Response<ComunicadosResponseModel> response) {
                switch (response.code()) {
                    case 200:
                        defaultResponseListeners.onResponse(GET_COMUNICADOS, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(GET_COMUNICADOS,errorModel.getCode(),errorModel.getMessage());
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(GET_COMUNICADOS, response.code(), parseError(response.errorBody()));
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(GET_COMUNICADOS, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(GET_COMUNICADOS, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<ComunicadosResponseModel> call, Throwable t) {
                Log.e("error",""+t.getMessage());
                if(!cancel){
                    defaultResponseListeners.onFailError(GET_COMUNICADOS, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }




    public void getChat(String query, String token, int opportunityId) {
        Call<ChatResponseModel> call;
        if(query!=null){
            call = service.getChat(query,token);
        }else {
            call = service.getChat(mContext.getString(R.string.base_url)+ mContext.getString(R.string.api_get_chat) + String.valueOf(opportunityId) ,token);
        }
        call.enqueue(new Callback<ChatResponseModel>() {
            @Override
            public void onResponse(Call<ChatResponseModel> call, Response<ChatResponseModel> response) {
                switch (response.code()) {
                    case 200:
                        defaultResponseListeners.onResponse(GET_CHAT, response.raw().message(),response.body());
                        break;
                    case 401:
                        ErrorModel errorModel = parseErrorBody(response.errorBody());
                        defaultResponseListeners.onFailError(GET_CHAT,errorModel.getCode(),errorModel.getMessage());
                        break;
                    case 400:
                        defaultResponseListeners.onFailError(GET_CHAT, response.code(), parseError(response.errorBody()));
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(GET_CHAT, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(GET_CHAT, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<ChatResponseModel> call, Throwable t) {
                if(!cancel){
                    defaultResponseListeners.onFailError(GET_CHAT, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }


    /**
     *Sprint Viernes 13 de septiembre
     */

    public void getVersionPlay(String token, String mobile) {
        Call<ResponseVersionPlayStore> call = service.getVersionPlay(mobile,token);
        call.enqueue(new Callback<ResponseVersionPlayStore>() {
            @Override
            public void onResponse(Call<ResponseVersionPlayStore> call, Response<ResponseVersionPlayStore> response) {
                switch (response.code()) {
                    case 200:
                        defaultResponseListeners.onResponse(GET_VERSION_PLAY_STORE, response.raw().message(),response.body());
                        break;
                    case 401:
                    case 400:
                        defaultResponseListeners.onFailError(GET_VERSION_PLAY_STORE, response.code(), parseError(response.errorBody()));
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(GET_VERSION_PLAY_STORE, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(GET_VERSION_PLAY_STORE, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<ResponseVersionPlayStore> call, Throwable t) {
                if(!cancel){
                    defaultResponseListeners.onFailError(GET_VERSION_PLAY_STORE, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    public Call<List<CategoriesEntity>> getFilterNotifications(String token) {
        Call<List<CategoriesEntity>> call = service.getFilterThemesNotifications(token);
        call.enqueue(new Callback<List<CategoriesEntity>>() {
            @Override
            public void onResponse(Call<List<CategoriesEntity>> call, Response<List<CategoriesEntity>> response) {
                switch (response.code()) {
                    case 200:
                        defaultResponseListeners.onResponse(GET_FILTER_THEMES_NOTIFICATIONS, response.raw().message(),response.body());
                        break;
                    case 401:
                    case 400:
                        defaultResponseListeners.onFailError(GET_FILTER_THEMES_NOTIFICATIONS, response.code(), parseError(response.errorBody()));
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(GET_FILTER_THEMES_NOTIFICATIONS, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(GET_FILTER_THEMES_NOTIFICATIONS, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<CategoriesEntity>> call, Throwable t) {
                if(!cancel){
                    defaultResponseListeners.onFailError(GET_FILTER_THEMES_NOTIFICATIONS, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
        return call;
    }

    public void getSettings() {
        Call<ResponseSettings> call = service.getSettings();
        call.enqueue(new Callback<ResponseSettings>() {
            @Override
            public void onResponse(Call<ResponseSettings> call, Response<ResponseSettings> response) {
                switch (response.code()) {
                    case 200:
                        defaultResponseListeners.onResponse(GET_CONTACT_WHATS, response.raw().message(),response.body());
                        break;
                    case 401:
                    case 400:
                        defaultResponseListeners.onFailError(GET_CONTACT_WHATS, response.code(), parseError(response.errorBody()));
                        break;
                    case 500:
                        defaultResponseListeners.onFailError(GET_CONTACT_WHATS, response.code(), "Error interno del servidor");
                        break;
                    default:
                        defaultResponseListeners.onFailError(GET_CONTACT_WHATS, response.code(), "No se pudo completar la acción");
                        break;
                }
            }

            @Override
            public void onFailure(Call<ResponseSettings> call, Throwable t) {
                if(!cancel){
                    defaultResponseListeners.onFailError(GET_CONTACT_WHATS, -1, failMessage(t));
                }else{
                    cancel=false;
                }
            }
        });
    }

    /**
     * Sets the listener for the callbacks
     * @param defaultResponseListeners Listener for the callbacks
     *  */
    public void setDefaultResponseListeners(DefaultResponseListeners defaultResponseListeners) {
        this.defaultResponseListeners = defaultResponseListeners;
    }

    @NonNull
    private String failMessage(Throwable t){
        if(t instanceof SocketTimeoutException){
            return "La conexión fue interrumpida, intentelo nuevamente";
        }
        if (!hasInternet()){
            return "No pudimos establecer la conexión";
        }
        return "No pudimos establecer la conexión";
    }


    private ErrorModel parseErrorBody(ResponseBody body){
        String mJsonString;
        try {
            mJsonString = body.string();
            JsonParser parser = new JsonParser();
            JsonElement mJson =  parser.parse(mJsonString);
            Gson gson = new Gson();
            return gson.fromJson(mJson, ErrorModel.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ErrorModel();
    }


    private String parseError(ResponseBody body){
        String mJsonString;
        try {
            mJsonString = body.string();
            JsonParser parser = new JsonParser();
            JsonElement mJson =  parser.parse(mJsonString);
            Gson gson = new Gson();
            ErrorModel object = gson.fromJson(mJson, ErrorModel.class);
            return errorMessage(String.valueOf(object.getCode()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "400";
    }

    public void logoutAction(){
        app.clearPreferences();
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mContext.startActivity(intent);
        realm.close();
    }

    private String parseErrorMedia(ResponseBody body){
        Gson gs = new Gson();
        Log.e("errroMedia",""+gs.toJson(body));
        String mJsonString;
        String codeMedia="1014";
        try {
            mJsonString = body.string();
            JsonParser parser = new JsonParser();
            JsonElement mJson =  parser.parse(mJsonString);
            Gson gson = new Gson();
            try {
                MediaError mediaError=gson.fromJson(mJson,MediaError.class);
                codeMedia=String.valueOf(mediaError.getCode());
            }catch (Exception e){
                try {
                    MediaErrorModel object=gson.fromJson(mJson,MediaErrorModel.class);
                    codeMedia=String.valueOf(object.getCode());
                }catch (Exception ex){

                }
            }
            /*MediaErrorModel object = gson.fromJson(mJson, MediaErrorModel.class);*/
           Log.e("parseMedia",""+mJson.toString()+"::gson::"+gson.toJson(mJson));
            return errorMessage(codeMedia);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "400";
    }

    private int parseErrorCode(ResponseBody body){
        String mJsonString;
        try {
            mJsonString = body.string();
            JsonParser parser = new JsonParser();
            JsonElement mJson =  parser.parse(mJsonString);
            Gson gson = new Gson();
            ErrorModel object = gson.fromJson(mJson, ErrorModel.class);
            return object.getCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 400;
    }



    /**
     * Selector of errors when onSucces
     * @param code Status code of the response
     * @return  a message of the error so it can be display to the user
     *  */
    private String errorMessage(String code){
        String message="Error ";
        switch (code){
            case "401":
                message = "Bad token";
                defaultResponseListeners.onExpireToken();
                break;
            case "402":
                message = "Contraseña invalida";
                break;
            case "403":
                message = "dosnt exist forein key";
                break;
            case "407":
                message = "El usuario ya existe";
                break;
            case "409":
                message = "Error de parametro";
                break;
            case "411":
                message = "El usuario no existe";
                break;

            case "1005":
                message = "Parámetros incompletos";
                break;
            case "1006":
                message = "El numero del empledo es requerido";
                break;
            case "1007":
                message = "El password es requerido";
                break;
            case "1008":
                message = "El usuario no se ha registrado";
                break;
            case "1009":
                message = "Acceso no permitido";
                break;
            case "1010":
                message = "Algo ha salido muy mal";
                break;
            case "1011":
                message = "El email es requerido";
                break;
            case "1012":
                message = "El correo ya esta en uso con otro numero de empleado";
                break;
            case "1013":
                message = "No se encontro el registro";
                break;
            case "1014":
                message = "No se pudo crear el archivo de media";
                break;
            case "1015":
                message = "Error al validar los archivos de multimedia";
                break;
            case "1016":
                message = "No se cargaron los archivos multimedia";
                break;
            case "1017":
                message = "Error al intentar cargar los archivos multimedia";
                break;
            case "1018":
                message = "El usuario ya esta registrado";
                break;
            case "1019":
                message = "El path del endpoint no existe";
                break;
            case "1020":
                message = "El usuario es requerido";
                break;
            case "1021":
                message = "La division es requerida";
                break;
            case "1022":
                message = "Datos incorrectos, valide de nuevo su información";
                break;
            case "1023":
                message = "Datos incorrectos, valide de nuevo su información";
                break;
            case "1024":
                message = "Usuario no registrado";
                break;
            case "1025":
                message = "El acceso ha sido bloquedo, contacte al administrador";
                break;
            case "1026":
                message = "Longitud miníma de 6 caracteres";
                break;
            case "1027":
                message = "Contraseña no segura, debe ser alfanumérica";
                break;
            case "1029":
                message = "No existe la oportunidad";
                break;
            case "1028":
                message = "Acceso no válido";
                break;
            case "1030":
                message = "Usuario no activo";
                break;
        }
        return message;
    }

    public void alertDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Se ha iniciado sesión desde otro dispositivo con la misma cuenta de usuario")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        logoutAction();
                    }
                })
                .show();
    }


    private  void onResponseMethod (int tag, Call<?> call, Response<?> response){
        HandledResponse handledResponse = handleResponse(call, response);
        if (handledResponse.isNoAnError()) {
            defaultResponseListeners.onResponse(tag, handledResponse.getMessage(), response.body());
        } else {
            defaultResponseListeners.onFailError(tag, handledResponse.getCode(), handledResponse.getMessage());
        }
    }

    private HandledResponse handleResponse(Call<?> call, Response<?> response) {
        defaultResponseListeners.onWebServiceEnd();
        boolean notAnError = true;
        String message = "";
        int code = response.code();
        switch (response.code()) {
            case 200:
            case 201:
            case 204:
                message = response.message();
                break;
            case 400:
                notAnError = false;
                Pair<String,Integer> parsedJson = parseError400(getErrorBody(response.errorBody()));
                message = parsedJson.first;
                code = parsedJson.second;
                break;
            case 404:
                notAnError = false;
                message = "No Found";
                break;
            case 500:
                notAnError = false;
                message = "Internal Server Error";
                break;
            case 503:
                notAnError = false;
                message = "Service Unavailable";
                break;
        }
        return new HandledResponse(notAnError, code, message);
    }

    private FailError failError(Throwable t) {
        defaultResponseListeners.onWebServiceEnd();
        int code=0;
        String message="";
        if (t instanceof SocketTimeoutException){
            code=504;
            message="La conexión de internet ha sido interrumpida, intentelo nuevamente.";
        }else if (t instanceof TimeoutException){
            code=505;
            message="No pudimos establecer una conexion";
        } else if (t instanceof ConnectException){
            code = 506;
            message="No pudimos establecer una conexion";
        }else if (t instanceof IllegalStateException){
            code=507;
            message="Hay objetos nulos";
        } else if (t instanceof IOException){
            code=507;
            message=""+t.getCause();
        }else{
            code = 508;
            message=""+t.getCause();
        }
        return new FailError(code,message);
    }

    private Pair<String, Integer> parseError400(String errorBody) {
        if (TextUtils.isEmpty(errorBody)) {
            return new Pair<>("", 400);
        }
        try {
            JSONObject object = new JSONObject(errorBody);
            String code = object.optString("code");
                if (code!=null && code!="") {
                    return new Pair<>(object.optString("message", ""), Integer.parseInt(code));//errorMessages(Integer.valueOf(code));
                }else{
                    return new Pair<>(object.optString("message", ""),2000);
                }
            } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Pair<>("", 400);
    }

    private String getErrorBody(ResponseBody response){
        try {
            if (response!=null){
                return  response.string();
            }else{
                return  "";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void onFailMethod(Call<?> call , int tag, Throwable t){
        defaultResponseListeners.onWebServiceEnd();
        if (!cancel) {
            FailError failError = failError(t);
            defaultResponseListeners.onFailError(tag, failError.getCode(),failError.getMessage());
        } else {
            cancel = false;
        }
    }

    private boolean hasInternet() {
        if (mContext!=null) {
            ConnectivityManager con = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = con.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected())
                return true;
            else
                return false;
        }else{
            return false;
        }
    }
}
