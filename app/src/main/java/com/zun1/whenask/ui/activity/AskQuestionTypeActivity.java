package com.zun1.whenask.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.zun1.whenask.Constant;
import com.zun1.whenask.R;


public class AskQuestionTypeActivity extends AppCompatActivity implements View.OnClickListener{
    private Button takePhotoPostBtn,voicePostBtn,textPostBtn;
    private Button seleteSubjectBtn;
    private Button seleteGradeBtn;
    private int subjectId,grade;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_question_type);
        findViewById();
        setOnClickListener();
        //学生的id和grade
        subjectId = 1;
        grade = 1;
        //home
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        this.setTitle(R.string.ask_question);
    }
    void findViewById(){
//        takePhotoPostBtn = (Button)this.findViewById(R.id.ask_photograph);
        voicePostBtn = (Button)this.findViewById(R.id.ask_record);
//        textPostBtn = (Button)this.findViewById(R.id.ask_text);
        seleteSubjectBtn = (Button)this.findViewById(R.id.ask_subject);
        seleteGradeBtn = (Button)this.findViewById(R.id.ask_grade);
    }
    void setOnClickListener(){
        //takePhotoPostBtn.setOnClickListener(this);
        voicePostBtn.setOnClickListener(this);
        //textPostBtn.setOnClickListener(this);
        seleteSubjectBtn.setOnClickListener(this);
        seleteGradeBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(AskQuestionTypeActivity.this,TakePicturePostActivity.class);
        switch (view.getId()){
            //选择问题的subject
            case R.id.ask_subject:
                final String [] subjectArray = {getResources().getString(R.string.China),getResources().getString(R.string.English),
                        getResources().getString(R.string.Math),getResources().getString(R.string.Biology),getResources().getString(R.string.physics),
                        getResources().getString(R.string.chemistry),getResources().getString(R.string.polity),getResources().
                        getString(R.string.history),getResources().getString(R.string.geography)};
                AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.selete_subject).setIcon(R.mipmap.logo).setItems(subjectArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        seleteSubjectBtn.setText(subjectArray[i]);
                        subjectId = i+1;
                    }
                }).create();
                Window window = dialog.getWindow();
                window.setGravity(Gravity.BOTTOM);
                dialog.show();
                break;
            //年级
            case R.id.ask_grade:
                final String [] gradeArray = {getResources().getString(R.string.grade_1),getResources().getString(R.string.grade_2),
                        getResources().getString(R.string.grade_3),getResources().getString(R.string.grade_4),getResources().getString(R.string.grade_5),
                        getResources().getString(R.string.grade_6),getResources().getString(R.string.grade_7),getResources().
                        getString(R.string.grade_8),getResources().getString(R.string.grade_9),getResources().getString(R.string.grade_10),
                        getResources().getString(R.string.grade_11),getResources().getString(R.string.grade_12)};
                AlertDialog dialoggrade = new AlertDialog.Builder(this).setTitle(R.string.selete_grade).setIcon(R.mipmap.logo).setItems(gradeArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        seleteGradeBtn.setText(gradeArray[i]);
                        grade = i+1;
                    }
                }).create();
                window = dialoggrade.getWindow();
                window.setGravity(Gravity.BOTTOM);
                dialoggrade.show();
                break;
//            case R.id.ask_photograph:
//                intent.putExtra(Constant.POSTSUBJECTID,subjectId);
//                intent.putExtra(Constant.POSTGRADE,grade);
//                startActivity(intent);
//                break;
            case R.id.ask_record:
                intent.putExtra(Constant.POSTSUBJECTID,subjectId);
                intent.putExtra(Constant.POSTGRADE,grade);
                startActivity(intent);
                break;
//            case R.id.ask_text:
//                intent.putExtra(Constant.POSTSUBJECTID,subjectId);
//                intent.putExtra(Constant.POSTGRADE,grade);
//                startActivity(intent);
//                break;
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

