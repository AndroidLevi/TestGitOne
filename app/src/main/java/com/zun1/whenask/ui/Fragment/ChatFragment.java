package com.zun1.whenask.ui.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.widget.EaseChatInputMenu;
import com.hyphenate.easeui.widget.EaseChatMessageList;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.zun1.whenask.R;

/**
 * Created by zun1user7 on 2016/8/26.
 */
public class ChatFragment extends EaseChatFragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.ease_fragment_chat,container,false);
        EaseChatMessageList messageList = (EaseChatMessageList) v.findViewById(R.id.message_list);
        messageList.setBackgroundResource(R.mipmap.backdrop);
        EaseTitleBar titleBar = (EaseTitleBar)v.findViewById(R.id.title_bar);
        titleBar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        titleBar.setRightLayoutVisibility(View.INVISIBLE);
        //EaseChatExtendMenu 扩展
//        EaseChatInputMenu inputMenu = (EaseChatInputMenu) v.findViewById(R.id.input_menu);
        //注册底部菜单扩展栏item
        //传入item对应的文字，图片及点击事件监听，extendMenuItemClickListener实现EaseChatExtendMenuItemClickListener
//        inputMenu.registerExtendMenuItem(R.string.attach_take_pic,R.drawable.ease_chat_takepic_normal,1,extendMenuItemClickListener);
//        inputMenu.registerExtendMenuItem(R.string.attach_picture,R.drawable.ease_chat_image_normal,2,extendMenuItemClickListener);
        return v;
    }
}
