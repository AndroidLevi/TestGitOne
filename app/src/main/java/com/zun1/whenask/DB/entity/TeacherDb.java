package com.zun1.whenask.DB.entity;

import android.content.Context;
import android.content.SharedPreferences;

import com.zun1.whenask.Constant;
import com.zun1.whenask.DB.UserDb;

/**
 * Created by dell on 2016/7/4.
 */
public class TeacherDb {

    private final String name = "teacher.ini";
    private  static TeacherDb teacherDb;
    private Context context;

    SharedPreferences sharedPreferences;

    public static TeacherDb instance(){
        if(teacherDb == null){
            synchronized (TeacherDb.class){
                teacherDb = new TeacherDb();
            }
        }
        return teacherDb;
    }
    private TeacherDb(){}
    public void init(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(name,Context.MODE_PRIVATE);
    }
    public void setTeacherInfo(String intro){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.TEACHERINTT,intro);
        editor.commit();
    }
    public String getTeacherInfo(){
        return sharedPreferences.getString(Constant.TEACHERINTT,"");
    }

}
