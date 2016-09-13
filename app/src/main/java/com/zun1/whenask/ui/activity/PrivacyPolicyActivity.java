package com.zun1.whenask.ui.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.zun1.whenask.R;

public class PrivacyPolicyActivity extends AppCompatActivity {
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        this.setTitle(R.string.nav_declaration);
        textView = (TextView)this.findViewById(R.id.privacy_policy_text);
        textView.setText("一、個人資料之安全\n" +
                "    保護會員的個人隱私是本平台重要的經營理念，當您提出申請、註冊使用、下載、使用軟體時，即表示您已閱讀、瞭解並同意接受本服務條款之所有內容，包含使用者名稱、生日、性別、email、地址，或其後使用本平台服務所提供或產生之數據資料。\n" +
                "    在未經會員同意之下，我們絕不會將會員的個人資料提供予任何與本站服務無關之第三人。會員應妥善保密自己的網路密碼及個人資料，不要將任何個人資料，尤其是網路密碼提供給任何人。\n" +
                "\n" +
                "二、\t個人資料的利用\n" +
                "    在消費者的同意情況下，使用範圍依照原來所說明的使用目的，除非事先說明、或依照相關法律規定，否則不會將資料提供給第三人、或移作其他目的使用。\n" +
                "    利用之目的例示如下：\n" +
                "    \n" +
                "    以會員身份使用本平台提供之各項服務時，於頁面中自動顯示會員資訊。\n" +
                "    宣傳廣告或行銷等\n" +
                "    提供會員各種最新活動等資訊、透過電子郵件、郵件、電話等提供與服務有關之資訊。 將會員所瀏覽之內容或廣告，依客戶之個人屬性或網站之瀏覽紀錄等項目，進行個人化作業、會員使用服務之分析、新服務之開發或既有服務之改善等。 針對民調、活動、留言等之意見，或其他服務關連事項，與會員進行聯繫。\n" +
                "    回覆客戶之詢問\n" +
                "    針對會員透過電子郵件、郵件、傳真、電話或其他任何直接間接連絡方式向本平台所提出之詢問進行回覆。\n" +
                "    其他\n" +
                "    提供個別服務時，亦可能於上述規定之目的以外，利用個人資料。此時將在該個別服務之網頁載明其要旨。\n" +
                "三、資料安全\n" +
                "    為保障會員的隱私及安全，本平台會員帳號資料會用密碼保護。本平台並盡力以合理之技術及程序，保障所有個人資料之安全。\n" +
                "四、個人資料查詢或更正的方式\n" +
                "        \n" +
                "    會員對於其個人資料，有查詢及閱覽、製給複製本、補充或更正、停止電腦處理及利用、刪除等需求時，立即聯絡我們 ，本平台將迅速進行處理。\n" +
                "    刪除、解除安裝本APP，將不會刪除您的會員資料，若您想要修改、停用、刪除您的個人資料或聯絡方式，或有任何建議，歡迎您直接聯絡我們。\n" +
                "五、隱私權保護政策修訂\n" +
                "        \n" +
                "    本公司將會不定時修改及變更本服務權益，建議您隨時注意網站公告，異動內容於公告日起生效，若您於變更或修改後繼續使用本服務，即視為您瞭解並已接受及同意變更修改後之服務條款。\n" +
                "    最新會員權益與相關規定，以產品官網說明為準，修正相關權益時，將不另行個別通知。會員如果對於本平台網站隱私權聲明、或與個人資料有關之相關事項有任何疑問，歡迎立即 聯絡我們 。");
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
