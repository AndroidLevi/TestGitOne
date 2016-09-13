package com.zun1.whenask.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.zun1.whenask.Constant;
import com.zun1.whenask.DB.UserDb;
import com.zun1.whenask.DB.dao.UserDao;
import com.zun1.whenask.R;
import com.zun1.whenask.SetLanguageApplication.SetLanguageApplicationClass;
import com.zun1.whenask.ToolsClass.CircleImageView;
import com.zun1.whenask.ToolsClass.HttpRequest;
import com.zun1.whenask.ToolsClass.TimeTool;
import com.zun1.whenask.ui.Fragment.MainStudentFragment;
import com.zun1.whenask.ui.Fragment.MainTeacherFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private SimpleDraweeView headImage;
    private final int SUPPORT_PICTURE = 1;
    private static String path = Environment.getExternalStorageDirectory().getAbsolutePath();//sd
    private Bitmap head;//头像Bitmap
    private Button askBtn, historyBtn, draftBtn;
    private TextView nicknameTextView;
    private ImageView genderIma;
    UserDb userDb;
    String headStrPath = null;
    //fragment
    private Fragment mainStudentFragment, mainTeacherFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //主界面
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        //侧滑
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        findViewById();
        setOnClickListener();
        //设置headImage
//        Bitmap bt = BitmapFactory.decodeFile(path + "head.png");//从Sd中找头像，转换成Bitmap
//        if (bt != null) {
//            @SuppressWarnings("deprecation")
//            Drawable drawable = new BitmapDrawable(bt);//转换成drawable
//            headImage.setImageDrawable(drawable);
//        } else {
//            /**
//             *  如果SD里面没有则需要从服务器取头像，取回来的头像再保存在SD中
//             *
//             */
//
        SharedPreferences sharedPreferences = getSharedPreferences(Constant.fileName, MODE_PRIVATE);
        Log.i("AVATER", sharedPreferences.getString(Constant.AVATER, ""));
        headImage.setImageURI(Uri.parse(sharedPreferences.getString(Constant.AVATER, "")));

        userDb = UserDb.instance();
        userDb.init(this);
        //fragment
        setTypeFragment();
        //finish all activity
        SetLanguageApplicationClass setLanguageApplicationClass = (SetLanguageApplicationClass) getApplication();
        setLanguageApplicationClass.delete();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    URL url = new URL("http://128.199.231.124:8000/mobileapi/public/ttt/");
//                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                    conn.setRequestMethod("POST");
//                    if (conn.getResponseCode() ==
//                            HttpURLConnection.HTTP_OK) {
//                        InputStream is = conn.getInputStream();
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//                        StringBuilder sb = new StringBuilder();
//                        String line = null;
//                        while ((line = reader.readLine())!=null){
//                            sb.append(line + "\n");
//                        }
//                        Log.i("xxxxxxxxxx",sb.toString());
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//        byte[] key = "iswhenaskproject".getBytes();
//        byte[] keyiv = "tcejorpksanehwsi".getBytes();
//        byte[] data = "1234".getBytes();
//        try {
//            byte[] back = des3EncodeCBC(key,keyiv,data);
//            Log.i("back",new String(back));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    private void setTypeFragment() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constant.fileName, Context.MODE_PRIVATE);
        String usertype = sharedPreferences.getString(Constant.USERTYPE, "1");
        //学生
        if (usertype.equals("1")) {
            mainStudentFragment = new MainStudentFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, mainStudentFragment).commit();
        }
        //老师
        if (usertype.equals("2")) {
            mainTeacherFragment = new MainTeacherFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, mainTeacherFragment).commit();
        }

    }

    void findViewById() {
        //navigationView head
        NavigationView navigationView = (NavigationView) this.findViewById(R.id.nav_view);
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        //headImage
        headImage = (SimpleDraweeView) headerLayout.findViewById(R.id.imageView);
        //user_nickname
        nicknameTextView = (TextView) headerLayout.findViewById(R.id.head_nickname);
        genderIma = (ImageView)headerLayout.findViewById(R.id.genderIma);
    }

    void setOnClickListener() {
        headImage.setOnClickListener(this);
    }
    //返回按钮
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            Intent intent = new Intent(Intent.ACTION_MAIN);
//            intent.addCategory(Intent.CATEGORY_HOME);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//            Process.killProcess(Process.myPid());
            finish();
            super.onBackPressed();
        }
    }

    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //menuItemSelect
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this,EaseChatActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //侧滑
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_image) {
            Log.i("------>", "点击了");
            Intent intent = new Intent(MainActivity.this, ResetUserActivity.class);
            startActivity(intent);
            // Handle the camera action
        } else if (id == R.id.nav_resetpassword) {
            Intent intent = new Intent(MainActivity.this, ResetPasswordActivity.class);
            startActivity(intent);

        }
//        else if (id == R.id.nav_noticfation) {
//
//        } else if (id == R.id.nav_buy) {
//
//        }
        else if (id == R.id.nav_quit) {
            //退出登录
            quit();
        }
//        else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //退出登录
    void quit() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //lat and lon
                SetLanguageApplicationClass setLanguageApplicationClass = (SetLanguageApplicationClass) getApplication();
                float lat = setLanguageApplicationClass.getLat();
                float lon = setLanguageApplicationClass.getLon();
                //获取上次登录的信息
                SharedPreferences sharedPreferences = getSharedPreferences(Constant.fileName, Context.MODE_PRIVATE);
                List<Map<String, String>> list = new ArrayList<>();
//                Map<String, String> mapusertype = new HashMap<>();
//                mapusertype.put("usertype", sharedPreferences.getString(Constant.USERTYPE, ""));
//                list.add(mapusertype);
                Map<String, String> mapuserid = new HashMap<>();
                mapuserid.put("userid", sharedPreferences.getString(Constant.USERID, ""));
                list.add(mapuserid);
                Map<String, String> maplon = new HashMap<>();
                maplon.put("lon", String.valueOf(lon));
                list.add(maplon);
                Map<String, String> maplat = new HashMap<>();
                maplat.put("lat", String.valueOf(lat));
                list.add(maplat);
                Map<String, String> mapdeviceid = new HashMap<>();
                mapdeviceid.put(Constant.DEVICEID, sharedPreferences.getString(Constant.DEVICEID,""));
                list.add(mapdeviceid);
//                Map<String, String> mapplatform = new HashMap<>();
//                mapplatform.put("platform", "2");
//                list.add(mapplatform);
                String url = Constant.EXITURL;
                String result = HttpRequest.postRequestBody(list, url);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String ErrorCode = jsonObject.optString("ErrorCode");
                    if (ErrorCode.equals("000")) {
                        //环信退出登录
                        easeUIlogout();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    //环信退出登录
    void easeUIlogout(){
        EMClient.getInstance().logout(true, new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.i("onSuccess","onSuccess");
                userDb.saveloginkeychain("");
                userDb.saveloginInfo("", "");
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.putExtra(Constant.DISPALYHOMEASUPENABLED,false);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(int i, String s) {
                Log.i("onError",s);
            }

            @Override
            public void onProgress(int i, String s) {
                Log.i("onProgress",s);
            }
        });
    }
    //click btn
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageView:
                openPictrue();
                break;
        }
    }

    //打开相册
    void openPictrue() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }else {
            Intent intent1 = new Intent(Intent.ACTION_PICK, null);
            intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/png");
            startActivityForResult(intent1, SUPPORT_PICTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100){
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                Intent intent1 = new Intent(Intent.ACTION_PICK, null);
                intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/png");
                startActivityForResult(intent1, SUPPORT_PICTURE);
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //返回的相册的图片
        switch (requestCode) {
            case SUPPORT_PICTURE:
                if (resultCode == RESULT_OK) {
                    cropPhoto(data.getData());
                    Log.i("uriyy", String.valueOf(data.getData()));
                }
                break;
            case 3:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    Log.i("uri", String.valueOf(data.getData()));
                    head = extras.getParcelable("data");
                    if (head != null) {
                        /**
                         * 上传服务器代码
                         */
                        //setPicToView(head);//保存在SD卡中
                        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(),head,null,null));
                        headStrPath =  getRealPathFromURI(uri);
                        Log.i("headPath",headStrPath);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                postHeadImageToNet();
                            }
                        }).start();
                        //headImage.setImageBitmap(head);
                        // 用ImageView显示出来
                        headImage.setImageURI(uri);
                    }
                }
                break;
        }
    }

    /**
     * 调用系统的裁剪
     *
     * @param uri
     */
    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }

    //setPicToView
    private void setPicToView(Bitmap mBitmap) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            return;
        }
        FileOutputStream b = null;
        File file = new File(path);
        file.mkdirs();// 创建文件夹
        String fileName = path + "head.png";//图片名字
        try {
            b = new FileOutputStream(fileName);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, b);// 把数据写入文件

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭流
                //b.flush();
                b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    //上传headImage
    void postHeadImageToNet() {
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            File file = new File(headStrPath);
            if (file.exists()) {
                Log.i("tashi cunzaide", "tashi cunzaide");
            }
        }
        SharedPreferences sharedPreferences = getSharedPreferences(Constant.fileName, MODE_PRIVATE);
        String IMGUR_CLIENT_ID = "...";
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        File file = new File(headStrPath);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("usertype", sharedPreferences.getString(Constant.USERTYPE, ""))
                .addFormDataPart("userid", sharedPreferences.getString(Constant.USERID, ""))
                .addFormDataPart("avatar", file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file))
                .build();
        Request request = new Request.Builder()
//                .header("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
                .url(Constant.POSTHEADIMGURL)
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            Log.i("responseXXXX", response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = getSharedPreferences(Constant.fileName, MODE_PRIVATE);
        nicknameTextView.setText(sharedPreferences.getString(Constant.NICKNAME,""));
        if (sharedPreferences.getString(Constant.NICKNAME,"").equals("")){
            nicknameTextView.setText(R.string.user_name);
        }
        if (sharedPreferences.getString(Constant.GENDER,"").equals("1")){
            genderIma.setBackgroundResource(R.mipmap.side_man);
        }else {
            genderIma.setBackgroundResource(R.mipmap.side_girl);
        }
    }
    //getRealPathFromURI
    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
    public int getDisplayDensity() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.densityDpi;
    }
    //加密
    public static byte[] des3EncodeCBC(byte[] key, byte[] keyiv, byte[] data)
            throws Exception {
        Key deskey = null;

        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance("desede" + "/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(keyiv);
        cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
        byte[] bOut = cipher.doFinal(data);
        return bOut;
    }
    public static byte[] des3DecodeCBC(byte[] key, byte[] keyiv, byte[] data)
            throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);

        Cipher cipher = Cipher.getInstance("desede" + "/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(keyiv);
        cipher.init(Cipher.DECRYPT_MODE, deskey, ips);
        byte[] bOut = cipher.doFinal(data);
        return bOut;
    }
}
