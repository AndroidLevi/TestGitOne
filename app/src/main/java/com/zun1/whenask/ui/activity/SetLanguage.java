package com.zun1.whenask.ui.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hyphenate.chat.EMChatConfig;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.UmengRegistrar;
import com.umeng.message.entity.UMessage;
import com.zun1.whenask.Constant;
import com.zun1.whenask.DB.UserDb;
import com.zun1.whenask.R;
import com.zun1.whenask.SetLanguageApplication.SetLanguageApplicationClass;
import com.zun1.whenask.ToolsClass.ActivityIndicatorView;
import com.zun1.whenask.ToolsClass.HttpRequest;
import com.zun1.whenask.WhenAskApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SetLanguage extends AppCompatActivity implements View.OnClickListener{
    private Button familiarBtn,complexBtn,englishBtn;
    private UserDb userDb;
    float lat;
    float lon;
    int ERRORCODE = 100;
    String device_model = null;//设备名
    String version_sdk = null;//sdk
    String version_release = null;//系统版本号
    String romType = null;//系统名
    String imei = null;//IMEI
    String android_imsi = null;//imsi
    String mac = null;//mac
    //UserDb
    private PushAgent mPushAgent;
    private String device_token;
    private static final int REQUEST_CODE=200;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ActivityIndicatorView.dismiss();
            switch (msg.what){
                case 000:
                    Toast.makeText(SetLanguage.this,R.string.auto_login_succeed,Toast.LENGTH_SHORT).show();
                    break;
                case 001:
                    Toast.makeText(SetLanguage.this,R.string.sever_error,Toast.LENGTH_SHORT).show();
                    break;
                case 124:
                    Toast.makeText(SetLanguage.this,R.string.auto_login_no,Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_language);
        //用于记录是否第一次启动应用
        sp=getSharedPreferences("device_token",MODE_PRIVATE);
        editor=sp.edit();
        //申请6.0的WRITE_SETTINGS权限，因为友盟推送需要用到
        permissionToWRITE_SETTINGS();
        //引入友盟推送服务
        mPushAgent = PushAgent.getInstance(this);
        mPushAgent.onAppStart();
//        UmengNotificationClickHandler umengNotificationClickHandler=new UmengNotificationClickHandler(){
//            @Override
//            public void dealWithCustomAction(Context context, UMessage uMessage) {
//                super.dealWithCustomAction(context, uMessage);
//                String pushId = uMessage.custom;
//                Log.i("Notification 2","dealWithCustomAction--"+pushId);
//                Intent intent=new Intent(SetLanguage.this, TeacherSolveQuestionActivity.class);
//                intent.putExtra("pushId",pushId);
//                startActivity(intent);
//               // startActivityForResult(intent,Integer.valueOf(Constant.PUSHID));
//            }
//        };
//        mPushAgent.setNotificationClickHandler(umengNotificationClickHandler);

        //在这里判断是否是第一次启动应用
        Boolean isFirstOpen=sp.getBoolean("isFirstOpen",true);//当获取不到isFirstOpen时默认是true，ture为第一次启动，false为非首次启动
        Log.i("Lee","isFirstOpen--"+isFirstOpen);
        if(isFirstOpen){//如果是第一次启动
            Log.i("Lee","第一次启动应用");
            fristGetDeviceToken();
        }else{
            Log.i("Lee","不是第一次启动应用");
            //mPushAgent.enable();//因为上面已经开启了mPushAgent.enable(new IUmengRegisterCallback()，所以不用再次开启mPushAgent.enable()
            //device_token = UmengRegistrar.getRegistrationId(this);//使用这一句跟下面这句是一样的
            device_token = mPushAgent.getRegistrationId();
            Log.i("Lee ——Not onRegistered", "device_token=" + device_token);
            //以下是添加拿到device_token后的操作
            if (ContextCompat.checkSelfPermission(SetLanguage.this,android.Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED){
                //申请READ_PHONE_STATE权限
                ActivityCompat.requestPermissions(SetLanguage.this,new String[]{android.Manifest.permission.READ_PHONE_STATE},400);
            }
            else {
                deviceInfo();
                deviceInfoToGetId();
            }
        }
        //initUMeng();
        findViewById();
        setOnClickListener();
        userDb = UserDb.instance();
        userDb.init(this);
        //this.setTitle(R.string.set_language);
        //add activity
        SetLanguageApplicationClass setLanguageApplicationClass = (SetLanguageApplicationClass) getApplication();
        setLanguageApplicationClass.activityList.add(this);

//        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED){
//            //申请READ_PHONE_STATE权限
//            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.READ_PHONE_STATE},400);
//        }
//        else {
//            deviceInfo();
//            deviceInfoToGetId();
//        }
        //deviceInfoToGetId
        //环信登陆
        boolean LastLogin =  EMClient.getInstance().isLoggedInBefore();
        Log.i("LastLogin", String.valueOf(LastLogin));
    }

    public void fristGetDeviceToken(){
        mPushAgent.enable(new IUmengRegisterCallback() {//用这种回调方法的话，只有在首次安装时才会执行！用于初次获取测试设备的Device Token。
            @Override
            public void onRegistered(final String s) {
                Log.i("Lee","go into onRegistered()");
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("Lee","go into run()");
                        int count=0;
                        do {//关键是在这里一直循环获取device_token，这里是在主线程不断循环，为什么不会造成ANR？难道是异步回调的原因吗？
                            count++;
                            device_token = UmengRegistrar.getRegistrationId(SetLanguage.this);
                            // device_token = mPushAgent.getRegistrationId();
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e)
                            {
                                Log.i("LeeInterruptedException",e.getMessage());
                            }
                        } while (TextUtils.isEmpty(device_token));//当device_token为null或""时，即还没获取到device_token，若是就继续循环
                        Log.i("Lee count",""+count);
                        Log.i("Lee ——onRegistered", "device_token=" + device_token);

                        editor.putBoolean("isFirstOpen",false);//已运行过应用一次，
                        editor.commit();
                        //以下是添加拿到device_token后的操作
                        if (ContextCompat.checkSelfPermission(SetLanguage.this,android.Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED){
                            //申请READ_PHONE_STATE权限
                            ActivityCompat.requestPermissions(SetLanguage.this,new String[]{android.Manifest.permission.READ_PHONE_STATE},400);
                        }
                        else {
                            deviceInfo();
                            deviceInfoToGetId();
                        }
                    }
                });
                Log.i("Lee","out of Runnable()");
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void permissionToWRITE_SETTINGS(){

        if(Build.VERSION.SDK_INT >= 23){
            if(!Settings.System.canWrite(this)){
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE);
            }
        }

    }
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (Settings.System.canWrite(this)) {
                //检查返回结果
                //Toast.makeText(SetLanguage.this, "WRITE_SETTINGS permission granted", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(SetLanguage.this, "WRITE_SETTINGS permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }
    void findViewById(){
        familiarBtn = (Button)this.findViewById(R.id.familiarBtn);
        complexBtn = (Button)this.findViewById(R.id.complexBtn);
        englishBtn = (Button)this.findViewById(R.id.englishBtn);

    }
    void setOnClickListener(){
        familiarBtn.setOnClickListener(this);
        complexBtn.setOnClickListener(this);
        englishBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        SetLanguageApplicationClass setLanguageApplicationClass = (SetLanguageApplicationClass) getApplication();
        Intent intent = null;
        if (ERRORCODE == 000){
            intent = new Intent(SetLanguage.this,MainActivity.class);
        }else {
            intent = new Intent(SetLanguage.this, LoginActivity.class);
        }
        switch (view.getId()){
            case R.id.familiarBtn:
                userDb.setLanguageInfo("zh-cn");
                setLanguageApplicationClass.setLanguage(Locale.SIMPLIFIED_CHINESE);
                startActivity(intent);
                break;
            case R.id.complexBtn:
                userDb.setLanguageInfo("zh-hk");
                setLanguageApplicationClass.setLanguage(Locale.TAIWAN);
                startActivity(intent);
                break;
            case R.id.englishBtn:
                userDb.setLanguageInfo("en-us");
                setLanguageApplicationClass.setLanguage(Locale.US);
                startActivity(intent);
                break;
        }
    }
    //下次判断是否登陆
    void autoLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constant.fileName, Context.MODE_PRIVATE);
        if (!sharedPreferences.getString(Constant.DEVICEID, "").equals("")) {
            //Log.i("deviceid", sharedPreferences.getString(Constant.DEVICEID, ""));
            //得到经纬度
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                //设置位置权限
                ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},100);
            }else {
                Log.i("xxxx","这个地方出错了");
                SetLanguageApplicationClass setLanguageApplicationClass = (SetLanguageApplicationClass) getApplication();
                lat = setLanguageApplicationClass.getLat();
                lon = setLanguageApplicationClass.getLon();
            }

            //获取上次登录的信息
            List<Map<String, String>> list = new ArrayList<Map<String, String>>();
//                Map<String,String> mapusertype = new HashMap<String, String>();
//                mapusertype.put("usertype",sharedPreferences.getString(Constant.USERTYPE,""));
//                list.add(mapusertype);
            Map<String, String> mapdeviceid = new HashMap<String, String>();
            mapdeviceid.put(Constant.DEVICEID, sharedPreferences.getString(Constant.DEVICEID, ""));
            list.add(mapdeviceid);
            Map<String, String> mapkeysign = new HashMap<String, String>();
            mapkeysign.put("keysign", sharedPreferences.getString(Constant.LOGINKEYCHAIN, ""));
            list.add(mapkeysign);
            Map<String, String> maplon = new HashMap<String, String>();
            maplon.put("lon", String.valueOf(lon));
            list.add(maplon);
            Map<String, String> maplat = new HashMap<String, String>();
            maplat.put("lat", String.valueOf(lat));
            list.add(maplat);
            String url = Constant.AUTOLOGINURL;
            String result = HttpRequest.postRequestBody(list, url);
            if (result == null) {
                Message message = Message.obtain();
                handler.sendEmptyMessage(message.what = 001);
                return;
            }
            //Log.i("result_autologin", result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                String errorCode = jsonObject.optString("ErrorCode");
                if (errorCode.equals("000") && EMClient.getInstance().isLoggedInBefore()) {
                    JSONObject jsonObject1 = jsonObject.optJSONObject("Items");
                    String loginkeychain = jsonObject1.optString("loginkeychain");
                    userDb.saveloginkeychain(loginkeychain);
                    Message message = Message.obtain();
                    handler.sendEmptyMessage(message.what = 000);
                    ERRORCODE = 000;
                    //得到用户的信息
                    getUserInfo();
                }
                if (errorCode.equals("124")) {
                    Message message = Message.obtain();
                    handler.sendEmptyMessage(message.what = 124);
                }
                if (errorCode.equals("112")) {
                    Message message = Message.obtain();
                    handler.sendEmptyMessage(message.what = 124);
                }
            } catch (JSONException e) {
                Message message = Message.obtain();
                handler.sendEmptyMessage(message.what = 001);
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onStart() {
        super.onStart();

    }
    //deviceInfoToGetId
    void deviceInfoToGetId(){
        ActivityIndicatorView.start(SetLanguage.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences(Constant.fileName, MODE_PRIVATE);
                if (sharedPreferences.getString(Constant.DEVICEID, "").equals("")) {
                    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                    Map<String, String> mapimei = new HashMap<String, String>();
                    mapimei.put("imei", imei);
                    list.add(mapimei);
                    Map<String, String> maptoken = new HashMap<String, String>();
                    maptoken.put("token", device_token);
                    list.add(maptoken);
                    Map<String, String> mapmodel = new HashMap<String, String>();
                    mapmodel.put("model", device_model);
                    list.add(mapmodel);
                    Map<String, String> maposversion = new HashMap<String, String>();
                    maposversion.put("osversion", version_release);
                    list.add(maposversion);
                    Map<String, String> mapimsi = new HashMap<String, String>();
                    mapimsi.put("imsi", android_imsi);
                    list.add(mapimsi);
                    Map<String, String> mapmacaddr = new HashMap<String, String>();
                    mapmacaddr.put("macaddr", mac);
                    list.add(mapmacaddr);
                    Map<String, String> mapossdkcode = new HashMap<String, String>();
                    mapossdkcode.put("ossdkcode", version_sdk);
                    list.add(mapossdkcode);
                    String url = Constant.DEVICEIDURL;
                    String result = HttpRequest.postRequestBody(list, url);
                    if (result == null) {
                        Log.i("result_deviceId","xxxxxxx");
                        Message message = Message.obtain();
                        handler.sendEmptyMessage(message.what = 001);
                        return;
                    }
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String ErrorCode = jsonObject.optString("ErrorCode");
                        if (ErrorCode.equals("000")) {
                            JSONObject jsonObjectItem = jsonObject.optJSONObject("Items");
                            String deviceid = jsonObjectItem.getString("deviceid");
                            userDb.savedeviceId(deviceid);
                            //自动登陆
                            autoLogin();
                        }

                    } catch (JSONException e) {
                        Log.i("result_deviceId__2", result);
                        Message message = Message.obtain();
                        handler.sendEmptyMessage(message.what = 001);
                        e.printStackTrace();
                    }
                }else {
                    //自动登陆
                    autoLogin();
                }
            }
        }).start();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SetLanguageApplicationClass setLanguageApplicationClass = (SetLanguageApplicationClass) getApplication();
        if (requestCode == 100 ) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                lat = setLanguageApplicationClass.getLat();
                lon = setLanguageApplicationClass.getLon();
            }else {
                lat = (float) 23.12345;
                lon = (float) 113.12345;
            }
        }
        if (requestCode == 400){
            //手机信息
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                deviceInfo();
                deviceInfoToGetId();
            }else {

            }
        }
    }
    //设备信息
    void deviceInfo(){
        //设备信息
        device_model = Build.MODEL;//设备名
        version_sdk = Build.VERSION.SDK;//sdk
        version_release = Build.VERSION.RELEASE;//系统版本号
        romType = Build.PRODUCT;//系统名
        imei =((TelephonyManager) this.getSystemService(TELEPHONY_SERVICE)).getDeviceId();//IMEI
        android_imsi = ((TelephonyManager)this.getSystemService(this.TELEPHONY_SERVICE)).getSubscriberId();//imsi
        if (android_imsi == null){
            android_imsi = "";
        }
        mac = ((WifiManager)getSystemService(this.WIFI_SERVICE)).getConnectionInfo().getMacAddress();//mac
    }
    //登陆成功获得用户的全部信息
    void getUserInfo(){
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Map<String, String> useridmap = new HashMap<String, String>();
        SharedPreferences sharedPreferences = getSharedPreferences(Constant.fileName, Context.MODE_PRIVATE);
        String userid = sharedPreferences.getString(Constant.USERID,"");
        String usertype = sharedPreferences.getString(Constant.USERTYPE,"");
        useridmap.put("userid", userid);
        list.add(useridmap);
        //学生
        String url;
        if (usertype.equals("1")){
            url = Constant.USERINFOURL_STUDENT;
        }else {
            //老师
            url = Constant.USERINFOURL_TEATHER;
        }
        String result =  HttpRequest.postRequestBody(list,url);
        Log.i("autoLogin",result);
        JSONObject data = null;
        try {
            data = new JSONObject(result);
            JSONObject jsonObject = data.optJSONObject("Items");
            String nickname = jsonObject.optString("nickname");
            String firstname = jsonObject.optString("firstname");

            String lastname = jsonObject.optString("lastname");
            String phone = jsonObject.optString("phone");
            String birthday = jsonObject.optString("birthday");
            Log.i("birthday",birthday);
            String address = jsonObject.optString("address");
            Log.i("birthday",address);
            String school = jsonObject.optString("school");
            String gender = jsonObject.optString("gender");
            String avatar = jsonObject.optString("avatar");
            String email = jsonObject.optString("email");
            //学生
            if (usertype.equals("1")){
                String grade = jsonObject.optString("grade");
                //String hobby = jsonObject.optString("hobby");
                userDb.setUserInfo(nickname,firstname,grade,lastname,phone,birthday,address,school,gender,avatar,email);
            }else {
                //老师
                String degree = jsonObject.optString("degree");
                String worklong = jsonObject.optString("worklong");
                String subjectidlist = jsonObject.optString("subjectidlist");
                userDb.setUserInfoTeather(nickname,firstname,lastname,phone,birthday,address,school,gender,avatar,email,degree,worklong,subjectidlist);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
