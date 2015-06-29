package com.wangshihai.noticeweather.util;

/**
 * Created by shiha on 2015-06-28.
 */
public interface HttpCallBackListener {
    void onFinish(String response);
    void onError(Exception e);
}
