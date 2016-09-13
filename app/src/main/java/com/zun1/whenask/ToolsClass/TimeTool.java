package com.zun1.whenask.ToolsClass;

import android.content.Context;
import android.util.Log;

import com.zun1.whenask.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by zun1user7 on 2016/8/3.
 */
public class TimeTool {

    public static String getTime(Context context,String backstage_time){
        String time = null;
        //backstage_time
        backstage_time = backstage_time.substring(0,10)+" "+backstage_time.substring(11,19);
        Log.i("backstage_time",backstage_time);
        //当前时间
        Date currentTiem = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date1 = format.format(currentTiem);
        //计算的时间差
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date d1 = df.parse(date1);
            Date d2 = df.parse(backstage_time);
            long diff = d1.getTime()-d2.getTime();
            long days = diff / (1000 * 60 * 60 * 24);
            long hours = (diff - days * (1000 * 60 * 60 * 24))
                    / (1000 * 60 * 60);
            long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours
                    * (1000 * 60 * 60))
                    / (1000 * 60);
            Log.i("diff", String.valueOf(hours));
            if (days > 7){
                time = backstage_time.substring(0,10);
            }else if (days>0){
                time = days+ context.getResources().getString(R.string.day);
            }else if (hours>0){
                time = hours+ context.getResources().getString(R.string.hours);
            }else if (minutes>0){
                time = minutes+ context.getResources().getString(R.string.minutes);
            }else {
                time = context.getResources().getString(R.string.justnow);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }
}
