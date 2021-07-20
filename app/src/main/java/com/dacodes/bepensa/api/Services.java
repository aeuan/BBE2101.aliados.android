package com.dacodes.bepensa.api;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface Services {

    @GET("v1/divisions/")
    Call<List<Object>> getDivisions();
}
