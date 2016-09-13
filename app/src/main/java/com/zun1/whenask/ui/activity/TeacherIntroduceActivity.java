package com.zun1.whenask.ui.activity;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.zun1.whenask.Constant;
import com.zun1.whenask.R;
import com.zun1.whenask.ToolsClass.ActivityIndicatorView;
import com.zun1.whenask.ToolsClass.HttpRequest;
import com.zun1.whenask.ToolsClass.Toast;
import com.zun1.whenask.ToolsClass.UserDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeacherIntroduceActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText textEdit;
    private Button submitBtn;
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            ActivityIndicatorView.dismiss();
            super.handleMessage(msg);
            switch (msg.what){
                case 000:
                    Toast.makeText(TeacherIntroduceActivity.this,R.string.post_succeed,Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 001:
                    UserDialog.serverError(TeacherIntroduceActivity.this);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_introduce);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        this.setTitle(R.string.nav_idea);
        textEdit = (EditText)this.findViewById(R.id.teacher_introduce_edittext);
        submitBtn = (Button)this.findViewById(R.id.teacher_introduce_submit);
        submitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.teacher_introduce_submit:
                postOpinionAction();
                break;
        }
    }
    void postOpinionAction(){
        ActivityIndicatorView.start(TeacherIntroduceActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences(Constant.fileName,MODE_PRIVATE);
                List<Map<String,String>> list = new ArrayList<>();
                Map<String,String> mapuserid  = new HashMap<>();
                mapuserid.put(Constant.USERID,sharedPreferences.getString(Constant.USERID,""));
                list.add(mapuserid);
                Map<String,String> mapusertype  = new HashMap<>();
                mapusertype.put(Constant.USERTYPE,sharedPreferences.getString(Constant.USERTYPE,""));
                list.add(mapusertype);
                Map<String,String> maptext  = new HashMap<>();
                maptext.put("text",textEdit.getText().toString().trim());
                list.add(maptext);
                Map<String,String> mapdeviceid  = new HashMap<>();
                mapdeviceid.put(Constant.DEVICEID,sharedPreferences.getString(Constant.DEVICEID,""));
                list.add(mapdeviceid);
                String url = Constant.OPINOINURL;
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
                } catch (JSONException e) {
                    Message message = Message.obtain();
                    handler.sendEmptyMessage(message.what = 001);
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
