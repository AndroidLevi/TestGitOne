package com.zun1.whenask.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.zun1.whenask.R;


/**
 * Created by dell on 2016/7/5.
 */
public class SelectSubjectAdapter extends RecyclerView.Adapter<SelectSubjectAdapter.SubjectViewHolder> implements RecyclerView.OnItemTouchListener{

    private final static String TAG = "RecyleView";
    private String[] mDataset;

    public SelectSubjectAdapter(String[] datas){
        mDataset = datas;
    }
    @Override
    public SubjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(),R.layout.subject_item,null);
        SubjectViewHolder subjectViewHolder = new SubjectViewHolder(view);
        return subjectViewHolder;
    }

    @Override
    public void onBindViewHolder(SubjectViewHolder holder, int position) {
        holder.checkBox.setText(mDataset[position]);
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }


    class SubjectViewHolder extends RecyclerView.ViewHolder
    {
        CheckBox checkBox;
        public SubjectViewHolder(View view){
            super(view);
            checkBox = (CheckBox)view.findViewById(R.id.item_subject);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        switch (e.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "down");

        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
