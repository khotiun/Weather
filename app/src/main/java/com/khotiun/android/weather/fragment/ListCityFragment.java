package com.khotiun.android.weather.fragment;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.khotiun.android.weather.R;
import com.khotiun.android.weather.model.CityName;
import com.khotiun.android.weather.model.CityNameLab;

import java.util.List;

/**
 * Created by hotun on 16.09.2017.
 */

public class ListCityFragment extends Fragment {
    private static String TAG = "ListCityFragment";
    private RecyclerView mCityRecyclerView;
    private CityAdapter mAdapter;
    private List<CityName> mList;

    public static Fragment newInstance() {
        return new ListCityFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_city, container, false);
        mCityRecyclerView = (RecyclerView) view.findViewById(R.id.list_city_rv);
        mCityRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mList = CityNameLab.getCityNameLab(getActivity()).getCityNames();
        mAdapter = new CityAdapter(mList);
        mCityRecyclerView.setAdapter(mAdapter);
        return view;
    }

    private class CityHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageButton mImDeleteCity;
        private TextView mNameTextView;
        private CityName mCityName;

        public void bindCity(CityName cityName) {
            mCityName = cityName;
            mNameTextView.setText(cityName.getName());

        }

        public CityHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mNameTextView = (TextView) itemView.findViewById(R.id.item_city_tv);
            mImDeleteCity = (ImageButton) itemView.findViewById(R.id.item_city_ib_delete);
            mImDeleteCity.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if(id == R.id.item_city_ib_delete){
                mList.remove(mCityName);
                CityNameLab.getCityNameLab(getActivity()).deleteCityName(mCityName);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private class CityAdapter extends RecyclerView.Adapter<CityHolder> {

        private List<CityName> mCityNames;

        public CityAdapter(List<CityName> cityNames) {
            mCityNames = cityNames;
        }

        @Override
        public CityHolder onCreateViewHolder(ViewGroup parent, int viewType) {//вызывается виджетом RecyclerView, когда ему потребуется новое представление для отображения элемента.
            // В этом методе мы создаем объект View и упаковываем его в ViewHolder. RecyclerView пока не ожидает,что представление будет связано с какими-либо данными.
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.item_city, parent, false);
            return new CityHolder(view);
        }

        @Override
        public void onBindViewHolder(CityHolder holder, int position) {//этот метод связывает представление View объекта ViewHolder с объектом модели. При вызове он получает ViewHolder
            // и позицию в наборе данных. Позиция используется для нахождения правильных данных модели, после чего View обновляется в соответствии с этими данными.
            CityName cityName = mCityNames.get(position);
            Log.d(TAG, cityName.getName());
            holder.bindCity(cityName);
        }

        @Override
        public int getItemCount() {
            return mCityNames.size();
        }
    }
}
