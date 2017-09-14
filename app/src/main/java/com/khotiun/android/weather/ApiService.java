package com.khotiun.android.weather;

import com.khotiun.android.weather.model.WeatherData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by hotun on 14.09.2017.
 */

public interface ApiService {

    @GET("data/2.5/forecast")
    Call<WeatherData> getWeather(@Query("q") String query,
                                 @Query("appid") String appid);
}

