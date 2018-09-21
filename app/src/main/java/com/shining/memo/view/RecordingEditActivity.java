package com.shining.memo.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;


import com.shining.memo.R;
import com.shining.memo.adapter.RecordingAdapter;
import com.shining.memo.model.RecordingContent;
import com.shining.memo.presenter.AudioRecordPresenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class RecordingEditActivity extends Activity implements View.OnClickListener,ViewRecord,View.OnTouchListener {

    private Button mBtnCancel,mBtnConfirm,mBtnAlarm,mBtnEdit,mBtnUrgent,mBtnRecord;
    private EditText editTitle;
    private RecyclerView mRecyclerView;
    private String filePath = "";
    private int urgent = 0;
    private int alarm = 0;
    private static boolean isplaying = false;
    private AudioRecordPresenter presenter;
    private RecordingAdapter adapter;
    private HashMap<Integer,RecordingContent> mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_edit);
        init();
        mMap = new HashMap<>();
        RecordingContent data = new RecordingContent();
        data.setType("text");
        data.setContent("123456123456123456123456123456123465612345611234561234561234561234561234561234656123456123456123456123456123456123456123456123456123465612345612345612345623456123456");
        mMap.put(0,data);
        data = new RecordingContent();
        data.setType("text");
        data.setContent("00:10  00:10 00:10");
        mMap.put(1,data);
        data = new RecordingContent();
        data.setType("audio");
        data.setContent("/storage/emulated/0/record/20180102074802.amr");
        data.setTime("00:10");
        mMap.put(2,data);
        adapter = new RecordingAdapter(mMap,this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(adapter);
    }

    private void init(){
        View view =  (View)findViewById(R.id.recording_edit_title);
        mBtnCancel = (Button)view.findViewById(R.id.title_edit_cancel);
        mBtnConfirm = (Button)view.findViewById(R.id.title_edit_confirm);
        mBtnAlarm = (Button)view.findViewById(R.id.title_edit_alarm);
        mBtnEdit = (Button)view.findViewById(R.id.title_edit_edit);
        mBtnUrgent = (Button)view.findViewById(R.id.title_edit_urgent);
        mBtnRecord = (Button)findViewById(R.id.audio_recording);
        mRecyclerView = (RecyclerView)findViewById(R.id.recording_recyclerView);
        editTitle = (EditText)findViewById(R.id.recording_title);

//      audioPresenter = new AudioPresenter(this,filePath);

        mBtnCancel.setOnClickListener(this);
        mBtnConfirm.setOnClickListener(this);
        mBtnAlarm.setOnClickListener(this);
        mBtnEdit.setOnClickListener(this);
        mBtnUrgent.setOnClickListener(this);
        mBtnRecord.setOnTouchListener(this);
        mRecyclerView.setOnClickListener(this);
    }


    public static Boolean hideInputMethod(Context context, View v) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            return imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        return false;
    }

    public static boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = { 0, 0 };
            v.getLocationInWindow(leftTop);
            int left = leftTop[0], top = leftTop[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                if(hideInputMethod(this, v)) {
                    editTitle.clearFocus();
                    return true; //隐藏键盘时，其他控件不响应点击事件
                }
            }
        }
        return super.dispatchTouchEvent(ev);
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
//                clickEdit();
                break;
            case R.id.title_edit_urgent:
                clickUrgent();
                break;
            case R.id.audio_rerecording:
                clickRerecording();
                break;
            case R.id.recording_recyclerView:
                Log.d("RecordingEditActivity","click");
                View v = View.inflate(mRecyclerView.getContext(),R.layout.item_recording_text,null);
                mRecyclerView.addView(v);
                break;

        }
    }
    private void clickCancel(){
        Intent intent = new Intent();
        intent.setClass(this,MainActivity.class);
        startActivity(intent);
    }
    private void clickConfirm(){

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

    }

    private void permissionForM() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        } else {
            presenter = new AudioRecordPresenter(this);
            presenter.startRecord();
        }
    }
    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onUpdateProgress(int progress) {

    }

    @Override
    public void onStopPlay() {
        isplaying = false;
    }

    @Override
    public void onRemoverPlay() {

    }

    @Override
    public void onUpdate(double db, long time) {

    }

    @Override
    public void onStop(String filePath) {
        int index = adapter.getCurrentIndex();
        if( index != -1){
            List<String> text =  adapter.distachText(mRecyclerView);
            
        }

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        try {
            switch (motionEvent.getAction()){

                case MotionEvent.ACTION_DOWN:
                    permissionForM();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    presenter.cancelRecord();    //取消录音（不保存录音文件）
           //         mTvTimes.setText("00:00");
           //         mBtnRecord.setText("Press here to record");

                    break;
                case MotionEvent.ACTION_UP:
                    if(motionEvent.getX() < 0 || motionEvent.getX() > view.getWidth()
                            || motionEvent.getY() < 0 || motionEvent.getY() > view.getHeight()){
                        motionEvent.setAction(MotionEvent.ACTION_CANCEL);
                        return onTouch(view,motionEvent);
                    }
                    presenter.stopRecord();
                    mBtnRecord.setText("Press here to record");
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }



}
