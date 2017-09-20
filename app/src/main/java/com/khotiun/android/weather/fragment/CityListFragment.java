package com.khotiun.android.weather.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.khotiun.android.weather.R;
import com.khotiun.android.weather.model.CityName;
import com.khotiun.android.weather.model.CityNameLab;

import java.util.List;

/**
 * Created by hotun on 16.09.2017.
 */

public class CityListFragment extends Fragment {

    // TAG= class name prefix - just my way of logging
    private static final String TAG = "CityListFragment";

    private RecyclerView mCityRecyclerView;

    private CityAdapter mAdapter;

    private List<CityName> mList;
    private ListCityEventListener mListCityEventListener;

    public static Fragment newInstance() {
        return new CityListFragment();
    }

    // for show detail information city
    public interface ListCityEventListener {
        public void listCityEvent(String city);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListCityEventListener = (ListCityEventListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement ListCityEventListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_city_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCityRecyclerView = (RecyclerView) view.findViewById(R.id.list_city);
        mCityRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mList = CityNameLab.getCityNameLab(getActivity()).getCityNames();

        mAdapter = new CityAdapter(mList);
        mCityRecyclerView.setAdapter(mAdapter);
    }

    public CityAdapter getAdapter() {
        return mAdapter;
    }

    private class CityHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageButton mDeleteCityView;
        private TextView mNameView;

        private CityName mCityName;

        public void bindCity(CityName cityName) {
            mCityName = cityName;
            mNameView.setText(cityName.getName());

        }

        public CityHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mNameView = (TextView) itemView.findViewById(R.id.item_city_title);
            mDeleteCityView = (ImageButton) itemView.findViewById(R.id.item_city_delete);
            mDeleteCityView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.item_city_delete) {
                deleteCity();
            } else if (id == R.id.item_city_card) {
                Log.d(TAG, "item");
                mListCityEventListener.listCityEvent(mCityName.getName());
            }
        }

        //delete a city from the list
        private void deleteCity() {
            Log.d(TAG, "Delete city");
            mList.remove(mCityName);
            CityNameLab.getCityNameLab(getActivity()).deleteCityName(mCityName);
            mAdapter.notifyDataSetChanged();
        }
    }

    public class CityAdapter extends RecyclerView.Adapter<CityHolder> {

        private List<CityName> mCityList;

        public CityAdapter(List<CityName> cityNames) {
            mCityList = cityNames;
        }

        @Override
        public CityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CityHolder(LayoutInflater.from(getActivity()).inflate(R.layout.item_city, parent, false));
        }

        @Override
        public void onBindViewHolder(CityHolder holder, int position) {
            CityName cityName = mCityList.get(position);
            holder.bindCity(cityName);
        }

        @Override
        public int getItemCount() {
            return mCityList.size();
        }

        // for redraw adapter with WeatherListFragment
        public void redrawAdapter(List<CityName> cityNames) {
            mList = cityNames;
            mAdapter.setList(cityNames);
        }

        public void setList(List<CityName> cityNames) {
            mCityList = cityNames;
            notifyDataSetChanged();
        }
    }
}
