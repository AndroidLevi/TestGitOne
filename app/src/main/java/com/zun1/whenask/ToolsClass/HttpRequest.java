package com.zun1.whenask.ToolsClass;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.http.FormUrlEncoded;

/**
 * Created by zun1user7 on 2016/6/30.
 */

public class HttpRequest {
    public FormBody formBody;
    public static String postRequestBody(List<Map<String,String>> requestList, String url){
        String result = null;
        OkHttpClient mOkHttpClient = new OkHttpClient();
        mOkHttpClient.connectTimeoutMillis();
        FormBody.Builder builder = new FormBody.Builder();
        Log.e("list", String.valueOf(requestList.size()));
        for (Map<String,String> i: requestList){
            Object[] set = i.keySet().toArray();
            builder.add((String) set[0],i.get((String) set[0]));
        }
        RequestBody requestBody =  builder.build();
        Request request1 = new Request.Builder().url(url).post(requestBody).build();
        Call call = mOkHttpClient.newCall(request1);
        try {
            result = call.execute().body().string();
            Log.i("result",result);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.i("exception",e.toString());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                String responseStr =  response.body().string();
//                Log.i("responseStr",responseStr);
//
//            }
//        });
       return result;
    }



}
