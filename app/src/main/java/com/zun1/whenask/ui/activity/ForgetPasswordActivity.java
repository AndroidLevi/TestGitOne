package com.zun1.whenask.ui.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zun1.whenask.Constant;
import com.zun1.whenask.R;
import com.zun1.whenask.SetLanguageApplication.SetLanguageApplicationClass;
import com.zun1.whenask.ToolsClass.HttpRequest;
import com.zun1.whenask.ToolsClass.UserDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener{
    private Button getAuthCodeBtn,submitBtn,rewriteBtn;
    private EditText emailEdit,autoCodeEdit,newPasswordEdit;
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 000:
                    Toast.makeText(ForgetPasswordActivity.this,R.string.getauthcode_succeed,Toast.LENGTH_SHORT).show();
                    break;
                case 001:
                    UserDialog.serverError(ForgetPasswordActivity.this);
                    break;
                case 002:
                    Toast.makeText(ForgetPasswordActivity.this,R.string.change_succeed,Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 004:
                    break;
                case 114:
                    Toast.makeText(ForgetPasswordActivity.this,R.string.email_exit,Toast.LENGTH_SHORT).show();
                    break;
                case 115:
                    Toast.makeText(ForgetPasswordActivity.this,R.string.authcode_past,Toast.LENGTH_SHORT).show();
                    break;
                case 116:
                    Toast.makeText(ForgetPasswordActivity.this,R.string.authcodeError,Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password);
        findViewById();
        setOnClickListener();
        //actionbar
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        this.setTitle(R.string.forget);
        //add activity
        SetLanguageApplicationClass setLanguageApplicationClass = (SetLanguageApplicationClass) getApplication();
        setLanguageApplicationClass.activityList.add(this);
    }
    void findViewById(){
        getAuthCodeBtn = (Button)this.findViewById(R.id.forget_password_authcode);
        submitBtn = (Button)this.findViewById(R.id.email_submit);
        rewriteBtn = (Button)this.findViewById(R.id.rewrite);
        emailEdit = (EditText)this.findViewById(R.id.edText_login_forget_password);
        autoCodeEdit = (EditText)this.findViewById(R.id.forget_password_editext_auto);
        newPasswordEdit = (EditText)this.findViewById(R.id.forget_password_editext_newpasswork);
    }
    void setOnClickListener(){
        getAuthCodeBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        rewriteBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.forget_password_authcode:
                //得到验证码
                getAuthCodeAction();
                break;
            case R.id.email_submit:
                //提交
                submitAciton();
                break;
            case R.id.rewrite:
                //重填
                emailEdit.setText("");
                autoCodeEdit.setText("");
                newPasswordEdit.setText("");
                break;
        }
    }
    //得到验证码
    void getAuthCodeAction(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Map<String,String>> list = new ArrayList<Map<String, String>>();
                Map<String,String> mapemail = new HashMap<String, String>();
                mapemail.put(Constant.EMAIL,emailEdit.getText().toString().trim());
                list.add(mapemail);
                String url = Constant.GETAUTHCODEFORMEMAILURL;
                String result = HttpRequest.postRequestBody(list,url);
                if (result == null){
                    Message message = Message.obtain();
                    handler.sendEmptyMessage(message.what = 001);
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String ErrorCode = jsonObject.optString("ErrorCode");
                    if (ErrorCode.equals("000")){
                        Message message = Message.obtain();
                        handler.sendEmptyMessage(message.what = 000);
                    }
                    if (ErrorCode.equals("114")){
                        Message message = Message.obtain();
                        handler.sendEmptyMessage(message.what = 114);
                    }
                } catch (JSONException e) {
                    Message message = Message.obtain();
                    handler.sendEmptyMessage(message.what = 001);
                    e.printStackTrace();
                }
            }
        }).start();
    }
    //提交
    void submitAciton(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Map<String,String>> list = new ArrayList<Map<String, String>>();
                Map<String,String> mapauthcode = new HashMap<String, String>();
                mapauthcode.put(Constant.AUTHCODE,autoCodeEdit.getText().toString().trim());
                list.add(mapauthcode);
                String url = Constant.VERIFYAUTHCODEURL;
                String result = HttpRequest.postRequestBody(list,url);
                if (result == null){
                    Message message = Message.obtain();
                    handler.sendEmptyMessage(message.what = 001);
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String ErrorCode = jsonObject.optString("ErrorCode");
                    if (ErrorCode.equals("000")){
//                        Message message = Message.obtain();
//                        handler.sendEmptyMessage(message.what = 001);
                        changePassWord();
                    }
                    if (ErrorCode.equals("115")){
                        Message message = Message.obtain();
                        handler.sendEmptyMessage(message.what = 115);
                    }
                    if (ErrorCode.equals("116")){
                        Message message = Message.obtain();
                        handler.sendEmptyMessage(message.what = 116);
                    }
                } catch (JSONException e) {
                    Message message = Message.obtain();
                    handler.sendEmptyMessage(message.what = 001);
                    e.printStackTrace();
                }
            }
        }).start();
    };
    //change password
    void changePassWord(){
        List<Map<String,String>> list = new ArrayList<Map<String, String>>();
        Map<String,String> mappassword = new HashMap<String, String>();
        mappassword.put(Constant.PASSWORD,newPasswordEdit.getText().toString().trim());
        list.add(mappassword);
        String url = Constant.FORGOTPASSWORDURL;
        String result = HttpRequest.postRequestBody(list,url);
        if (result == null){
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(result);
            String ErrorCode = jsonObject.optString("ErrorCode");
            if (ErrorCode.equals("000")){
                Message message = Message.obtain();
                handler.sendEmptyMessage(message.what = 002);
                changePassWord();
            }
            if (ErrorCode.equals("004")){
                Message message = Message.obtain();
                handler.sendEmptyMessage(message.what = 004);
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
}
