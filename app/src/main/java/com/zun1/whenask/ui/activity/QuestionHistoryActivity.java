package com.zun1.whenask.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.zun1.whenask.Constant;
import com.zun1.whenask.R;
import com.zun1.whenask.ToolsClass.HttpRequest;
import com.zun1.whenask.ToolsClass.RecyclerItemClickListerer;
import com.zun1.whenask.adapter.HistoryItem;
import com.zun1.whenask.adapter.RecycleItemAdapter;
import com.zun1.whenask.ui.Fragment.StudentHistoryFinishedFragment;
import com.zun1.whenask.ui.Fragment.StudentHistoryUnfinishedFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionHistoryActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView mRecyclerView;
    private Button unfinishedBtn, finishedBtn;
    private List<HistoryItem> data = new ArrayList<>();
    private List<HistoryItem> dataUnfinish = new ArrayList<>();
    private List<HistoryItem> dataFinish = new ArrayList<>();
    boolean unfinished;
    private RecycleItemAdapter mRecycleItemAdapter;
    //标志第一次完成
    boolean flageFinish = true;
    //Fragment
    private Fragment unFinishedFragment, finishedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_history);
        findViewById();
        setOnClickListener();
//        unfinished = true;
//        //获得数据
//        initData(unfinished);
//        //actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        this.setTitle(R.string.ask_history);
//        mRecyclerView.setAdapter(mRecycleItemAdapter = new RecycleItemAdapter(QuestionHistoryActivity.this, data));
//        //item onclickListener
//        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListerer.RecyclerItemClickListener(this, mRecyclerView, new RecyclerItemClickListerer.RecyclerItemClickListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                //点击事件
//               Log.i("ccc","这里出错了");
//                Intent intent = new Intent(QuestionHistoryActivity.this,QuestionDetailedActivity.class);
//                intent.putExtra(Constant.QID,data.get(position).questionqId);
//                startActivity(intent);
//            }
//            @Override
//            public void onItemLongClick(View view, int position) {
//                //
//            }
//        }));
        //默认Fragment
        unFinishedFragment = new StudentHistoryUnfinishedFragment();
        finishedFragment = new StudentHistoryFinishedFragment();
        setDefaultFragment();
    }

    private void setDefaultFragment() {
        //FragmentTransaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!unFinishedFragment.isAdded()) {
            transaction.hide(finishedFragment).add(R.id.id_layout_to_recycler, unFinishedFragment, "1").commit();
        } else {
            transaction.hide(finishedFragment).show(unFinishedFragment).commit();
        }
//        unFinishedFragment = new StudentHistoryUnfinishedFragment();
//        getSupportFragmentManager().beginTransaction().replace(R.id.id_layout_to_recycler,unFinishedFragment).commit();
    }

    private void setFinishFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!finishedFragment.isAdded()) {
            transaction.hide(unFinishedFragment).add(R.id.id_layout_to_recycler, finishedFragment, "2").commit();
        } else {
            transaction.hide(unFinishedFragment).show(finishedFragment).commit();
        }

//        finishedFragment = new StudentHistoryFinishedFragment();
//        getSupportFragmentManager().beginTransaction().replace(R.id.id_layout_to_recycler,finishedFragment).commit();
    }

    void findViewById() {
        //mRecyclerView = (RecyclerView) this.findViewById(R.id.rv_list);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        mRecyclerView.setLayoutManager(layoutManager);
        unfinishedBtn = (Button) this.findViewById(R.id.question_history_unfinished);
        finishedBtn = (Button) this.findViewById(R.id.question_history_finished);

    }

    void setOnClickListener() {
        unfinishedBtn.setOnClickListener(this);
        finishedBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.question_history_unfinished:
                setDefaultFragment();
//                data.clear();
//                data.addAll(dataUnfinish);
//                mRecycleItemAdapter.notifyDataSetChanged();
                finishedBtn.setBackgroundResource(R.mipmap.unchecked);
                unfinishedBtn.setBackgroundResource(R.mipmap.checked);
                Log.i("1234", "1234");
                break;
            case R.id.question_history_finished:
                setFinishFragment();
//                if (flageFinish == true) {
//                    flageFinish = false;
//                    initData(false);
//                }
//                data.clear();
//                data.addAll(dataFinish);
//                mRecycleItemAdapter.notifyDataSetChanged();
                finishedBtn.setBackgroundResource(R.mipmap.checked);
                unfinishedBtn.setBackgroundResource(R.mipmap.unchecked);
                break;
        }
    }

//    void initData(final boolean unfinished) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                SharedPreferences sharedPreferences = getSharedPreferences(Constant.fileName, MODE_PRIVATE);
//                List<Map<String, String>> list = new ArrayList<Map<String, String>>();
//                Map<String, String> mapuserid = new HashMap<String, String>();
//                mapuserid.put(Constant.USERID, sharedPreferences.getString(Constant.USERID, ""));
//                list.add(mapuserid);
//                Map<String, String> mapsubjectid = new HashMap<String, String>();
//                mapsubjectid.put(Constant.POSTSUBJECTID, "0");
//                list.add(mapsubjectid);
//                Map<String, String> mapindex = new HashMap<String, String>();
//                mapindex.put(Constant.INDEX, "1");
//                list.add(mapindex);
//                String url;
//                if (unfinished == true) {
//                    url = Constant.HISTORYQUESTIONWITHNOURL;
//                } else {
//                    url = Constant.HISTORYQUESTIONURL;
//                }
//                String result = HttpRequest.postRequestBody(list, url);
//                //Log.i("result", result);
//                if (result == null) {
//                    return;
//                }
//                //数据解析
//                backItemData(result, unfinished);
//                //传网络的数据给item
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (unfinished == true) {
//                            data.addAll(dataUnfinish);
//                        } else {
//                            data.addAll(dataFinish);
//                        }
//                        //更新
//                        mRecycleItemAdapter.notifyDataSetChanged();
//                    }
//                });
//
//            }
//        }).start();

//    }
//    void backItemData(String result, boolean unfinished) {
//        try {
//            JSONObject jsonObjectAll = new JSONObject(result);
//            String ErrorCode = jsonObjectAll.optString("ErrorCode");
//            if (ErrorCode.equals("126")) {
//                return;
//            }
//            JSONArray jsonArrayItemAll = jsonObjectAll.optJSONArray("Items");
//            //Log.i("jsonArrayItemAll", String.valueOf(jsonArrayItemAll.length()));
//            for (int i = 0; i < jsonArrayItemAll.length(); i++) {
//                JSONObject jsonObjectItem = jsonArrayItemAll.getJSONObject(i);
//                String picture = jsonObjectItem.optString("picture");
//                String fulltext = jsonObjectItem.optString("fulltext");
//                String voice = jsonObjectItem.optString("voice");
//                String grade = jsonObjectItem.optString("grade");
//                String subjectid = jsonObjectItem.optString("subjectid");
//                //科目id
//                String subjectidStr = subjectIdToText(subjectid);
//                String id = jsonObjectItem.optString("id");
//                String asktime = jsonObjectItem.optString("asktime");
//                String status = jsonObjectItem.optString("status");
//                HistoryItem item = new HistoryItem();
//                item.questionText = fulltext;
//                item.questionSubject = subjectidStr;
//                item.questionImage = picture;
//                item.questionqId = id;
//                if (unfinished == true) {
//                    dataUnfinish.add(item);
//                } else {
//                    dataFinish.add(item);
//                }
//
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }


    //将获得的科目id转成文字
//    String subjectIdToText(String subjectId) {
//        String returnStr = null;
//        switch (subjectId) {
//            case "1":
//                returnStr = getResources().getString(R.string.China);
//                break;
//            case "2":
//                returnStr = getResources().getString(R.string.English);
//                break;
//            case "3":
//                returnStr = getResources().getString(R.string.Math);
//                break;
//            case "4":
//                returnStr = getResources().getString(R.string.Biology);
//                break;
//            case "5":
//                returnStr = getResources().getString(R.string.physics);
//                break;
//            case "6":
//                returnStr = getResources().getString(R.string.chemistry);
//                break;
//            case "7":
//                returnStr = getResources().getString(R.string.polity);
//                break;
//            case "8":
//                returnStr = getResources().getString(R.string.history);
//                break;
//            case "9":
//                returnStr = getResources().getString(R.string.geography);
//                break;
//        }
//        return returnStr;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
