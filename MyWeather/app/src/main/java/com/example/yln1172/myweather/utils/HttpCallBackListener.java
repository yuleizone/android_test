package com.example.yln1172.myweather.utils;

/**
 * Created by yln1172 on 2017/2/7.
 */
public interface HttpCallBackListener {
    void onFinish(String response);
    void onError(Exception e);
}
