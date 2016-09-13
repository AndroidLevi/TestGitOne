package com.zun1.whenask.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.zun1.whenask.Constant;
import com.zun1.whenask.R;
import com.zun1.whenask.SetLanguageApplication.SetLanguageApplicationClass;

public class SelectRegisterType extends AppCompatActivity implements View.OnClickListener{
    private ImageButton studentTypeBtn,teacherTypeBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_register_type);
        this.setTitle(R.string.please_selector);
        //actionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        findViewById();
        setOnClickListener();
        //get language to setting button picture
        getLanguageToPicture();
        //add activity
        SetLanguageApplicationClass setLanguageApplicationClass = (SetLanguageApplicationClass) getApplication();
        setLanguageApplicationClass.activityList.add(this);
    }
    void findViewById(){
        studentTypeBtn = (ImageButton)this.findViewById(R.id.selecttype_studentBtn);
        teacherTypeBtn = (ImageButton)this.findViewById(R.id.selecttype_teacherBtn);
    }
    void setOnClickListener(){
        studentTypeBtn.setOnClickListener(this);
        teacherTypeBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.selecttype_studentBtn:
                Intent intent = new Intent(this,SelectStudentGrade.class);
                startActivity(intent);
                break;
            case R.id.selecttype_teacherBtn:
                Intent intent1 = new Intent(this,RegisterActivity.class);
                startActivity(intent1);
                break;
        }
    }
    void getLanguageToPicture(){
        SharedPreferences sharedPreferences = getSharedPreferences(Constant.fileName, Context.MODE_PRIVATE);
        String languageCode = sharedPreferences.getString(Constant.LANGUAGE_CODE,"");
        Log.i("languageCode",languageCode);
        if (languageCode.equals("zh-hk")){
            studentTypeBtn.setBackgroundResource(R.mipmap.select_student_f);
            teacherTypeBtn.setBackgroundResource(R.mipmap.select_teacher_f);
        }
        if (languageCode.equals("zh-cn")){
            studentTypeBtn.setBackgroundResource(R.mipmap.select_student_j);
            teacherTypeBtn.setBackgroundResource(R.mipmap.select_teacher_j);
        }
        if (languageCode.equals("en-us")){
            studentTypeBtn.setBackgroundResource(R.mipmap.select_student_en);
            teacherTypeBtn.setBackgroundResource(R.mipmap.select_teacher_en);
        }
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
