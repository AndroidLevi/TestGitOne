package com.zun1.whenask.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.zun1.whenask.Constant;
import com.zun1.whenask.DB.DatabaseHelper;
import com.zun1.whenask.R;
import com.zun1.whenask.ToolsClass.RecyclerItemClickListerer;
import com.zun1.whenask.ToolsClass.TextFromInt;
import com.zun1.whenask.adapter.HistoryItem;
import com.zun1.whenask.adapter.RecycleItemAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class QuestionDraftActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private List<HistoryItem> data = new ArrayList<>();
    private RecycleItemAdapter mRecycleItemAdapter;
    private Toolbar toolbar;
    private Button deleteBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_draft);
        findViewById();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecyclerView.setAdapter(mRecycleItemAdapter = new RecycleItemAdapter(this,data));
        initData();
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListerer.RecyclerItemClickListener(QuestionDraftActivity.this, mRecyclerView, new RecyclerItemClickListerer.RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(QuestionDraftActivity.this,TakePicturePostActivity.class);
                intent.putExtra("flag",true);
                intent.putExtra("image",data.get(position).questionImage);
                intent.putExtra(Constant.POSTSUBJECTID,data.get(position).questionSubject);
                intent.putExtra(Constant.POSTGRADE,data.get(position).questionGrade);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
    }
    void findViewById(){
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView = (RecyclerView)this.findViewById(R.id.id_question_draft_recyclerview);
        mRecyclerView.setLayoutManager(manager);
        toolbar = (Toolbar)this.findViewById(R.id.id_question_draft_toolbar);
        deleteBtn = (Button)this.findViewById(R.id.question_draft_delete);
        deleteBtn.setOnClickListener(new deleteAll());
    }
    void initData(){
         query();
    }
    //清空
    private class deleteAll implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            new android.support.v7.app.AlertDialog.Builder(QuestionDraftActivity.this)
                    .setTitle(R.string.sure_delete_all)
                    .setMessage(R.string.sure)
                    .setPositiveButton(R.string.sure_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //删除数据库
                            delete();
                            //删除图片
                            deleteFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/drafIma/"));
                            data.clear();
                            mRecycleItemAdapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton(R.string.sure_no, null)
                    .show();

        }
    }
    //delete 数据库
    void delete(){
        DatabaseHelper databaseHelper = new DatabaseHelper(QuestionDraftActivity.this,"question_db",1);
        SQLiteDatabase db6 = databaseHelper.getWritableDatabase();
        db6.delete("user","id=?",new String[]{"1"});
    }
    public void deleteFile(File file) {
        if (file.exists() == false) {
            return;
        } else {
            if (file.isFile()) {
                file.delete();
                return;
            }
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    file.delete();
                    return;
                }
                for (File f : childFile) {
                    deleteFile(f);
                }
                file.delete();
            }
        }
    }
    //查询
    void query(){
        DatabaseHelper dbHelper5 = new DatabaseHelper(QuestionDraftActivity.this, "question_db",1);
        SQLiteDatabase db5 = dbHelper5.getReadableDatabase();
        //创建游标对象
        Cursor cursor = db5.query("user", new String[]{"id","subject","grade","image","text"}, "id=?", new String[]{"1"}, null, null, null, null);
        //利用游标遍历所有数据对象
        while(cursor.moveToNext()){
            String grade = cursor.getString(cursor.getColumnIndex("grade"));
            String subject = cursor.getString(cursor.getColumnIndex("subject"));
            String image = cursor.getString(cursor.getColumnIndex("image"));
            String text = cursor.getString(cursor.getColumnIndex("text"));
            HistoryItem historyItem = new HistoryItem();
            historyItem.questionSubject = TextFromInt.intToText(QuestionDraftActivity.this,subject);
            historyItem.questionText = text;
            historyItem.questionImage = String.valueOf(Uri.fromFile(new File(image)));
            historyItem.status = "";
            historyItem.questionGrade = grade;
            data.add(historyItem);
            //日志打印输出
            Log.i("query","grade-->"+grade);
            Log.i("query","subject-->"+subject);
            Log.i("query","image-->"+image);
            Log.i("query","text-->"+text);
            File file = new File(image);
            if (file.exists()){
                Log.i("exists","存在");
            }else {
                Log.i("exists","不存在");
            }
        }
        mRecycleItemAdapter.notifyDataSetChanged();
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
