package com.wangshihai.noticeweather.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wangshihai.noticeweather.R;
import com.wangshihai.noticeweather.util.HTTPUtil;
import com.wangshihai.noticeweather.util.HttpCallBackListener;
import com.wangshihai.noticeweather.util.JSONUtil;

/**
 * Created by shiha on 2015-07-02.
 */
public class WeatherActivity extends Activity {
    private LinearLayout linearLayout;
    private TextView pushTime;
    private TextView cityNameText;
    private TextView weather;
    private TextView temp1;
    private TextView temp2;
    private TextView currentTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        linearLayout = (LinearLayout) findViewById(R.id.weath_info);
        cityNameText = (TextView) findViewById(R.id.county_name);
        pushTime = (TextView) findViewById(R.id.push_time);
        weather = (TextView) findViewById(R.id.weath_desp);
        temp1 = (TextView) findViewById(R.id.temp1);
        temp2 = (TextView) findViewById(R.id.temp2);
        currentTime = (TextView) findViewById(R.id.current_date);
        String countyCode = getIntent().getStringExtra("countyCode");
        if(!TextUtils.isEmpty(countyCode)){
            pushTime.setText("同步中...");
            linearLayout.setVisibility(View.VISIBLE);
            cityNameText.setVisibility(View.VISIBLE);
            queryWeatherCode(countyCode);
        }else{
            showWeather();
        }
    }

    private void queryWeatherCode(String countyCode){
        String address = "http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
        queryWeatherInfo(address, "countyCode");
    }
    private void queryWeather(String countyCode){
        String address = "http://www.weather.com.cn/data/cityinfo/"+countyCode+".html";
        queryWeatherInfo(address,"weatherCode");
    }
    private void queryWeatherInfo(String address, final String type){
        HTTPUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                if(!TextUtils.isEmpty(response)){
                    if("countyCode".equals(type)){
                        String[] array = response.split("\\|");
                        if(array != null && array.length > 0){
                            String weatherCode = array[1];
                            queryWeather(weatherCode);
                        }
                    }else  if("weatherCode".equals(type)){
                        JSONUtil.handleWeatherResponse(WeatherActivity.this,response);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showWeather();
                            }
                        });
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        pushTime.setText("同步失败");
                    }
                });
            }
        });

    }
    private void showWeather(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        cityNameText.setText(pref.getString("county_name",""));
        temp1.setText(pref.getString("temp1",""));
        temp2.setText(pref.getString("temp2",""));
        weather.setText(pref.getString("weathInfo",""));
        pushTime.setText("发布时间:"+pref.getString("pushTime",""));
        currentTime.setText("现在时间:"+pref.getString("current_time",""));
        linearLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);




    }
}
