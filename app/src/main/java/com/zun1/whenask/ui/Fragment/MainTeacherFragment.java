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
import com.zun1.whenask.ui.activity.TeacherAnswerHistoryActivity;
import com.zun1.whenask.ui.activity.TeacherIntroduceActivity;
import com.zun1.whenask.ui.activity.TeacherSolveQuestionActivity;

/**
 * Created by zun1user7 on 2016/7/15.
 */
public class MainTeacherFragment extends Fragment implements View.OnClickListener{
    private Button solveProblemBtn,questionHistoryBtn;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View v = inflater.inflate(R.layout.select_teacherlayout,container,false);
        solveProblemBtn = (Button)v.findViewById(R.id.solveProblem);
        questionHistoryBtn = (Button)v.findViewById(R.id.questionHistory);
        solveProblemBtn.setOnClickListener(this);
        questionHistoryBtn.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.solveProblem:
                Intent intent = new Intent(getActivity(), TeacherSolveQuestionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.questionHistory:
                Intent intent1 = new Intent(getActivity(),TeacherAnswerHistoryActivity.class);
                startActivity(intent1);
                break;
        }
    }
}
