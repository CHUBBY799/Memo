package com.shining.memo.view;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.shining.memo.R;

public class AlarmClockActivity extends Activity{
    private static final int MSG_ALARM = 0xa1;
    private MediaPlayer mediaPlayer;
    private Button mBtnOk;
    private TextView mTvTitle,mTvTime;
    private String time,title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // hide title
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        winParams.flags |= (WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.alarm_notice);
        setFinishOnTouchOutside(false);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        init();
        startMedia();
    }

    private void init(){
        mBtnOk = (Button)findViewById(R.id.alarm_ok);
        mTvTitle = (TextView)findViewById(R.id.alarm_title);
        mTvTime = (TextView)findViewById(R.id.alarm_time);
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer != null){
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    mHandler.removeMessages(MSG_ALARM);
                }
                finish();
            }
        });
        try{
            time = getIntent().getStringExtra("time");
            title = getIntent().getStringExtra("title");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();
        mTvTime.setText(time);
        Spanned spanned = Html.fromHtml(title,Html.FROM_HTML_MODE_COMPACT);
        if(spanned.length() > 0)
            mTvTitle.setText(spanned.subSequence(0,spanned.length() - 1));
        else
            mTvTitle.setText("No title!");
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    private void startMedia() {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(this,RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)); //铃声类型为默认闹钟铃声
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
            mHandler.sendEmptyMessageDelayed(MSG_ALARM,25000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            if(mediaPlayer != null){
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
    };

}
