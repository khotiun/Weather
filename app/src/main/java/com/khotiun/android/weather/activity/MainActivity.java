package com.khotiun.android.weather.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import com.khotiun.android.weather.R;
import com.khotiun.android.weather.fragment.CityListFragment;
import com.khotiun.android.weather.fragment.WeatherListFragment;
import com.khotiun.android.weather.model.CityNameLab;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements WeatherListFragment.FavoriteCityEventListener, CityListFragment.ListCityEventListener {

    // TAG= class name prefix - just my way of logging
    private static final String TAG = "MainActivity";

    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(mViewPager);
        mTabLayout = (TabLayout) findViewById(R.id.tablayout);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    //add city into favorite list
    @Override
    public void addCityEvent() {
        CityListFragment fragListCity = (CityListFragment) adapter.getItem(1);
        fragListCity.getAdapter().redrawAdapter(CityNameLab.getCityNameLab(this).getCityNames());
    }

    //show detail weather
    @Override
    public void listCityEvent(String city) {
        WeatherListFragment fragSearchCity = (WeatherListFragment) adapter.getItem(0);
        ((EditText) fragSearchCity.getView().findViewById(R.id.weather_enter_city)).setText(city);
        fragSearchCity.getResponse();
        mViewPager.setCurrentItem(0);
    }

    //Add fragments to the adapter and sets the adapter to ViewPager
    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(WeatherListFragment.newInstance(), getString(R.string.city_cearch));
        adapter.addFragment(CityListFragment.newInstance(), getString(R.string.favorite_city));
        viewPager.setAdapter(adapter);
    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private final List<Fragment> mList = new ArrayList<>();
        private final List<String> mTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mList.get(position);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);
        }

        //for add fragment into ViewPager
        public void addFragment(Fragment fragment, String title) {
            mList.add(fragment);
            mTitleList.add(title);
        }
    }
}
