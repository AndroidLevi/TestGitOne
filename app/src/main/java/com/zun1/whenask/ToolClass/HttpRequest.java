package com.zun1.whenask.ToolClass;

import android.util.Log;

import java.io.IOException;
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

    public static void postRequestBody(List<Map<String,String>> requestList){
        OkHttpClient mOkHttpClient = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        for (Map<String,String> i: requestList){
             //builder.add(i.keySet().toString(),i.get(i.keySet()));
            Log.e("--->",i.keySet().toString()+ i.get(i.keySet()));
        }
        RequestBody requestBody =  builder.build();
        Request request1 = new Request.Builder().url("").post(requestBody).build();
        Call call = mOkHttpClient.newCall(request1);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("exception",e.toString());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseStr =  response.body().string();
                Log.i("responseStr",responseStr);
            }
        });
    }



}
