package com.wangshihai.noticeweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wangshihai.noticeweather.R;
import com.wangshihai.noticeweather.db.NoticeWeatherDB;
import com.wangshihai.noticeweather.model.City;
import com.wangshihai.noticeweather.model.County;
import com.wangshihai.noticeweather.model.Province;
import com.wangshihai.noticeweather.util.HTTPUtil;
import com.wangshihai.noticeweather.util.HttpCallBackListener;
import com.wangshihai.noticeweather.util.JSONUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private ListView listView;
    private TextView textView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<String>();
    private NoticeWeatherDB noticeWeatherDB;
    private List<Province> provinces;
    private List<City> citys;
    private List<County> countys;
    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;
    private int currentLevel;
    private static final int PROVINCE_LEVEL = 0;
    private static final int CITY_LEVEL = 1;
    private static final  int COUNTY_LEVEL = 2;
    private ProgressDialog progressDialog;
    private boolean isFromWeatherActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity",false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPreferences.getBoolean("county_selected",false) && !isFromWeatherActivity){
            Intent intent = new Intent(MainActivity.this,WeatherActivity.class);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.list);
        textView = (TextView) findViewById(R.id.title);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        noticeWeatherDB = NoticeWeatherDB.getIntence(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel == PROVINCE_LEVEL){
                    selectedProvince = provinces.get(position);
                    queryCitys();
                }else if(currentLevel == CITY_LEVEL){
                    selectedCity = citys.get(position);
                    queryCountys();
                }else if(currentLevel == COUNTY_LEVEL){
                    String countyCode = countys.get(position).getCounty_code();
                    Intent intent = new Intent(MainActivity.this,WeatherActivity.class);
                    intent.putExtra("countyCode",countyCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvince();
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/
    private void queryProvince(){
        provinces = noticeWeatherDB.getProvinces();
        if(provinces != null && provinces.size() > 0){
            dataList.clear();
            for (Province province:provinces
                 ) {
                dataList.add(province.getProvince_name());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText("中国");
            currentLevel = PROVINCE_LEVEL;
        }else{
            queryFromServer(null,"province");
        }
    }

    private void  queryCitys(){
        citys = noticeWeatherDB.getCitys(selectedProvince.getId());
        if(citys != null && citys.size() > 0 ){
            dataList.clear();
            for (City city: citys
                 ) {
                dataList.add(city.getCity_name());
            }
            adapter.notifyDataSetChanged();
            textView.setText(selectedProvince.getProvince_name());
            currentLevel = CITY_LEVEL;
        }else{
            queryFromServer(selectedProvince.getProvince_code(),"city");
        }
    }

    private void queryCountys(){
        countys = noticeWeatherDB.getCountys(selectedCity.getCity_id());
        if(countys != null && countys.size() > 0 ){
            dataList.clear();
            for (County county:countys
                 ) {
                dataList.add(county.getCounty_name());
            }
            adapter.notifyDataSetChanged();
            textView.setText(selectedCity.getCity_name());
            currentLevel = COUNTY_LEVEL;
        }else{
            queryFromServer(selectedCity.getCity_code(),"county");
        }
    }

    private void queryFromServer(final String code, final String type){
        String address;
        if(!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        }else{
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HTTPUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if("province".equals(type)){
                    result = JSONUtil.handleProvinceResponse(noticeWeatherDB,response);
                }else if("city".equals(type)){
                    result = JSONUtil.handleCityResponse(noticeWeatherDB,response,selectedProvince.getId());
                }else if("county".equals(type)){
                    result = JSONUtil.handleCountyReponse(noticeWeatherDB,response,selectedCity.getCity_id());
                }
                if(result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvince();
                            }else if("city".equals(type)){
                                queryCitys();
                            }else if("county".equals(type)){
                                queryCountys();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(MainActivity.this,"数据加载失败,请重试！",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }

    private void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("数据加载中，请稍后...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog(){
        if(progressDialog != null){
            progressDialog.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        if(currentLevel == COUNTY_LEVEL){
            queryCitys();
        }else if(currentLevel == CITY_LEVEL){
            queryProvince();
        }else{
            if(isFromWeatherActivity){
                Intent intent = new Intent(this,WeatherActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }
}
