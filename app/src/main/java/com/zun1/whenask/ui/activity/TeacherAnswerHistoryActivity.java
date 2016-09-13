package com.zun1.whenask.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.zun1.whenask.Constant;
import com.zun1.whenask.R;
import com.zun1.whenask.ToolsClass.HttpRequest;
import com.zun1.whenask.ToolsClass.RecyclerItemClickListerer;
import com.zun1.whenask.ToolsClass.TimeTool;
import com.zun1.whenask.adapter.QuestionItem;
import com.zun1.whenask.adapter.TeacherAnswerItem;
import com.zun1.whenask.adapter.TeacherAnswerItemAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class TeacherAnswerHistoryActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private List<TeacherAnswerItem> data = new ArrayList<>();
    private TeacherAnswerItemAdapter mTeacherAnswerItemAdapter;
    //答案
    //问题
    private List<QuestionItem> questionItemData = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    boolean isLoadingMore = false;
    int index = 1;
    LinearLayoutManager manager;
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            swipeRefreshLayout.setRefreshing(false);
            switch (msg.what){
                case 010:
                    mTeacherAnswerItemAdapter.notifyDataSetChanged();
                    break;
                case 126:
                    //Toast.makeText(TeacherAnswerHistoryActivity.this,R.string.no_answer_history,Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_answer_history);
        //actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        this.setTitle(R.string.ask_history);
        findViewById();
        initData(index);
        mRecyclerView.setAdapter(mTeacherAnswerItemAdapter = new TeacherAnswerItemAdapter(this,data));
        //设置刷新的颜色
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListerer.RecyclerItemClickListener(this, mRecyclerView, new RecyclerItemClickListerer.RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(TeacherAnswerHistoryActivity.this,QuestionFormAnswerActivity.class);
                intent.putExtra("questionItem",(Serializable)questionItemData.get(position));
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
        //上拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (swipeRefreshLayout.isRefreshing()){
                    index = 1;
                    data.clear();
                    initData(index);
                }
            }
        });
        //下拉加载
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = manager.findLastVisibleItemPosition();
                int totallItemCount = manager.getItemCount();
                //lastVisibleItem >= totalItemCount - 2 表示剩下2个item自动加载，各位自由选择
                // dy>0 表示向下滑动
                if (lastVisibleItem >= totallItemCount - 2 && dy > 0){

                    if (isLoadingMore){

                    }else {
                        index++;
                        initData(index);
                        isLoadingMore = false;
                    }
                }
            }
        });
        swipeRefreshLayout.measure(0,0);
        swipeRefreshLayout.setRefreshing(true);
    }
    void findViewById(){
        mRecyclerView = (RecyclerView)this.findViewById(R.id.teacher_answer_history_recycler);
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        swipeRefreshLayout = (SwipeRefreshLayout)this.findViewById(R.id.id_answer_swipeRefreshLayout);
    }
    void initData(final int index){
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences(Constant.fileName,MODE_PRIVATE);
                String userid = sharedPreferences.getString(Constant.USERID,"");
                List<Map<String,String>> list = new ArrayList<>();
                Map<String,String> mapuserid = new HashMap<>();
                mapuserid.put(Constant.USERID,userid);
                list.add(mapuserid);
                Map<String,String> mapindex = new HashMap<>();
                mapindex.put(Constant.INDEX, String.valueOf(index));
                list.add(mapindex);
                String url = Constant.TEACHERANSWERHISTORYURL;
                String resutl = HttpRequest.postRequestBody(list,url);
                if (resutl == null){
                    Message message = Message.obtain();
                    handler.sendEmptyMessage(message.what = 001);
                    return;
                }
                try {
                    JSONObject jsonobject = new JSONObject(resutl);
                    String ErrorCode = jsonobject.optString("ErrorCode");
                    if (ErrorCode.equals("126")){
                        Message message = Message.obtain();
                        handler.sendEmptyMessage(message.what = 126);
                    }if (ErrorCode.equals("000")){
                        JSONArray items = jsonobject.optJSONArray("Items");
                        Log.i("Items的length", String.valueOf(items.length()));
                        for (int i = 0;i < items.length();i++){
                            JSONObject item = items.getJSONObject(i);
                            String status = item.optString("status");
                            String ans_fulltext = item.optString("ans_fulltext");
                            String answertime = item.optString("answertime");
                            String ans_voicelength = item.optString("ans_voicelength");
                            String ask_status = item.optString("ask_status");
                            String ask_picture = item.optString("ask_picture");
                            String ask_voice = item.optString("ask_voice");
                            String rank = item.optString("rank");
                            String grade = item.optString("grade");
                            String ans_picture = item.optString("ans_picture");
                            String subjectid = item.optString("subjectid");
                            String ans_voice = item.optString("ans_voice");
                            String ask_fulltext = item.optString("ask_fulltext");
                            String ask_voicelength = item.optString("ask_voicelength");
                            String asktime = item.optString("asktime");
                            //answerItem
                            TeacherAnswerItem answerItem = new TeacherAnswerItem();
                            answerItem.answerHistoryIma = ans_picture;
                            answerItem.answerHistoryText = ans_fulltext;
                            answerItem.answerHistoryVoice = ans_voice;
                            answerItem.answerHistoryTime = TimeTool.getTime(TeacherAnswerHistoryActivity.this,answertime);;
                            answerItem.answerHistoryRank = rank;
                            answerItem.answerHistoryStatus = status;
                            answerItem.ans_voicelength = ans_voicelength;
                            data.add(answerItem);
                            //questionItem
                            QuestionItem questionItem = new QuestionItem();
                            questionItem.subjectid = subjectid;
                            questionItem.grade = grade;
                            questionItem.asktime = asktime;
                            questionItem.ask_picture = ask_picture;
                            questionItem.ask_fulltext = ask_fulltext;
                            questionItem.ask_voice = ask_voice;
                            questionItem.voicelength = ask_voicelength;
                            questionItemData.add(questionItem);
                        }
                        Message message = Message.obtain();
                        handler.sendEmptyMessage(message.what = 010);
                    }
                } catch (JSONException e) {
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
}
