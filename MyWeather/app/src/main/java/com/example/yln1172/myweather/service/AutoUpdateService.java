package com.example.yln1172.myweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.example.yln1172.myweather.receiver.AutoUpdateReceiver;
import com.example.yln1172.myweather.utils.HttpCallBackListener;
import com.example.yln1172.myweather.utils.HttpUtil;
import com.example.yln1172.myweather.utils.Utility;

import java.util.Calendar;

/**
 * Created by yln1172 on 2017/2/13.
 */
public class AutoUpdateService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();

        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        int eightHours = 8 * 60 * 60 *1000; //8小时毫秒数
        //int eightHours = 10*1000; //8小时毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + eightHours;
        Intent i = new Intent(this,AutoUpdateReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this,0,i,0);
        /*
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 5);
        */
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        //manager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pi);
        return super.onStartCommand(intent,flags,startId);
    }

    private void updateWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weather_code = prefs.getString("weather_code","");
        String address = "http://www.weather.com.cn/data/cityinfo/"+ weather_code + ".html";
        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(AutoUpdateService.this,response);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
