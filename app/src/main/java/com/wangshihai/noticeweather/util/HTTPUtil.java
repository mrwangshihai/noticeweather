package com.wangshihai.noticeweather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by shiha on 2015-06-28.
 */
public class HTTPUtil {

    public static void sendHttpRequest(final  String address,final HttpCallBackListener httpCallBackListener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection httpURLConnection = null;
                try {
                    URL url = new URL(address);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setConnectTimeout(8000);
                    httpURLConnection.setReadTimeout(8000);
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuffer reponse = new StringBuffer();
                    String line;
                    while((line = bufferedReader.readLine()) != null){
                        reponse.append(line);
                    }
                    if(bufferedReader != null) {
                        bufferedReader.close();
                    }
                    if(httpCallBackListener != null){
                        httpCallBackListener.onFinish(reponse.toString());
                    }
                } catch (Exception e) {
                    httpCallBackListener.onError(e);
                }
            }
        }).start();
    }
}
