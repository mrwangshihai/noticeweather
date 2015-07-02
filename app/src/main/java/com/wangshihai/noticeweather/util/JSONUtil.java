package com.wangshihai.noticeweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.wangshihai.noticeweather.db.NoticeWeatherDB;
import com.wangshihai.noticeweather.model.City;
import com.wangshihai.noticeweather.model.County;
import com.wangshihai.noticeweather.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by shiha on 2015-06-28.
 */
public class JSONUtil {

    public static synchronized boolean handleProvinceResponse(NoticeWeatherDB noticeWeatherDB,String reponse){
        if(!TextUtils.isEmpty(reponse)){
            String[] provinces = reponse.split(",");
            if(provinces != null && provinces.length > 0){
                for(int i = 0;i < provinces.length;i++){
                    String[] provinceData = provinces[i].split("\\|");
                    Province province = new Province();
                    province.setProvince_code(provinceData[0]);
                    province.setProvince_name(provinceData[1]);
                    noticeWeatherDB.saveProvince(province);
                }
                return true;
            }
            return true;
        }else{
            return false;
        }
    }

    public  static  synchronized boolean handleCityResponse(NoticeWeatherDB noticeWeatherDB,String reponse,int provinceId){
        if(!TextUtils.isEmpty(reponse)){
            String[] citys = reponse.split(",");
            if(citys != null && citys.length > 0 ){
                    for (String cityStr : citys
                ){
                    String[] cityData = cityStr.split("\\|");
                    City city = new City();
                    city.setProvince_id(provinceId);
                        city.setCity_code(cityData[0]);
                        city.setCity_name(cityData[1]);
                        noticeWeatherDB.saveCity(city);
                }
                return true;
            }
            return false;
        }else{
            return false;
        }
    }

    public static synchronized boolean handleCountyReponse(NoticeWeatherDB noticeWeatherDB,String reponse,int cityId){
        if(!TextUtils.isEmpty(reponse)){
            String[] countys = reponse.split(",");
            if(countys != null && countys.length > 0){
                for (String countyData :countys
                     ) {
                    String [] countyArray = countyData.split("\\|");
                    County county = new County();
                    county.setCity_id(cityId);
                    county.setCounty_code(countyArray[0]);
                    county.setCounty_name(countyArray[1]);
                    noticeWeatherDB.saveCounty(county);

                }
                return  true;
            }
            return false;
        }else{
            return false;
        }
    }

    public static  void handleWeatherResponse(Context context,String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
            String countyName = weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weatherDesp = weatherInfo.getString("weather");
            String pushTime = weatherInfo.getString("ptime");
            saveWeathInfo(context,countyName,pushTime,temp1,temp2,weatherDesp,weatherCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void saveWeathInfo(Context context,String countyName,String pushTime,String temp1,String temp2,String weathInfo,String weatherCode){
        SimpleDateFormat simpleDateFormat
                = new SimpleDateFormat("yyyy年M月d日 HH时mm分ss秒", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("county_selected",true);
        editor.putString("county_name",countyName);
        editor.putString("pushTime",pushTime);
        editor.putString("temp1",temp1);
        editor.putString("temp2",temp2);
        editor.putString("weathInfo",weathInfo);
        editor.putString("weatherCode",weatherCode);
        editor.putString("current_time",simpleDateFormat.format(Calendar.getInstance().getTime()));
        editor.commit();
    }

}
