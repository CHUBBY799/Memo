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
import android.widget.Toast;

import com.shining.memo.R;
import com.shining.memo.model.Task;
import com.shining.memo.model.TaskImpl;
import com.shining.memo.model.TaskModel;
import com.shining.memo.presenter.AudioPresenter;
import com.shining.memo.widget.RoundProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

public class AudioEditActivity extends Activity implements View.OnClickListener,ViewAudioEdit {

    private Button mBtnCancel,mBtnConfirm,mBtnAlarm,mBtnEdit,mBtnUrgent,mBtnReRecording;
    private ImageView micImage;
    private EditText editTitle;
    private String filePath = "";
    private int urgent = 0;
    private int alarm = 0;
    public  static boolean isplaying = false;
    private AudioPresenter audioPresenter;
    private RoundProgressBar mRoundProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_edit);
        init();
        if(savedInstanceState != null){
            alarm = savedInstanceState.getInt("alarm");
            urgent = savedInstanceState.getInt("urgent");
            isplaying = savedInstanceState.getBoolean("isplaying");
            if(isplaying)
                audioPresenter.doPlay();
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(isplaying)
            audioPresenter.onPausePlay();
        outState.putInt("urgent",urgent);
        outState.putInt("alarm",alarm);
        outState.putString("filePath",filePath);
        outState.putBoolean("isplaying",isplaying);
    }
    private void init(){
        View view =  (View)findViewById(R.id.audio_edit_title);
        mBtnCancel = (Button)view.findViewById(R.id.title_edit_cancel);
        mBtnConfirm = (Button)view.findViewById(R.id.title_edit_confirm);
        mBtnAlarm = (Button)view.findViewById(R.id.title_edit_alarm);
        mBtnEdit = (Button)view.findViewById(R.id.title_edit_edit);
        mBtnUrgent = (Button)view.findViewById(R.id.title_edit_urgent);
        mBtnReRecording = (Button)findViewById(R.id.audio_rerecording);
        micImage = (ImageView)findViewById(R.id.image_mic_audio);
        editTitle = (EditText)findViewById(R.id.list_title);
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
    public void onStopPlay() {
        isplaying = false;
    }
    @Override
    public void onRemoverPlay() {
        mRoundProgressBar.setProgress(0);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.title_edit_cancel:
                clickCancel();
                break;
            case R.id.title_edit_confirm:
                clickConfirm();
                break;
            case R.id.title_edit_alarm:
                clickAlarm();
                break;
            case R.id.title_edit_edit:
                try {
                    clickEdit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.title_edit_urgent:
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
        if(audioPresenter.saveAudio(task,filePath)){
            Toast.makeText(this,"save successful",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setClass(this, MainActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(this,"save failed",Toast.LENGTH_SHORT).show();
        }
        audioPresenter.setMediaPlayerNull();
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
                    alarm = 1;
                }
                break;
            default:
                alarm = 0;
                break;
        }
    }
    private void clickEdit() throws JSONException {
        TaskModel t = new TaskImpl(this);
        List<JSONObject> list = t.getNotAlarmTasksByUrgentDesc(0);
        System.out.println(list.size());
        for(int i = 0; i < list.size(); i++){
            System.out.println(list.get(i).getString("title"));
            System.out.println(list.get(i).getString("type"));
        }
        Intent intent = new Intent();
        intent.setClass(this,AudioViewActivity.class);
        intent.putExtra("filePath",filePath);
        startActivity(intent);
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
//        Intent intent = new Intent();
//        intent.setClass(this,AudioRecordingActivity.class);
//        startActivity(intent);
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
