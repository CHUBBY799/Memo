package com.shining.memo.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
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
import com.shining.memo.presenter.AudioPlayPresenter;
import com.shining.memo.presenter.AudioRecordPresenter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;


public class RecordingEditActivity extends Activity implements View.OnClickListener,ViewRecord,View.OnTouchListener, RecordingAdapter.TextChanged {

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
        data.setContent("/storage/emulated/0/record/20180107064032.amr");
        mMap.put(2,data);
        adapter = new RecordingAdapter(mMap,this,this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                initRecycleView();
            }
        });
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
        mBtnCancel.setOnClickListener(this);
        mBtnConfirm.setOnClickListener(this);
        mBtnAlarm.setOnClickListener(this);
        mBtnEdit.setOnClickListener(this);
        mBtnUrgent.setOnClickListener(this);
        mBtnRecord.setOnTouchListener(this);
        mRecyclerView.setOnClickListener(this);
    }

    private static int number = -1,tagIndex = -1;
    private void initRecycleView(){
        int totalHeight = mRecyclerView.getHeight();
        View view = (View)findViewById(R.id.lineHeight);
        EditText editText = (EditText)view.findViewById(R.id.item_editText);
        int lineHeight = editText.getLineHeight();
        int usedHeight = 0;
        for(int i = 0; i < mMap.size(); i++){
            usedHeight += mRecyclerView.getChildAt(i).getHeight();
        }
        number = (int)Math.ceil((totalHeight - usedHeight)/ lineHeight);
        String string = "";
        for(int i = 0; i < number; i++)
            string += "\n";
        tagIndex = mMap.size();
        RecordingContent content = new RecordingContent();
        content.setType("text");
        content.setColor("#000000");
        content.setContent(string);
        mMap.put(mMap.size(),content);
        adapter.notifyDataSetChanged();
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
            int usedHeight = 0;
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
            mBtnRecord.setText("Release button to save");
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
    public void onStop(String filePath){
        Log.d("EDG", "onStop: "+ filePath);
        int index = adapter.getCurrentIndex();
        if( index != -1){
            List<String> text =  adapter.distachText(mRecyclerView);
            HashMap<Integer,RecordingContent> map= presenter.insertAudioRecording(mMap,text,index,filePath);
            mMap.clear();
            mMap.putAll(map);
            Log.d("RecordingAdapter", mMap.toString());
            adapter.notifyItemRangeChanged(index,mMap.size() - index);

            if(AudioRecordPresenter.insertIndex + 1 < mMap.size() && mMap.get(AudioRecordPresenter.insertIndex + 1).getType().equals("text")){
                 mRecyclerView.post(new Runnable() {
                     @Override
                     public void run() {
                         requestFocusable(AudioRecordPresenter.insertIndex + 1,0,"first");
                     }
                 });
            }
        }else{
            presenter.insertAudioRecording(mMap,filePath,number);
            adapter.notifyItemRangeChanged(mMap.size() - 1,mMap.size());
        }
    }

    private void test(){
        for(int i=0; i<mRecyclerView.getChildCount();i++){
            View v = mRecyclerView.getChildAt(i);
            try{
                RecordingAdapter.TextViewHolder holder = (RecordingAdapter.TextViewHolder)
                        mRecyclerView.getChildViewHolder(mRecyclerView.getChildAt(i));
                Log.d("TAG", "test: " + holder.editText.getText().toString());
            }catch(Exception e){
                RecordingAdapter.AudioViewHolder holder = (RecordingAdapter.AudioViewHolder)
                        mRecyclerView.getChildViewHolder(mRecyclerView.getChildAt(i));
                Log.d("TAG", "test: " + holder.button.getText().toString());
            }

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

    @Override
    public int getCurrentFirstIndex() {
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
        return linearManager.findFirstVisibleItemPosition();
    }

    @Override
    public void requestFocusable(int index,int  position, String type) {
        if(index >= 0){
            test();
            mRecyclerView.requestFocus();
            mRecyclerView.getChildAt(index - getCurrentFirstIndex()).requestFocus();
            if( mMap.get(index).getType() == "text"){
                Log.d("TAG", "requestFocusable: " + (index - getCurrentFirstIndex()) +"--" + index + "--" + getCurrentFirstIndex());
                RecordingAdapter.TextViewHolder holder = (RecordingAdapter.TextViewHolder)
                        mRecyclerView.getChildViewHolder(mRecyclerView.getChildAt(index - getCurrentFirstIndex()));
                if(type.equals("end"))
                    holder.editText.setSelection(holder.editText.getText().toString().length());
                else if(type.equals("specific"))
                    holder.editText.setSelection(position);
                else
                    holder.editText.setSelection(0);
            }
        }
    }

    @Override
    public HashMap<Integer, RecordingContent> getMap() {
        return mMap;
    }

    @Override
    public void deleteEditText(HashMap<Integer, RecordingContent> map,final int index, final int position, final String type) {
        adapter.notifyItemRemoved(index);
        adapter.notifyItemRangeChanged(index - 1,mMap.size());
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                requestFocusable(index - 1,position,type);
            }
        });
    }

    @Override
    public void TextChanged(String text,int index) {
        mMap.get(index).setContent(text);
        Log.d("TextChanged", "TextChanged: "+ mMap.toString());
    }
    @Override
    public void DefaultEditText(int number, int index) {
        this.number = number;
        tagIndex = index;
    }

    @Override
    public int getDefaultNumber() {
        return number;
    }

}
