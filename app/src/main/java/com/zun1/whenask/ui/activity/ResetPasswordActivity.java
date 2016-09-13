package com.zun1.whenask.ui.activity;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zun1.whenask.Constant;
import com.zun1.whenask.R;
import com.zun1.whenask.ToolsClass.HttpRequest;
import com.zun1.whenask.ToolsClass.UserDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText password_now,password_new,password_sure;
    private Button submitBtn,resetBtn;
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 000:
                    Toast.makeText(ResetPasswordActivity.this,R.string.change_succeed,Toast.LENGTH_SHORT).show();
                    break;
                case 001:
                    UserDialog.serverError(ResetPasswordActivity.this);
                    break;
                case 102:
                    Toast.makeText(ResetPasswordActivity.this,R.string.originpassword_error,Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resetpassword);
        findViewById();
        setOnClickListener();
        //actionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        this.setTitle(R.string.resetpassword);

    }
    void  findViewById(){
        password_new = (EditText)this.findViewById(R.id.resetpassword_new);
        password_now = (EditText)this.findViewById(R.id.resetpassword_now);
        password_sure = (EditText)this.findViewById(R.id.resetpassword_sure);
        submitBtn = (Button)this.findViewById(R.id.resetpassword_submit);
        resetBtn = (Button)this.findViewById(R.id.resetpassword_reset);
    }
    void setOnClickListener(){
        submitBtn.setOnClickListener(this);
        resetBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Log.e("---------->","xxxx");
        switch (view.getId()){
            case R.id.resetpassword_submit:
                if (password_new.getText().toString().equals(password_sure.getText().toString())){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SharedPreferences sharedPreferences = getSharedPreferences(Constant.fileName,MODE_PRIVATE);
                            String usertype = sharedPreferences.getString(Constant.USERTYPE,"");
                            String userid = sharedPreferences.getString(Constant.USERID,"");
                            List<Map<String,String>> list = new ArrayList<Map<String, String>>();
                            Map<String,String> mapusertype = new HashMap<String, String>();
                            mapusertype.put("usertype",usertype);
                            list.add(mapusertype);
                            Map<String,String> mapuserid = new HashMap<String, String>();
                            mapuserid.put("userid",userid);
                            list.add(mapuserid);
                            Map<String,String> originpassword = new HashMap<String, String>();
                            originpassword.put("originpassword",password_now.getText().toString().trim());
                            list.add(originpassword);
                            Map<String,String> password = new HashMap<String, String>();
                            password.put("password",password_new.getText().toString().trim());
                            list.add(password);
                            String url = Constant.RESETPASSWORIDURL;
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
                                if (ErrorCode.equals("102")){
                                    Message message = Message.obtain();
                                    handler.sendEmptyMessage(message.what = 102);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                }else {
                    Toast.makeText(ResetPasswordActivity.this,R.string.sure_password_error,Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.resetpassword_reset:
                password_now.setText("");
                password_new.setText("");
                password_sure.setText("");
                break;
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
