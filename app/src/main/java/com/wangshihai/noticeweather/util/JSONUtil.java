package com.wangshihai.noticeweather.util;

import android.text.TextUtils;

import com.wangshihai.noticeweather.db.NoticeWeatherDB;
import com.wangshihai.noticeweather.model.City;
import com.wangshihai.noticeweather.model.County;
import com.wangshihai.noticeweather.model.Province;

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

}
