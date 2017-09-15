package com.khotiun.android.weather.api;

import android.support.annotation.NonNull;

import com.khotiun.android.weather.utils.Config;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hotun on 14.09.2017.
 */

public class RetroClient {

    //Get Retrofit Instance
    @NonNull
    private static Retrofit getRetrofitInstance() {
        return new Retrofit.Builder()
                .baseUrl(Config.ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    //Get API Service
    public static ApiService getApiService() {
        return getRetrofitInstance().create(ApiService.class);
    }
}
