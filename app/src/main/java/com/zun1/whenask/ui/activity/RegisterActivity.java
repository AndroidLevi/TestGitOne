package com.zun1.whenask.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.zun1.whenask.Constant;
import com.zun1.whenask.DB.UserDb;
import com.zun1.whenask.R;
import com.zun1.whenask.SetLanguageApplication.SetLanguageApplicationClass;
import com.zun1.whenask.ToolsClass.ActivityIndicatorView;
import com.zun1.whenask.ToolsClass.HttpRequest;
import com.zun1.whenask.ToolsClass.UserDialog;
import com.zun1.whenask.service.network.Register;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener{
    private EditText userName,firstname,lastname,password,email,iphonenumber,address,id;
    private CheckBox checkBox;
    private Button submit,reset;
    private Boolean isSureIphoneNumber = false,isSureEmail= false,isSurePassWord= false,isSureUsername= false,isSureId= false;
    private static final String TAG = "Register";
    private String userNames,firstnames,lastnames,passwords,emails,iphonenumbers,addresss,language;
    private Map<String,String> userItem;
    private List<Map<String,String>> userInfo;
    private UserDb userDb;
    private String url;
    private EditText authEdit;
    private Button getAuthBtn;
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what != 000){

            }
            ActivityIndicatorView.dismiss();
            switch (msg.what){
                case 000:
                    Toast.makeText(RegisterActivity.this,R.string.post_succeed,Toast.LENGTH_SHORT).show();
                    break;
                case 114:
                    Toast.makeText(RegisterActivity.this,R.string.email_format_error,Toast.LENGTH_SHORT).show();
                    break;
                case 132:
                    Toast.makeText(RegisterActivity.this,R.string.post_email_fail,Toast.LENGTH_SHORT).show();
                    break;
                case 141:
                    Toast.makeText(RegisterActivity.this,R.string.username_registed,Toast.LENGTH_SHORT).show();
                    break;
                case 142:
                    Toast.makeText(RegisterActivity.this,R.string.email_registed,Toast.LENGTH_SHORT).show();
                    break;
                case 143:
                    Toast.makeText(RegisterActivity.this,R.string.phone_registed,Toast.LENGTH_SHORT).show();
                    break;
                case 144:
                    Toast.makeText(RegisterActivity.this,R.string.id_registed,Toast.LENGTH_SHORT).show();
                    break;
                case 115:
                    Toast.makeText(RegisterActivity.this,R.string.authcode_past,Toast.LENGTH_SHORT).show();
                    break;
                case 116:
                    Toast.makeText(RegisterActivity.this,R.string.authcodeError,Toast.LENGTH_SHORT).show();
                    break;
                case 200:
                    UserDialog.serverError(RegisterActivity.this);
                    break;
                case 500:
                    Toast.makeText(RegisterActivity.this,R.string.register_succeed,Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        userDb = UserDb.instance();
        userDb.init(this);
        findViewById();
        setOnClickListener();
        this.setTitle(R.string.register);
        Intent intent = getIntent();
        //add activity
        SetLanguageApplicationClass setLanguageApplicationClass = (SetLanguageApplicationClass) getApplication();
        setLanguageApplicationClass.activityList.add(this);
        //actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
    void findViewById(){
        userName = (EditText)this.findViewById(R.id.edText_register_username);
        firstname = (EditText)this.findViewById(R.id.edText_register_firstname);
        lastname = (EditText)this.findViewById(R.id.edText_register_lastname);
        password = (EditText)this.findViewById(R.id.edText_register_password);
        email = (EditText)this.findViewById(R.id.edText_register_email);
        iphonenumber = (EditText)this.findViewById(R.id.edText_register_iphonenumber);
        //address = (EditText)this.findViewById(R.id.edText_register_address);
        //id = (EditText)this.findViewById(R.id.edText_register_identi);
        //checkBox = (CheckBox)this.findViewById(R.id.is_savePassword);
        submit = (Button)this.findViewById(R.id.register_submit);
        reset = (Button)this.findViewById(R.id.register_reset);
        authEdit = (EditText)this.findViewById(R.id.edText_register_auth);
        getAuthBtn = (Button)this.findViewById(R.id.button_register_getauth);
    }
    void setOnClickListener(){
        submit.setOnClickListener(this);
        reset.setOnClickListener(this);
        getAuthBtn.setOnClickListener(this);
        //checkBox.setOnCheckedChangeListener(this);
        //id.addTextChangedListener(new IdentificationIs());
        iphonenumber.addTextChangedListener(new IphoneNumber());
        userName.addTextChangedListener(new NameChange());
        password.addTextChangedListener(new PasswordChange());
        email.addTextChangedListener(new EmailChange());

    }
    private String getEdit(EditText editText){
        return editText.getText().toString();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.register_submit:
                if (isSureUsername != true){
                    Toast.makeText(RegisterActivity.this,R.string.username_format_error,Toast.LENGTH_SHORT).show();
                    return;
                }else if (isSurePassWord != true){
                    Toast.makeText(RegisterActivity.this,R.string.password_format_error,Toast.LENGTH_SHORT).show();
                    return;
                }else if (isSureEmail != true){
                    Toast.makeText(RegisterActivity.this,R.string.email_format_error,Toast.LENGTH_SHORT).show();
                    return;
                }else if (isSureIphoneNumber != true){
                    Toast.makeText(RegisterActivity.this,R.string.phone_format_error,Toast.LENGTH_SHORT).show();
                    return;
                }
//                else if (isSureId != true){
//                    Toast.makeText(RegisterActivity.this,R.string.id_format_error,Toast.LENGTH_SHORT).show();
//                    return;
//                }
                ActivityIndicatorView.start(RegisterActivity.this);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SetLanguageApplicationClass applicationClass = (SetLanguageApplicationClass) getApplication();
                        SharedPreferences sharedPreferences = getSharedPreferences("user.ini", Context.MODE_PRIVATE);
                        List<Map<String,String>> list = new ArrayList<Map<String, String>>();
                        Map<String,String> mapname = new HashMap<String, String>();
                        mapname.put(Constant.USERNAME,getEdit(userName));
                        list.add(mapname);
                        Map<String,String> maplastname = new HashMap<String, String>();
                        maplastname.put(Constant.LASTNAME,getEdit(lastname));
                        list.add(maplastname);
                        Map<String,String> mapfirstname = new HashMap<String, String>();
                        mapfirstname.put(Constant.FIRSTNAME,getEdit(firstname));
                        list.add(mapfirstname);
                        Map<String,String> mappassword = new HashMap<String, String>();
                        mappassword.put(Constant.PASSWORD,md5(getEdit(password)));
                        list.add(mappassword);
                        Map<String,String> mapemail = new HashMap<String, String>();
                        mapemail.put(Constant.EMAIL,getEdit(email));
                        list.add(mapemail);
                        Map<String,String> mapphone = new HashMap<String, String>();
                        mapphone.put(Constant.PHONE,getEdit(iphonenumber));
                        list.add(mapphone);
//                        Map<String,String> mapaddress = new HashMap<String, String>();
//                        mapaddress.put(Constant.ADDRESS,getEdit(address));
//                        list.add(mapaddress);
                        Map<String,String> maplanguage_code = new HashMap<String, String>();
                        maplanguage_code.put(Constant.LANGUAGE_CODE,sharedPreferences.getString(Constant.LANGUAGE_CODE,""));
                        list.add(maplanguage_code);
//                        Map<String,String> mapidentification = new HashMap<String, String>();
//                        mapidentification.put(Constant.IDENTIFICATION,getEdit(id));
//                        list.add(mapidentification);
                        Map<String,String> mapauthcode = new HashMap<String, String>();
                        mapauthcode.put("authcode",getEdit(authEdit));
                        list.add(mapauthcode);
                        Map<String,String> maparea = new HashMap<String, String>();
                        maparea.put(Constant.AREA,"1");
                        list.add(maparea);
                        Intent intentGrade = getIntent();
                        if (intentGrade.getIntExtra("grade",14) < 13){
                            Map<String,String> mapgrade = new HashMap<String, String>();
                            mapgrade.put(Constant.GRADE, String.valueOf(intentGrade.getIntExtra("grade",1)));
                            list.add(mapgrade);
                        }
//                        Map<String,String> maplon = new HashMap<String, String>();
//                        maplon.put("lon", String.valueOf(applicationClass.getLon()));
//                        list.add(maplon);
//                        Map<String,String> maplat = new HashMap<String, String>();
//                        maplat.put("lat",String.valueOf(applicationClass.getLat()));
//                        list.add(maplat);
                        Log.i("list.size", String.valueOf(list.size()));
                        //判断是学生还是老师注册
                        if (intentGrade.getIntExtra("grade",14) < 13){
                             url = Constant.REGISTERURL;
                            Log.i("student_teacher","学生");
                        }else {
                            url = Constant.REGISTERURL_TEACHER;
                            Log.i("student_teacher","老师");
                        }

                        String result = HttpRequest.postRequestBody(list,url);
                        if (result == null){
                            Log.i("youwentile","youwentile");
                            //服务器出错
                            Message message = Message.obtain();
                            handler.sendEmptyMessage(message.what = 200);
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String errorCode = jsonObject.optString("ErrorCode");
                            if (errorCode.equals("000")){
                                Message message = Message.obtain();
                                handler.sendEmptyMessage(message.what = 500);
                                //判断是学生还是老师注册成功
                                if (intentGrade.getIntExtra("grade",14) < 13){
                                    Log.i("student_teacher","学生");
                                    Intent intent = new Intent(RegisterActivity.this,LoginImportActivity.class);
                                    startActivity(intent);
                                }else {
                                    Intent intent = new Intent(RegisterActivity.this,TeacherSelectSubject.class);
                                    Log.i("student_teacher","老师");
                                    intent.putExtra(Constant.USERNAME,userName.getText().toString().trim());
                                    startActivity(intent);
                                }

                            }if (errorCode.equals("141")){
                                Message message = Message.obtain();
                                handler.sendEmptyMessage(message.what = 141);
                            }if (errorCode.equals("142")){
                                Message message = Message.obtain();
                                handler.sendEmptyMessage(message.what = 142);
                            }if (errorCode.equals("143")){
                                Message message = Message.obtain();
                                handler.sendEmptyMessage(message.what = 143);
                            }if (errorCode.equals("144")){
                                Message message = Message.obtain();
                                handler.sendEmptyMessage(message.what = 144);
                            }if (errorCode.equals("115")){
                                Message message = Message.obtain();
                                handler.sendEmptyMessage(message.what = 115);
                            }
                            if (errorCode.equals("116")){
                                Message message = Message.obtain();
                                handler.sendEmptyMessage(message.what = 116);
                            }
                            if (errorCode.equals("137")){
                                //服务器出错
                                Message message = Message.obtain();
                                handler.sendEmptyMessage(message.what = 200);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case R.id.register_reset:
                //重填
                userName.setText("");
                firstname.setText("");
                lastname.setText("");
                password.setText("");
                email.setText("");
                iphonenumber.setText("");
                authEdit.setText("");
                break;
            case R.id.button_register_getauth:
                if (email.getText().length() == 0){
                    Toast.makeText(RegisterActivity.this,R.string.email_format_error,Toast.LENGTH_SHORT).show();
                    return;
                }
                ActivityIndicatorView.start(RegisterActivity.this);
                //得到验证码
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences sharedPreferences = getSharedPreferences(Constant.fileName,MODE_PRIVATE);
                        List<Map<String,String>> list = new ArrayList<Map<String, String>>();
                        Map<String,String> mapemail = new HashMap<String, String>();
                        mapemail.put(Constant.EMAIL,getEdit(email));
                        list.add(mapemail);
                        Map<String,String> maplanguagecode = new HashMap<String, String>();
                        maplanguagecode.put(Constant.LANGUAGE_CODE,sharedPreferences.getString(Constant.LANGUAGE_CODE,""));
                        list.add(maplanguagecode);
                        Intent intentGrade = getIntent();
                        String usertype ;
                        if (intentGrade.getIntExtra("grade",14) < 13){
                            usertype = "1";
                        }else {
                            usertype = "2";
                        }
                        Map<String,String> mapusertype = new HashMap<String, String>();
                        mapusertype.put(Constant.USERTYPE,usertype);
                        list.add(mapusertype);
                        String url = Constant.REGISTERGETAUTHCODEURL;
                        String result = HttpRequest.postRequestBody(list,url);
                        if (result == null){
                            //服务器出错
                            Message message = Message.obtain();
                            handler.sendEmptyMessage(message.what = 200);
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String ErrorCode = jsonObject.optString("ErrorCode");
                            if (ErrorCode.equals("000")){
                                Message message = Message.obtain();
                                handler.sendEmptyMessage(message.what = 000);
                            }
                            if (ErrorCode.equals("132")){
                                Message message = Message.obtain();
                                handler.sendEmptyMessage(message.what = 132);
                            }
                            if (ErrorCode.equals("114")){
                                Message message = Message.obtain();
                                handler.sendEmptyMessage(message.what = 114);
                            }
                        } catch (JSONException e) {
                            Message message = Message.obtain();
                            handler.sendEmptyMessage(message.what = 200);
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
        }
    }

//    class IdentificationIs implements TextWatcher{
//        @Override
//        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//        }
//
//        @Override
//        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//        }
//
//        @Override
//        public void afterTextChanged(Editable editable) {
//            isSureId = Pattern.matches("^(^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$)|(^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])((\\d{4})|\\d{3}[Xx])$)$"
//            ,editable.toString());
//            if (isSureId){
//                Log.i(TAG,"ID-->" +isSureId);
//                id.setTextColor(Color.WHITE);
//            }else {
//                Log.i(TAG,"id--->" + isSureId);
//                id.setTextColor(Color.RED);
//            }
//        }
//    }
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(b){
            Log.i(TAG,"SAVEPASSWORD：：：："+ true);

            //userDb.setUserInfo(getEdit(userName),getEdit(firstname),getEdit(lastname),"qweewqwe",getEdit(address),getEdit(id));
        }else {
            Log.i(TAG,"SAVEPASS::::"+false);
        }
    }

    class IphoneNumber implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            isSureIphoneNumber = Pattern.matches("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$",editable.toString());
            //isSureIphoneNumber = editable.toString().matches("^((13[0-9])|(15[^4,\\\\D])|(18[0,5-9]))\\\\d{8}$");

            if(isSureIphoneNumber){
                iphonenumber.setTextColor(Color.WHITE);
                Log.i(TAG, "isSureIphoneNumber" + editable.toString()+"》"+isSureIphoneNumber.toString());
            }else {
                iphonenumber.setTextColor(Color.RED);
                Log.i(TAG, "isSureIphoneNumber" +editable.toString()+"》"+ isSureIphoneNumber.toString());
            }
        }
    }

    class EmailChange implements TextWatcher{
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            isSureEmail = Pattern.matches("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$",editable.toString());
            //isSureEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(editable.toString()).matches();
            if(isSureEmail){
                email.setTextColor(Color.WHITE);
                Log.i(TAG, "isEmail-- " + editable.toString()+":::"+isSureEmail);
            }else {
                email.setTextColor(Color.RED);
                Log.i(TAG, "isEmail--" + editable.toString()+":::"+isSureEmail);
            }
        }
    }
    class PasswordChange implements TextWatcher{
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            isSurePassWord = Pattern.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])[a-zA-Z\\d]{8,20}$",editable.toString());
            if(isSurePassWord){
                password.setTextColor(Color.WHITE);
                Log.i(TAG, "password:::: " + editable.toString()+"::::"+isSurePassWord.toString());
            }else {
                password.setTextColor(Color.RED);
                Log.i(TAG, "password:::: " +editable.toString()+ ":::"+ isSurePassWord.toString());
            }
        }
    }
    class NameChange implements TextWatcher
    {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

            isSureUsername = Pattern.matches("^[a-zA-Z0-9_]{8,20}",editable.toString());
            if(isSureUsername){
                userName.setTextColor(Color.WHITE);
                Log.i(TAG, "isSureUsername ::::" + editable.toString()+"::"+isSureUsername.toString());
            }else {
                userName.setTextColor(Color.RED);
                Log.i(TAG,"isSureUsername::::   " + editable.toString() +"::  "+ isSureUsername.toString());
            }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
