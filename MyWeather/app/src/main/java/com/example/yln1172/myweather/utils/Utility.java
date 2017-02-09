package com.example.yln1172.myweather.utils;

import android.text.TextUtils;

import com.example.yln1172.myweather.db.MyWeatherDB;
import com.example.yln1172.myweather.model.City;
import com.example.yln1172.myweather.model.County;
import com.example.yln1172.myweather.model.Province;

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

}
