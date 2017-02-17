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
import com.example.yln1172.myweather.service.AutoUpdateService;
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
    private String weatherCodeStr;


    private TextView tvDate1; //明天的日期
    private TextView tvDsc1;  //明天的描述
    private TextView tvTempStart1; //明天的最低温度
    private TextView tvTempEnd1; //明天的最高温度

    private TextView tvDate2; //后天的日期
    private TextView tvDsc2;  //后天的描述
    private TextView tvTempStart2; //后天的最低温度
    private TextView tvTempEnd2; //后天的最高温度

    private TextView tvDate3; //大后天的日期
    private TextView tvDsc3;  //大后天的描述
    private TextView tvTempStart3; //大后天的最低温度
    private TextView tvTempEnd3; //大后天的最高温度

    private TextView tvDate4; //大大后天的日期
    private TextView tvDsc4;  //大大后天的描述
    private TextView tvTempStart4; //大大后天的最低温度
    private TextView tvTempEnd4; //大大后天的最高温度

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

        tvDate1         = (TextView)findViewById(R.id.tv_date1);
        tvDsc1          = (TextView)findViewById(R.id.tv_dsc1);
        tvTempStart1    = (TextView)findViewById(R.id.tv_weather_forest1_tmp_start);
        tvTempEnd1      = (TextView)findViewById(R.id.tv_weather_forest1_tmp_end);

        tvDate2         = (TextView)findViewById(R.id.tv_date2);
        tvDsc2          = (TextView)findViewById(R.id.tv_dsc2);
        tvTempStart2    = (TextView)findViewById(R.id.tv_weather_forest2_tmp_start);
        tvTempEnd2      = (TextView)findViewById(R.id.tv_weather_forest2_tmp_end);

        tvDate3         = (TextView)findViewById(R.id.tv_date3);
        tvDsc3          = (TextView)findViewById(R.id.tv_dsc3);
        tvTempStart3    = (TextView)findViewById(R.id.tv_weather_forest3_tmp_start);
        tvTempEnd3      = (TextView)findViewById(R.id.tv_weather_forest3_tmp_end);

        tvDate4         = (TextView)findViewById(R.id.tv_date4);
        tvDsc4          = (TextView)findViewById(R.id.tv_dsc4);
        tvTempStart4    = (TextView)findViewById(R.id.tv_weather_forest4_tmp_start);
        tvTempEnd4      = (TextView)findViewById(R.id.tv_weather_forest4_tmp_end);

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
                    weatherCodeStr = weather_code;
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
        //String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        //queryFromServer(address,"weatherCode");

        String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + weatherCode;
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
                            weatherCodeStr = weatherCode;
                            queryWeatherFromWeatherCode(weatherCode);
                        }
                    }
                }else if("weatherCode".equals(type)){
                    //Utility.handleWeatherResponse(WeatherActivity.this,response);
                    Utility.parseWeatherResponseXml(WeatherActivity.this,response,weatherCodeStr);
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

        //预测的天气
        tvDate1.setText(sharedPreferences.getString("weather_date1",""));
        tvDsc1.setText(sharedPreferences.getString("weather_dsc1",""));
        tvTempStart1.setText(sharedPreferences.getString("temp_start1",""));
        tvTempEnd1.setText(sharedPreferences.getString("temp_end1",""));

        tvDate2.setText(sharedPreferences.getString("weather_date2",""));
        tvDsc2.setText(sharedPreferences.getString("weather_dsc2",""));
        tvTempStart2.setText(sharedPreferences.getString("temp_start2",""));
        tvTempEnd2.setText(sharedPreferences.getString("temp_end2",""));

        tvDate3.setText(sharedPreferences.getString("weather_date3",""));
        tvDsc3.setText(sharedPreferences.getString("weather_dsc3",""));
        tvTempStart3.setText(sharedPreferences.getString("temp_start3",""));
        tvTempEnd3.setText(sharedPreferences.getString("temp_end3",""));

        tvDate4.setText(sharedPreferences.getString("weather_date4",""));
        tvDsc4.setText(sharedPreferences.getString("weather_dsc4",""));
        tvTempStart4.setText(sharedPreferences.getString("temp_start4",""));
        tvTempEnd4.setText(sharedPreferences.getString("temp_end4",""));

        lyWeatherInfo.setVisibility(View.VISIBLE);
        tvCityName.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
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