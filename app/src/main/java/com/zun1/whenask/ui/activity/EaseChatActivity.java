package com.zun1.whenask.ui.activity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.widget.EaseChatMessageList;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.exceptions.HyphenateException;
import com.zun1.whenask.Constant;
import com.zun1.whenask.R;
import com.zun1.whenask.ToolsClass.HttpRequest;
import com.zun1.whenask.ui.Fragment.ChatFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EaseChatActivity extends AppCompatActivity{
    private EMGroup group;
    private String oppositeAvatar = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ease_chat);
//        EMGroupManager.EMGroupOptions option = new EMGroupManager.EMGroupOptions();
//        option.maxUsers = 200;
//        option.style = EMGroupManager.EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;
//        try {
//            group = EMClient.getInstance().groupManager().createGroup("你好啊","这是一个开心的聊天室", new String[]{"tttt1234"}, "我是bb", option);
//            Log.i("id",group.getGroupId());
//        } catch (HyphenateException e) {
//            e.printStackTrace();
//        }
        getAvatarFromUserName();
        setFragment();
    }
    void setFragment(){
        //new 出EaseChatFagment
//        EaseChatFragment chatFragment = new EaseChatFragment();
        //得到自己的头像
        final SharedPreferences sharedPreferences = getSharedPreferences(Constant.fileName,MODE_PRIVATE);
        ChatFragment chatFragment = new ChatFragment();
        EaseUI easeUI = EaseUI.getInstance();
        easeUI.setUserProfileProvider(new EaseUI.EaseUserProfileProvider() {
            @Override
            public EaseUser getUser(String username) {
                EaseUser user = new EaseUser(username);
                if (username.equals("teacher123")){
                    user.setAvatar(sharedPreferences.getString(Constant.AVATER,""));
                }if (username.equals("tttt1234")){
                    user.setAvatar(oppositeAvatar);
                }
                Log.i("username",username);
                //user.setNick("黄维立");
                return user;
            }
        });
        //传入参数
        Bundle args = new Bundle();
        args.putInt(EaseConstant.EXTRA_CHAT_TYPE,EaseConstant.CHATTYPE_GROUP);
        args.putString(EaseConstant.EXTRA_USER_ID,"1472439369850");
        chatFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().add(R.id.container,chatFragment).commit();
    }
    //通过username得到头像
    void getAvatarFromUserName(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String userName = "tttt1234";
                List<Map<String,String>> list = new ArrayList();
                Map<String,String> mapusername = new HashMap<>();
                mapusername.put(Constant.USERNAME,userName);
                list.add(mapusername);
                String url = Constant.LOOKOPPOSITEURL;
                String result = HttpRequest.postRequestBody(list,url);
                if (result == null){
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String ErrorCode = jsonObject.optString("ErrorCode");
                    if (ErrorCode.equals("000")){
                        JSONObject items = jsonObject.optJSONObject("Items");
                        String nickname = items.optString("nickname");
                        String avatar = items.optString("avatar");
                        oppositeAvatar = avatar;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
