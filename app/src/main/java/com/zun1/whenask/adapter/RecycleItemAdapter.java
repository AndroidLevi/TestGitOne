package com.zun1.whenask.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.zun1.whenask.R;

import java.util.List;

/**
 * Created by zun1user7 on 2016/7/15.
 */
public class RecycleItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    String[] text;
    private List<HistoryItem> mDatas;
    public RecycleItemAdapter(Context context,List<HistoryItem> data){
        this.mContext = context;
        this.mDatas = data;
        mLayoutInflater = LayoutInflater.from(context);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Fresco.initialize(mContext);
        return new HistoryItemHolder(mLayoutInflater.inflate(R.layout.question_history_item,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //获得网络的数据，更改item的内容
        ((HistoryItemHolder) holder).questionText.setText(mDatas.get(position).questionText);
        ((HistoryItemHolder) holder).questionIma.setImageURI(Uri.parse(mDatas.get(position).questionImage));
        ((HistoryItemHolder) holder).subject.setText(mDatas.get(position).questionSubject);
        ((HistoryItemHolder) holder).statusText.setText(mDatas.get(position).status);
    }

    @Override
    public int getItemCount() {
        Log.i("getItemCount", String.valueOf(mDatas.size()));
        return mDatas.size();
    }
    //得到item控件的id和信息
    public class HistoryItemHolder extends RecyclerView.ViewHolder{
        TextView subject,questionText,statusText;
        Button deleteBtn,collectBtn;
        SimpleDraweeView questionIma;
        public HistoryItemHolder(View itemView) {
            super(itemView);
            subject = (TextView)itemView.findViewById(R.id.question_item_subject);
            questionText = (TextView)itemView.findViewById(R.id.question_item_text);
            statusText = (TextView)itemView.findViewById(R.id.question_item_status);
            questionText.setMovementMethod(ScrollingMovementMethod.getInstance());
            deleteBtn = (Button)itemView.findViewById(R.id.question_item_delete);
            collectBtn = (Button)itemView.findViewById(R.id.question_item_collect);
            questionIma = (SimpleDraweeView) itemView.findViewById(R.id.question_item_image);
        }
    }
}
