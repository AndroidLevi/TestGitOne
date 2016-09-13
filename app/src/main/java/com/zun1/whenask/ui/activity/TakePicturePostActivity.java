package com.zun1.whenask.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.zun1.whenask.Constant;
import com.zun1.whenask.DB.DatabaseHelper;
import com.zun1.whenask.DB.UserDb;
import com.zun1.whenask.R;
import com.zun1.whenask.ToolsClass.ActivityIndicatorView;
import com.zun1.whenask.ToolsClass.HttpRequest;
import com.zun1.whenask.ToolsClass.UserDialog;
import com.zun1.whenask.adapter.QuestionItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.framed.ErrorCode;

import static java.security.AccessController.getContext;

public class TakePicturePostActivity extends AppCompatActivity implements View.OnClickListener {
    private Button redBtn, yellowBtn, greenBtn, purplewBtn, lightgreenBtn, pinkBtn, lightpupleBtn;
    private Button lookQuestionDetailedBtn;
    private TextView textFormOne;
    private ImageView canvasImaga;
    private Button recordbtn, playBtn;
    private EditText post_text;
    private Bitmap baseBitmap;
    private Canvas canvas;
    private Paint paint;
    int height;
    int width;
    //语音操作对象
    private MediaPlayer mPlayer = null;
    private MediaRecorder mRecorder = null;
    //语音文件保存路径
    private String FileName = null;
    //为了表示返回方法中辨别你的程序打开了相机
    final int TAKE_PICTURE = 1;
    static final int SUPPORT_CAMERA = 100;
    //相册返回
    static final int SUPPORT_PICTURE = 200;
    //标记是否是返回的图片
    boolean backImage_flag = false;
    //uritempFile为Uri类变量，实例化uritempFile
    Uri uritempFile = null;
    //发送按钮
    private Button postbtn;
    //图片保存路径
    private String tempImagePath = null;
    SharedPreferences sharedPreferences;
    //老师回答问题页面过来
    String questionPicture;
    String id;
    //画笔颜色
    int paintColor = Color.RED;
    //toolbar
    private Toolbar toolbar;
    //Dialog 录音动画弹框
    private Dialog mDialog;
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what != 003) {
                ActivityIndicatorView.dismiss();
            }
            switch (msg.what) {
                case 000:
                    //放弃成功
                    Toast.makeText(TakePicturePostActivity.this, R.string.giveup_succeed, Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 2:
                    //服务器出错
                    UserDialog.serverError(TakePicturePostActivity.this);
                    break;
                case 0001:
                    //老师和学生的发送提示
                    if (questionPicture != null) {
                        Toast.makeText(TakePicturePostActivity.this, R.string.answer_succeed, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(TakePicturePostActivity.this, R.string.post_succeed, Toast.LENGTH_SHORT).show();
                    }
                    finish();
                    break;
                case 003:
                    Toast.makeText(TakePicturePostActivity.this, R.string.image_null, Toast.LENGTH_SHORT).show();
                    break;
                case 020:
                    Toast.makeText(TakePicturePostActivity.this, R.string.voice_no_record, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture_post);
        findViewById();
        listener();
        //设置sdcard的路径
        FileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        FileName += "/audiorecordtest.aac";
        tempImagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tempImage.png";
        //sharedPreferences
        sharedPreferences = getSharedPreferences(Constant.fileName, MODE_PRIVATE);
        // 创建画笔
        paint = new Paint();
        //toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //老师回答问题页面过来
        Intent intent = getIntent();
        questionPicture = intent.getStringExtra("picture");
        id = intent.getStringExtra(Constant.QID);
        if (questionPicture != null) {
            baseBitmap = returnBitmap(Uri.parse(questionPicture));
            canvasImaga.setImageBitmap(returnBitmap(Uri.parse(questionPicture)));
            backImage_flag = true;
            textFormOne.setText(R.string.solvequestion);
            lookQuestionDetailedBtn.setVisibility(View.VISIBLE);
        } else {
            lookQuestionDetailedBtn.setVisibility(View.INVISIBLE);
            //从草稿过来的
            if (intent.getBooleanExtra("flag",false) == true) {
                try {
                    baseBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(intent.getStringExtra("image")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                baseBitmap = BitmapFactory.decodeFile(intent.getStringExtra("image"));
                backImage_flag = true;
                canvasImaga.setImageBitmap(baseBitmap);
            }
        }
        //home
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        query();
//        delete();
        if (ContextCompat.checkSelfPermission(TakePicturePostActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TakePicturePostActivity.this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 300);
        }
    }

    void findViewById() {
        canvasImaga = (ImageView) this.findViewById(R.id.canvas_image);
        //得到控件的高度和宽度
        getHeightAndWidth();
        recordbtn = (Button) this.findViewById(R.id.record_btn);
        playBtn = (Button) this.findViewById(R.id.paly_Btn);
        redBtn = (Button) this.findViewById(R.id.red_btn);
        yellowBtn = (Button) this.findViewById(R.id.yellow_btn);
        greenBtn = (Button) this.findViewById(R.id.green_btn);
        purplewBtn = (Button) this.findViewById(R.id.purple_btn);
        lightgreenBtn = (Button) this.findViewById(R.id.lightgreen_btn);
        pinkBtn = (Button) this.findViewById(R.id.pink_btn);
        lightpupleBtn = (Button) this.findViewById(R.id.lightpuple_btn);
        postbtn = (Button) this.findViewById(R.id.post_btn);
        post_text = (EditText) this.findViewById(R.id.post_text);
        toolbar = (Toolbar) this.findViewById(R.id.student_detailed_toolbar);
        lookQuestionDetailedBtn = (Button) this.findViewById(R.id.student_question_detailed_looked);
        textFormOne = (TextView) this.findViewById(R.id.question_for_one);
    }

    void listener() {
        canvasImaga.setOnTouchListener(new imageTochLister());
        canvasImaga.setOnClickListener(this);
        recordbtn.setOnTouchListener(new record());
        playBtn.setOnClickListener(this);
        redBtn.setOnClickListener(new changePaintColor());
        yellowBtn.setOnClickListener(new changePaintColor());
        greenBtn.setOnClickListener(new changePaintColor());
        purplewBtn.setOnClickListener(new changePaintColor());
        lightgreenBtn.setOnClickListener(new changePaintColor());
        pinkBtn.setOnClickListener(new changePaintColor());
        lightpupleBtn.setOnClickListener(new changePaintColor());
        postbtn.setOnClickListener(new postAllToNet());
        lookQuestionDetailedBtn.setOnClickListener(new lookAction());
    }

    //单击事件
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.canvas_image:
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle("请选择上传的图片")
                        .setItems(new String[]{"拍照", "从手机相册中选择"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.i("i1", String.valueOf(i));
                                if (i == 0) {
                                    Log.i("i2", String.valueOf(i));
                                    //打开相机
                                    ActivityCompat.requestPermissions(TakePicturePostActivity.this, new String[]{android.Manifest.permission.CAMERA}, SUPPORT_CAMERA);
                                    if (ContextCompat.checkSelfPermission(TakePicturePostActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                        Log.i("i3", String.valueOf(i));
                                        return;
                                    }
                                } else {
                                    //打开相册
                                    openPicture();
                                }
                            }
                        })
                        .setIcon(R.mipmap.logo)
                        .setNegativeButton("取消", null).create();
                Window window = alertDialog.getWindow();
                window.setGravity(Gravity.BOTTOM);
                alertDialog.show();
                Log.i("width", String.valueOf(width));
                break;
            //播放录音
            case R.id.paly_Btn:
                try {
                    mPlayer = new MediaPlayer();
                    mPlayer.setDataSource(FileName);
                    mPlayer.prepare();
                    mPlayer.start();
                } catch (IOException e) {
                    Message message = Message.obtain();
                    handler.sendEmptyMessage(message.what = 020);
                    Log.e("Exception", "播放失败");
                }
                break;
        }
    }

    //画东西listener
    public class imageTochLister implements View.OnTouchListener {

        int startX;
        int startY;
        int stopX;
        int stopY;
        //判断是否移动
        boolean flag = true;
        int x = 0, y = 0;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (baseBitmap == null) {
                // 创建一张空白图片
                baseBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                // 创建一张画布
                canvas = new Canvas(baseBitmap);
                // 画布背景为灰色
                canvas.drawColor(Color.WHITE);
                // 创建画笔
                paint = new Paint();
                // 画笔颜色为红色
                paint.setColor(paintColor);
                // 宽度5个像素
                paint.setStrokeWidth(5);
                // 先将灰色背景画上
                canvas.drawBitmap(baseBitmap, new Matrix(), paint);
                canvasImaga.setImageBitmap(baseBitmap);
            }
            if (backImage_flag == true) {
                backImage_flag = false;
                Bitmap bitmap = Bitmap.createScaledBitmap(baseBitmap, width, height, false);
                baseBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                // 创建一张画布
                canvas = new Canvas(baseBitmap);
                // 画布背景为灰色
                canvas.drawColor(Color.TRANSPARENT);
                // 创建画笔
                paint = new Paint();
                // 画笔颜色为红色
                paint.setColor(paintColor);
                // 宽度5个像素
                paint.setStrokeWidth(5);
                // 先将灰色背景画上
                canvas.drawBitmap(baseBitmap, new Matrix(), paint);
                canvasImaga.setImageBitmap(baseBitmap);
            }
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (x == 0 && y == 0) {
                        flag = false;
                    }
                    // 获取手按下时的坐标
                    startX = (int) motionEvent.getX();
                    startY = (int) motionEvent.getY();
                    Log.i("ACTION_DOWN", "ACTION_DOWN");
                    break;
                case MotionEvent.ACTION_MOVE:
                    // 获取手移动后的坐标
                    stopX = (int) motionEvent.getX();
                    stopY = (int) motionEvent.getY();
                    // 在开始和结束坐标间画一条线
                    canvas.drawLine(startX, startY, stopX, stopY, paint);
                    x = Math.abs(stopX - startX);
                    y = Math.abs(stopY - startY);
                    // 实时更新开始坐标
                    startX = (int) motionEvent.getX();
                    startY = (int) motionEvent.getY();
                    canvasImaga.setImageBitmap(baseBitmap);
                    Log.i("ACTION_MOVE", "ACTION_MOVE");
                    flag = true;
                    break;
                case MotionEvent.ACTION_UP:
                    Log.i("------------x", String.valueOf(x));
                    Log.i("------------y", String.valueOf(y));
                    if (x == 0 && y == 0) {
                        flag = false;
                        Log.i("false:", "这里已经UP了");
                    } else {
                        flag = true;
                        x = 0;
                        y = 0;
                    }
                    Log.i("ACTION_UP", "ACTION_UP");
                    break;
            }
            return flag;
        }
    }

    //录音和播放事件
    public class record implements View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (ContextCompat.checkSelfPermission(TakePicturePostActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(TakePicturePostActivity.this,R.string.voice_limits,Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    //录音
                    startVoice();
                    Log.i("开始录音", "开始录音");
//                    if (ContextCompat.checkSelfPermission(TakePicturePostActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions(TakePicturePostActivity.this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 300);
//                    } else {
//
//                    }
                    break;
                case MotionEvent.ACTION_UP:
                    //停止录音
                    try {
                        Log.i("ACTION_UP", "停止录音");
                        mRecorder.stop();
                        mRecorder.release();
                        mRecorder = null;
                        stopAnimation();
                    } catch (Exception e) {
                        Log.e("", "prepare() failed");
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    if (mRecorder != null) {
                        mRecorder.release();
                        mRecorder = null;
                        stopAnimation();
                    }
                    Log.i("cancel","cancel了录音");
                    break;
            }
            return true;
        }
    }

    //打开相机
    public void openCamera() {
        if (ContextCompat.checkSelfPermission(TakePicturePostActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TakePicturePostActivity.this, new String[]{android.Manifest.permission.CAMERA}, SUPPORT_CAMERA);
        }

    }

    //打开相册
    public void openPicture() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.putExtra("return-data", true);
        startActivityForResult(intent, SUPPORT_PICTURE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i("i3", String.valueOf(123445));
        if (requestCode == SUPPORT_CAMERA) {
            if (permissions[0].equals(android.Manifest.permission.CAMERA)) {
                //允许拍照
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(intent, TAKE_PICTURE);
            } else {
                //不允许拍照
            }
        }
        if (requestCode == 300) {
            if (permissions[0].equals(android.Manifest.permission.RECORD_AUDIO) && permissions[1].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //允许录音
                //startVoice();
            } else {
                //不允许录音
            }
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//            } else {
//
//            }
        }
    }

    //开始录音
    void startVoice() {
        if (mRecorder != null) {
            mRecorder.reset();
        } else {
            mRecorder = new MediaRecorder();
        }

        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setOutputFile(FileName);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRecorder.start();
        startAnimation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //拍照返回的结果
        if (requestCode == TAKE_PICTURE) {
            if (resultCode == RESULT_OK) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                baseBitmap = bitmap;
                canvasImaga.setImageBitmap(bitmap);
                backImage_flag = true;
                Log.e("---->", String.valueOf(bitmap));
            }
        }
        //返回的相册的图片
        if (requestCode == SUPPORT_PICTURE) {
            Log.e("---->", String.valueOf(SUPPORT_PICTURE));
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    baseBitmap = bitmap;
                    canvasImaga.setImageBitmap(bitmap);
                    backImage_flag = true;
                    Log.e("--->", String.valueOf(bitmap));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //改变画笔的颜色
    public class changePaintColor implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.red_btn:
                    paint.setColor(Color.argb(255, 255, 0, 0));
                    paintColor = Color.argb(255, 255, 0, 0);
                    break;
                case R.id.yellow_btn:
                    paint.setColor(Color.argb(255, 236, 213, 41));
                    paintColor = Color.argb(255, 236, 213, 41);
                    break;
                case R.id.green_btn:
                    paint.setColor(Color.argb(255, 2, 255, 221));
                    paintColor = Color.argb(255, 2, 255, 221);
                    break;
                case R.id.purple_btn:
                    paint.setColor(Color.argb(255, 121, 118, 217));
                    paintColor = Color.argb(255, 121, 118, 217);
                    break;
                case R.id.lightgreen_btn:
                    paint.setColor(Color.argb(255, 67, 236, 166));
                    paintColor = Color.argb(255, 67, 236, 166);
                    break;
                case R.id.pink_btn:
                    paint.setColor(Color.argb(255, 255, 156, 156));
                    paintColor = Color.argb(255, 255, 156, 156);
                    break;
                case R.id.lightpuple_btn:
                    paint.setColor(Color.argb(255, 228, 111, 244));
                    paintColor = Color.argb(255, 228, 111, 244);
                    break;

            }
        }
    }

    //发送按钮
    public class postAllToNet implements View.OnClickListener {
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        MediaType MEDIA_TYPE_VOICE = MediaType.parse("audio/x-m4a");

        //bitmap 转文件
        @Override
        public void onClick(View view) {
            if (baseBitmap == null) {
                Message message = Message.obtain();
                handler.sendEmptyMessage(message.what = 003);
                return;
            }
            if (ContextCompat.checkSelfPermission(TakePicturePostActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(TakePicturePostActivity.this,R.string.save_limits,Toast.LENGTH_SHORT).show();
                return;
            }
            ActivityIndicatorView.start(TakePicturePostActivity.this);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File file = new File(tempImagePath);
                    try {
                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                        baseBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                        bos.flush();
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Intent intent = getIntent();
                    OkHttpClient client = new OkHttpClient();
                    MultipartBody.Builder builder = new MultipartBody.Builder();
                    builder.setType(MultipartBody.FORM).addFormDataPart(Constant.USERID, sharedPreferences.getString(Constant.USERID, ""))
                            .addFormDataPart("text", String.valueOf(post_text.getText()))
                            .addFormDataPart("image", file.getName(), RequestBody.create(MEDIA_TYPE_PNG, new File(tempImagePath)));
                    //判断语音文件是否存在
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        File voiceFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/audiorecordtest.aac");
                        if (voiceFile.exists()) {
                            builder.addFormDataPart("voice", "audiorecordtest.aac", RequestBody.create(MEDIA_TYPE_VOICE, new File(FileName)));
                        }
                    }
                    String url = null;
                    if (questionPicture != null) {
                        //老师回答问题
                        builder.addFormDataPart(Constant.QID, id);
                        url = Constant.TEACHERCOMMITANSWERURL;
                    } else {
                        //学生发布问题
                        builder.addFormDataPart(Constant.SUBJECT, String.valueOf(intent.getIntExtra(Constant.POSTSUBJECTID, 1)))
                                .addFormDataPart(Constant.GRADE, String.valueOf(intent.getIntExtra(Constant.POSTGRADE, 1)));
                        url = Constant.POSTQUESTIONURL;
                    }

                    RequestBody requestBody = builder.build();
//                    RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
//                            .addFormDataPart(Constant.USERID,sharedPreferences.getString(Constant.USERID,""))
//                            .addFormDataPart(Constant.GRADE, String.valueOf(intent.getIntExtra(Constant.POSTGRADE,1)))
//                            .addFormDataPart(Constant.SUBJECT,String.valueOf(intent.getIntExtra(Constant.POSTSUBJECTID,1)))
//                            .addFormDataPart("text", String.valueOf(post_text.getText()))
//                            .addFormDataPart("voice","audiorecordtest.amr",RequestBody.create(MEDIA_TYPE_VOICE,new File(FileName)))
//                            .addFormDataPart("image",file.getName(),RequestBody.create(MEDIA_TYPE_PNG,new File(tempImagePath)))
//                            .build();

                    Request request = new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .build();
                    try {
                        Log.i("xxxxxxxx","xxxxxxx");
                        Response response = client.newCall(request).execute();
                        String result = response.body().string();
                        Log.i("xxxxxxxx","xxxxxxx");
                        Log.i("result", result);
                        if (result == null) {
                            Message message = Message.obtain();
                            handler.sendEmptyMessage(message.what = 2);
                            return;
                        }
                        JSONObject jsonObject = new JSONObject(result);
                        String ErrorCode = jsonObject.optString("ErrorCode");
                        if (ErrorCode.equals("000")) {
                            Message message = Message.obtain();
                            handler.sendEmptyMessage(message.what = 0001);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Message message = Message.obtain();
                        handler.sendEmptyMessage(message.what = 2);
                        Log.i("数据解析错误", "数据解析错误");
                    }

                }
            }).start();
        }
    }

    //returnBitmapFormUri
    private Bitmap returnBitmap(Uri uri) {
        Bitmap bitmap = null;
        FileBinaryResource resource = (FileBinaryResource) Fresco.getImagePipelineFactory().getMainDiskStorageCache().getResource(new SimpleCacheKey(uri.toString()));
        File file = resource.getFile();
        bitmap = BitmapFactory.decodeFile(file.getPath());
        return bitmap;
    }

    //得到控件的高度和宽度
    void getHeightAndWidth() {
        ViewTreeObserver vot = canvasImaga.getViewTreeObserver();
        vot.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                height = canvasImaga.getHeight();
                width = canvasImaga.getWidth();
                Log.i("height", String.valueOf(height));
            }
        });
    }
    //返回键

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                teacherToBack();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //老师解答和学生提问返回
            teacherToBack();
        }

        return super.onKeyDown(keyCode, event);
    }

    //老师解答返回和学生提问返回
    void teacherToBack() {
        if (questionPicture != null) {
            new android.support.v7.app.AlertDialog.Builder(this)
                    .setTitle(R.string.giveup_qustion)
                    .setMessage(R.string.sure)
                    .setPositiveButton(R.string.sure_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    SharedPreferences sharedPreferences = getSharedPreferences(Constant.fileName, MODE_PRIVATE);
                                    String userid = sharedPreferences.getString(Constant.USERID, "");
                                    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                                    Map<String, String> mapuserid = new HashMap<String, String>();
                                    mapuserid.put(Constant.USERID, userid);
                                    list.add(mapuserid);
                                    Map<String, String> mapqid = new HashMap<String, String>();
                                    mapqid.put(Constant.QID, id);
                                    list.add(mapqid);
                                    String url = Constant.TEACHERGIVEUPQUSTIONURL;
                                    String result = HttpRequest.postRequestBody(list, url);
                                    if (result == null) {
                                        return;
                                    }
                                    try {
                                        JSONObject jsonObject = new JSONObject(result);
                                        String ErrorCode = jsonObject.optString("ErrorCode");
                                        if (ErrorCode.equals("000")) {
                                            Message message = Message.obtain();
                                            handler.sendEmptyMessage(message.what = 000);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                    })
                    .setNegativeButton(R.string.sure_no, null)
                    .show();
        }
        //学生问题的返回
        else {
            if (baseBitmap != null){
                new android.support.v7.app.AlertDialog.Builder(this)
                        .setTitle(R.string.save_draft)
                        .setMessage(R.string.sure)
                        .setPositiveButton(R.string.sure_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //保存至草稿
                                insert();
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.sure_no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .show();
            }
            else {
                finish();
            }

        }
    }

    //老师查看问题
    class lookAction implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(TakePicturePostActivity.this, QuestionFormAnswerActivity.class);
            Intent intent1 = getIntent();
            QuestionItem questionItem = (QuestionItem)intent1.getSerializableExtra("questionItem");
            intent.putExtra("questionItem",(QuestionItem)questionItem);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/audiorecordtest.aac");
            if (file.exists()) {
                file.delete();
            }
        }
        if (mPlayer != null){
            mPlayer.stop();
        }
    }
    //录音动画
    public void startAnimation() {
        AnimationDrawable rocketAnimation;
        View view = LayoutInflater.from(TakePicturePostActivity.this).inflate(R.layout.record_animation_layout, null);
        ImageView imageView = (ImageView)view.findViewById(R.id.record_animation_ima);
        imageView.setBackgroundResource(R.drawable.ani_list);
        rocketAnimation = (AnimationDrawable) imageView.getBackground();
        rocketAnimation.start();
        android.support.v7.app.AlertDialog.Builder mBuilder = new android.support.v7.app.AlertDialog.Builder(TakePicturePostActivity.this, R.style.selectorDialog);
        mBuilder.setView(view);
        mDialog = mBuilder.create();
        //mDialog.setCancelable(false);
        Window win = mDialog.getWindow();

        WindowManager.LayoutParams params = win.getAttributes();
        params.alpha = 0.5f;
        params.dimAmount = 0.1f;
        params.gravity = Gravity.CENTER;
        win.setAttributes(params);
        //win.setBackgroundDrawableResource(R.mipmap.alpha);
        mDialog.show();
    }
    public void stopAnimation(){
        mDialog.dismiss();
    }
    //保存图片到sd卡
    public static void saveBitmapToFile(Bitmap bitmap, String _file)
            throws IOException {
        BufferedOutputStream os = null;
        try {
            File file = new File(_file);
            // String _filePath_file.replace(File.separatorChar +
            // file.getName(), "");
            int end = _file.lastIndexOf(File.separator);
            String _filePath = _file.substring(0, end);
            File filePath = new File(_filePath);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            file.createNewFile();
            os = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {

                }
            }
        }
    }
    //插入数据库
    void insert(){
        //创建存放数据的ContentValues对象
        ContentValues values = new ContentValues();
        Intent intent = getIntent();
        //像ContentValues中存放数据
        values.put("id", 1);
        values.put("subject",String.valueOf(intent.getIntExtra(Constant.POSTSUBJECTID, 1)));
        values.put("grade",String.valueOf(intent.getIntExtra(Constant.POSTGRADE, 1)));
        //设置sdcard的路径
        String draftPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/drafIma"+"/imga"+fetchPlacesCount()+".png";
        try {
            saveBitmapToFile(baseBitmap,draftPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        values.put("image",draftPath);
        values.put("text",post_text.getText().toString().trim());
        DatabaseHelper dbHelper3 = new DatabaseHelper(TakePicturePostActivity.this,"question_db",1);
        SQLiteDatabase db3 = dbHelper3.getWritableDatabase();
        //数据库执行插入命令
        db3.insert("user", null, values);
    }
    void query(){
        DatabaseHelper dbHelper5 = new DatabaseHelper(TakePicturePostActivity.this, "question_db",1);
        SQLiteDatabase db5 = dbHelper5.getReadableDatabase();
        //创建游标对象
        Cursor cursor = db5.query("user", new String[]{"id","subject","grade","image","text"}, "id=?", new String[]{"1"}, null, null, null, null);
        //利用游标遍历所有数据对象
        while(cursor.moveToNext()){
            String grade = cursor.getString(cursor.getColumnIndex("grade"));
            String subject = cursor.getString(cursor.getColumnIndex("subject"));
            String image = cursor.getString(cursor.getColumnIndex("image"));
            String text = cursor.getString(cursor.getColumnIndex("text"));
            //日志打印输出
            Log.i("query","grade-->"+grade);
            Log.i("query","subject-->"+subject);
            Log.i("query","image-->"+image);
            Log.i("query","text-->"+text);
        }
    }
    //表的行数
    int fetchPlacesCount() {
        DatabaseHelper dbHelper7 = new DatabaseHelper(TakePicturePostActivity.this, "question_db",1);
        SQLiteDatabase db7 = dbHelper7.getReadableDatabase();
        String sql = "SELECT COUNT(*) FROM " + "user";
        SQLiteStatement statement = db7.compileStatement(sql);
        int count = (int) statement.simpleQueryForLong();
        Log.i("count", String.valueOf(count));
        return count;
    }
    void delete(){
        DatabaseHelper databaseHelper = new DatabaseHelper(TakePicturePostActivity.this,"question_db",1);
        SQLiteDatabase db6 = databaseHelper.getWritableDatabase();
        db6.delete("user","id=?",new String[]{"1"});
    }
}
