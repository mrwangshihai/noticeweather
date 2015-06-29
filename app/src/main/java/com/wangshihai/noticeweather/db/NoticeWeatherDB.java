package com.wangshihai.noticeweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wangshihai.noticeweather.model.City;
import com.wangshihai.noticeweather.model.County;
import com.wangshihai.noticeweather.model.Province;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by shiha on 2015-06-28.
 */
public class NoticeWeatherDB {
    public static final String DB_NAME = "notice_weather";
    public static final int VERSION = 1;
    public static NoticeWeatherDB noticeWeatherDB;
    private SQLiteDatabase db;

    private NoticeWeatherDB(Context context){
        NoticeWeatherOpenHelper dbHelper = new NoticeWeatherOpenHelper(context,DB_NAME,null,VERSION);
        db = dbHelper.getWritableDatabase();
    }

    public synchronized  static NoticeWeatherDB getIntence (Context context){
        if(noticeWeatherDB == null){
            noticeWeatherDB = new NoticeWeatherDB(context);
        }
        return noticeWeatherDB;
    }

    public void saveCity(City city){
        if(city != null){
            ContentValues contentValues = new ContentValues();
            contentValues.put("city_name",city.getCity_name());
            contentValues.put("city_code",city.getCity_code());
            contentValues.put("province_id",city.getProvince_id());
            db.insert("city",null,contentValues);
        }
    }

    public List<City> getCitys(int provinceId){
        List<City> citys = new ArrayList<City>();
        Cursor cursor = db.query("city",null,"province_id=?",new String[]{String.valueOf(provinceId)},null,null,null,null);

        if(cursor.getCount() > 0 && cursor.moveToFirst()){
            do{
                City city = new City();
                city.setCity_id(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCity_code(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setCity_name(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setProvince_id(cursor.getInt(cursor.getColumnIndex("province_id")));
                citys.add(city);
            }while (cursor.moveToNext());
        }
        if(cursor !=null){
            cursor.close();
        }
        return  citys;
    }

    public void saveProvince(Province province){
        if(province != null){
            ContentValues contentValues = new ContentValues();
            contentValues.put("province_name",province.getProvince_name());
            contentValues.put("province_code",province.getProvince_code());
            db.insert("province",null,contentValues);
        }
    }

    public List<Province> getProvinces(){
        List<Province> provinces = new ArrayList<Province>();
        Cursor cursor = db.query("province",null,null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvince_name(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvince_code(cursor.getString(cursor.getColumnIndex("province_code")));
                provinces.add(province);
            }while (cursor.moveToNext());
        }
        if(cursor !=null){
            cursor.close();
        }
        return  provinces;
    }

    public void saveCounty(County county){
        if(county != null){
            ContentValues contentValues = new ContentValues();
            contentValues.put("county_name",county.getCounty_name());
            contentValues.put("county_code",county.getCounty_code());
            contentValues.put("city_id",county.getCity_id());
            db.insert("county",null,contentValues);
        }
    }

    public List<County> getCountys(int cityId){
        List<County> countys = new ArrayList<County>();
        Cursor cursor = db.query("county",null,"city_id=?",new String[]{String.valueOf(cityId)},null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                County county = new County();
                county.setCity_id(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCounty_name(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCounty_code(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCity_id(cursor.getInt(cursor.getColumnIndex("city_id")));
                countys.add(county);
            }while (cursor.moveToNext());
        }
        if(cursor !=null){
            cursor.close();
        }
        return  countys;
    }

}
