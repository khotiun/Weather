package com.khotiun.android.weather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.khotiun.android.weather.model.WeatherData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.R.attr.data;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private EditText etCity;
    private Button btnSearchCity;
    private TextView tvInfo;
    private WeatherData mWeatherData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etCity = (EditText) findViewById(R.id.activity_main_et_city);
        tvInfo = (TextView) findViewById(R.id.activity_main_tv);
        btnSearchCity = (Button) findViewById(R.id.activity_main_btn_search_city);
        btnSearchCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Creating an object of our api interface
                ApiService api = RetroClient.getApiService();
                Call<WeatherData> call = api.getWeather(etCity.getText().toString(), Config.API_KEY);
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
    }
}
