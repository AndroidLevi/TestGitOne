package com.zun1.whenask.ui.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zun1.whenask.Constant;
import com.zun1.whenask.R;
import com.zun1.whenask.ToolsClass.HttpRequest;
import com.zun1.whenask.ToolsClass.RecyclerItemClickListerer;
import com.zun1.whenask.adapter.HistoryItem;
import com.zun1.whenask.adapter.RecycleItemAdapter;
import com.zun1.whenask.ui.activity.QuestionDetailedActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zun1user7 on 2016/8/11.
 */
public class StudentHistoryUnfinishedFragment extends Fragment{
    private RecyclerView mRecyclerView;
    private List<HistoryItem> data = new ArrayList<>();
    private RecycleItemAdapter mRecycleItemAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    int index = 1;
    boolean isLoadingMore = false;
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_student_unfinished,container,false);
        final LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView = (RecyclerView)v.findViewById(R.id.id_recycler_question_unfinished);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mRecycleItemAdapter = new RecycleItemAdapter(getActivity(),data));
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.id_unfinished_swipeReefreshLayout);
        //设置刷新的颜色
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListerer.RecyclerItemClickListener(getActivity(), mRecyclerView, new RecyclerItemClickListerer.RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //点击事件
               Log.i("ccc","这里出错了");
                Intent intent = new Intent(getActivity(),QuestionDetailedActivity.class);
                intent.putExtra(Constant.QID,data.get(position).questionqId);
                startActivity(intent);
            }
            @Override
            public void onItemLongClick(View view, int position) {
                //
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
                if (lastVisibleItem >= totallItemCount -1 && dy > 0){
                    Log.i("dy","向下滑动");
                    if (isLoadingMore){

                    }else {
                        index ++;
                        initData(index);
                        isLoadingMore = false;
                    }
                }
            }
        });
        swipeRefreshLayout.measure(0,0);
        swipeRefreshLayout.setRefreshing(true);
        initData(index);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initData(final int index){
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constant.fileName, Context.MODE_PRIVATE);
                List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                Map<String, String> mapuserid = new HashMap<String, String>();
                mapuserid.put(Constant.USERID, sharedPreferences.getString(Constant.USERID, ""));
                list.add(mapuserid);
                Map<String, String> mapsubjectid = new HashMap<String, String>();
                mapsubjectid.put(Constant.POSTSUBJECTID, "0");
                list.add(mapsubjectid);
                Map<String, String> mapindex = new HashMap<String, String>();
                mapindex.put(Constant.INDEX, String.valueOf(index));
                list.add(mapindex);
                String url;
                url = Constant.HISTORYQUESTIONWITHNOURL;
                String result = HttpRequest.postRequestBody(list, url);
                //Log.i("result", result);
                if (result == null) {
                    return;
                }
                //数据解析
                backItemData(result);
                //传网络的数据给item
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRecycleItemAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });


            }
        }).start();
    }
    void backItemData(String result) {
        try {
            JSONObject jsonObjectAll = new JSONObject(result);
            String ErrorCode = jsonObjectAll.optString("ErrorCode");
            if (ErrorCode.equals("126")) {
                return;
            }
            JSONArray jsonArrayItemAll = jsonObjectAll.optJSONArray("Items");
            //Log.i("jsonArrayItemAll", String.valueOf(jsonArrayItemAll.length()));
            for (int i = 0; i < jsonArrayItemAll.length(); i++) {
                JSONObject jsonObjectItem = jsonArrayItemAll.getJSONObject(i);
                String picture = jsonObjectItem.optString("picture");
                String fulltext = jsonObjectItem.optString("fulltext");
                String voice = jsonObjectItem.optString("voice");
                String grade = jsonObjectItem.optString("grade");
                String subjectid = jsonObjectItem.optString("subjectid");
                //科目id
                String subjectidStr = subjectIdToText(subjectid);
                String id = jsonObjectItem.optString("id");
                String asktime = jsonObjectItem.optString("asktime");
                String status = jsonObjectItem.optString("status");
                HistoryItem item = new HistoryItem();
                item.questionText = fulltext;
                item.questionSubject = subjectidStr;
                item.questionImage = picture;
                item.questionqId = id;
                item.status = getStatusToStr(status);
                data.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //将获得的科目id转成文字
    String subjectIdToText(String subjectId) {
        String returnStr = null;
        switch (subjectId) {
            case "1":
                returnStr = getResources().getString(R.string.China);
                break;
            case "2":
                returnStr = getResources().getString(R.string.English);
                break;
            case "3":
                returnStr = getResources().getString(R.string.Math);
                break;
            case "4":
                returnStr = getResources().getString(R.string.Biology);
                break;
            case "5":
                returnStr = getResources().getString(R.string.physics);
                break;
            case "6":
                returnStr = getResources().getString(R.string.chemistry);
                break;
            case "7":
                returnStr = getResources().getString(R.string.polity);
                break;
            case "8":
                returnStr = getResources().getString(R.string.history);
                break;
            case "9":
                returnStr = getResources().getString(R.string.geography);
                break;
        }
        return returnStr;
    }
    //将获得的问题status转成文字
    public String getStatusToStr(String status){
        String statusStr = null;
        switch(status){
            case "1":
                statusStr = getActivity().getResources().getString(R.string.question_status_1);
                break;
            case "2":
                statusStr = getActivity().getResources().getString(R.string.question_status_2);
                break;
            case "3":
                statusStr = getActivity().getResources().getString(R.string.question_status_3);
                break;
            case "4":
                statusStr = getActivity().getResources().getString(R.string.question_status_4);
                break;
        }
        return statusStr;
    }
}
