package com.shining.memo.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.shining.memo.R;
import com.shining.memo.model.Task;
import com.shining.memo.presenter.AudioPresenter;
import com.shining.memo.widget.RoundProgressBar;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.io.File;
import java.util.Date;

public class AudioSettingActivity extends Activity implements View.OnClickListener,ViewAudioSetting{

    private Button mBtnCancel,mBtnConfirm,mBtnAlarm,mBtnEdit,mBtnUrgent,mBtnReRecording;
    private ImageView micImage;
    private EditText editTitle;
    private String filePath = "";
    private int urgent = 0;
    private int alarm = 0;
    public static boolean isplaying = false;
    private AudioPresenter audioPresenter;
    private RoundProgressBar mRoundProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_setting);
        init();
    }


    private void init(){
        View view =  (View)findViewById(R.id.audio_view_title);
        mBtnCancel = (Button)view.findViewById(R.id.title_cancel);
        mBtnConfirm = (Button)view.findViewById(R.id.title_confirm);
        mBtnAlarm = (Button)view.findViewById(R.id.title_alarm);
        mBtnEdit = (Button)view.findViewById(R.id.title_edit);
        mBtnUrgent = (Button)view.findViewById(R.id.title_urgent);
        mBtnReRecording = (Button)findViewById(R.id.audio_rerecording);
        micImage = (ImageView)findViewById(R.id.image_mic_audio);
        editTitle = (EditText)findViewById(R.id.edit_title_audio);
        filePath = getIntent().getStringExtra("filePath");
        audioPresenter = new AudioPresenter(this,filePath);
        mRoundProgressBar = (RoundProgressBar)findViewById(R.id.roundProgress);

        mBtnCancel.setOnClickListener(this);
        mBtnConfirm.setOnClickListener(this);
        mBtnAlarm.setOnClickListener(this);
        mBtnEdit.setOnClickListener(this);
        mBtnUrgent.setOnClickListener(this);
        mBtnReRecording.setOnClickListener(this);
        micImage.setOnClickListener(this);

    }

    public void deleteFile(){
        if(!filePath.equals("")){
            File file = new File(filePath);
            if (file.exists())
                file.delete();
            filePath = "";
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onUpdateProgress(int progress) {
        mRoundProgressBar.setProgress(progress);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.title_cancel:
                clickCancel();
                break;
            case R.id.title_confirm:
                clickConfirm();
                break;
            case R.id.title_alarm:
                clickAlarm();
                break;
            case R.id.title_edit:
                clickEdit();
                break;
            case R.id.title_urgent:
                clickUrgent();
                break;
            case R.id.audio_rerecording:
                clickRerecording();
                break;
            case R.id.image_mic_audio:
                clickMicphone();
        }
    }

    private void clickCancel(){
        deleteFile();
        Intent intent = new Intent();
        intent.setClass(this,MainActivity.class);
        startActivity(intent);
    }

    private void clickConfirm(){
        Task task = new Task();
        task.setType("audio");
        task.setUrgent(urgent);
        task.setAlarm(alarm);
        String title = editTitle.getText().toString();
        task.setTitle(title);
        audioPresenter.saveAudio(task,filePath);
    }

    private void clickAlarm(){
        Intent alarmIntent = new Intent(this, AlarmActivity.class);
        startActivityForResult(alarmIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case 1:
                if (resultCode == RESULT_OK){
                    String year = data.getStringExtra("year");
                    Log.d("dsafas", year);
                }
                break;
            default:
        }
    }

    private void clickEdit(){

    }

    private void clickUrgent(){
        if (urgent == 0){
            urgent = 1;
            mBtnUrgent.setActivated(true);
        }else {
            urgent = 0;
            mBtnUrgent.setActivated(false);
        }
    }

    private void clickRerecording(){
        deleteFile();
        Intent intent = new Intent();
        intent.setClass(this,AudioRecordingActivity.class);
        startActivity(intent);
    }



    private void clickMicphone(){
        if(!isplaying)
        {
            audioPresenter.doPlay();
            isplaying = true;
        }
        else{
            audioPresenter.onPausePlay();
            isplaying = false;
        }
    }


}
