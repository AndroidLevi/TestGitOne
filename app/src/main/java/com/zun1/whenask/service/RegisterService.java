package com.zun1.whenask.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.zun1.whenask.Constant;

import java.io.FileDescriptor;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

/**
 * Created by dell on 2016/6/29.
 */
public class RegisterService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /*private final static String TAG = "RegisterService";
    OkHttpClient mOkHttpClient;
    RequestBody requestBody;
    Request request;
    private final IBinder mBinder = new RegisterBind();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public class RegisterBind extends Binder{
        public void String(){

        }
        public RegisterService getService(){
            return RegisterService.this;
        }
    }

    public interface Register{
        public void getReister();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mOkHttpClient = new OkHttpClient();
        buildPostRequest(intent);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try {
                    response = mOkHttpClient.newCall(request).execute();
                    Log.i(TAG, response.body().string());
                    if(response.isSuccessful()){
                        Log.i(TAG, "成功");
                    }else {
                        throw new IOException("Unexpected code " + response);
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("LoginService", "loginService: ");


    }

    private void buildPostRequest(Intent it){
        Bundle bundle = it.getExtras();
        requestBody = new FormBody.Builder()
                .add(Constant.USERNAME, bundle.getString(Constant.USERNAME))
                .add(Constant.TRUSTNAME,bundle.getString(Constant.TRUSTNAME))
                .add(Constant.PASSWORD,bundle.getString(Constant.TRUSTNAME))
                .add(Constant.EMAIL,bundle.getString(Constant.EMAIL))
                .add(Constant.ADDRESS,bundle.getString(Constant.ADDRESS))
                .add(Constant.PHONE,bundle.getString(Constant.PHONE))
                .add("identification","324324215235211241")
                .build();
        request = new Request.Builder()
                .url(Constant.URL)
                .post(requestBody)
                .build();


    }
    private void startConnection(OkHttpClient okHttpClient ){
    }*/



}
