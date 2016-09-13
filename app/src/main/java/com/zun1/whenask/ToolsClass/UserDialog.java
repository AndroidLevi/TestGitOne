package com.zun1.whenask.ToolsClass;

import android.content.Context;
import android.widget.Toast;

import com.zun1.whenask.R;

/**
 * Created by zun1user7 on 2016/7/14.
 */
public class UserDialog {
    public static void serverError(Context context){
        Toast.makeText(context, R.string.sever_error,Toast.LENGTH_SHORT).show();
    }
}
