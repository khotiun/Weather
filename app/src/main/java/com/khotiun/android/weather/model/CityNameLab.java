package com.khotiun.android.weather.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.khotiun.android.weather.database.WeatherBaseHelper;
import com.khotiun.android.weather.database.WeatherCursorWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.R.attr.id;
import static com.khotiun.android.weather.database.WeatherDbSchema.WeatherTable;


/**
 * Created by hotun on 30.06.2017.
 */
//singleton
public class CityNameLab {
    private static CityNameLab sCityNameLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    // there can be only one object of this class
    public static CityNameLab getCityNameLab(Context context) {
        if (sCityNameLab == null) {
            sCityNameLab = new CityNameLab(context);
        }
        return sCityNameLab;
    }

    private CityNameLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new WeatherBaseHelper(mContext).getWritableDatabase();//get bd for write
    }

    public void addCityName(CityName c) {
        ContentValues values = getContentValues(c);
        mDatabase.insert(WeatherTable.NAME, null, values);
    }

    public List<CityName> getCityNames() {
        List<CityName> citys = new ArrayList<>();

        WeatherCursorWrapper cursor = queryCrimes(null, null);

        try {

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                citys.add(cursor.getCityName());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return citys;
    }

    private static ContentValues getContentValues(CityName cityName) {
        ContentValues values = new ContentValues();
        values.put(WeatherTable.Cols.CITY, cityName.getName());

        return values;
    }

    //read bd
    private WeatherCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                WeatherTable.NAME,
                null, // Columns - null select all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );

        return new WeatherCursorWrapper(cursor);
    }

    public void deleteCityName(CityName c) {
        mDatabase.delete(WeatherTable.NAME, WeatherTable.Cols.CITY + " = ?", new String[]{c.getName()});
    }
}
