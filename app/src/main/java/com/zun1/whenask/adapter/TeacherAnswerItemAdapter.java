package com.zun1.whenask.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.zun1.whenask.Constant;
import com.zun1.whenask.R;
import com.zun1.whenask.ToolsClass.HttpRequest;
import com.zun1.whenask.ToolsClass.Player;
import com.zun1.whenask.ToolsClass.UserDialog;
import com.zun1.whenask.ToolsClass.VoiceTimeTool;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zun1user7 on 2016/7/27.
 */
public class TeacherAnswerItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<TeacherAnswerItem> mDatas;
    public Handler handler = new Handler(){
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what){

        }
    }
};
    public TeacherAnswerItemAdapter(Context context, List<TeacherAnswerItem>data){
        this.mContext = context;
        this.mDatas = data;
        this.mLayoutInflater = LayoutInflater.from(context);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Fresco.initialize(mContext);
        return new AnswerItemHolder(mLayoutInflater.inflate(R.layout.teacher_history_answer_item,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
          //更新item
        ((AnswerItemHolder) holder).answerIma.setImageURI(mDatas.get(position).answerHistoryIma);
        ((AnswerItemHolder) holder).answerText.setText(mDatas.get(position).answerHistoryText);
        ((AnswerItemHolder) holder).answerTime.setText(mDatas.get(position).answerHistoryTime);
        ((AnswerItemHolder) holder).rankText.setText("rank:"+mDatas.get(position).answerHistoryRank);
        ((AnswerItemHolder) holder).voiceBtn.setText(VoiceTimeTool.voiceTimeFromvoicelength(mDatas.get(position).ans_voicelength));
        if (mDatas.get(position).answerHistoryStatus.equals("1")){
            ((AnswerItemHolder) holder).statusText.setTextColor(Color.GREEN);
        }else {
            ((AnswerItemHolder) holder).statusText.setTextColor(Color.RED);
        }
        ((AnswerItemHolder) holder).statusText.setText(statusFromBackStr(mDatas.get(position).answerHistoryStatus));
    }
    @Override
    public int getItemCount() {
        return mDatas.size();
    }
    public class AnswerItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView answerText,answerTime;
        SimpleDraweeView answerIma;
        Button voiceBtn;
        TextView rankText,statusText;
        Player player;
        public AnswerItemHolder(View itemView) {
            super(itemView);
            answerText = (TextView)itemView.findViewById(R.id.answer_history_text);
            answerTime = (TextView)itemView.findViewById(R.id.answer_history_time);
            voiceBtn = (Button)itemView.findViewById(R.id.answer_history_play);
            statusText = (TextView) itemView.findViewById(R.id.answer_history_status);
            rankText = (TextView)itemView.findViewById(R.id.answer_history_rank);
            answerIma = (SimpleDraweeView)itemView.findViewById(R.id.answer_history_image);
            voiceBtn.setOnClickListener(this);
            player = new Player();
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.answer_history_play:
                    if (mDatas.get(getAdapterPosition()).answerHistoryVoice.equals("NULL")){
                        Toast.makeText(mContext,R.string.no_voice,Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        player.playUrl(mDatas.get(getAdapterPosition()).answerHistoryVoice,mContext);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }
    String statusFromBackStr(String statusStr){
        String status = null;
        switch (statusStr){
            case "1":
                status = mContext.getResources().getString(R.string.student_status_yes);
                break;
            case "2":
                status = mContext.getResources().getString(R.string.student_status_assess);
                break;
            case "3":
                status = mContext.getResources().getString(R.string.student_status_no);
                break;
        }
        return status;
    }
}
