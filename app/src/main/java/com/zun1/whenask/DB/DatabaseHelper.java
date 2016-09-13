package com.zun1.whenask.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by zun1user7 on 2016/8/17.
 */
public class DatabaseHelper extends SQLiteOpenHelper{
    private static final int VERSION = 1;
    private static final String SWORD= "SWORD";
    //带全部参数的构造函数，此构造函数必不可少
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    //带两个参数的构造函数，调用的其实是带三个参数的构造函数
    //带两个参数的构造函数，调用的其实是带三个参数的构造函数
    public DatabaseHelper(Context context,String name){
        this(context,name,VERSION);
    }
    //带三个参数的构造函数，调用的是带所有参数的构造函数
    public DatabaseHelper(Context context,String name,int version){
        this(context, name,null,version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(SWORD,"create a Database");
        //创建数据库操作
        String sql = "create table user(id int,subject varchar(20),grade varchar(20),image varchar(50),text varchar(20))";
        //执行创建数据库操作
        db.execSQL(sql);
    }
   //更新数据库
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
