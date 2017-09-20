package com.khotiun.android.weather.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.khotiun.android.weather.model.CityName;

/**
 * Created by hotun on 09.07.2017.
 */

public class WeatherCursorWrapper extends CursorWrapper {

    public WeatherCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    //for take city name
    public CityName getCityName() {
        String name = getString(getColumnIndex(WeatherDbSchema.WeatherTable.Cols.CITY));
        CityName cityName = new CityName(name);

        return cityName;
    }
}
