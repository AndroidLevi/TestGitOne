package com.zun1.whenask.ui.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.zun1.whenask.R;

public class TermsForUsageActivity extends AppCompatActivity {
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_for_usage);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        this.setTitle(R.string.nav_use);
        textView = (TextView)this.findViewById(R.id.terms_usage_text);
        textView.setText("When Ask帐号（即When Ask用户ID）的所有权归When Ask應用程式，用户完成注册申请手续后，" +
                "获得When Ask帐号的使用权。用户应提供及时、详尽及准确的个人资料，并在变动等关键时刻更新注册资料以及简历、" +
                "名片等各种信息，使得自己的个人资料符合及时、详尽及准确的要求。所有原始键入的资料将引用为注册资料。因注册信息不真实而引起的问题，" +
                "以及问题所带来的后果，When Ask不承担任何责任。When Ask因经营需要，有权回收用户的When Ask帐号。\n" +
                "\n" +
                "    用户不应将其帐号、密码转让或出借予他人使用。如用户发现帐号遭他人非法使用，应立即通知。因黑客行为或用户的保管疏忽而导致帐号、密码遭他人非法使用，When Ask不承担任何责任。\n" +
                "\n" +
                "    用户理解并接受提供的服务可能包括广告，用户同意When Ask有权在提供网络服务过程中以各种方式投放各种商业性广告或其他任何类型的商业信息（包括但不限于在When Ask的任何页面上投放广告）。" +
                "并且，用户同意接受When Ask通过电子邮件、手机短信或其他方式向用户发送招聘信息或其他相关商业信息");
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
