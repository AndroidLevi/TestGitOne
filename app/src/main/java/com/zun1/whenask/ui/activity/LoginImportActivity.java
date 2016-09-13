package com.zun1.whenask.ui.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.Process;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.zun1.whenask.Constant;
import com.zun1.whenask.DB.UserDb;
import com.zun1.whenask.R;
import com.zun1.whenask.SetLanguageApplication.SetLanguageApplicationClass;
import com.zun1.whenask.ToolsClass.ActivityIndicatorView;
import com.zun1.whenask.ToolsClass.HttpRequest;
import com.zun1.whenask.ToolsClass.UserDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.framed.ErrorCode;

public class LoginImportActivity extends AppCompatActivity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener{
    private Button forgetpasswordBtn;
    private EditText username,password;
    private EditText authcodeEdit;
    private Button submitBtn,resetBtn;
    private UserDb userDb;
    private CheckBox saveBtn;
    private SimpleDraweeView simpleDraweeView;
    float lat,lon;
    //登陆的key
    String key = null;
    public android.os.Handler handler = new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what != 050){

            }
            ActivityIndicatorView.dismiss();
            switch (msg.what){
                case 000:
                    Toast.makeText(LoginImportActivity.this,R.string.login_succeed,Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    UserDialog.serverError(LoginImportActivity.this);
                    break;
                case 050:
                    UserDialog.serverError(LoginImportActivity.this);
                    break;
                case 060:
                    break;
                case 101:
                    Toast.makeText(LoginImportActivity.this,R.string.user_exit,Toast.LENGTH_SHORT).show();
                    break;
                case 116:
                    Toast.makeText(LoginImportActivity.this,R.string.authcodeError,Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.login);
        userDb = UserDb.instance();
        userDb.init(this);
        findViewById();
        setOnClickListener();
        userDb = UserDb.instance();
        userDb.init(this);
        this.setTitle(R.string.login);
        //home
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //add activity
        SetLanguageApplicationClass setLanguageApplicationClass = (SetLanguageApplicationClass) getApplication();
        setLanguageApplicationClass.activityList.add(this);
    }
    //UI
    void findViewById(){
        forgetpasswordBtn = (Button)this.findViewById(R.id.forget_password);
        username = (EditText)this.findViewById(R.id.edText_login_username);
        password = (EditText)this.findViewById(R.id.edText_login_password);
        saveBtn = (CheckBox) this.findViewById(R.id.login_savebtn);
        resetBtn = (Button)this.findViewById(R.id.login_reset);
        submitBtn = (Button)this.findViewById(R.id.login_submit);
        //simpleDraweeView
        simpleDraweeView = (SimpleDraweeView) findViewById(R.id.authcode_picture);
        authcodeEdit = (EditText)this.findViewById(R.id.edText_login_authcode);
        //得到保存username、password
        SharedPreferences sharedPreferences = getSharedPreferences(Constant.fileName,Context.MODE_PRIVATE);
        String usernameStr = sharedPreferences.getString(Constant.USERNAME,"");
        String passwordStr = sharedPreferences.getString(Constant.PASSWORD,"");
        username.setText(usernameStr);
        password.setText(passwordStr);
        //是否选中贮存密码
        saveBtn.setChecked(sharedPreferences.getBoolean(Constant.ISCHECK,false));
        //生成验证码
        getAuthCode();
    }
    //监听器
    void setOnClickListener(){
        forgetpasswordBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        resetBtn.setOnClickListener(this);
        saveBtn.setOnCheckedChangeListener(this);
        simpleDraweeView.setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        //保存是否选中
        userDb.savePasswordCheck(b);
    }

    //按钮监听器
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.forget_password:
                Intent intent = new Intent(LoginImportActivity.this,ForgetPasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.login_submit:
                submitAction();
                break;
            case R.id.login_reset:
                username.setText("");
                password.setText("");
                break;
            case R.id.authcode_picture:
                getAuthCode();
                break;
        }
    }
    //提交按钮
    void submitAction(){
        Log.i("---->","提交");
        if (username.getText().length() == 0 || password.getText().length() == 0){
            Toast.makeText(LoginImportActivity.this,R.string.no_empty,Toast.LENGTH_SHORT).show();
            return;
        }
        if (authcodeEdit.getText().length() == 0){
            Toast.makeText(LoginImportActivity.this,R.string.no_empty_auth,Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityIndicatorView.start(LoginImportActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                SetLanguageApplicationClass applicationClass = (SetLanguageApplicationClass) getApplication();
                List<Map<String,String>> list = new ArrayList<Map<String, String>>();
                Map<String,String> mapusername = new HashMap<String, String>();
                mapusername.put("username",username.getText().toString().trim());
                list.add(mapusername);
                Map<String,String> mappassword = new HashMap<String, String>();
                mappassword.put("password",md5(password.getText().toString().trim()));
                list.add(mappassword);
                Map<String,String> maplon = new HashMap<String, String>();
                if (ContextCompat.checkSelfPermission(LoginImportActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                    //设置位置权限
                    ActivityCompat.requestPermissions(LoginImportActivity.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},100);
                }else {
                    SetLanguageApplicationClass setLanguageApplicationClass = (SetLanguageApplicationClass) getApplication();
                    lat = setLanguageApplicationClass.getLat();
                    lon = setLanguageApplicationClass.getLon();
                }
                maplon.put("lon", String.valueOf(lon));
                list.add(maplon);
                Map<String,String> maplat = new HashMap<String, String>();
                maplat.put("lat", String.valueOf(lat));
                list.add(maplat);
                SharedPreferences sharedPreferences = getSharedPreferences(Constant.fileName,MODE_PRIVATE);
                Map<String,String> mapdevicenum = new HashMap<String, String>();
                mapdevicenum.put(Constant.DEVICEID,sharedPreferences.getString(Constant.DEVICEID,""));
                list.add(mapdevicenum);
//                Map<String,String> mapplatform = new HashMap<String, String>();
//                mapplatform.put("platform","2");
//                list.add(mapplatform);
//                Map<String,String> maplanguagecode = new HashMap<String, String>();
//                maplanguagecode.put("languagecode","zh-cn");
//                list.add(maplanguagecode);
//                Map<String,String> mapdevicetoken = new HashMap<String, String>();
//                mapdevicetoken.put("devicetoken","123456");
//                list.add(mapdevicetoken);
                Map<String,String> mapcaptchakey = new HashMap<String, String>();
                mapcaptchakey.put("captchakey",key);
                //Log.i("key",key);
                list.add(mapcaptchakey);
                Map<String,String> mapcaptchacode = new HashMap<String, String>();
                mapcaptchacode.put("captchacode", String.valueOf(authcodeEdit.getText().toString().trim()));
                list.add(mapcaptchacode);
                String url = Constant.LOGINURL;
                String result = HttpRequest.postRequestBody(list,url);
                //如果服务器为空
                if (result == null){
                    Message message = new Message();
                    int serverError = message.what = 1;
                    handler.sendEmptyMessage(serverError);
                    return;
                }
                loginsuccedjsonanalysis(result);
            }
        }).start();
    }
    //登陆成功数据解析
    void loginsuccedjsonanalysis(String result){
        try {
            Message message = Message.obtain();
            JSONObject jsonObject = new JSONObject(result);
            String ErrorCode = jsonObject.optString("ErrorCode");
            switch (ErrorCode){
                case "000":
                    JSONObject jsonObject1 = jsonObject.optJSONObject("Items");
                    String usertype = jsonObject1.optString("usertype");
                    String loginkeychain = jsonObject1.optString("loginkeychain");
                    String userid = jsonObject1.optString("userid");
                    Log.i("jsonObject",ErrorCode+usertype+loginkeychain+userid);
                    userDb.saveloginkeychain(loginkeychain);
                    userDb.saveloginInfo(usertype,userid);
                    getUserInfo();
                    //环信登陆
                    easeUILogin();
                    //是否保存密码
                    if (saveBtn.isChecked()){
                        userDb.saveUserInfo(username.getText().toString().trim(),password.getText().toString().trim());
                    }else {
                        userDb.saveUserInfo(username.getText().toString().trim(),"");
                    }
                    break;
                case "101":
                    message.what = 101;
                    handler.sendEmptyMessage(message.what);
                    break;
                case "112":
                    message.what = 116;
                    handler.sendEmptyMessage(message.what);
                    break;
                case "116":
                    message.what = 116;
                    handler.sendEmptyMessage(message.what);
                    break;
            }
        } catch (JSONException e) {
            Message message = Message.obtain();
            handler.sendEmptyMessage(message.what = 1);
            e.printStackTrace();
        }
    }
    //环信登陆
    void easeUILogin(){
        EMClient.getInstance().login(username.getText().toString().trim(), md5(password.getText().toString().trim()), new EMCallBack() {
            @Override
            public void onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                Intent intent = new Intent(LoginImportActivity.this,MainActivity.class);
                startActivity(intent);
                Message message = Message.obtain();
                message.what = 000;
                handler.sendEmptyMessage(message.what);
                Log.i("onSuccess","onSuccess");
            }

            @Override
            public void onError(int i, String s) {
                  Log.i("onError",s);
            }

            @Override
            public void onProgress(int i, String s) {
                 Log.i("onProgress",s);
            }
        });
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
    //MD5加密
    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }
    //生成验证码
    void getAuthCode(){
        ActivityIndicatorView.start(LoginImportActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Map<String,String>> list = new ArrayList<Map<String, String>>();
                Map<String,String> mapnum = new HashMap<String, String>();
                String url = Constant.AUTHCODEURL;
                mapnum.put("num", String.valueOf((int) (Math.random() * 10000)));
                String result = HttpRequest.postRequestBody(list,url);
                if (result == null){
                    Message message = Message.obtain();
                    handler.sendEmptyMessage(message.what = 050);
                    key = "";
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONObject item = jsonObject.optJSONObject("Items");
                    final String pictureUrl = item.optString("url");
                    String ErrorCode = jsonObject.optString("ErrorCode");
                    if (ErrorCode.equals("000")){
                        Message message = Message.obtain();
                        handler.sendEmptyMessage(message.what = 060);
                    }
                    key = item.optString("key");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            simpleDraweeView.setImageURI(Uri.parse(pictureUrl));
                            authcodeEdit.setText("");
                        }
                    });

                } catch (Exception e) {
                    Message message = Message.obtain();
                    handler.sendEmptyMessage(message.what = 050);
                    e.printStackTrace();
                }
            }
        }).start();
    }
    //监听返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
//            Log.i("返回键的监听","xxxx");
//            Intent intent = new Intent(Intent.ACTION_MAIN);
//            intent.addCategory(Intent.CATEGORY_HOME);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//            Process.killProcess(Process.myPid());
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
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
    }
}
