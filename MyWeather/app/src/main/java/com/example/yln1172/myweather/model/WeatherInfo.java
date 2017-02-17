package com.example.yln1172.myweather.model;

/**
 * Created by yln1172 on 2017/2/16.
 */
public class WeatherInfo {
    private String date;
    private String highTemp;
    private String lowTemp;
    private String weatherDsc;

    public String getHighTemp() {
        return highTemp;
    }

    public void setHighTemp(String highTemp) {
        this.highTemp = highTemp;
    }

    public String getLowTemp() {
        return lowTemp;
    }

    public void setLowTemp(String lowTemp) {
        this.lowTemp = lowTemp;
    }

    public String getWeatherDsc() {
        return weatherDsc;
    }

    public void setWeatherDsc(String weatherDsc) {
        this.weatherDsc = weatherDsc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
