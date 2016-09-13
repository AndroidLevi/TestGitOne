package com.zun1.whenask.SetLanguageApplication;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import com.zun1.whenask.Constant;
import com.zun1.whenask.WhenAskApplication;
import com.zun1.whenask.ui.activity.QuestionHistoryActivity;
import com.zun1.whenask.ui.activity.SetLanguage;
import com.zun1.whenask.ui.activity.TeacherSolveQuestionActivity;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by zun1user7 on 2016/7/4.
 */

public class SetLanguageApplicationClass extends Application {

    private String mac;
    private String imem;
    private float lon;
    private float lat;
    private LocationManager locationManager;
    Location location = null;
    private String language_code;
    public List<Activity> activityList = null;
    private PushAgent mPushAgent;

    @Override
    public void onCreate() {
        super.onCreate();
        final SharedPreferences sharedPreferences = getSharedPreferences(Constant.fileName,MODE_PRIVATE);
        String language_code = sharedPreferences.getString(Constant.LANGUAGE_CODE,"zh-cn");
        if (language_code.equals("zh-cn")){
            setLanguage(Locale.SIMPLIFIED_CHINESE);
        }
        if (language_code.equals("zh-hk")){
            setLanguage(Locale.TAIWAN);
        }
        if (language_code.equals("en-us")){
            setLanguage(Locale.US);
        }
        Log.i("language_code",language_code);
        //setLanguage(Locale.TAIWAN);
        activityList = new ArrayList<Activity>();
        //友盟监听通知
        mPushAgent = PushAgent.getInstance(this);
        // mPushAgent.setDebugMode(true);//这句有什么用？？
        UmengNotificationClickHandler umengNotificationClickHandler=new UmengNotificationClickHandler(){
            @Override
            public void dealWithCustomAction(Context context, UMessage uMessage) {
                super.dealWithCustomAction(context, uMessage);
                SharedPreferences sharedPreferences1 = getSharedPreferences(Constant.fileName,MODE_PRIVATE);
                if (sharedPreferences.getString(Constant.LOGINKEYCHAIN,"").length() != 0){
                    String pushId = uMessage.custom;
                    Log.i("Notification","pushId--"+pushId);
                    //通知老师
                    if (sharedPreferences.getString(Constant.USERTYPE,"").equals("2")){
                        Intent intent=new Intent(SetLanguageApplicationClass.this, TeacherSolveQuestionActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("pushId",pushId);
                        //startActivityForResult(intent,Integer.valueOf(Constant.PUSHID));
                        startActivity(intent);
                    }else {
                        //通知学生
                        Intent intent=new Intent(SetLanguageApplicationClass.this, QuestionHistoryActivity.class);
                        startActivity(intent);
                    }

                }else {
                    Intent intent = new Intent(SetLanguageApplicationClass.this, SetLanguage.class);
                    startActivity(intent);
                }

            }
        };
        mPushAgent.setNotificationClickHandler(umengNotificationClickHandler);
        //环信
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        //options.setAcceptInvitationAlways(false);
        //初始化
        EaseUI.getInstance().init(this, options);
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        //EMClient.getInstance().setDebugMode(true);
    }
    public  void setLanguage(Locale language){
        String languageToLoad  = "zh";
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = getResources().getConfiguration();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        config.locale = language;
        getResources().updateConfiguration(config, metrics);
    }

    public String getMac(){
        WifiManager manager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        WifiInfo infor = manager.getConnectionInfo();
        mac = infor.getMacAddress();
        return mac;
    }

    public  String getImem(){
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imem = telephonyManager.getDeviceId();
        return imem;
    }
    public float getLat(){
        float latitude ;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if( PackageManager.PERMISSION_GRANTED  ==ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION))
            location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        Log.i("location", String.valueOf(location));
        if (location == null){
            latitude = (float) 23.12345;
        }else {
            latitude = (float) location.getLatitude();
        }
        return latitude;
    }

    public float getLon(){
        float longitude ;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if( PackageManager.PERMISSION_GRANTED  ==ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION))
            location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        if (location == null){
            longitude = (float) 113.12345 ;
        }else {
            longitude = (float) location.getLatitude();
        }
        return longitude;
    }
    //delete遍历所有Activity finish
    public void delete(){
        for (Activity activity:activityList){
            activity.finish();
        }if (activityList.size() == 0){
            activityList.clear();
        }
    }
}
