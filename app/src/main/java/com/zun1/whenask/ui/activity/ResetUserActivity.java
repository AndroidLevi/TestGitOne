package com.zun1.whenask.ui.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.android.gms.appindexing.Action;
//import com.google.android.gms.appindexing.AppIndex;
//import com.google.android.gms.appindexing.Thing;
//import com.google.android.gms.common.api.GoogleApiClient;
import com.zun1.whenask.Constant;
import com.zun1.whenask.DB.UserDb;
import com.zun1.whenask.R;
import com.zun1.whenask.ToolsClass.ActivityIndicatorView;
import com.zun1.whenask.ToolsClass.HttpRequest;
import com.zun1.whenask.ToolsClass.TextFromInt;
import com.zun1.whenask.ToolsClass.UserDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.zun1.whenask.DB.dao.UserDao.Properties.username;

public class ResetUserActivity extends AppCompatActivity implements View.OnClickListener,View.OnTouchListener{
    private EditText schoolEdit,gradeEdit, usernameEdit,nameEdit,borthEdit,emailEdit,addressEdit,useEdit,ideaEdit,declarationEdit;
    private TextView schoolEducationText,gradeAgelimitText;
    private RadioButton manRadioBtn,womanRadioBtn;
    private RadioGroup sexRadioGroup ;
    private Toolbar toolbar;
    private Button saveBtn;
    String result;
    UserDb userDb;
    private DatePickerDialog datePickerDialog = null;
    int year ; //获取Calendar对象中的年
    int month;//获取Calendar对象中的月
    int day;//获取这个月的第几天
    //保存年级/年限
    int saveGrade;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ActivityIndicatorView.dismiss();
            switch (msg.what){
                case 000:
                    Toast.makeText(ResetUserActivity.this,getResources().getText(R.string.save_succeed),Toast.LENGTH_SHORT).show();
                    break;
                case 001:
                    UserDialog.serverError(ResetUserActivity.this);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_reset_user);
        findViewById();
        //getUserInfo
        getUserInfoToEdit();
        setOnCheckedChangeListener();
        //set toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("");
        saveBtn.setOnClickListener(this);
        userDb = UserDb.instance();
        userDb.init(this);
        //home

    }
   void findViewById(){
       usernameEdit = (EditText)this.findViewById(R.id.nav_edText_username);
       nameEdit = (EditText)this.findViewById(R.id.nav_edText_name);
       borthEdit = (EditText)this.findViewById(R.id.nav_broth);
       emailEdit = (EditText)this.findViewById(R.id.nav_edText_email);
       //addressEdit = (EditText)this.findViewById(R.id.nav_edText_address);
       schoolEdit = (EditText)this.findViewById(R.id.nav_edText_school);
       gradeEdit = (EditText)this.findViewById(R.id.nav_edText_grade);

       useEdit = (EditText)this.findViewById(R.id.nav_edText_use);
       ideaEdit = (EditText)this.findViewById(R.id.nav_edText_idea);
       declarationEdit = (EditText)this.findViewById(R.id.nav_edText_declaration) ;

       manRadioBtn = (RadioButton)this.findViewById(R.id.nav_RadionButton_man);
       womanRadioBtn = (RadioButton)this.findViewById(R.id.nav_RadionButton_woman);
       sexRadioGroup = (RadioGroup) this.findViewById(R.id.nav_RadionButton_sex);
       toolbar = (Toolbar)this.findViewById(R.id.userInfo_toolbar);
       saveBtn = (Button)this.findViewById(R.id.reset_user_save);
       schoolEducationText = (TextView)this.findViewById(R.id.scholl_education_text);
       gradeAgelimitText = (TextView)this.findViewById(R.id.grade_agelimit_text);
       //老师
       if (getSharedPreferences(Constant.fileName,MODE_PRIVATE).getString(Constant.USERTYPE,"").equals("2")){
            schoolEducationText.setText(R.string.teacher_education);
           gradeAgelimitText.setText(R.string.teacher_agelimit);
       }
       //borthEdit.addTextChangedListener(new borthChange());
//       borthEdit.setOnClickListener(new borthChange());
       borthEdit.setOnTouchListener(new borthChange());
       gradeEdit.setOnTouchListener(new gradeChange());
       schoolEdit.setOnTouchListener(new schoolChange());

       useEdit.setOnTouchListener(this);
       ideaEdit.setOnTouchListener(this);
       declarationEdit.setOnTouchListener(this);

       //初始化Calendar日历对象
       Calendar mycalendar=Calendar.getInstance(Locale.CHINA);
       Date mydate=new Date(); //获取当前日期Date对象
       mycalendar.setTime(mydate);////为Calendar对象设置时间为当前日期

       year = mycalendar.get(Calendar.YEAR); //获取Calendar对象中的年
       month=mycalendar.get(Calendar.MONTH);//获取Calendar对象中的月
       day=mycalendar.get(Calendar.DAY_OF_MONTH);//获取这个月的第几天
       datePickerDialog = new DatePickerDialog(ResetUserActivity.this,Datelistener,year,month,day);
   }
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            switch (view.getId()) {
                case R.id.nav_edText_use:
                    Intent intent = new Intent(ResetUserActivity.this, TermsForUsageActivity.class);
                    startActivity(intent);
                    break;
                case R.id.nav_edText_idea:
                    Intent intent1 = new Intent(ResetUserActivity.this, TeacherIntroduceActivity.class);
                    startActivity(intent1);
                    break;
                case R.id.nav_edText_declaration:
                    Intent intent2 = new Intent(ResetUserActivity.this, PrivacyPolicyActivity.class);
                    startActivity(intent2);
                    break;
            }
        }
        return false;
    }

    //日期的选择
    class borthChange implements View.OnTouchListener{
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                borthEdit.setInputType(InputType.TYPE_NULL);
                datePickerDialog.show();
            }
            return false;
        }
    }
    //日期选择器
    private DatePickerDialog.OnDateSetListener Datelistener = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker datePicker, int myyear, int monthOfYear, int dayOfMonth) {
            //修改year、month、day的变量值，以便以后单击按钮时，DatePickerDialog上显示上一次修改后的值
            year = myyear;
            month = monthOfYear;
            day = dayOfMonth;
            //更新
            String time = null;
            if (month<10 && day<10){
                time = year+"-"+"0"+month+"-"+"0"+day;
            }else if (month<10){
                time = year+"-"+"0"+month+"-"+day;
            }else if (day<10){
                time = year+"-"+month+"-"+"0"+day;
            }else {
                time = year+"-"+month+"-"+day;
            }

            borthEdit.setText(time);
        }
    };
    //年级选择
    class gradeChange implements View.OnTouchListener{
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                if (getSharedPreferences(Constant.fileName, MODE_PRIVATE).getString(Constant.USERTYPE, "").equals("1")) {
                    gradeEdit.setInputType(InputType.TYPE_NULL);
                    final String grade[] = new String[]{
                            getResources().getString(R.string.grade_1),
                            getResources().getString(R.string.grade_2),
                            getResources().getString(R.string.grade_3),
                            getResources().getString(R.string.grade_4),
                            getResources().getString(R.string.grade_5),
                            getResources().getString(R.string.grade_6),
                            getResources().getString(R.string.grade_7),
                            getResources().getString(R.string.grade_8),
                            getResources().getString(R.string.grade_9),
                            getResources().getString(R.string.grade_10),
                            getResources().getString(R.string.grade_11),
                            getResources().getString(R.string.grade_12)
                    };
                    AlertDialog dialog = new AlertDialog.Builder(ResetUserActivity.this).setTitle(R.string.selete_grade).setIcon(R.mipmap.logo).setItems(grade, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            gradeEdit.setText(grade[i]);
                            saveGrade = i + 1;
                        }
                    }).setNegativeButton(R.string.sure_yes, null).create();
                    Window window = dialog.getWindow();
                    window.setGravity(Gravity.BOTTOM);
                    dialog.show();
                }
            }
            return false;
        }
    }
    //学历选择
    public class schoolChange implements View.OnTouchListener{
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                if (getSharedPreferences(Constant.fileName, MODE_PRIVATE).getString(Constant.USERTYPE, "").equals("2")) {
                    schoolEdit.setInputType(InputType.TYPE_NULL);
                    final String record[] = new String[]{
                            getResources().getString(R.string.record_1),
                            getResources().getString(R.string.record_2),
                            getResources().getString(R.string.record_3),
                            getResources().getString(R.string.record_4),
                            getResources().getString(R.string.record_5)
                    };
                    AlertDialog dialog = new AlertDialog.Builder(ResetUserActivity.this).setTitle(R.string.selete_record).setIcon(R.mipmap.logo).setItems(record, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            schoolEdit.setText(record[i]);
                            saveGrade = i + 1;
                        }
                    }).setNegativeButton(R.string.sure_yes, null).create();
                    Window window = dialog.getWindow();
                    window.setGravity(Gravity.BOTTOM);
                    dialog.show();
                }
            }
            return false;
        }
    }
    void setOnCheckedChangeListener() {
        sexRadioGroup.setOnCheckedChangeListener(new radioChange());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.reset_user_save:
                //修改用户信息
                resetUserInfo();
                break;
        }
    }

    class radioChange implements RadioGroup.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {

        }
    }
    //getUserInfo
    void getUserInfoToEdit(){
        SharedPreferences sharedPreferences = getSharedPreferences(Constant.fileName, Context.MODE_PRIVATE);
        String nickname = sharedPreferences.getString(Constant.NICKNAME,"");
        usernameEdit.setText(nickname);
        String firstname = sharedPreferences.getString(Constant.FIRSTNAME,"");
        String lastname = sharedPreferences.getString(Constant.LASTNAME,"");
        nameEdit.setText(lastname+firstname);
        String gender = sharedPreferences.getString(Constant.GENDER,"");
        Log.i("gender2",gender);
        if (gender.equals("1")){
           manRadioBtn.setChecked(true);
        }else {
            womanRadioBtn.setChecked(true);
        }
        String birthday = sharedPreferences.getString(Constant.BIRTHDAY,"");
        borthEdit.setText(birthday);
        String email = sharedPreferences.getString(Constant.EMAIL,"");
        emailEdit.setText(email);
//        String address = sharedPreferences.getString(Constant.ADDRESS,"");
//        addressEdit.setText(address);
        //学生
        String school;
        String grade;
//        school = sharedPreferences.getString(Constant.SCHOOL,"");
//        grade = sharedPreferences.getString(Constant.GRADE,"");
        if (getSharedPreferences(Constant.fileName,MODE_PRIVATE).getString(Constant.USERTYPE,"").equals("1")){
            school = sharedPreferences.getString(Constant.SCHOOL,"");
            grade = TextFromInt.intToGrade(ResetUserActivity.this,sharedPreferences.getString(Constant.GRADE,""));
        }else {
            //老师
            school = TextFromInt.intToRecord(ResetUserActivity.this,sharedPreferences.getString(Constant.DEGREE,""));
            grade = sharedPreferences.getString(Constant.WORKLONG,"");
        }
        schoolEdit.setText(school);
        gradeEdit.setText(grade);
    }
    //修改用户信息
    public void resetUserInfo(){
        ActivityIndicatorView.start(ResetUserActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final SharedPreferences sharedPreferences = getSharedPreferences(Constant.fileName,MODE_PRIVATE);
                List<Map<String,String>> list = new ArrayList<>();
                Map<String,String> mapuserid = new HashMap<>();
                mapuserid.put(Constant.USERID,sharedPreferences.getString(Constant.USERID,""));
                list.add(mapuserid);
                Map<String,String> mapnickname = new HashMap<>();
                mapnickname.put(Constant.NICKNAME,usernameEdit.getText().toString().trim());
                list.add(mapnickname);
                String gender = null;
                if (manRadioBtn.isChecked() == true){
                    gender = "1";
                }else {
                    gender = "2";
                }
                Map<String,String> mapgender = new HashMap<>();
                mapgender.put(Constant.GENDER,gender);
                list.add(mapgender);
                Map<String,String> mapschool = new HashMap<>();
                mapschool.put(Constant.SCHOOL,schoolEdit.getText().toString().trim());
                list.add(mapschool);
//                Map<String,String> mapaddress = new HashMap<>();
//                mapaddress.put(Constant.ADDRESS,addressEdit.getText().toString().trim());
//                list.add(mapaddress);
                Map<String,String> mapbirthday = new HashMap<>();
                mapbirthday.put(Constant.BIRTHDAY,editTextToStr(borthEdit));
                list.add(mapbirthday);
                String url;
                //学生
                if (getSharedPreferences(Constant.fileName,MODE_PRIVATE).getString(Constant.USERTYPE,"").equals("1")){
                    Map<String,String> mapgrade = new HashMap<>();
                    mapgrade.put(Constant.GRADE, String.valueOf(saveGrade));
                    list.add(mapgrade);
                    url = Constant.STUDENTRESETUSERINFOURL;
                }else {
                    //老师
                    Map<String,String> mapdegree = new HashMap<>();
                    Log.i("DEGREE",String.valueOf(saveGrade));
                    mapdegree.put(Constant.DEGREE, String.valueOf(saveGrade));
                    list.add(mapdegree);
                    Map<String,String> mapworklong = new HashMap<>();
                    mapworklong.put(Constant.WORKLONG,editTextToStr(gradeEdit));
                    list.add(mapworklong);
                    url = Constant.TEACHERRESETUSERINFOURL;
                }

                String result = HttpRequest.postRequestBody(list,url);
                if (result == null){
                    Message message = Message.obtain();
                    handler.sendEmptyMessage(message.what = 001);
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String ErrorCode = jsonObject.optString("ErrorCode");
                    if (ErrorCode.equals("000")){
                        final String finalGender = gender;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Message message = Message.obtain();
                                handler.sendEmptyMessage(message.what = 000);
                                String schoolStr = null;
                                String gradeStr = null;
                                if (getSharedPreferences(Constant.fileName,MODE_PRIVATE).getString(Constant.USERTYPE,"").equals("1")) {
                                    //学生
                                    schoolStr = editTextToStr(schoolEdit);
                                    gradeStr = String.valueOf(saveGrade);
                                    userDb.setUserInfoStudent(editTextToStr(usernameEdit), gradeStr,editTextToStr(borthEdit),"",schoolStr, finalGender,editTextToStr(emailEdit));
                                }else {
                                    schoolStr = String.valueOf(saveGrade);
                                    Log.i("schoolStr",String.valueOf(saveGrade));
                                    gradeStr = editTextToStr(gradeEdit);
                                    userDb.setUserInfoTeacher(editTextToStr(usernameEdit), gradeStr,editTextToStr(borthEdit),"",schoolStr, finalGender,editTextToStr(emailEdit));
                                }

                            }
                        });
                    }
                } catch (JSONException e) {
                    Message message = Message.obtain();
                    handler.sendEmptyMessage(message.what = 001);
                    e.printStackTrace();
                }
            }
        }).start();

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

    public String editTextToStr(EditText editText){
       return editText.getText().toString().trim();
   }
}
