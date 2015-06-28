package com.wangshihai.noticeweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by shiha on 2015-06-28.
 */
public class NoticeWeatherOpenHelper extends SQLiteOpenHelper {

    public static String CREATE_PROVINCE = "create table Province(\n" +
            "    id integer primary key autoincrement,\n" +
            "province_name text,\n" +
            "province_code text\n" +
            ")";
    public static String CREATE_CITY = "create table city(\n" +
            "    id integer primary key autoincrement,\n" +
            "    city_name text,\n" +
            "    city_code text,\n" +
            "    province_id integer\n" +
            ")";
    public static String CREATE_COUNTY = "create table county(\n" +
            "    id integer primary key autoincrement,\n" +
            "    county_name text,\n" +
            "    county_code text,\n" +
            "    city_id integer\n" +
            ")";

    public NoticeWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_COUNTY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
