package com.dacodes.bepensa;

import java.io.IOException;

import okhttp3.Response;

/**
 * created by Carlos Chin Ku
 * email:efrainck94@gmail.com
 */
public class Interceptor implements okhttp3.Interceptor {

    private AppController appController;
    public Interceptor(AppController appController){
        this.appController = appController;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        final Response proceed = chain.proceed(chain.request());
        proceed.newBuilder().addHeader("Authorization",appController.getAccessToken()).build();
        return proceed;
    }
}
