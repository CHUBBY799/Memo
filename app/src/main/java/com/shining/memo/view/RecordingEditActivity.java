package com.shining.memo.view;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.shining.memo.R;
import com.shining.memo.adapter.RecordingAdapter;
import com.shining.memo.model.RecordingContent;
import com.shining.memo.model.Task;
import com.shining.memo.presenter.AudioRecordPresenter;
import com.shining.memo.presenter.PhotoPresenter;
import com.shining.memo.presenter.RecordingPresenter;
import com.shining.memo.presenter.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class RecordingEditActivity extends Activity implements View.OnClickListener,ViewRecord, RecordingAdapter.TextChanged,Switch.OnCheckedChangeListener {

    private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 0x03;
    private static final int REQUEST_CAMERA=0xa1;
    private static final int REQUEST_GALLERY=0xa3;
    private Button mBtnCancel,mBtnConfirm,mBtnAlarm,mBtnEdit,mBtnRecord,mBtnPhoto,mBtnFinish,mBtnGallery,mBtnCamera;
    private Switch mSwitchUrgent;
    private TextView mTvTime;
    private EditText editTitle;
    private RecyclerView mRecyclerView;
    private static boolean isRecording = false;
    private String photoPath="";
    private int urgent = 0;
    private int alarm = 0;
    private AudioRecordPresenter presenter;
    private RecordingAdapter adapter;
    private HashMap<Integer,RecordingContent> mMap;
    private RecordingPresenter recordingPresenter;
    private PhotoPresenter photoPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_edit);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        init();
        presenter = new AudioRecordPresenter(this);
        photoPresenter = new PhotoPresenter(this);
        recordingPresenter = new RecordingPresenter(this);
        mMap = new HashMap<>();
//        RecordingContent data = new RecordingContent();
//        data.setType("text");
//        data.setContent("123456123456123456123456123456123465612345611234561234561234561234561234561234656123456123456123456123456123456123456123456123456123465612345612345612345623456123456");
//        mMap.put(0,data);
//        data = new RecordingContent();
//        data.setType("text");
//        data.setContent("00:10  00:10 00:10");
//        mMap.put(1,data);
//        data = new RecordingContent();
//        data.setType("audio");
//        data.setContent("/storage/emulated/0/record/20180107064032.amr");
//        mMap.put(2,data);
//        data = new RecordingContent();
//        data.setType("photo");
//        data.setContent("/storage/emulated/0/photo/20180109234824.jpg");
//        mMap.put(3,data);
//        data = new RecordingContent();
//        data.setType("text");
//        data.setContent("");
//        mMap.put(4,data);
        adapter = new RecordingAdapter(mMap,this,this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setItemAnimator(null);
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
        if(isRecording)
            animationTranslate(findViewById(R.id.bottom_recording_audio),findViewById(R.id.bottom_recording_edit),500);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(presenter.stopRecord() > 0)
            isRecording = true;
        else
            isRecording = false;
    }

    private void init(){
        View view =  (View)findViewById(R.id.bottom_recording_edit);
        mBtnCancel = (Button)view.findViewById(R.id.bottom_cancel);
        mBtnConfirm = (Button)view.findViewById(R.id.bottom_confirm);
        mBtnAlarm = (Button)view.findViewById(R.id.bottom_alarm);
        mBtnEdit = (Button)view.findViewById(R.id.bottom_textedit);
        view =  (View)findViewById(R.id.bottom_recording_audio);
        mBtnFinish = (Button)view.findViewById(R.id.audio_finish);
        mTvTime = (TextView)view.findViewById(R.id.audio_time_duration);
        view =  (View)findViewById(R.id.bottom_recording_photo);
        mBtnGallery = (Button)view.findViewById(R.id.photo_gallery);
        mBtnCamera = (Button)view.findViewById(R.id.photo_camera);

        mSwitchUrgent = (Switch)findViewById(R.id.bottom_urgent);
        mBtnRecord = (Button)findViewById(R.id.bottom_audio);
        mBtnPhoto = (Button)findViewById(R.id.bottom_photo);
        mRecyclerView = (RecyclerView)findViewById(R.id.recording_recyclerView);
        editTitle = (EditText)findViewById(R.id.recording_title);
        mBtnCancel.setOnClickListener(this);
        mBtnConfirm.setOnClickListener(this);
        mBtnAlarm.setOnClickListener(this);
        mBtnEdit.setOnClickListener(this);
        mBtnRecord.setOnClickListener(this);
        mBtnPhoto.setOnClickListener(this);
        mBtnFinish.setOnClickListener(this);
        mBtnCamera.setOnClickListener(this);
        mBtnGallery.setOnClickListener(this);
        mSwitchUrgent.setOnCheckedChangeListener(this);
    }

    private void initRecycleView(){
        String string = "";
        if(mMap.isEmpty()) {
            RecordingContent content = new RecordingContent();
            content.setType("text");
            content.setColor("#000000");
            content.setContent(string);
            mMap.put(mMap.size(),content);
            adapter.notifyDataSetChanged();
        }
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
            case R.id.bottom_cancel:
                clickCancel();
                break;
            case R.id.bottom_confirm:
                clickConfirm();
                break;
            case R.id.bottom_alarm:
                clickAlarm();
                break;
            case R.id.bottom_textedit:
//                clickEdit();
                break;
            case R.id.bottom_audio:
                clickRerecording();
                break;
            case R.id.audio_finish:
                clickFinish();
                break;
            case R.id.bottom_photo:
                cliclPhotoRecording();
                break;
            case R.id.photo_gallery:
                photoPresenter.openAlbum(this,REQUEST_GALLERY);
                break;
            case R.id.photo_camera:
                photoPath = photoPresenter.takePicture(this,REQUEST_CAMERA);
                break;
        }
    }
    private void clickCancel(){
        Intent intent = new Intent();
        intent.setClass(this,MainActivity.class);
        startActivity(intent);
    }
    private void clickConfirm(){
        Task task = new Task();
        task.setType(taskType());
        task.setUrgent(urgent);
        task.setAlarm(alarm);
        String title = editTitle.getText().toString();
        task.setTitle(title);
        if(recordingPresenter.saveRecording(task,mMap)){
            Toast.makeText(this,"save successful",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setClass(this, MainActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(this,"save failed",Toast.LENGTH_SHORT).show();
        }
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
            case REQUEST_CAMERA:
                animationTranslate(findViewById(R.id.bottom_recording_photo),findViewById(R.id.bottom_recording_edit),500);
                if(resultCode == RESULT_OK && !photoPath.equals("")){
                    Log.d("uri", photoPath.toString());
                    onStop(photoPath,"photo");
                }else {
                    photoPath = "";
                }
                break;
            case REQUEST_GALLERY:
                animationTranslate(findViewById(R.id.bottom_recording_photo),findViewById(R.id.bottom_recording_edit),500);
                if(resultCode == RESULT_OK){
                    Uri uri = data.getData();
                    if(uri != null){
                        try{
                            String[] filePathColumns = {MediaStore.Images.Media.DATA};
                            Cursor c = getContentResolver().query(uri, filePathColumns, null, null, null);
                            c.moveToFirst();
                            int columnIndex = c.getColumnIndex(filePathColumns[0]);
                            String imagePath = c.getString(columnIndex);
                            c.close();
                            Intent intent = new Intent();
                            intent.setClass(this,PhotoConfirmActivity.class);
                            intent.putExtra("photoPath",imagePath);
                            startActivityForResult(intent,0xa5);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case 0xa5:
                if(resultCode == RESULT_OK){
                    String imagePath = data.getStringExtra("photoPath");
                    onStop(imagePath,"photo");
                }else {
                    photoPresenter.openAlbum(this,REQUEST_GALLERY);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (urgent == 0){
            urgent = 1;
            ToastUtils.showShort(this,urgent+"");
            int usedHeight = 0;
        }else {
            urgent = 0;
            ToastUtils.showShort(this,urgent+"");
        }
    }

    private void clickRerecording(){
        try {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)
                        && shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    ToastUtils.showShort(this, "您已经拒绝过一次");
                }
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                presenter.startRecord();
                animationTranslate(findViewById(R.id.bottom_recording_edit),findViewById(R.id.bottom_recording_audio),500);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void clickFinish(){
        presenter.stopRecord();
        animationTranslate(findViewById(R.id.bottom_recording_audio),findViewById(R.id.bottom_recording_edit),500);
    }


    public void animationTranslate(final View oldView, final View newView, final int duration){
        Animator animator = ViewAnimationUtils.createCircularReveal(oldView,0,oldView.getHeight()/2,oldView.getWidth(),0);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(duration);
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                oldView.setVisibility(View.GONE);
                newView.setVisibility(View.VISIBLE);
                Animator animator = ViewAnimationUtils.createCircularReveal(newView,0,newView.getHeight()/2,0,newView.getWidth());
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.setDuration(duration);
                animator.start();
            }
        });
    }


    private void cliclPhotoRecording(){
        if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                ||checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                ToastUtils.showShort(this, "您已经拒绝过一次");
            }
            requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else {
            animationTranslate(findViewById(R.id.bottom_recording_edit),findViewById(R.id.bottom_recording_photo),500);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CAMERA_PERMISSIONS_REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                }
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onUpdate(double db, long time) {
//        mIvVolume.getDrawable().setLevel((int)(db / 10));
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        mTvTime.setText(sdf.format(new Date(time)));
    }

    @Override
    public void onStop(String filePath, String type){
        int index = adapter.getCurrentIndex();
        String currentType = adapter.getCurrentType();
        if( index != -1){
            HashMap<Integer,RecordingContent> map = null;
            if(currentType.equals("text")){
                List<String> text =  adapter.distachText(mRecyclerView);
                map = recordingPresenter.insertRecording(mMap,text,index,filePath,type);
            }else{
                map = recordingPresenter.insertRecording(mMap,filePath,index,currentType,type);
            }
            mMap.clear();
            mMap.putAll(map);
            checkDefaultEditTex();
            adapter.setRequestFocusableArgs(RecordingPresenter.insertIndex,0,"end");
            adapter.notifyItemRangeChanged(index,mMap.size() - index);
            if(type.equals("photo"))
                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.requestFocusable(mRecyclerView);
                    }
                });
        }else{
            recordingPresenter.insertRecording(mMap,filePath,type);
            adapter.notifyItemRangeChanged(mMap.size() - 1,mMap.size());
        }
    }

    private void checkDefaultEditTex(){
        if(mMap.size() -1 >= 0){
            if(mMap.get(mMap.size() - 1).getType() != "text"){
                RecordingContent content = new RecordingContent();
                content.setType("text");
                content.setColor(mMap.get(mMap.size() - 1).getColor());
                content.setContent("");
                mMap.put(mMap.size(),content);
            }
        }
    }

    @Override
    public int getCurrentFirstIndex() {
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
        return linearManager.findFirstVisibleItemPosition();
    }


    @Override
    public HashMap<Integer, RecordingContent> getMap() {
        return mMap;
    }

    @Override
    public void deleteEditText(HashMap<Integer, RecordingContent> map,int index,int position,String type) {
        adapter.notifyItemRemoved(index);
        checkDefaultEditTex();
        if (index - 1 >= 0) {
            adapter.setRequestFocusableArgs(index - 1,position,type);
            adapter.notifyItemRangeChanged(index - 1, mMap.size());

        } else {
            adapter.setRequestFocusableArgs(index,position,type);
            adapter.notifyItemRangeChanged(0, mMap.size());
        }
    }

    @Override
    public void updateAdapter(int index) {
        adapter.notifyItemRangeChanged(index,mMap.size() - index);
    }

    @Override
    public void recyclerViewFocusable() {
        mRecyclerView.requestFocus();
    }

    @Override
    public void TextChanged(String text,int index) {
        mMap.get(index).setContent(text);
//        Log.d("TextChanged", "TextChanged: "+ mMap.toString());
    }

    private String taskType(){
        for(int i = 0; i < mMap.size(); i++){
            if(mMap.get(i).getType() == "audio")
                return "audio";
        }
        return "text";
    }


}
