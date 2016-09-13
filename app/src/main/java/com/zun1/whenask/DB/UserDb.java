package com.zun1.whenask.DB;

import android.content.Context;
import android.content.SharedPreferences;

import com.zun1.whenask.Constant;

import retrofit2.http.PUT;

/**
 * Created by dell on 2016/7/4.
 */
public class UserDb {

    private final String fileName = "user.ini";
    private Context context;
    SharedPreferences sharedPreferences;

    private static UserDb userDb = null;
    public static UserDb instance(){
        if(userDb == null){
            synchronized (UserDb.class){
                userDb = new UserDb();
            }
        }
        return userDb;
    }
    private UserDb(){

    }
    public void init(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(fileName,context.MODE_PRIVATE);
    }
    public String getLoginImmportPassword(){
        return sharedPreferences.getString(Constant.PASSWORD,"");
    }
    public String getLoginImportName(){
         return sharedPreferences.getString(Constant.USERNAME,"");
    }
    public void setUserInfo(String nickname,String firstname,String grade,String lastname,String phone,String birthday,String address,String school,String gender,String avatar,String email){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.NICKNAME,nickname);
        editor.putString(Constant.FIRSTNAME,firstname);
        editor.putString(Constant.GRADE,grade);
        editor.putString(Constant.LASTNAME,lastname);
        editor.putString(Constant.PHONE,phone);
        editor.putString(Constant.BIRTHDAY,birthday);
        editor.putString(Constant.ADDRESS,address);
        editor.putString(Constant.SCHOOL,school);
        editor.putString(Constant.GENDER,gender);
        editor.putString(Constant.AVATER,avatar);
        //editor.putString(Constant.HOBBY,hobby);
        editor.putString(Constant.EMAIL,email);
        editor.commit();
    }
    public void setUserInfoTeather(String nickname,String firstname,String lastname,String phone,String birthday,String address,String school,String gender,String avatar,String email,String degree,String worklong,String subjectidlist){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.NICKNAME,nickname);
        editor.putString(Constant.FIRSTNAME,firstname);
        editor.putString(Constant.LASTNAME,lastname);
        editor.putString(Constant.PHONE,phone);
        editor.putString(Constant.BIRTHDAY,birthday);
        editor.putString(Constant.ADDRESS,address);
        editor.putString(Constant.SCHOOL,school);
        editor.putString(Constant.GENDER,gender);
        editor.putString(Constant.AVATER,avatar);
        editor.putString(Constant.EMAIL,email);
        editor.putString(Constant.DEGREE,degree);
        editor.putString(Constant.WORKLONG,worklong);
        editor.putString(Constant.SUBJECTIDLIST,subjectidlist);
        editor.commit();
    }
    public void setUserInfoStudent(String nickname,String grade,String birthday,String address,String school,String gender,String email){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.NICKNAME,nickname);
        editor.putString(Constant.GRADE,grade);
        editor.putString(Constant.BIRTHDAY,birthday);
        editor.putString(Constant.ADDRESS,address);
        editor.putString(Constant.SCHOOL,school);
        editor.putString(Constant.GENDER,gender);
        editor.putString(Constant.EMAIL,email);
        editor.commit();
    }
    public void setUserInfoTeacher(String nickname,String worklong,String birthday,String address,String degree,String gender,String email){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.NICKNAME,nickname);
        editor.putString(Constant.WORKLONG,worklong);
        editor.putString(Constant.BIRTHDAY,birthday);
        editor.putString(Constant.ADDRESS,address);
        editor.putString(Constant.DEGREE,degree);
        editor.putString(Constant.GENDER,gender);
        editor.putString(Constant.EMAIL,email);
        editor.commit();
    }
    public void setLanguageInfo(String languageInfo){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.LANGUAGE_CODE,languageInfo);
        editor.commit();
    }
    public void saveloginkeychain(String loginkeychain){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.LOGINKEYCHAIN,loginkeychain);
        editor.commit();
    }
    public void saveloginInfo(String usertype,String userid){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.USERTYPE,usertype);
        editor.putString(Constant.USERID,userid);
        editor.commit();
    }
    public void saveUserInfo(String username,String password){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.USERNAME,username);
        editor.putString(Constant.PASSWORD,password);
        editor.commit();
    }
    public void savePasswordCheck(boolean ischeck){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constant.ISCHECK,ischeck);
        editor.commit();
    }
    //登记android设备 deviceInfoToGetId
    public void savedeviceId(String deviceId){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.DEVICEID,deviceId);
        editor.commit();
    }
    //
}
