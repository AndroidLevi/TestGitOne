package com.zun1.whenask.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.zun1.whenask.Constant;
import com.zun1.whenask.DB.UserDb;
import com.zun1.whenask.R;
import com.zun1.whenask.SetLanguageApplication.SetLanguageApplicationClass;
import com.zun1.whenask.ToolsClass.ActivityIndicatorView;
import com.zun1.whenask.ToolsClass.UserDialog;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TeacherSelectSubject extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener,View.OnClickListener{
    private CheckBox chineseBtn,mathBtn,englishBtn,biologyBtn,physicsBtn,
            chemistryBtn,polityBtn,historyBtn,geographyBtn;
    private ImageView imageView_01,imageView_02;
    private Button takephoto,completebtn;
    //标记选择两个
    int flagNum = 0;
    //是否拍照
    int SUPPORT_CAMERA = 100;
    int TAKE_PICTURE = 200;
    //标记图片放的位置（判断第一张是否有图片）
    boolean flagOne = false;
    Toolbar toolbar;
    //两张图片的Uri
    Uri certificate_1 = null;
    Uri certificate_2 = null;
    //上传的老师科目
    List<String> listSubject = new ArrayList<>();
    //上传图片的个数
    private int imagenum = 1;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ActivityIndicatorView.dismiss();
            switch (msg.what){
                case 000:
                    Toast.makeText(TeacherSelectSubject.this,R.string.post_succeed,Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(TeacherSelectSubject.this,LoginImportActivity.class);
                    startActivity(intent1);
                    break;
                case 001:
                    UserDialog.serverError(TeacherSelectSubject.this);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_subject);
        findViewById();
        setOnCheckedChangeListener();
        //set actionbar
        setSupportActionBar(toolbar);
        List<String> list = new ArrayList<String>();
        list.add("1");
        list.add("2");
        list.remove("1");
        Log.i("list's num", list.get(0));
        //add activity
        SetLanguageApplicationClass setLanguageApplicationClass = (SetLanguageApplicationClass) getApplication();
        setLanguageApplicationClass.activityList.add(this);
    }
    void findViewById(){
        chineseBtn = (CheckBox) this.findViewById(R.id.chinese);
        mathBtn = (CheckBox) this.findViewById(R.id.math);
        englishBtn = (CheckBox) this.findViewById(R.id.english);
        biologyBtn = (CheckBox) this.findViewById(R.id.biology);
        physicsBtn = (CheckBox) this.findViewById(R.id.physics);
        chemistryBtn = (CheckBox) this.findViewById(R.id.chemistry);
        polityBtn = (CheckBox) this.findViewById(R.id.polity);
        historyBtn = (CheckBox) this.findViewById(R.id.history);
        geographyBtn = (CheckBox) this.findViewById(R.id.geography);
        takephoto = (Button)this.findViewById(R.id.select_subject_takephoto);
        imageView_01 = (ImageView)this.findViewById(R.id.postImage_01);
        imageView_02 = (ImageView)this.findViewById(R.id.postImage_02);
        toolbar = (Toolbar)this.findViewById(R.id.resetuser_toolbar);
        completebtn = (Button)this.findViewById(R.id.select_selectsubject_complete);
    }
    void setOnCheckedChangeListener(){
        chineseBtn.setOnCheckedChangeListener(this);
        mathBtn.setOnCheckedChangeListener(this);
        englishBtn.setOnCheckedChangeListener(this);
        biologyBtn.setOnCheckedChangeListener(this);
        physicsBtn.setOnCheckedChangeListener(this);
        chemistryBtn.setOnCheckedChangeListener(this);
        polityBtn.setOnCheckedChangeListener(this);
        historyBtn.setOnCheckedChangeListener(this);
        geographyBtn.setOnCheckedChangeListener(this);
        takephoto.setOnClickListener(this);
        imageView_01.setOnClickListener(this);
        imageView_02.setOnClickListener(this);
        completebtn.setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b){
            if (flagNum >=2){
                Toast.makeText(TeacherSelectSubject.this,"一个人只能选择两个",Toast.LENGTH_SHORT).show();
                compoundButton.setChecked(false);
                return;
            }
            listSubject.add(getIntFromSubjectStr(compoundButton.getText().toString()));
            Log.i("text",compoundButton.getText().toString());
            getIntFromSubjectStr(compoundButton.getText().toString());
            flagNum ++;
        }else {
            listSubject.remove(getIntFromSubjectStr(compoundButton.getText().toString()));
            flagNum --;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.select_subject_takephoto:
                //opentakephoto
                TAKE_PICTURE = 200;
                opentakephoto();
                break;
            case R.id.postImage_01:
                TAKE_PICTURE = 300;
                opentakephoto();
                break;
            case R.id.postImage_02:
                TAKE_PICTURE = 400;
                opentakephoto();
                break;
            case R.id.select_selectsubject_complete:
                //上传证书
                if (certificate_1 == null){
                    Toast.makeText(TeacherSelectSubject.this,getResources().getText(R.string.post_twopicture),Toast.LENGTH_SHORT).show();
                    return;
                }
                if (listSubject.size()<1){
                    Toast.makeText(TeacherSelectSubject.this,getResources().getText(R.string.select_subject),Toast.LENGTH_SHORT).show();
                    return;
                }
                postCertificate();
                break;
        }
    }
    void opentakephoto(){
        if (ContextCompat.checkSelfPermission(TeacherSelectSubject.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TeacherSelectSubject.this, new String[]{android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, SUPPORT_CAMERA);
        }else {
            //允许拍照
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivityForResult(intent, TAKE_PICTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == TAKE_PICTURE){
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,null,null));
                Log.i("uri", String.valueOf(uri));
                if (TAKE_PICTURE == 300){
                    imageView_01.setImageBitmap(bitmap);
                    certificate_1 = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,null,null));
                    flagOne =true;
                    return;
                }
                if (TAKE_PICTURE == 400){
                    imageView_02.setImageBitmap(bitmap);
                    certificate_2 = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,null,null));
                    return;
                }
                if (flagOne == false){
                    imageView_01.setImageBitmap(bitmap);
                    certificate_1 = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,null,null));
                    flagOne =true;
                }else {
                   imageView_02.setImageBitmap(bitmap);
                    certificate_2 = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,null,null));
                }


            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i("i3", String.valueOf(123445));
        if (requestCode == SUPPORT_CAMERA) {
            if (permissions[0].equals(android.Manifest.permission.CAMERA)&& permissions[1].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //允许拍照
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(intent, TAKE_PICTURE);
            } else {
                //不允许拍照
            }
        }
    }
    //上传证书
    void postCertificate(){
        final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        ActivityIndicatorView.start(TeacherSelectSubject.this);
         new Thread(new Runnable() {
             @Override
             public void run() {
                 Intent intent = getIntent();
                 String username = intent.getStringExtra(Constant.USERNAME);
                 File file_1 = new File(getRealPathFromURI(certificate_1));
                 File file_2 = null;
                 if (certificate_2 != null){
                     file_2 = new File(getRealPathFromURI(certificate_2));
                 }
                 String subjectIdList = new String();
                 for (int i = 0;i<listSubject.size();i++){
                     subjectIdList+=listSubject.get(i);
                     if (i<listSubject.size()-1){
                         subjectIdList+=",";
                     }
                 }
                 Log.i("subjectIdList",subjectIdList);
                 OkHttpClient client = new OkHttpClient();
                 Log.i("filename",file_1.getName());
                 MultipartBody.Builder builder = new MultipartBody.Builder();
                 builder.setType(MultipartBody.FORM)
                         .addFormDataPart(Constant.USERNAME,username)
                         .addFormDataPart(Constant.SUBJECTLIST,subjectIdList)
                         .addFormDataPart(Constant.CERTIFICATE_1,file_1.getName(),RequestBody.create(MEDIA_TYPE_PNG,file_1));
                 if (certificate_2 != null){
                     builder.addFormDataPart(Constant.CERTIFICATE_2,file_2.getName(),RequestBody.create(MEDIA_TYPE_PNG,file_2));
                     imagenum = 2;
                 }
                 builder.addFormDataPart("imagenum", String.valueOf(imagenum));
                 RequestBody requestBody = builder.build();
//                 RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
//                         .addFormDataPart(Constant.USERNAME,username)
//                         .addFormDataPart(Constant.SUBJECTLIST,subjectIdList)
//                         .addFormDataPart(Constant.CERTIFICATE_1,file_1.getName(),RequestBody.create(MEDIA_TYPE_PNG,file_1))
//                         .addFormDataPart(Constant.CERTIFICATE_2,file_2.getName(),RequestBody.create(MEDIA_TYPE_PNG,file_2))
//                         .addFormDataPart("imagenum","2")
//                         .build();
                 Request request = new Request.Builder()
                         .url(Constant.TEACHERPOSTCERTIFICATEURL)
                         .post(requestBody)
                         .build();
                 try {
                     Response response = client.newCall(request).execute();
                     String result = response.body().string();
                     Log.i("result",result);
                     if (result == null){
                         Message message = Message.obtain();
                         handler.sendEmptyMessage(message.what = 001);
                         return;
                     }
                     JSONObject jsonObject = new JSONObject(result);
                     String ErrorCode = jsonObject.optString("ErrorCode");
                     if (ErrorCode.equals("000")){
                         Message message = Message.obtain();
                         handler.sendEmptyMessage(message.what = 000);
                         runOnUiThread(new Runnable() {
                             @Override
                             public void run() {
                             }
                         });
                     }
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             }
         }).start();
    }
    //getRealPathFromURI
    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
    //getIntFromSubjectStr
    public String getIntFromSubjectStr(String subject){
        String subjectInt = null;
        switch (subject){
            case "中文":
                subjectInt = "1";
                break;
            case "英文":
                subjectInt = "2";
                break;
            case "数学":
                subjectInt = "3";
                break;
            case "生物":
                subjectInt = "4";
                break;
            case "物理":
                subjectInt = "5";
                break;
            case "化学":
                subjectInt = "6";
                break;
            case "政治":
                subjectInt = "7";
                break;
            case "历史":
                subjectInt = "8";
                break;
            case "地理":
                subjectInt = "9";
                break;
        }
        return subjectInt;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
