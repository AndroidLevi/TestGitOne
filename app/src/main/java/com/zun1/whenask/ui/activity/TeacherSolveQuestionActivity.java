package com.zun1.whenask.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.zun1.whenask.Constant;
import com.zun1.whenask.PushBean;
import com.zun1.whenask.R;
import com.zun1.whenask.ToolsClass.HttpRequest;
import com.zun1.whenask.ToolsClass.Player;
import com.zun1.whenask.ToolsClass.TextFromInt;
import com.zun1.whenask.ToolsClass.TimeTool;
import com.zun1.whenask.ToolsClass.UserDialog;
import com.zun1.whenask.ToolsClass.VoiceTimeTool;
import com.zun1.whenask.adapter.QuestionItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TeacherSolveQuestionActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView subjectText,gradeText,timeText,textText;
    private SimpleDraweeView simpleDraweeView;
    private Button playBtn,solveBtn,giveupBtn;
    //语音

    //题目id
    String id;
    //图片

    //数据的总个数
    int dataAllSum = 0;
    //数据的位置
    int dataSum = 0;
    //请求回来的数据
    String result = null;
    //保存问题详细
    QuestionItem questionItem = new QuestionItem();
    //
    Player player = new Player();
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 000:
                    Log.i("zhixinglema","zhixinglema");
                    Intent intent = new Intent(TeacherSolveQuestionActivity.this,TakePicturePostActivity.class);
                    intent.putExtra(Constant.QID,id);
                    intent.putExtra("picture",picture);
                    intent.putExtra("questionItem",(Serializable) questionItem);
                    startActivity(intent);
                    break;
                case 126:
                    Toast.makeText(TeacherSolveQuestionActivity.this,R.string.no_question,Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 001:
                    UserDialog.serverError(TeacherSolveQuestionActivity.this);
                    break;
                case 128:
                    Toast.makeText(TeacherSolveQuestionActivity.this,R.string.question_exit,Toast.LENGTH_SHORT).show();
                    break;
                case 145:
                    Toast.makeText(TeacherSolveQuestionActivity.this,R.string.no_through_certification,Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private String pushID;
    private String fulltext;
    private String voicelength;
    private String grade;
    private String subjectid;
    private String asktime;
    private  String voice;
    private  String picture;

    private String sid;
    private PushBean pushBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_teacher_solve_question);
        findViewById();
        //actionbar
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.setTitle(R.string.solvequestion);
        setOnClickListener();
        //判断是否是来自推送的消息
        pushID=getIntent().getStringExtra("pushId");
        Log.i("onCreate--","pushID"+pushID);
        if(pushID!=null){
            postRequest();

        }
        else{
            initData(dataSum);
        }

    }
    //点击推送时的请求网络
    private void postRequest() {
        final OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add(Constant.PUSHID,pushID)
                .build();

        final Request request = new Request.Builder()
                .url(Constant.TUTOR_LOOKQUESKTION)
                .post(formBody)
                .build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String result=response.body().string();
                        //解析数据
                        showPushData(result);
                        response.body().close();
                        Log.i("WY","打印POST响应的数据：" +result );
                        try {
                            getQuestionData(result,1);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    } else {
                        throw new IOException("Unexpected code " + response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
    void findViewById(){
        subjectText = (TextView)this.findViewById(R.id.teacher_solvequestion_subject);
        gradeText = (TextView)this.findViewById(R.id.teacher_solvequestion_grade);
        timeText = (TextView)this.findViewById(R.id.teacher_solvequestion_time);
        textText = (TextView)this.findViewById(R.id.teacher_solvequestion_text);
        simpleDraweeView = (SimpleDraweeView) this.findViewById(R.id.teacher_solvequestion_image);
        playBtn = (Button)this.findViewById(R.id.teacher_solvequestion_play);
        solveBtn = (Button)this.findViewById(R.id.teacher_solvequestion_solve);
        giveupBtn = (Button)this.findViewById(R.id.teacher_solvequestion_nosolve);
    }
    void setOnClickListener(){
        playBtn.setOnClickListener(this);
        solveBtn.setOnClickListener(this);
        giveupBtn.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.teacher_solvequestion_play:
                if (voice.equals("NULL")){
                    Toast.makeText(this,R.string.no_voice,Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    player.playUrl(voice,TeacherSolveQuestionActivity.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                Intent intent = new Intent();
//                Log.i("voice",voice);
//                Uri uri = Uri.parse(voice);
//                intent.setDataAndType(uri, "audio/*");
//                intent.setAction(Intent.ACTION_VIEW);
//                startActivity(intent);
                break;
            case R.id.teacher_solvequestion_solve:
                //解决问题
                teacherToSolve();
                break;
            case R.id.teacher_solvequestion_nosolve:
                //下一道题目
                nextQuestion();
                break;
        }
    }
    void initData(final int datasum){
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences(Constant.fileName,MODE_PRIVATE);
                String subjectidlist = sharedPreferences.getString(Constant.SUBJECTIDLIST,"");
                Log.i("subjectidlist",subjectidlist);
                List<Map<String,String>> list = new ArrayList<Map<String, String>>();
                Map<String,String> mapsubjectidlist = new HashMap<String, String>();
                list.add(mapsubjectidlist);
                mapsubjectidlist.put(Constant.SUBJECTIDLIST,subjectidlist);
                String url = Constant.TEACHERRANDOMGETQUESTIONURL;
                result = HttpRequest.postRequestBody(list,url);
                Log.i("result",result);
                if (result == null){
                    return;
                }
                //解析数据
                getQuestionData(result,datasum);
            }
        }).start();
    }
    public void showPushData(String result){
        Gson gson = new Gson();
        pushBean = gson.fromJson(result, PushBean.class);
        Log.i("pushBean", pushBean.toString());

        picture =pushBean.getItems().getPicture();
        fulltext = pushBean.getItems().getFulltext();
        voicelength =  pushBean.getItems().getVoicelength();
        grade = pushBean.getItems().getGrade();
        subjectid = pushBean.getItems().getSubjectid();
        // sid = questionData.optString("sid");
        voice = pushBean.getItems().getVoice();
        id = pushID;
        asktime = pushBean.getItems().getAsktime();
        //保存问题内容
        questionItem.subjectid = pushBean.getItems().getSubjectid();
        questionItem.grade = pushBean.getItems().getGrade();
        questionItem.asktime = pushBean.getItems().getAsktime();
        questionItem.ask_picture = pushBean.getItems().getPicture();
        questionItem.ask_voice = pushBean.getItems().getVoice();
        questionItem.ask_fulltext = pushBean.getItems().getFulltext();
        questionItem.voicelength = pushBean.getItems().getVoicelength();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                subjectText.setText(TextFromInt.intToText(TeacherSolveQuestionActivity.this, pushBean.getItems().getSubjectid()));
                gradeText.setText(TextFromInt.intToGrade(TeacherSolveQuestionActivity.this, pushBean.getItems().getGrade()));
                timeText.setText(TimeTool.getTime(TeacherSolveQuestionActivity.this, pushBean.getItems().getAsktime()));
                simpleDraweeView.setImageURI(Uri.parse(pushBean.getItems().getPicture()));
                textText.setText(pushBean.getItems().getFulltext());
                playBtn.setText(VoiceTimeTool.voiceTimeFromvoicelength( pushBean.getItems().getVoicelength()));
            }
        });
    }
    void getQuestionData(String result,int datasum) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            String ErrorCode = jsonObject.optString("ErrorCode");
            if (ErrorCode.equals("000")) {
                JSONArray jsonArray = jsonObject.optJSONArray("Items");
                dataAllSum = jsonArray.length();
                JSONObject questionData = jsonArray.getJSONObject(datasum);
                picture = questionData.optString("picture");
                fulltext = questionData.optString("fulltext");
                voicelength = questionData.optString("voicelength");
                grade = questionData.optString("grade");
                subjectid = questionData.optString("subjectid");
                sid = questionData.optString("sid");
                voice = questionData.optString("voice");
                id = questionData.optString("id");
                asktime = questionData.optString("asktime");
            }
            if (ErrorCode.equals("126")) {
                Message message = Message.obtain();
                handler.sendEmptyMessage(message.what = 126);
            }
        } catch (JSONException e) {
            Message message = Message.obtain();
            handler.sendEmptyMessage(message.what = 001);
            e.printStackTrace();
        }
        //保存问题内容
        questionItem.subjectid = subjectid;
        questionItem.grade = grade;
        questionItem.asktime = asktime;
        questionItem.ask_picture = picture;
        questionItem.ask_voice = voice;
        questionItem.ask_fulltext = fulltext;
        questionItem.voicelength = voicelength;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                subjectText.setText(TextFromInt.intToText(TeacherSolveQuestionActivity.this, subjectid));
                gradeText.setText(TextFromInt.intToGrade(TeacherSolveQuestionActivity.this, grade));
                timeText.setText(TimeTool.getTime(TeacherSolveQuestionActivity.this, asktime));
                simpleDraweeView.setImageURI(Uri.parse(picture));
                textText.setText(fulltext);
                playBtn.setText(VoiceTimeTool.voiceTimeFromvoicelength(voicelength));
            }
        });
    }
    //老师解决问题
    void teacherToSolve(){
        new  AlertDialog.Builder(this)
                .setTitle(R.string.sure_to_solve_question)
                .setMessage(R.string.sure)
                .setPositiveButton(R.string.sure_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //确认答题
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                SharedPreferences sharedPreferences = getSharedPreferences(Constant.fileName,MODE_PRIVATE);
                                String userid = sharedPreferences.getString(Constant.USERID,"");
                                List<Map<String,String>> list = new ArrayList<Map<String, String>>();
                                Map<String,String> mapuserid = new HashMap<String, String>();
                                mapuserid.put(Constant.USERID,userid);
                                list.add(mapuserid);
                                Map<String,String> mapqid = new HashMap<String, String>();
                                mapqid.put(Constant.QID,id);
                                list.add(mapqid);
                                String url = Constant.TEACHERSOLVEQUESTIONURL;
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
                                        Log.i("执行了","执行了");
                                        Message message = Message.obtain();
                                        handler.sendEmptyMessage(message.what = 000);
                                    }
                                    if (ErrorCode.equals("128")){
                                        Message message = Message.obtain();
                                        handler.sendEmptyMessage(message.what = 128);
                                    }
                                    if (ErrorCode.equals("145")){
                                        Message message = Message.obtain();
                                        handler.sendEmptyMessage(message.what = 145);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }).start();
                    }
                })
                .setNegativeButton(R.string.sure_no,null)
                .show();
    }
    //下一道题目
    void nextQuestion(){
        if(pushID!=null){
            finish();
            return;
        }
        dataSum = dataSum + 1;
        if (dataSum == dataAllSum){
            dataSum = 0;
            initData(dataSum);
            return;
        }
        getQuestionData(result,dataSum);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null){
            player.stop();
        }
    }

    //用于本Activity的singleTask模式，避免重复创建
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //判断是否是来自推送的消息
        pushID=getIntent().getStringExtra("pushId");
        Log.i("onNewIntent--","pushID"+pushID);
        if(pushID!=null){
            postRequest();
        }

    }



    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode ==Integer.valueOf( Constant.PUSHID ) ){
//            final int pushID=data.getIntExtra("pushId",200);
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    Log.i("pushId--",""+pushID);
//                    List<Map<String,String>> list = new ArrayList<Map<String, String>>();
//                    Map<String,String> mapsubjectidlist = new HashMap<String, String>();
//                    mapsubjectidlist.put(Constant.PUSHID,String.valueOf(pushID));
//                    list.add(mapsubjectidlist);
//                    String url = Constant.TUTOR_LOOKQUESKTION;
//                  String  result = HttpRequest.postRequestBody(list,url);
//                    Log.i("onActivityResult--",result);
//                    if (result == null){
//                        return;
//                    }
//                    //解析数据
//                    getQuestionData(result,1);
//                }
//            }).start();
//        }
//    }
}
