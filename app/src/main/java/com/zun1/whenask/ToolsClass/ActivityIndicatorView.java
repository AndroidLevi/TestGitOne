package com.zun1.whenask.ToolsClass;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.zun1.whenask.R;

/**
 * Created by zun1user7 on 2016/8/10.
 */
public class ActivityIndicatorView {
    private static Dialog mDialog;

    public static void start(Context context) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context, R.style.full_screen_dialog);
        //ProgressBar progressBar = new ProgressBar(context);
//        LinearLayout layout = new LinearLayout(context);
//        layout.addView(progressBar,new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        View view = LayoutInflater.from(context).inflate(R.layout.activity_indicator_view, null);
        mBuilder.setView(view);

        mDialog = mBuilder.create();
        mDialog.setCancelable(false);
        Window win = mDialog.getWindow();
        //显示对话框时，后面的Activity不变暗，可选操作。
        //win.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        //win.setWindowAnimations(R.style.anim_dialog_bottom);
        //获取对话框当前的参数值
        WindowManager.LayoutParams params = win.getAttributes();
        params.alpha = 0.5f;
        params.dimAmount = 0.1f;
        params.gravity = Gravity.CENTER;
        win.setAttributes(params);
        win.setBackgroundDrawableResource(R.mipmap.alpha);
        mDialog.show();
    }

    public static void dismiss() {
        mDialog.dismiss();
    }
}
