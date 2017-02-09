package com.example.yln1172.myweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.yln1172.myweather.model.City;
import com.example.yln1172.myweather.model.County;
import com.example.yln1172.myweather.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yln1172 on 2017/2/6.
 */
public class MyWeatherDB {
    public static String DB_NAME = "myzone_weather";
    public static final int VERSION = 1;
    private static MyWeatherDB myWeatherDB;
    private SQLiteDatabase db;
    public MyWeatherDB(Context context){
        MyWeatherDBHelper dbHelper = new MyWeatherDBHelper(context,DB_NAME,null,VERSION);
        db = dbHelper.getWritableDatabase();
    }

    public synchronized static MyWeatherDB getInstance(Context context){
        if(myWeatherDB == null){
            myWeatherDB = new MyWeatherDB(context);
        }
        return  myWeatherDB;
    }

    public void saveProvince (Province province){
        if(province != null){
            ContentValues values = new ContentValues();
            values.put("province_name",province.getProvinceName());
            values.put("province_code",province.getProvinceCode());
            db.insert("Province",null,values);
        }
    }

    public List<Province> loadProvinces(){
        List<Province> list = new ArrayList<>();
        Cursor cursor = db.query("Province",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                list.add(province);
            }while(cursor.moveToNext());
        }
        return  list;
    }

    public void saveCity (City city){
        if(city != null){
            ContentValues values = new ContentValues();
            values.put("city_name",city.getCityName());
            values.put("city_code",city.getCityCode());
            values.put("province_id",city.getProvinceId());
            db.insert("City",null,values);
        }
    }

    public List<City> loadCities(int provinceId){
        List<City> list = new ArrayList<>();
        Cursor cursor = db.query("City",null,"province_id = ?",new String[]{String.valueOf(provinceId)},null,null,null);
        if(cursor.moveToFirst()){
            do{
                City province = new City();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                province.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                list.add(province);
            }while(cursor.moveToNext());
        }
        return  list;
    }

    public void saveCounty (County county){
        if(county != null){
            ContentValues values = new ContentValues();
            values.put("county_name",county.getCountyName());
            values.put("county_code",county.getCountyCode());
            values.put("city_id",county.getCityId());
            db.insert("County",null,values);
        }
    }

    public List<County> loadCounties(int cityId){
        List<County> list = new ArrayList<>();
        Cursor cursor = db.query("County",null,"city_id = ?",new String[]{String.valueOf(cityId)},null,null,null);
        if(cursor.moveToFirst()){
            do{
                County province = new County();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                province.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                list.add(province);
            }while(cursor.moveToNext());
        }
        return  list;
    }
}
