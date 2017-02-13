package com.example.yln1172.myweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yln1172.myweather.R;
import com.example.yln1172.myweather.utils.HttpCallBackListener;
import com.example.yln1172.myweather.utils.HttpUtil;
import com.example.yln1172.myweather.utils.Utility;

/**
 * Created by yln1172 on 2017/2/10.
 */
public class WeatherActivity extends Activity {
    private TextView tvCityName;
    private TextView tvPublishDate;
    private TextView tvWeatherDate;
    private TextView tvTempStart;
    private TextView tvTempEnd;
    private TextView tvTempDsc;
    private LinearLayout lyWeatherInfo;
    private Button btSwitchCity;
    private Button btFresh;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle saveInstance) {
        super.onCreate(saveInstance);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);

        tvCityName      = (TextView)findViewById(R.id.tv_city_name);
        tvPublishDate   = (TextView)findViewById(R.id.tv_publish_time);
        tvWeatherDate   = (TextView)findViewById(R.id.tv_weather_time);
        tvTempStart     = (TextView)findViewById(R.id.tv_weather_temp_start);
        tvTempEnd       = (TextView)findViewById(R.id.tv_weather_temp_end);
        tvTempDsc       = (TextView)findViewById(R.id.tv_weather_description);
        lyWeatherInfo   = (LinearLayout)findViewById(R.id.ly_weather_ifo);

        btSwitchCity    = (Button)findViewById(R.id.bt_switch_city);
        btFresh         = (Button)findViewById(R.id.bt_refresh);

        btSwitchCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(this,ChooseAreaActivity.class);
                Intent intent = new Intent(WeatherActivity.this,ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity",true);
                startActivity(intent);
                finish();
            }
        });

        btFresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                String weather_code = prefs.getString("weather_code","");
                if(!TextUtils.isEmpty(weather_code)){
                    showProgressDialog();
                    queryWeatherFromWeatherCode(weather_code);
                }
            }
        });

        String countyCode = getIntent().getStringExtra("county_code");
        if(!TextUtils.isEmpty(countyCode)){
            tvPublishDate.setText("同步中...");
            lyWeatherInfo.setVisibility(View.INVISIBLE);
            tvCityName.setVisibility(View.INVISIBLE);
            queryWeatherFromCountyCode(countyCode);
        }else{
            //已经关注的天气
            showWeather();
        }
    }

    //县级代号对应的天气
    private void queryWeatherFromCountyCode(String countyCode){
        String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
        queryFromServer(address,"countyCode");
    }

    private void queryWeatherFromWeatherCode(String weatherCode){
        //新的接口
        // http://wthrcdn.etouch.cn/WeatherApi?citykey=101010100  通过城市id获得天气数据，xml文件数据,
        //http://wthrcdn.etouch.cn/WeatherApi?city=北京 通过城市名字获得天气数据，xml文件数据
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        queryFromServer(address,"weatherCode");
    }

    private void queryFromServer(final String address, final String type){
        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                if("countyCode".equals(type)){
                    if(!TextUtils.isEmpty(response)){
                        //解析天气代号
                        String[] array = response.split("\\|");
                        if(array != null || array.length == 2){
                            String weatherCode = array[1];
                            queryWeatherFromWeatherCode(weatherCode);
                        }
                    }
                }else if("weatherCode".equals(type)){
                    Utility.handleWeatherResponse(WeatherActivity.this,response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                            closeProgressDialog();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvPublishDate.setText("同步失败");
                    }
                });
                closeProgressDialog();
            }
        });
    }

    private void showWeather(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        tvCityName.setText(sharedPreferences.getString("city_name",""));
        tvTempStart.setText(sharedPreferences.getString("temp_start",""));
        tvTempEnd.setText(sharedPreferences.getString("temp_end",""));
        tvTempDsc.setText(sharedPreferences.getString("weather_dsc",""));
        tvWeatherDate.setText(sharedPreferences.getString("current_date",""));
        tvPublishDate.setText("今天"+sharedPreferences.getString("publish_time","") + "发布");
        lyWeatherInfo.setVisibility(View.VISIBLE);
        tvCityName.setVisibility(View.VISIBLE);
    }

    private void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }
}