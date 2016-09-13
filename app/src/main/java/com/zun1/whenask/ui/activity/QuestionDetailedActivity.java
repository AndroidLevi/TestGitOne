package com.zun1.whenask.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bartoszlipinski.recyclerviewheader.RecyclerViewHeader;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.zun1.whenask.Constant;
import com.zun1.whenask.R;
import com.zun1.whenask.ToolsClass.HttpRequest;
import com.zun1.whenask.ToolsClass.Player;
import com.zun1.whenask.ToolsClass.TextFromInt;
import com.zun1.whenask.ToolsClass.TimeTool;
import com.zun1.whenask.ToolsClass.VoiceTimeTool;
import com.zun1.whenask.adapter.AnswerItem;
import com.zun1.whenask.adapter.AnswerItemAdapter;
import com.zun1.whenask.adapter.HistoryItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionDetailedActivity extends AppCompatActivity {
    private RecyclerViewHeader headerView;
    private RecyclerView mRecyclerView;
    private AnswerItemAdapter answerItemAdapter;
    private List<AnswerItem> data = new ArrayList<>();
    private TextView subjectText,gradeText,timeText,textText;
    private SimpleDraweeView simpleDraweeView;
    private Button voiceBtn;
    String voice = null;
    private Player player = new Player();
    String answerVoice = null;
    //问题id
    String qid;
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_question_detailed);
        Log.i("cccc","到达这里了");
        findViewById();
        //set adapter
        initData();
        mRecyclerView.setAdapter(answerItemAdapter = new AnswerItemAdapter(this,data));

//        answerItemAdapter.buttonSetOnclick(new AnswerItemAdapter.ButtonInterface() {
//            @Override
//            public void onclick(View view, int position) {
//                try {
//                    player.playUrl(answerVoice,QuestionDetailedActivity.this);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
        //actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        this.setTitle(R.string.student_question_detailed);
    }
    void findViewById(){
        headerView = (RecyclerViewHeader)findViewById(R.id.question_detailed_header);
        mRecyclerView = (RecyclerView)this.findViewById(R.id.question_detailed_recyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        headerView.attachTo(mRecyclerView,true);
        subjectText = (TextView)this.findViewById(R.id.qustion_detailed_subject);
        gradeText = (TextView)this.findViewById(R.id.question_detailed_grade);
        timeText = (TextView)this.findViewById(R.id.question_detailed_time);
        textText = (TextView)this.findViewById(R.id.question_detailed_text);
        textText.setMovementMethod(ScrollingMovementMethod.getInstance());
        simpleDraweeView = (SimpleDraweeView)this.findViewById(R.id.question_detailed_image);
        voiceBtn = (Button)this.findViewById(R.id.question_detailed_voice);
        voiceBtn.setOnClickListener(new voicePlayClickListener());
    }
    //播放问题语音
    public  class  voicePlayClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if (voice.equals("NULL")){
                Toast.makeText(QuestionDetailedActivity.this,R.string.no_voice,Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                player.playUrl(voice,QuestionDetailedActivity.this);
                Log.i("xxxx",voice);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    void initData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences(Constant.fileName,MODE_PRIVATE);
                String userid = sharedPreferences.getString(Constant.USERID,"");
                Intent intent = getIntent();
                qid = intent.getStringExtra(Constant.QID);
                Log.i("qid",qid);
                List<Map<String,String>> list = new ArrayList<>();
                Map<String,String> mapuserid = new HashMap<>();
                mapuserid.put(Constant.USERID,userid);
                list.add(mapuserid);
                Map<String,String> mapqid = new HashMap<>();
                mapqid.put(Constant.QID,qid);
                list.add(mapqid);
                String url =  Constant.QUESTIONDETAILEDURL;
                String result = HttpRequest.postRequestBody(list,url);
                if (result == null){
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String ErrorCode = jsonObject.optString("ErrorCode");
                    if (ErrorCode.equals("000")){
                        JSONObject jsonObjectItem = jsonObject.optJSONObject("Items");
                        final String picture = jsonObjectItem.optString("picture");
                        final String fulltext = jsonObjectItem.optString("fulltext");
                        final String grade = jsonObjectItem.optString("grade");
                        final String subjectid = jsonObjectItem.optString("subjectid");
                        JSONArray answeritems = jsonObjectItem.optJSONArray("answeritems");
                        voice = jsonObjectItem.optString("voice");
                        final String answerStatus = jsonObjectItem.optString("answerStatus");
                        final String asktime = jsonObjectItem.optString("asktime");
                        final String voicelength = jsonObjectItem.optString("voicelength");
                        //老师的答案
                        answerFormQuestion(answeritems);
                        //下载语音
                        //更新UI
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                subjectText.setText(TextFromInt.intToText(QuestionDetailedActivity.this,subjectid));
                                gradeText.setText(TextFromInt.intToGrade(QuestionDetailedActivity.this,grade));
                                timeText.setText(TimeTool.getTime(QuestionDetailedActivity.this,asktime));
                                Log.i("asktime",asktime);
                                textText.setText(fulltext);
                                simpleDraweeView.setImageURI(Uri.parse(picture));
                                voiceBtn.setText(VoiceTimeTool.voiceTimeFromvoicelength(voicelength));
                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    void answerFormQuestion(JSONArray answeritems){
        try {
            for (int i=0;i<answeritems.length();i++) {
                JSONObject jsonObjectitem = answeritems.getJSONObject(i);
                String status = jsonObjectitem.optString("status");
                String picture = jsonObjectitem.optString("picture");
                String fulltext = jsonObjectitem.optString("fulltext");
                String answertime = jsonObjectitem.optString("answertime");
                String tid = jsonObjectitem.optString("tid");
                String aid = jsonObjectitem.optString("aid");
                String voice = jsonObjectitem.optString("voice");
                String voicelength = jsonObjectitem.optString("voicelength");
                answerVoice = voice;
                AnswerItem answerItem = new AnswerItem();
                answerItem.answerImage = picture;
                answerItem.answerText = fulltext;
                answerItem.answerVoice = voice;
                answerItem.answerTime = TimeTool.getTime(this,answertime);
                answerItem.qid = qid;
                answerItem.aid = aid;
                answerItem.status = status;
                answerItem.voicelength = voicelength;
                data.add(answerItem);
                //刷新item
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        answerItemAdapter.notifyDataSetChanged();
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.stop();
    }
}
