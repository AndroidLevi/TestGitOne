package com.zun1.whenask.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Sampler;
import android.service.voice.VoiceInteractionService;
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
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.SocketHandler;

/**
 * Created by zun1user7 on 2016/7/27.
 */
public class AnswerItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<AnswerItem> mDatas;
    int RANK = 0;
//    public ButtonInterface buttonInterface;
//    /**
//     *按钮点击事件需要的方法
//     */
//    public void buttonSetOnclick(ButtonInterface buttonInterface){
//        this.buttonInterface=buttonInterface;
//    }
//    /**
//     * 按钮点击事件对应的接口
//     */
//    public interface ButtonInterface{
//        public void onclick( View view,int position);
//    }
    public Handler handler = new Handler(){
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what){
            case 000:
                if (RANK > 0){
                    Toast.makeText(mContext,mContext.getResources().getString(R.string.tadopt_succeed),Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(mContext,mContext.getResources().getString(R.string.notadopt_succeed),Toast.LENGTH_SHORT).show();
                }
                ((Activity)mContext).finish();
                break;
            case 001:
                UserDialog.serverError(mContext);
                break;
            case 002:
                UserDialog.serverError(mContext);
                break;
        }
    }
};
    public AnswerItemAdapter(Context context,List<AnswerItem>data){
        this.mContext = context;
        this.mDatas = data;
        this.mLayoutInflater = LayoutInflater.from(context);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Fresco.initialize(mContext);
        return new AnswerItemHolder(mLayoutInflater.inflate(R.layout.teacher_answer_item,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
          //更新item
        ((AnswerItemHolder) holder).answerIma.setImageURI(mDatas.get(position).answerImage);
        ((AnswerItemHolder) holder).answerText.setText(mDatas.get(position).answerText);
        ((AnswerItemHolder) holder).answerTime.setText(mDatas.get(position).answerTime);
        ((AnswerItemHolder) holder).answerStatus.setText(statusFromBackStr(mDatas.get(position).status));
        Log.i("status",mDatas.get(position).status);
        ((AnswerItemHolder) holder).voiceBtn.setText(VoiceTimeTool.voiceTimeFromvoicelength(mDatas.get(position).voicelength));
        if (mDatas.get(position).status.equals("1")){
            ((AnswerItemHolder) holder).answerStatus.setTextColor(Color.GREEN);
        }else {
            ((AnswerItemHolder) holder).answerStatus.setTextColor(Color.RED);
        }
        if (position == 0 && mDatas.get(position).status.equals("2")){
            ((AnswerItemHolder) holder).tadoptBtn.setVisibility(View.VISIBLE);
            ((AnswerItemHolder) holder).notadoptBtn.setVisibility(View.VISIBLE);
        }
//        ((AnswerItemHolder) holder).voiceBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (buttonInterface != null){
//                    buttonInterface.onclick(view,position);
//                }
//            }
//        });
    }
    @Override
    public int getItemCount() {
        return mDatas.size();
    }
    public class AnswerItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView answerText,answerTime,answerStatus;
        SimpleDraweeView answerIma;
        Button voiceBtn,tadoptBtn,notadoptBtn;
        Player player;
        public AnswerItemHolder(View itemView) {
            super(itemView);
            answerText = (TextView)itemView.findViewById(R.id.answer_text);
            answerTime = (TextView)itemView.findViewById(R.id.answer_time);
            voiceBtn = (Button)itemView.findViewById(R.id.answer_play);
            tadoptBtn = (Button)itemView.findViewById(R.id.answer_tadopt);
            notadoptBtn = (Button)itemView.findViewById(R.id.answer_notadopt);
            answerIma = (SimpleDraweeView)itemView.findViewById(R.id.answer_image);
            answerStatus = (TextView)itemView.findViewById(R.id.status);
            voiceBtn.setOnClickListener(this);
            notadoptBtn.setOnClickListener(this);
            tadoptBtn.setOnClickListener(this);
            player = new Player();
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.answer_play:
                    try {
                        if (mDatas.get(getAdapterPosition()).answerVoice.equals("NULL")){
                            Toast.makeText(mContext,R.string.no_voice,Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Log.i("voice",mDatas.get(getAdapterPosition()).answerVoice);
                        player.playUrl(mDatas.get(getAdapterPosition()).answerVoice,mContext);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.i("点击了", String.valueOf(getAdapterPosition()));
                    break;
                case R.id.answer_notadopt:
                    studentToAnswer(mContext.getResources().getString(R.string.no_tadopt_answer),"0",getAdapterPosition(),0);
                    break;
                case R.id.answer_tadopt:
                    studentToAnswer(mContext.getResources().getString(R.string.tadopt_answer),"1",getAdapterPosition(),2);
                    break;
            }
        }
    }
    void studentToAnswer(String title, final String isagree, final int postion, final int rank){
        RANK = rank;
        LinearLayout mLinearLayout = new LinearLayout(mContext);
        mLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        final RatingBar ratingBar = new RatingBar(mContext);
        ratingBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        ratingBar.setNumStars(5);
        ratingBar.setStepSize(1.0f);
        ratingBar.setRating(rank);
        mLinearLayout.addView(ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                if (v<2.0 && rank > 0){
                    ratingBar.setRating(2.0f);
                }
                Log.i("ratingbar", String.valueOf(ratingBar.getRating()));
            }
        });
        Log.i("ratingbar", String.valueOf(ratingBar.getNumStars()));
        new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setView(mLinearLayout)
                .setPositiveButton(R.string.sure_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constant.fileName,Context.MODE_PRIVATE);
                                String userid = sharedPreferences.getString(Constant.USERID,"");
                                List<Map<String,String>> list = new ArrayList<Map<String, String>>();
                                Map<String,String> mapuserid = new HashMap<String, String>();
                                mapuserid.put(Constant.USERID,userid);
                                list.add(mapuserid);
                                Map<String,String> mapqid = new HashMap<String, String>();
                                mapqid.put(Constant.QID,mDatas.get(postion).qid);
                                list.add(mapqid);
                                Map<String,String> mapaid = new HashMap<String, String>();
                                mapaid.put("aid",mDatas.get(postion).aid);
                                list.add(mapaid);
                                Map<String,String> mapisagree = new HashMap<String, String>();
                                mapisagree.put(Constant.ISAGREE,isagree);
                                list.add(mapisagree);
                                Map<String,String> maprank = new HashMap<String, String>();
                                maprank.put(Constant.RANK, String.valueOf((int)ratingBar.getRating()));
                                list.add(maprank);
                                String url = Constant.STUDENTCHOICEANSWERURL;
                                String result = HttpRequest.postRequestBody(list,url);
                                if (result == null){
                                    Message message = Message.obtain();
                                    handler.sendEmptyMessage(message.what = 001);
                                    return;
                                }
                                try {
                                    JSONObject jsonobject = new JSONObject(result);
                                    String ErrorCode = jsonobject.optString("ErrorCode");
                                    if (ErrorCode.equals("000")){
                                        Message message = Message.obtain();
                                        handler.sendEmptyMessage(message.what = 000);
                                    }
                                } catch (JSONException e) {
                                    Message message =  Message.obtain();
                                    handler.sendEmptyMessage(message.what = 002);
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                })
                .setNegativeButton(R.string.sure_no,null)
                .show();
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
