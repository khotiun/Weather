package com.khotiun.android.weather;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.khotiun.android.weather.api.ApiService;
import com.khotiun.android.weather.api.RetroClient;
import com.khotiun.android.weather.model.WeatherData;
import com.khotiun.android.weather.utils.Config;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hotun on 15.09.2017.
 */

public class SearchCityFragment extends Fragment {

    private static final String TAG = "SearchCityFragment";
    private EditText etCity;
    private Button btnSearchCity;
    private TextView tvInfo;
    private WeatherData mWeatherData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_city, container, false);
        etCity = (EditText) view.findViewById(R.id.fragment_cearch_city_et_city);
        tvInfo = (TextView) view.findViewById(R.id.fragment_cearch_city_tv);
        btnSearchCity = (Button) view.findViewById(R.id.fragment_cearch_city_btn_search_city);
        btnSearchCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Creating an object of our api interface
                ApiService api = RetroClient.getApiService();
                Call<WeatherData> call = api.getWeather(etCity.getText().toString(), Config.API_KEY);
                //enqueue
                call.enqueue(new Callback<WeatherData>() {
                    @Override
                    public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                        Log.d(TAG, "onResponse");
                        mWeatherData = response.body();
                        tvInfo.setText(mWeatherData.getCity().toString());
                        if (response.isSuccessful()) {
                            Log.d(TAG, "response.isSuccessful()");
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherData> call, Throwable t) {
                        Log.d(TAG, t.toString());
                    }
                });
            }
        });
        return view;
    }
}
