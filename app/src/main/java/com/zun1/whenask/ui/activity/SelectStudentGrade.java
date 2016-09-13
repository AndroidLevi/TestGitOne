package com.zun1.whenask.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.zun1.whenask.Constant;
import com.zun1.whenask.R;
import com.zun1.whenask.SetLanguageApplication.SetLanguageApplicationClass;

public class SelectStudentGrade extends AppCompatActivity implements View.OnClickListener{
    private Button primaryBtn,middleBtn,seniorBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_student_grade);
        //actionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        findViewById();
        setOnClickListener();
        //different language to different picture
        languaeChangePicture();
        this.setTitle(R.string.please_selector);
        //add activity
        SetLanguageApplicationClass setLanguageApplicationClass = (SetLanguageApplicationClass) getApplication();
        setLanguageApplicationClass.activityList.add(this);
    }
    void findViewById(){
        primaryBtn = (Button)this.findViewById(R.id.studentgrade_primary);
        middleBtn = (Button)this.findViewById(R.id.studentgrade_middle);
        seniorBtn = (Button)this.findViewById(R.id.studentgrade_senior);
    }
    void setOnClickListener(){
        primaryBtn.setOnClickListener(this);
        middleBtn.setOnClickListener(this);
        seniorBtn.setOnClickListener(this);
    }
    //different language to different picture
    void languaeChangePicture(){
        SharedPreferences sharedPreferences = getSharedPreferences(Constant.fileName, Context.MODE_PRIVATE);
        String languageCode = sharedPreferences.getString(Constant.LANGUAGE_CODE,"");
        Log.i("languageCode",languageCode);
        if (languageCode.equals("zh-hk")){
            primaryBtn.setBackgroundResource(R.mipmap.iam_student_ps_f);
            middleBtn.setBackgroundResource(R.mipmap.student_middle_j);
            seniorBtn.setBackgroundResource(R.mipmap.student_senior_j);
        }
        if (languageCode.equals("zh-cn")){
            primaryBtn.setBackgroundResource(R.mipmap.student_primary_j);
            middleBtn.setBackgroundResource(R.mipmap.student_middle_j);
            seniorBtn.setBackgroundResource(R.mipmap.student_senior_j);
        }
        if (languageCode.equals("en-us")){
            primaryBtn.setBackgroundResource(R.mipmap.iam_student_ps_en);
            middleBtn.setBackgroundResource(R.mipmap.iam_student_fs_en);
            seniorBtn.setBackgroundResource(R.mipmap.iam_student_fs2_en);
        }

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.studentgrade_primary:
                String[] gradeArrayP= new String[]{getResources().getString(R.string.grade_1),getResources().getString(R.string.grade_2),
                        getResources().getString(R.string.grade_3),getResources().getString(R.string.grade_4),getResources().getString(R.string.grade_5),
                        getResources().getString(R.string.grade_6)};
                putGrade(gradeArrayP,1);
                break;
            case R.id.studentgrade_middle:
                String[] gradeArrayM = new String[]{getResources().getString(R.string.grade_7),getResources().getString(R.string.grade_8),getResources().getString(R.string.grade_9)};
                putGrade(gradeArrayM,7);
                break;
            case R.id.studentgrade_senior:
                String[] gradeArrayS = new String[]{getResources().getString(R.string.grade_10),getResources().getString(R.string.grade_11)
                        ,getResources().getString(R.string.grade_12)};
                putGrade(gradeArrayS,10);
                break;
        }
    }
    //根据不同的年级，传不同的值
    void putGrade(String[] gradeArray, final int gradeNumber){
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.selete_grade).setIcon(R.mipmap.logo).setItems(gradeArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i("i", String.valueOf(i+gradeNumber));
                Intent intent = new Intent(SelectStudentGrade.this,RegisterActivity.class);
                intent.putExtra("grade",i+gradeNumber);
                startActivity(intent);
            }
        }).setNegativeButton(R.string.sure_yes,null).create();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        dialog.show();
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
