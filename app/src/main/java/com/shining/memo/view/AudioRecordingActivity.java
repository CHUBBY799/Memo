package com.shining.memo.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.shining.memo.R;
import com.shining.memo.presenter.AudioPresenter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AudioRecordingActivity extends Activity implements ViewAudioRecording,View.OnTouchListener,View.OnClickListener {

    private Button mBtnRecord,mBtnBack;
    private TextView mTvTimes;
    private ImageView mIvVolume;
    private AudioPresenter audioPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recording);
        init();
    }


    private void init(){
        mBtnRecord = (Button)findViewById(R.id.recording_btn);
        mTvTimes = (TextView)findViewById(R.id.recording_times);
        mIvVolume = (ImageView)findViewById(R.id.recording_mic);
        mBtnBack = (Button)findViewById(R.id.recording_back);
        mBtnRecord.setOnTouchListener(this);
        mBtnBack.setOnClickListener(this);
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        try {
            switch (motionEvent.getAction()){

                case MotionEvent.ACTION_DOWN:
                    permissionForM();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    audioPresenter.cancelRecord();    //取消录音（不保存录音文件）
                    mTvTimes.setText("00:00");
                    mBtnRecord.setText("Press here to record");
                    break;
                case MotionEvent.ACTION_UP:
                    if(motionEvent.getX() < 0 || motionEvent.getX() > view.getWidth()
                            || motionEvent.getY() < 0 || motionEvent.getY() > view.getHeight()){
                        motionEvent.setAction(MotionEvent.ACTION_CANCEL);
                        return onTouch(view,motionEvent);
                    }
                    audioPresenter.stopRecord();
                    mBtnRecord.setText("Press here to record");
                    break;
            }
        }catch (Exception e){
            Log.d("Exception",e.getMessage());
        }
        return true;
    }

    private void permissionForM() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        } else {
            audioPresenter = new AudioPresenter(this);
            mBtnRecord.setText("Release button to save");
            audioPresenter.startRecord();
        }

    }


    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onUpdate(double db, long time) {
        mIvVolume.getDrawable().setLevel((int)(db / 10));
        Date date = new Date(time);
        SimpleDateFormat sd = new SimpleDateFormat("mm:ss");
        mTvTimes.setText(sd.format(date));
    }


    @Override
    public void onStop(String filePath) {
        mTvTimes.setText("00:00");
        Intent intent = new Intent();
        intent.setClass(this,AudioEditActivity.class);
        intent.putExtra("filePath",filePath);
        startActivity(intent);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.recording_back:
                Intent intent = new Intent();
                intent.setClass(this,MainActivity.class);
                startActivity(intent);
                break;
        }
    }
}
