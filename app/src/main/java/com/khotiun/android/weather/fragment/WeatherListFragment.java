package com.khotiun.android.weather.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.TextView;

import com.khotiun.android.weather.R;
import com.khotiun.android.weather.api.ApiService;
import com.khotiun.android.weather.api.RetroClient;
import com.khotiun.android.weather.model.CityName;
import com.khotiun.android.weather.model.CityNameLab;
import com.khotiun.android.weather.model.ListWeather;
import com.khotiun.android.weather.model.WeatherData;
import com.khotiun.android.weather.utils.Config;
import com.khotiun.android.weather.utils.InternetConnection;
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

public class WeatherListFragment extends Fragment implements View.OnClickListener {

    // TAG= class name prefix - just my way of logging
    private static final String TAG = "WeatherListFragment";

    private EditText mEnterCityView;
    private ImageButton mSearchCityView;
    private TextView mCityNameView;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mAddCityView;
    private CoordinatorLayout mCoordinatorLayout;
    private TextInputLayout mTextInputLayout;
    private ProgressBar mProgressBar;

    private WeatherAdapter mAdapter;

    private WeatherData mWeatherData;
    private boolean isFaforiteCity = false;

    // for add favorite city
    private FavoriteCityEventListener someEventListener;

    public static Fragment newInstance() {
        return new WeatherListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public interface FavoriteCityEventListener {
        public void addCityEvent();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            someEventListener = (FavoriteCityEventListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement FavoriteCityEventListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weather_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgressBar = (ProgressBar) view.findViewById(R.id.weather_progress_bar);
        mTextInputLayout = (TextInputLayout) view.findViewById(R.id.weather_til);
        mCoordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.weather_coordinator_layout);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.weather_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCityNameView = (TextView) view.findViewById(R.id.weather_city_name);
        mAddCityView = (FloatingActionButton) view.findViewById(R.id.weather_add_to_favorite);
        mAddCityView.setOnClickListener(this);
        mSearchCityView = (ImageButton) view.findViewById(R.id.weather_search);
        mSearchCityView.setOnClickListener(this);
        mEnterCityView = (EditText) view.findViewById(R.id.weather_enter_city);
        mEnterCityView.addTextChangedListener(new TextWatcher() {
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
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.weather_search) {
            getResponse();
        } else if (viewId == R.id.weather_add_to_favorite) {
            compareCity();
            if (!isFaforiteCity) {
                Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Add " + mCityNameView.getText().toString() + " to favorites?", Snackbar.LENGTH_LONG)
                        .setAction(getResources().getString(R.string.yes), snackbarOnClickListener);
                snackbar.show();
            } else {
                Snackbar.make(mCoordinatorLayout, getResources().getString(R.string.this_city_is_already_added_to_favorites), Snackbar.LENGTH_SHORT).show();
            }
            isFaforiteCity = false;
        }
    }
    //to receive a response from the server
    public void getResponse() {
        if (mEnterCityView.getText().toString().equals("")) {
            mTextInputLayout.setError(getResources().getString(R.string.empty_field));
            return;
        }
        mProgressBar.setVisibility(View.VISIBLE);
        checkingInternetConnection();
        hideKeyboard();
        ApiService api = RetroClient.getApiService();
        Call<WeatherData> call = api.getWeather(mEnterCityView.getText().toString(), Config.API_KEY);
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
                mCityNameView.setText(mWeatherData.getCity().getName());
                mAddCityView.setImageResource(R.drawable.ic_star_border_white);
                compareCity();
                mCoordinatorLayout.setVisibility(View.VISIBLE);

                mAdapter = new WeatherAdapter(mWeatherData.getList());
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                Log.d(TAG, "onFailure");
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    //Hide keyboard
    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    //Internet connection check
    private void checkingInternetConnection() {
        if (!InternetConnection.checkConnection(getActivity().getApplicationContext())) {
            final AlertDialog.Builder dialog;

            //Alert Dialog for User Interaction
            Log.d(TAG, "Internet");
            dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle(getString(R.string.can_not_connect_to_the_internet));
            dialog.setMessage(getString(R.string.retry));
            dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (InternetConnection.checkConnection(getActivity().getApplicationContext())) {
                        dialog.cancel();
                    } else {
                        checkingInternetConnection();
                    }
                }
            });
            dialog.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }
            });
            dialog.show();
        }
    }

    //Find the coincidence of cities if a match is found to change the icon fab
    private void compareCity() {
        List<CityName> cityNames = CityNameLab.getCityNameLab(getActivity()).getCityNames();
        for (CityName city : cityNames) {
            if (city.getName().equals(mCityNameView.getText().toString())) {
                mAddCityView.setImageResource(R.drawable.ic_star_filt);
                isFaforiteCity = true;
            }
        }
    }

    //snackbar listener
    View.OnClickListener snackbarOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CityName cityName = new CityName(mCityNameView.getText().toString());
            getCityNameLab(getActivity()).addCityName(cityName);
            mAddCityView.setImageResource(R.drawable.ic_star_filt);
            Log.d(TAG, getCityNameLab(getActivity()).getCityNames().size() + "");
            someEventListener.addCityEvent();
        }
    };

    private class WeatherHolder extends RecyclerView.ViewHolder {

        private TextView mDayView, mDateView, mTimeView, mWiatherView, mTempMinView, mTempMaxView;
        private ImageView mPictureWitherView;

        private SimpleDateFormat mOldTimeFormat, mNewTimeFormat;
        private Date mDate;

        public WeatherHolder(View itemView) {
            super(itemView);
            mDayView = (TextView) itemView.findViewById(R.id.item_weather_day);
            mDateView = (TextView) itemView.findViewById(R.id.item_weather_date);
            mTimeView = (TextView) itemView.findViewById(R.id.item_weather_time);
            mWiatherView = (TextView) itemView.findViewById(R.id.item_weather_sky);
            mTempMinView = (TextView) itemView.findViewById(R.id.item_weather_temp_min);
            mTempMaxView = (TextView) itemView.findViewById(R.id.item_weather_temp_max);
            mPictureWitherView = (ImageView) itemView.findViewById(R.id.item_weather_picture);
        }

        public void bindDataWeather(ListWeather dataWeather) {
            String oldDateString = dataWeather.getDtTxt();
            String resultDay = convertDay(oldDateString);
            String resultDate = convertDate(oldDateString);
            String resultTime = convertTime(oldDateString);
            mCityNameView.setText(mWeatherData.getCity().getName());
            mDayView.setText(resultDay);
            mDateView.setText(resultDate);
            mTimeView.setText(resultTime);
            mWiatherView.setText(dataWeather.getWeather().get(0).getDescription());
            int tempMin = getTemp(dataWeather.getMain().getTempMin());
            int tempMax = getTemp(dataWeather.getMain().getTempMax());
            mTempMinView.setText(tempMin + "°C");
            mTempMaxView.setText(tempMax + "°C");
            String urlWeather = Config.URL_PICTURE + dataWeather.getWeather().get(0).getIcon() + ".png";
            setImage(urlWeather, mPictureWitherView);

        }

        private void setImage(String mUrl, ImageView view) {
            Picasso.with(getContext()).load(mUrl)
                    .placeholder(R.mipmap.empty_photo)
                    .into(view);
        }

        private int getTemp(double temp) {
            return (int) Math.round(temp - 273);
        }

        //get the day in the format Mon
        private String convertDay(String oldDateString) {
            mOldTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            mNewTimeFormat = new SimpleDateFormat("E", Locale.ENGLISH);
            mDate = null;

            try {
                mDate = mOldTimeFormat.parse(oldDateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String resultDay = mNewTimeFormat.format(mDate);

            return resultDay;
        }

        //get the date in the format dd/MM
        private String convertDate(String oldDateString) {
            mOldTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            mNewTimeFormat = new SimpleDateFormat("dd/MM", Locale.ENGLISH);
            mDate = null;

            try {
                mDate = mOldTimeFormat.parse(oldDateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String resultTime = mNewTimeFormat.format(mDate);

            return resultTime;
        }
        //get the time in the format HH:mm
        private String convertTime(String oldDateString) {
            mOldTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            mNewTimeFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
            mDate = null;

            try {
                mDate = mOldTimeFormat.parse(oldDateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String resultTime = mNewTimeFormat.format(mDate);

            return resultTime;
        }
    }

    private class WeatherAdapter extends RecyclerView.Adapter<WeatherHolder> {

        private List<ListWeather> mWeatherList;

        public WeatherAdapter(List<ListWeather> listWeathers) {
            mWeatherList = listWeathers;
        }

        @Override
        public WeatherHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new WeatherHolder(LayoutInflater.from(getActivity()).inflate(R.layout.item_weather, parent, false));
        }

        @Override
        public void onBindViewHolder(WeatherHolder holder, int position) {
            ListWeather dataWeather = mWeatherList.get(position);
            holder.bindDataWeather(dataWeather);
        }

        @Override
        public int getItemCount() {
            return mWeatherList.size();
        }
    }
}