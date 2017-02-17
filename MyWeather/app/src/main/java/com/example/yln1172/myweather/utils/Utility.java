package com.example.yln1172.myweather.utils;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.yln1172.myweather.db.MyWeatherDB;
import com.example.yln1172.myweather.model.BackupInfo;
import com.example.yln1172.myweather.model.City;
import com.example.yln1172.myweather.model.County;
import com.example.yln1172.myweather.model.Province;
import com.example.yln1172.myweather.model.WeatherInfo;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
            //saveWeatherInfo(context,cityName,weatherCode,tempStart,tempEnd,weatherDsc,publishTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void parseWeatherResponseXml(Context context,String response, String weatherCode){
        List<WeatherInfo> weatherInfoList = new ArrayList<>();
        BackupInfo backupInfo = new BackupInfo();
        backupInfo.setWeatherCode(weatherCode);
        try{
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(response));
            int eventType = xmlPullParser.getEventType();
            WeatherInfo info = null;
            while(eventType != XmlPullParser.END_DOCUMENT){
                String nodeName = xmlPullParser.getName();
                switch (eventType){
                    case XmlPullParser.START_TAG:{
                        if("city".equals(nodeName)){
                            //城市名称
                            backupInfo.setCityName(xmlPullParser.nextText());
                        } else if("updatetime".equals(nodeName)){
                            //更新时间
                            backupInfo.setPublishTime(xmlPullParser.nextText());
                        }else if("weather".equals(nodeName)) {
                            info = new WeatherInfo();
                        }else if("date".equals(nodeName)){
                            //星期几,一般是xx日星期x格式
                            String temp = xmlPullParser.nextText();
                            int npos = temp.indexOf("日");
                            if(npos != -1){
                                temp = temp.substring(npos + 1);
                            }
                            info.setDate(temp);
                        }
                        else if("high".equals(nodeName)){
                            //最高温度
                            String temp = xmlPullParser.nextText();
                            //去掉"高温 "
                            temp = temp.substring(3);
                            info.setHighTemp(temp);
                        }else if("low".equals(nodeName)){
                            //最低温度
                            String temp = xmlPullParser.nextText();
                            //去掉"低温 "
                            temp = temp.substring(3);
                            info.setLowTemp(temp);
                        } else if("type".equals(nodeName)){
                            //日期描述
                            info.setWeatherDsc(xmlPullParser.nextText());
                        }
                    }
                    break;
                    case XmlPullParser.END_TAG:{
                       if("weather".equals(nodeName)){
                            weatherInfoList.add(info);
                        }
                    }
                    break;
                    default:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //SimpleDateFormat sdf =   new SimpleDateFormat("dd日",Locale.CHINA);
        //String day = sdf.format(new Date());
        //筛选出当天的天气,正常的话有5条,依次为今天，明天，后天，大后天，大大后天
        if(weatherInfoList.size() != 5){
            return;
        }

        backupInfo.setCurrentTemperatureDsc(weatherInfoList.get(0).getWeatherDsc());
        backupInfo.setCurrentTemperatureStart(weatherInfoList.get(0).getLowTemp());
        backupInfo.setCurrentTemperatureEnd(weatherInfoList.get(0).getHighTemp());
        backupInfo.setCurrentDate(weatherInfoList.get(0).getDate());

        backupInfo.setTemperatureDsc1(weatherInfoList.get(1).getWeatherDsc());
        backupInfo.setTemperatureStart1(weatherInfoList.get(1).getLowTemp());
        backupInfo.setTemperatureEnd1(weatherInfoList.get(1).getHighTemp());
        backupInfo.setDate1(weatherInfoList.get(1).getDate());

        backupInfo.setTemperatureDsc2(weatherInfoList.get(2).getWeatherDsc());
        backupInfo.setTemperatureStart2(weatherInfoList.get(2).getLowTemp());
        backupInfo.setTemperatureEnd2(weatherInfoList.get(2).getHighTemp());
        backupInfo.setDate2(weatherInfoList.get(2).getDate());

        backupInfo.setTemperatureDsc3(weatherInfoList.get(3).getWeatherDsc());
        backupInfo.setTemperatureStart3(weatherInfoList.get(3).getLowTemp());
        backupInfo.setTemperatureEnd3(weatherInfoList.get(3).getHighTemp());
        backupInfo.setDate3(weatherInfoList.get(3).getDate());

        backupInfo.setTemperatureDsc4(weatherInfoList.get(4).getWeatherDsc());
        backupInfo.setTemperatureStart4(weatherInfoList.get(4).getLowTemp());
        backupInfo.setTemperatureEnd4(weatherInfoList.get(4).getHighTemp());
        backupInfo.setDate4(weatherInfoList.get(4).getDate());

        saveWeatherInfo(context,backupInfo);
        /*
        for(WeatherInfo tempInfo:weatherInfoList){
            //今天是多少号
            if(tempInfo.getDate().contains(day)){
                //隔开"高温 " 或者"低温"
                int length = 3;
                tempStart = tempInfo.getLowTemp().substring(length);
                tempEnd   = tempInfo.getHighTemp().substring(length);
                weatherDsc = tempInfo.getWeatherDsc();
                saveWeatherInfo(context,cityName,weatherCode,tempStart,tempEnd,weatherDsc,publishTime);
                break;
            }
        }
        */
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static void saveWeatherInfo(Context context, BackupInfo backupInfo){
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        SimpleDateFormat sdf =   new SimpleDateFormat("yyyy年MM月dd日",Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name",backupInfo.getCityName());
        editor.putString("weather_code",backupInfo.getWeatherCode());
        editor.putString("publish_time",backupInfo.getPublishTime());
        editor.putString("weather_dsc",backupInfo.getCurrentTemperatureDsc());
        editor.putString("temp_start",backupInfo.getCurrentTemperatureStart());
        editor.putString("temp_end",backupInfo.getCurrentTemperatureEnd());

        editor.putString("weather_date1",backupInfo.getDate1());
        editor.putString("weather_dsc1",backupInfo.getTemperatureDsc1());
        editor.putString("temp_start1",backupInfo.getTemperatureStart1());
        editor.putString("temp_end1",backupInfo.getTemperatureEnd1());

        editor.putString("weather_date2",backupInfo.getDate2());
        editor.putString("weather_dsc2",backupInfo.getTemperatureDsc2());
        editor.putString("temp_start2",backupInfo.getTemperatureStart2());
        editor.putString("temp_end2",backupInfo.getTemperatureEnd2());

        editor.putString("weather_date3",backupInfo.getDate3());
        editor.putString("weather_dsc3",backupInfo.getTemperatureDsc3());
        editor.putString("temp_start3",backupInfo.getTemperatureStart3());
        editor.putString("temp_end3",backupInfo.getTemperatureEnd3());

        editor.putString("weather_date4",backupInfo.getDate4());
        editor.putString("weather_dsc4",backupInfo.getTemperatureDsc4());
        editor.putString("temp_start4",backupInfo.getTemperatureStart4());
        editor.putString("temp_end4",backupInfo.getTemperatureEnd4());

        editor.putString("current_date",sdf.format(new Date()));
        editor.commit();
    }

}
