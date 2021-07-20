package com.dacodes.bepensa.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.entities.PackegeOpportunity.DivisionEntity;
import com.dacodes.bepensa.models.HandledResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RetrofitServices {

    private AppController app;
    private Services service;
    private String logTag;
    private Context mContext;
    private DefaultResponseListeners defaultResponseListeners;
    Realm realm;

    public enum WebServicesTags{
        TAG_GET_DIVISIONS, TAG_LOG_IN,TAG_ON_FAIL_CALL
    }


    boolean cancel=false;
    public interface DefaultResponseListeners {
        void onResponse(WebServicesTags id, String message, Object object);
        void onFailError(WebServicesTags id, int status, String message);
        void onWebServiceStart();
        void onWebServiceEnd();
        void onExpireToken();
    }



    public RetrofitServices(AppController app, String logTag, Context context,
                               DefaultResponseListeners defaultResponseListeners) {
        this.app = app;
        Retrofit retrofit = app.getRetrofit();
        this.service = retrofit.create(Services.class);
        this.logTag = logTag;
        this.mContext = context;
        this.defaultResponseListeners = defaultResponseListeners;
        realm = Realm.getDefaultInstance();
    }

    public Call<Object> getDatas(Object object){
        Call<Object> call=null;
        if (object instanceof DivisionEntity){

        }

        return call;
    }

    public Call<List<Object>> getListDatas(WebServicesTags tags){
        if (tags==WebServicesTags.TAG_GET_DIVISIONS) {
            Log.i(logTag,"TAG_GET_DIVISIONS");
            return service.getDivisions();
        }
        else {
            Log.i(logTag,"ON_FAILS_CALL");
            return null;
        }
    }

    public Call<Object> getObjet(WebServicesTags tags){
        if (tags==WebServicesTags.TAG_GET_DIVISIONS) {
            Log.i(logTag,"TAG_GET_DIVISIONS");
            return null;
        }
        else {
            Log.i(logTag,"ON_FAILS_CALL");
            return null;
        }
    }

    public void executeCallListObject(WebServicesTags tags) {
        Call<List<Object>> apiList = getListDatas(tags);
        if (apiList!=null) {
            apiList.enqueue(new Callback<List<Object >>() {
                @Override
                public void onResponse(Call<List<Object>> call, Response<List<Object>> response) {
                    onResponseMethod(tags,call,response);
                }

                @Override
                public void onFailure(Call<List<Object>> call, Throwable t) {
                    onFailMethod(call,tags,t);
                }
            });
        }else{
            onFailMethod(apiList,WebServicesTags.TAG_ON_FAIL_CALL,new Throwable());
        }
    }

    public void executeCallObject(WebServicesTags tags) {
        Call<Object> apiList = getObjet(tags);
        if (apiList!=null) {
            apiList.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    onResponseMethod(tags,call,response);
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    onFailMethod(call,tags,t);
                }
            });
        }else{
            onFailMethod(apiList,WebServicesTags.TAG_ON_FAIL_CALL,new Throwable());
        }
    }


    public void callApi(final Object classz) {

        Call<Object> apiList = getDatas(classz);
        apiList.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {

            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.d("ApiCall_Error", t.toString());
            }
        });
    }



    private void onResponseMethod (WebServicesTags tag, Call<?> call, Response<?> response){
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
                Pair<String, Integer> parsedJson = parseError400(getErrorBody(response.errorBody()));
                message = parsedJson.first;
                code = parsedJson.second;
                break;
            case 401:
                notAnError = false;
                Pair<String, Integer> parsedJson2 = parseError401(getErrorBody(response.errorBody()));
                message = parsedJson2.first;
                code = parsedJson2.second;
                //logoutAction();
                break;
            case 403:
                notAnError = false;
                message = "Forbidden";
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
        Log.v(logTag, "message: " + message);
        return new HandledResponse(notAnError, code, message);
    }

    private Pair<String, Integer> parseError400(String errorBody) {
        if (TextUtils.isEmpty(errorBody)) {
            return new Pair<>("", 400);
        }
        try {
            JSONObject object = new JSONObject(errorBody);
            String code = object.optString("code");
            return new Pair<>(object.optString("message", ""), Integer.parseInt(code));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Pair<>("", 400);
    }

    private Pair<String, Integer> parseError401(String errorBody) {
        if (TextUtils.isEmpty(errorBody)) {
            return new Pair<>("", 400);
        }
        try {
            JSONObject object = new JSONObject(errorBody);
            String code = object.optString("code");
            return new Pair<>(object.optString("message", ""), Integer.parseInt(code));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Pair<>("", 401);
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

    private void onFailMethod(Call<?> call , WebServicesTags tag, Throwable t){
        defaultResponseListeners.onWebServiceEnd();
        if (!cancel) {
            defaultResponseListeners.onFailError(tag, -1, failMessage(t));
        } else {
            cancel = false;
        }
    }

    private boolean hasInternet() {
        ConnectivityManager con = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = con.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    @NonNull
    private String failMessage(Throwable t) {
        if (t instanceof SocketTimeoutException) {
            return "La conexión de internet ha sido interrumpida, intentelo nuevamente.";
        }
        if (!hasInternet()) {
            return "Error de conexión. Revise su conexión a internet.";
        }
        return "Error de conexión."+t.getLocalizedMessage();
    }
}
