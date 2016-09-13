package com.zun1.whenask.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zun1.whenask.R;
import com.zun1.whenask.ToolsClass.Player;
import com.zun1.whenask.ToolsClass.TextFromInt;
import com.zun1.whenask.ToolsClass.TimeTool;
import com.zun1.whenask.ToolsClass.VoiceTimeTool;
import com.zun1.whenask.adapter.QuestionItem;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;

public class QuestionFormAnswerActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView subjectText,gradeText,timeText,textText;
    private SimpleDraweeView simpleDraweeView;
    private Button voiceBtn;
    Player mPlayer = new Player();
    QuestionItem data = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_form_answer);
        //actionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        this.setTitle(R.string.question_detailed);
        findViewById();
        setOnClickListener();
        initData();
    }
   void findViewById(){
       subjectText = (TextView)this.findViewById(R.id.question_subject);
       gradeText = (TextView)this.findViewById(R.id.question_grade);
       timeText = (TextView)this.findViewById(R.id.question_time);
       simpleDraweeView = (SimpleDraweeView)this.findViewById(R.id.question_image);
       textText = (TextView)this.findViewById(R.id.question_text);
       voiceBtn = (Button)this.findViewById(R.id.question_voice);
   }
    void initData(){
        Intent intent = getIntent();
        data = (QuestionItem) intent.getSerializableExtra("questionItem");
        subjectText.setText(TextFromInt.intToText(this,data.subjectid));
        gradeText.setText(TextFromInt.intToGrade(this,data.grade));
        timeText.setText(TimeTool.getTime(this,data.asktime));
        simpleDraweeView.setImageURI(Uri.parse(data.ask_picture));
        textText.setText(data.ask_fulltext);
        voiceBtn.setText(VoiceTimeTool.voiceTimeFromvoicelength(data.voicelength));
    }
    void setOnClickListener(){
        voiceBtn.setOnClickListener(this);
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
    public void onClick(View view) {
       switch (view.getId()){
           case R.id.question_voice:
               if (data.ask_voice.equals("NULL")){
                   Toast.makeText(this,R.string.no_voice,Toast.LENGTH_SHORT).show();
                   return;
               }
               try {
                   mPlayer.playUrl(data.ask_voice,QuestionFormAnswerActivity.this);
               } catch (IOException e) {
                   e.printStackTrace();
               }
               break;
       }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayer != null){
            mPlayer.stop();
        }
    }
}
