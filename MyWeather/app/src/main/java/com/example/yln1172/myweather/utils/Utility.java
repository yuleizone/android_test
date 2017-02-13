package com.example.yln1172.myweather.utils;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.yln1172.myweather.db.MyWeatherDB;
import com.example.yln1172.myweather.model.City;
import com.example.yln1172.myweather.model.County;
import com.example.yln1172.myweather.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yln1172 on 2017/2/7.
 */
public class Utility {
    public synchronized static boolean handleProvinceResponse(MyWeatherDB myWeatherDB,String response){
        if(!TextUtils.isEmpty(response)){
            String[] allProvinces = response.split(",");
            if(allProvinces != null && allProvinces.length > 0){
                for(String temp : allProvinces){
                    String[] array = temp.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    myWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    public  static boolean handleCityResponse(MyWeatherDB myWeatherDB,String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            String[] allCities = response.split(",");
            if(allCities != null && allCities.length > 0){
                for(String temp : allCities){
                    String[] array = temp.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    myWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return  false;
    }

    public static boolean handleCountyResponse(MyWeatherDB myWeatherDB,String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            String[] allCounties = response.split(",");
            if(allCounties != null && allCounties.length > 0){
                for(String temp : allCounties){
                    String[] array = temp.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    myWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return  false;
    }

    public static void handleWeatherResponse(Context context,String response){
        try{
            JSONObject jsonObject =new JSONObject((response));
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String tempStart = weatherInfo.getString("temp1");
            String tempEnd = weatherInfo.getString("temp2");
            String weatherDsc = weatherInfo.getString("weather");
            String publishTime = weatherInfo.getString("ptime");
            saveWeatherInfo(context,cityName,weatherCode,tempStart,tempEnd,weatherDsc,publishTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static void saveWeatherInfo(Context context, String cityName, String weatherCode, String temp1, String temp2, String weatherDsc, String publishTime){
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        SimpleDateFormat sdf =   new SimpleDateFormat("yyyy年MM月dd日",Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name",cityName);
        editor.putString("weather_code",weatherCode);
        editor.putString("temp_start",temp1);
        editor.putString("temp_end",temp2);
        editor.putString("weather_dsc",weatherDsc);
        editor.putString("publish_time",publishTime);
        editor.putString("current_date",sdf.format(new Date()));
        editor.commit();
    }

}
