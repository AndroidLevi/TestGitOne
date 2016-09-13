package com.zun1.whenask.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.zun1.whenask.Constant;
import com.zun1.whenask.R;
import com.zun1.whenask.SetLanguageApplication.SetLanguageApplicationClass;
import com.zun1.whenask.service.RegisterService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoginActivity extends AppCompatActivity {
    ActionBar actionBar;
    @BindView(R.id.register_button)
    Button register;
    String a = null;
    @BindView(R.id.user)
    Button user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ask_user);
        ButterKnife.bind(this);
        this.setTitle(R.string.register_login);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //add activity
        SetLanguageApplicationClass setLanguageApplicationClass = (SetLanguageApplicationClass) getApplication();
        setLanguageApplicationClass.activityList.add(this);
    }

    @OnClick(R.id.register_button)
    public void register(){
        Intent register = new Intent(this,SelectRegisterType.class);
        startActivity(register);
    }

    @OnClick(R.id.user)
    public void user(){
        Intent intent = new Intent(this,LoginImportActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        boolean homeEnabled = intent.getBooleanExtra(Constant.DISPALYHOMEASUPENABLED,true);
        if (homeEnabled == false){
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }
    //返回按钮
//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }
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
