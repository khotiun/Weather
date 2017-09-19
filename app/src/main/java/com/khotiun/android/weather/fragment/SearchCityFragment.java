package com.khotiun.android.weather.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.khotiun.android.weather.R;
import com.khotiun.android.weather.activity.MainActivity;
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

import static com.khotiun.android.weather.model.CityNameLab.getCityNameLab;

/**
 * Created by hotun on 15.09.2017.
 */

public class SearchCityFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "SearchCityFragment";
    private EditText mEtCity;
    private ImageButton mIBtnSearchCity;
    private TextView mTvCityName;
    private RelativeLayout mRelativeLayout;
    private RecyclerView mRecyclerView;
    private CoordinatorLayout mCoordinatorLayout;
    private FloatingActionButton mFab;
    private TextInputLayout mTextInputLayout;
    private ProgressBar mProgressBar;
    private WeatherAdapter mAdapter;
    private WeatherData mWeatherData;
    private boolean isFaforiteCity = false;
    private OnSomeEventListener someEventListener;

    public static Fragment newInstance() {
        return new SearchCityFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public interface OnSomeEventListener {
        public void someEvent();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            someEventListener = (OnSomeEventListener) context;
        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_city, container, false);
        mProgressBar = (ProgressBar) view.findViewById(R.id.city_pb);
        mTextInputLayout = (TextInputLayout) view.findViewById(R.id.city_til);
        mRelativeLayout = (RelativeLayout) view.findViewById(R.id.city_rl);
        mCoordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.city_coordinator_layout);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.city_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTvCityName = (TextView) view.findViewById(R.id.city_tv_name);
        mFab = (FloatingActionButton) view.findViewById(R.id.city_fab);
        mFab.setOnClickListener(this);
        mIBtnSearchCity = (ImageButton) view.findViewById(R.id.city_ibtn_search_city);
        mIBtnSearchCity.setOnClickListener(this);
        mEtCity = (EditText) view.findViewById(R.id.city_et_search);
        mEtCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTextInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.city_ibtn_search_city) {
            if (mEtCity.getText().toString().equals("")) {
                mTextInputLayout.setError(getResources().getString(R.string.empty_field));
                return;
            }
            mProgressBar.setVisibility(View.VISIBLE);
            //Hide keyboard
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            ApiService api = RetroClient.getApiService();
            Call<WeatherData> call = api.getWeather(mEtCity.getText().toString(), Config.API_KEY);
            //enqueue for asynchronously
            call.enqueue(new Callback<WeatherData>() {
                @Override
                public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                    Log.d(TAG, "onResponse");
                    mProgressBar.setVisibility(View.GONE);
                    mWeatherData = response.body();
                    if (mWeatherData == null) {
                        mTextInputLayout.setError(getResources().getString(R.string.Incorrect_city_name));
                        return;
                    }
                    isFaforiteCity = false;
                    mTvCityName.setText(mWeatherData.getCity().getName());
                    mFab.setImageResource(R.drawable.ic_star_border_white);
                  coincidenceCity();
                    mCoordinatorLayout.setVisibility(View.VISIBLE);
                    mAdapter = new WeatherAdapter(mWeatherData.getList());
                    mRecyclerView.setAdapter(mAdapter);
                }

                @Override
                public void onFailure(Call<WeatherData> call, Throwable t) {
                    Log.d(TAG, "onFailure");
                }
            });
        } else if (viewId == R.id.city_fab) {
            coincidenceCity();
            if (!isFaforiteCity) {
                Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Add " + mTvCityName.getText().toString() + " to favorites?", Snackbar.LENGTH_LONG)
                        .setAction("yes", snackbarOnClickListener);
                snackbar.show();
            } else {
                Snackbar.make(mCoordinatorLayout, getResources().getString(R.string.this_city_is_already_added_to_favorites), Snackbar.LENGTH_SHORT).show();
            }
            isFaforiteCity = false;
        }
    }

    private void coincidenceCity() {
        List<CityName> cityNames = CityNameLab.getCityNameLab(getActivity()).getCityNames();
        for (CityName city : cityNames) {
            if (city.getName().equals(mTvCityName.getText().toString())) {
                mFab.setImageResource(R.drawable.ic_star_filt);
                isFaforiteCity = true;
            }
        }
    }

    View.OnClickListener snackbarOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CityName cityName = new CityName(mTvCityName.getText().toString());
            getCityNameLab(getActivity()).addCityName(cityName);
            mFab.setImageResource(R.drawable.ic_star_filt);
            Log.d(TAG, getCityNameLab(getActivity()).getCityNames().size() + "");
            someEventListener.someEvent();
        }
    };


    private class WeatherHolder extends RecyclerView.ViewHolder {

        private TextView tvDay, tvDayMounth, tvTime, tvWiather, tvTempMin, tvTempMax;
        private ImageView ivWither;

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

            String oldDateString = dataWeather.getDtTxt();
            String resultDay = convertDay(oldDateString);
            String resultDate = convertDate(oldDateString);
            String resultTime = convertTime(oldDateString);
            mTvCityName.setText(mWeatherData.getCity().getName());
            tvDay.setText(resultDay);
            tvDayMounth.setText(resultDate);
            tvTime.setText(resultTime);
            tvWiather.setText(dataWeather.getWeather().get(0).getDescription());
            int tempMin = getTemp(dataWeather.getMain().getTempMin());
            int tempMax = getTemp(dataWeather.getMain().getTempMax());
            tvTempMin.setText(tempMin + "°C");
            tvTempMax.setText(tempMax + "°C");
            String urlWeather = Config.URL_WEATHER + dataWeather.getWeather().get(0).getIcon() + ".png";
            setImage(urlWeather, ivWither);

        }

        private void setImage(String mUrl, ImageView view) {
            Picasso.with(getContext()).load(mUrl)
                    .placeholder(R.mipmap.empty_photo)
                    .into(view);
        }

        private int getTemp(double temp) {
            return (int) Math.round(temp - 273);
        }

        private String convertDay(String oldDateString) {

            SimpleDateFormat oldDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat newDayFormat = new SimpleDateFormat("E", Locale.ENGLISH);

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