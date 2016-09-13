package com.zun1.whenask.ToolsClass;

import android.content.Context;

import com.zun1.whenask.R;

/**
 * Created by zun1user7 on 2016/7/25.
 */
public class TextFromInt {
    public static String intToText(Context context,String subjectid){
        String subjectText = null;
        switch (subjectid){
            case "1":
                subjectText =context.getResources().getString(R.string.China);
                break;
            case "2":
                subjectText = context.getResources().getString(R.string.English);
                break;
            case "3":
                subjectText =context.getResources().getString(R.string.Math);
                break;
            case "4":
                subjectText = context.getResources().getString(R.string.Biology);
                break;
            case "5":
                subjectText = context.getResources().getString(R.string.physics);
                break;
            case "6":
                subjectText = context.getResources().getString(R.string.chemistry);
                break;
            case "7":
                subjectText = context.getResources().getString(R.string.polity);
                break;
            case "8":
                subjectText = context.getResources().getString(R.string.history);
                break;
            case "9":
                subjectText = context.getResources().getString(R.string.geography);
                break;

        }
        return subjectText;
    }
    public static String intToGrade(Context context,String grade){
        String gradeText = null;
        switch (grade){
            case "1":
                gradeText = context.getResources().getString(R.string.grade_1);
                break;
            case "2":
                gradeText = context.getResources().getString(R.string.grade_2);
                break;
            case "3":
                gradeText = context.getResources().getString(R.string.grade_3);
                break;
            case "4":
                gradeText = context.getResources().getString(R.string.grade_4);
                break;
            case "5":
                gradeText = context.getResources().getString(R.string.grade_5);
                break;
            case "6":
                gradeText = context.getResources().getString(R.string.grade_6);
                break;
            case "7":
                gradeText = context.getResources().getString(R.string.grade_7);
                break;
            case "8":
                gradeText = context.getResources().getString(R.string.grade_8);
                break;
            case "9":
                gradeText = context.getResources().getString(R.string.grade_9);
                break;
            case "10":
                gradeText = context.getResources().getString(R.string.grade_10);
                break;
            case "11":
                gradeText = context.getResources().getString(R.string.grade_11);
                break;
            case "12":
                gradeText = context.getResources().getString(R.string.grade_12);
                break;
        }
        return gradeText;
    }
    public static String intToRecord(Context context,String grade){
        String gradeText = null;
        switch (grade){
            case "1":
                gradeText = context.getResources().getString(R.string.record_1);
                break;
            case "2":
                gradeText = context.getResources().getString(R.string.record_2);
                break;
            case "3":
                gradeText = context.getResources().getString(R.string.record_3);
                break;
            case "4":
                gradeText = context.getResources().getString(R.string.record_4);
                break;
            case "5":
                gradeText = context.getResources().getString(R.string.record_5);
                break;
        }
        return gradeText;
    }
}
