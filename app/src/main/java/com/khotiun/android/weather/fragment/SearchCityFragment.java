package com.khotiun.android.weather.fragment;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.khotiun.android.weather.R;
import com.khotiun.android.weather.api.ApiService;
import com.khotiun.android.weather.api.RetroClient;
import com.khotiun.android.weather.model.CityName;
import com.khotiun.android.weather.model.CityNameLab;
import com.khotiun.android.weather.model.ListWeather;
import com.khotiun.android.weather.model.WeatherData;
import com.khotiun.android.weather.utils.Config;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hotun on 15.09.2017.
 */

public class SearchCityFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "SearchCityFragment";
    private EditText etCity;
    private Button btnSearchCity;
    private TextView tvCityName;
    private RelativeLayout mRelativeLayout;
    private RecyclerView mRecyclerView;
    private CoordinatorLayout mCoordinatorLayout;
    private FloatingActionButton mFab;
    private WeatherAdapter mAdapter;
    private WeatherData mWeatherData;

    public static Fragment newInstance() {
        return new SearchCityFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_city, container, false);
        mRelativeLayout = (RelativeLayout) view.findViewById(R.id.city_rl);
        mCoordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.city_coordinator_layout);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.city_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        etCity = (EditText) view.findViewById(R.id.city_et_search);
        tvCityName = (TextView) view.findViewById(R.id.city_tv_name);
        mFab = (FloatingActionButton) view.findViewById(R.id.city_fab);
        mFab.setOnClickListener(this);
        btnSearchCity = (Button) view.findViewById(R.id.city_btn_search_city);
        btnSearchCity.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.city_btn_search_city){
            ApiService api = RetroClient.getApiService();
            Call<WeatherData> call = api.getWeather(etCity.getText().toString(), Config.API_KEY);
            //enqueue for asynchronously
            call.enqueue(new Callback<WeatherData>() {
                @Override
                public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                    Log.d(TAG, "onResponse");
                    mRelativeLayout.setVisibility(View.VISIBLE);
                    mWeatherData = response.body();
                    tvCityName.setText(mWeatherData.getCity().getName());
                    mAdapter = new WeatherAdapter(mWeatherData.getList());
                    mRecyclerView.setAdapter(mAdapter);
                        if (response.isSuccessful()) {
                            Log.d(TAG, "response.isSuccessful()");
                        }
                }
                @Override
                public void onFailure(Call<WeatherData> call, Throwable t) {
                    Log.d(TAG, t.toString());
                }
            });
        } else if(viewId == R.id.city_fab){
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Add " + tvCityName.getText().toString() + " to favorites?", Snackbar.LENGTH_LONG)
                    .setAction("Да", snackbarOnClickListener);
            snackbar.show();
        }
    }

    View.OnClickListener snackbarOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CityName cityName = new CityName(tvCityName.getText().toString());
            CityNameLab.getCityNameLab(getActivity()).addCityName(cityName);
            mFab.setImageResource(R.drawable.ic_star_filt);
            Log.d(TAG, CityNameLab.getCityNameLab(getActivity()).getCityNames().size() + "");

        }
    };

    private String convertDay(String oldDateString) {

        SimpleDateFormat oldDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat newDayFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);

        Date date = null;
        try {
            date = oldDateFormat.parse(oldDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String resultDay = newDayFormat.format(date);

        return resultDay;
    }

    private String convertDate(String oldDateString) {

        SimpleDateFormat oldTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat newTimeFormat = new SimpleDateFormat("dd/MM", Locale.ENGLISH);

        Date time = null;
        try {
            time = oldTimeFormat.parse(oldDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String resultTime = newTimeFormat.format(time);

        return resultTime;
    }

    private String convertTime(String oldDateString) {

        SimpleDateFormat oldTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat newTimeFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

        Date time = null;
        try {
            time = oldTimeFormat.parse(oldDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String resultTime = newTimeFormat.format(time);

        return resultTime;
    }

    private void setImage(String mUrl, ImageView view) {
        Picasso.with(getContext()).load(mUrl)
                .placeholder(R.mipmap.empty_photo)
                .into(view);
    }

    private class WeatherHolder extends RecyclerView.ViewHolder {

        private TextView tvDay, tvDayMounth, tvTime, tvWiather, tvTempMin, tvTempMax;
        private ImageView ivWither;
        private ListWeather mDataWeather;

        public WeatherHolder(View itemView) {
            super(itemView);
            tvDay = (TextView) itemView.findViewById(R.id.item_tv_day);
            tvDayMounth = (TextView) itemView.findViewById(R.id.item_tv_date);
            tvTime = (TextView) itemView.findViewById(R.id.item_tv_time);
            tvWiather = (TextView) itemView.findViewById(R.id.item_tv_weather);
            tvTempMin = (TextView) itemView.findViewById(R.id.item_tv_temp_min);
            tvTempMax = (TextView) itemView.findViewById(R.id.item_tv_temp_max);
            ivWither = (ImageView) itemView.findViewById(R.id.item_iv);
        }

        public void bindDataWeather(ListWeather dataWeather) {
            mDataWeather = dataWeather;
            String oldDateString = dataWeather.getDtTxt();
            String resultDay = convertDay(oldDateString);
            String resultDate = convertDate(oldDateString);
            String resultTime = convertTime(oldDateString);
            tvCityName.setText(mWeatherData.getCity().getName());
            tvDay.setText(resultDay);
            tvDayMounth.setText(resultDate);
            tvTime.setText(resultTime);
            tvWiather.setText(dataWeather.getWeather().get(0).getDescription());
            tvTempMin.setText(dataWeather.getMain().getTempMin() + "");
            tvTempMax.setText(dataWeather.getMain().getTempMax() + "");
            String urlWeather = Config.URL_WEATHER + dataWeather.getWeather().get(0).getIcon() + ".png";
            setImage(urlWeather, ivWither);

        }
    }

    private class WeatherAdapter extends RecyclerView.Adapter<WeatherHolder> {

        private List<ListWeather> mListWeathers;

        public WeatherAdapter(List<ListWeather> listWeathers) {
            mListWeathers = listWeathers;
        }

        @Override
        public WeatherHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.item_weather, parent, false);
            return new WeatherHolder(view);
        }

        @Override
        public void onBindViewHolder(WeatherHolder holder, int position) {
            ListWeather dataWeather = mListWeathers.get(position);
            holder.bindDataWeather(dataWeather);
        }

        @Override
        public int getItemCount() {
            return mListWeathers.size();
        }
    }
}