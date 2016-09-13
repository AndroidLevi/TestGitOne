package com.zun1.whenask.ui.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.zun1.whenask.R;
import com.zun1.whenask.ui.activity.AskQuestionTypeActivity;
import com.zun1.whenask.ui.activity.QuestionDraftActivity;
import com.zun1.whenask.ui.activity.QuestionHistoryActivity;

/**
 * Created by zun1user7 on 2016/7/15.
 */
public class MainStudentFragment extends Fragment implements View.OnClickListener{
    private Button askBtn,historyBtn,draftBtn;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_main,container,false);
        //UI
        askBtn = (Button) v.findViewById(R.id.ask_question);
        historyBtn = (Button) v.findViewById(R.id.ask_history);
        draftBtn = (Button) v.findViewById(R.id.ask_draft);
        askBtn.setOnClickListener(this);
        historyBtn.setOnClickListener(this);
        draftBtn.setOnClickListener(this);
        return  v;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ask_question:
                Intent intent = new Intent(getActivity(),AskQuestionTypeActivity.class);
                startActivity(intent);
                break;
            case R.id.ask_history:
                Intent intent1 = new Intent(getActivity(), QuestionHistoryActivity.class);
                startActivity(intent1);
                break;
            case R.id.ask_draft:
                Intent intent2 = new Intent(getActivity(), QuestionDraftActivity.class);
                startActivity(intent2);
                break;
        }
    }
}
