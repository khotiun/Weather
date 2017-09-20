package com.khotiun.android.weather.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by hotun on 09.07.2017.
 */

public class WeatherBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "WeatherBase.db";

    public WeatherBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    //create table
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + WeatherDbSchema.WeatherTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                WeatherDbSchema.WeatherTable.Cols.CITY + ")"
        );
    }

    //upgrade db
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
