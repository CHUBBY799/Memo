package com.shining.memo.view;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.shining.memo.R;
import com.shining.memo.adapter.RecordingAdapter;
import com.shining.memo.model.Alarm;
import com.shining.memo.model.RecordingContent;
import com.shining.memo.model.Task;
import com.shining.memo.presenter.AudioRecordPresenter;
import com.shining.memo.presenter.PhotoPresenter;
import com.shining.memo.presenter.RecordingPresenter;
import com.shining.memo.utils.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class RecordingEditActivity extends Activity implements View.OnClickListener,ViewRecord, RecordingAdapter.TextChanged,Switch.OnCheckedChangeListener{
    private final static  String TAG = "RecordingEditActivity";
    private static final int MSG_RECORDING = 0x110;
    private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 0x03;
    private static final int REQUEST_ALARM=0xb3;
    private static final int REQUEST_CAMERA=0xa1;
    private static final int REQUEST_GALLERY=0xa3;
    private Button mBtnCancel,mBtnConfirm,mBtnAlarm,mBtnEdit,mBtnRecord,mBtnPhoto,mBtnAudioCancel,mBtnFinish,mBtnGallery,mBtnCamera;
    private Button mBtnFont,mBtnLine,mBtnFontSize,mBtnColor;
    private Switch mSwitchUrgent;
    private TextView mTvTime;
    private EditText editTitle;
    private RecyclerView mRecyclerView;
    private static boolean isRecording = false,isPhotoChoosing = false,isTextEdit = false;
    private String photoPath="";
    private int urgent = 1;
    private int alarm = 0;
    private Alarm alarmObject;
    private AudioRecordPresenter presenter;
    private RecordingAdapter adapter;
    private HashMap<Integer,RecordingContent> mMap;
    private RecordingPresenter recordingPresenter;
    private PhotoPresenter photoPresenter;
    private PopupWindow mFontPopupWindow;
    private OnTextClick onTextClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_edit);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        init();
        presenter = new AudioRecordPresenter(this);
        photoPresenter = new PhotoPresenter(this);
        recordingPresenter = new RecordingPresenter(this);
        onTextClick = new OnTextClick();
        mMap = new HashMap<>();
        adapter = new RecordingAdapter(mMap,this,this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                initRecycleView();
            }
        });
        if(isRecording) {
            animationTranslate(findViewById(R.id.bottom_recording_audio),findViewById(R.id.bottom_recording_edit),500);
            isRecording = false;
        }
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
        if(presenter.stopRecord() > 0)
            isRecording = true;
        else
            isRecording = false;
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");
        if(isPhotoChoosing){
            isPhotoChoosing = false;
            animationTranslate(findViewById(R.id.bottom_recording_photo),findViewById(R.id.bottom_recording_edit),500);
        }else if(isRecording){
            presenter.cancelRecord();
            handler.removeMessages(MSG_RECORDING);
            isRecording = false;
            animationTranslate(findViewById(R.id.bottom_recording_audio),findViewById(R.id.bottom_recording_edit),500);
        }else if(isTextEdit){
            isTextEdit = false;
            animationTranslate(findViewById(R.id.bottom_recording_textedit),findViewById(R.id.bottom_recording_edit),500);
        }else{
            super.onBackPressed();
        }
    }

    private void init(){
        Log.d(TAG, "init: ");
        View view =  (View)findViewById(R.id.bottom_recording_edit);
        mBtnCancel = (Button)view.findViewById(R.id.bottom_cancel);
        mBtnConfirm = (Button)view.findViewById(R.id.bottom_confirm);
        mBtnAlarm = (Button)view.findViewById(R.id.bottom_alarm);
        mBtnEdit = (Button)view.findViewById(R.id.bottom_textedit);
        view =  (View)findViewById(R.id.bottom_recording_audio);
        mBtnAudioCancel = (Button)view.findViewById(R.id.audio_cancel);
        mBtnFinish = (Button)view.findViewById(R.id.audio_finish);
        mTvTime = (TextView)view.findViewById(R.id.audio_time_duration);
        view =  (View)findViewById(R.id.bottom_recording_photo);
        mBtnGallery = (Button)view.findViewById(R.id.photo_gallery);
        mBtnCamera = (Button)view.findViewById(R.id.photo_camera);

        view =  (View)findViewById(R.id.bottom_recording_textedit);
        mBtnFont = (Button)view.findViewById(R.id.textedit_font);
        mBtnLine = (Button)view.findViewById(R.id.textedit_line);
        mBtnFontSize = (Button)view.findViewById(R.id.textedit_fontsize);
        mBtnColor = (Button)view.findViewById(R.id.textedit_color);

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
        mBtnAudioCancel.setOnClickListener(this);
        mBtnFinish.setOnClickListener(this);
        mBtnCamera.setOnClickListener(this);
        mBtnGallery.setOnClickListener(this);
        mSwitchUrgent.setOnCheckedChangeListener(this);

        mBtnFont.setOnClickListener(this);
        mBtnLine.setOnClickListener(this);
        mBtnFontSize.setOnClickListener(this);
        mBtnColor.setOnClickListener(this);
    }

    private void initRecycleView(){
        Log.d(TAG, "initRecycleView: ");
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
        Log.d(TAG, "hideInputMethod: ");
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            return imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        return false;
    }

    public static boolean isShouldHideInput(View v, MotionEvent event) {
        Log.d(TAG, "isShouldHideInput: ");
        if (v != null) {
            int[] leftTop = { 0, 0 };
            v.getLocationInWindow(leftTop);
            int left = leftTop[0], top = leftTop[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                    return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = findViewById(R.id.linearLayout);
            if (!isShouldHideInput(v, ev) && (isPhotoChoosing)) {
                isPhotoChoosing = false;
                animationTranslate(findViewById(R.id.bottom_recording_photo), findViewById(R.id.bottom_recording_edit), 500);
                return true;
            }
            else if (isShouldHideInput(mRecyclerView, ev)) {
                try {
                    v = mRecyclerView.getChildAt(getCurrentLastIndex());
                    int[] leftTop = { 0, 0 };
                    v.getLocationInWindow(leftTop);
                    int left = leftTop[0], top = leftTop[1], bottom = top + v.getHeight(), right = left
                            + v.getWidth();
                    if (ev.getX() > left && ev.getX() < right &&  ev.getY() < bottom){
                        return super.dispatchTouchEvent(ev);
                    }else {
                        RecordingAdapter.TextViewHolder holder = adapter.getTextViewHolder(mRecyclerView);
                        holder.editText.requestFocus();
                        holder.editText.setSelection(holder.editText.getText().length());
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.showSoftInput(holder.editText, 0);
                        return true;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: ");
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
                clickEdit();
                break;
            case R.id.bottom_audio:
                clickRerecording();
                break;
            case R.id.audio_cancel:
                clickAudioCancel();
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
            case R.id.textedit_font:
                upPopwindow(mBtnFont,120,220,"font");
                break;
            case R.id.textedit_line:
                upPopwindow(mBtnLine,120,110,"line");
                break;
            case R.id.textedit_fontsize:
                upPopwindow(mBtnFontSize,120,110,"fontsize");
                break;
            case R.id.textedit_color:
                upPopwindow(mBtnColor,120,385,"color");
                break;

        }
    }
    private void upPopwindow(View btnPop,int width,int height,String type) {
        View contentView = null;
        switch(type){
            case "font":
                contentView = LayoutInflater.from(this).inflate(R.layout.textedit_font, null);
                contentView.findViewById(R.id.textedit_bold).setOnClickListener(onTextClick);
                contentView.findViewById(R.id.textedit_italy).setOnClickListener(onTextClick);
                contentView.findViewById(R.id.textedit_italy_bold).setOnClickListener(onTextClick);
                contentView.findViewById(R.id.textedit_normal).setOnClickListener(onTextClick);
                break;
            case "line":
                contentView = LayoutInflater.from(this).inflate(R.layout.textedit_line, null);
                contentView.findViewById(R.id.textedit_deleteline).setOnClickListener(onTextClick);
                contentView.findViewById(R.id.textedit_underline).setOnClickListener(onTextClick);
                break;
            case "fontsize":
                contentView = LayoutInflater.from(this).inflate(R.layout.textedit_fontsize, null);
                contentView.findViewById(R.id.textedit_increase).setOnClickListener(onTextClick);
                contentView.findViewById(R.id.textedit_decrease).setOnClickListener(onTextClick);
                break;
            case "color":
                contentView = LayoutInflater.from(this).inflate(R.layout.textedit_color, null);
                contentView.findViewById(R.id.textedit_color_red).setOnClickListener(onTextClick);
                contentView.findViewById(R.id.textedit_color_orange).setOnClickListener(onTextClick);
                contentView.findViewById(R.id.textedit_color_blue).setOnClickListener(onTextClick);
                contentView.findViewById(R.id.textedit_color_yellow).setOnClickListener(onTextClick);
                contentView.findViewById(R.id.textedit_color_purple).setOnClickListener(onTextClick);
                contentView.findViewById(R.id.textedit_color_green).setOnClickListener(onTextClick);
                contentView.findViewById(R.id.textedit_color_black).setOnClickListener(onTextClick);
                break;
        }
        mFontPopupWindow = new PopupWindow(contentView, width, height);  // 尽量写成数值，要不然左右弹出的位置不好控制
        mFontPopupWindow.setAnimationStyle(R.style.anim);
        ColorDrawable dw = new ColorDrawable(this.getResources().getColor(R.color.item_btn_backgroud));
        mFontPopupWindow.setOutsideTouchable(true);
        mFontPopupWindow.setBackgroundDrawable(dw);
        mFontPopupWindow.setOutsideTouchable(true);
        mFontPopupWindow.setFocusable(true);
        int popupWidth = mFontPopupWindow.getWidth();
        int btnWidth = btnPop.getWidth();
        mFontPopupWindow.showAsDropDown(btnPop, (btnWidth - popupWidth) / 2, 0);
    }

    private void clickCancel(){
        Log.d(TAG, "clickCancel: ");
        Intent intent = new Intent();
        intent.setClass(this,MainActivity.class);
        startActivity(intent);
    }
    private void clickConfirm(){
        Log.d(TAG, "clickConfirm: ");
        Task task = new Task();
        task.setType(taskType());
        task.setUrgent(urgent);
        task.setAlarm(alarm);
        String title = editTitle.getText().toString();
        task.setTitle(title);
        if(recordingPresenter.saveRecording(task,mMap,alarmObject)){
            Toast.makeText(this,"save successful",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setClass(this, MainActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(this,"save failed",Toast.LENGTH_SHORT).show();
        }
    }
    private void clickEdit(){
        isTextEdit = true;
        animationTranslate(findViewById(R.id.bottom_recording_edit),findViewById(R.id.bottom_recording_textedit),500);
    }
    private void clickAlarm(){
        Log.d(TAG, "clickAlarm: ");
        Intent alarmIntent = new Intent(this, AlarmActivity.class);
        if(alarm == 1){
            alarmIntent.putExtra("date",alarmObject.getDate());
            alarmIntent.putExtra("time",alarmObject.getTime());
            alarmIntent.putExtra("pop",alarmObject.getPop());
            alarmIntent.putExtra("ringtone",0);
            alarmIntent.putExtra("alarm",1);
        }
        startActivityForResult(alarmIntent, REQUEST_ALARM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d(TAG, "onActivityResult: ");
        switch (requestCode){
            case REQUEST_ALARM:
                if (resultCode == RESULT_OK){
                    if(alarm == 0){
                        alarmObject = new Alarm();
                        alarmObject.setDate(data.getStringExtra("date"));
                        alarmObject.setTime(data.getStringExtra("time"));
                        alarmObject.setPop(data.getIntExtra("pop",0));
                        alarmObject.setPath(String.valueOf(data.getIntExtra("ringtone",0)));
                    }
                    alarm =  data.getIntExtra("alarm",1);
                }
                break;
            case REQUEST_CAMERA:
                isPhotoChoosing = false;
                animationTranslate(findViewById(R.id.bottom_recording_photo),findViewById(R.id.bottom_recording_edit),500);
                if(resultCode == RESULT_OK && !photoPath.equals("")){
                    Log.d("uri", photoPath.toString());
                    onStop(photoPath,"photo");
                }else {
                    photoPath = "";
                }
                break;
            case REQUEST_GALLERY:
                isPhotoChoosing = false;
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
                    isPhotoChoosing = true;
                    photoPresenter.openAlbum(this,REQUEST_GALLERY);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        Log.d(TAG, "onCheckedChanged: ");
        if (b){
            urgent = 1;
        }else {
            urgent = 0;
        }
    }

    private void clickRerecording(){
        Log.d(TAG, "clickRerecording: ");
        try {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)
                        && shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    ToastUtils.showShort(this, "您已经拒绝过一次");
                }
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                isRecording = true;
                animationTranslate(findViewById(R.id.bottom_recording_edit),findViewById(R.id.bottom_recording_audio),500);
                handler.sendEmptyMessageDelayed(MSG_RECORDING,1000);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    long startTime = 0;
    Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            startTime++;
            ToastUtils.showShort(RecordingEditActivity.this,(4 - startTime)+"");
            if (startTime == 4) {
                handler.removeMessages(MSG_RECORDING);
                startTime = 0;
                presenter.startRecord();
            }
            else
                handler.sendEmptyMessageDelayed(MSG_RECORDING, 1000);
        };
    };

    private void clickAudioCancel(){
        Log.d(TAG, "clickAudioCancel: ");
        presenter.cancelRecord();
        isRecording = false;
        mTvTime.setText("00:00:00");
        animationTranslate(findViewById(R.id.bottom_recording_audio),findViewById(R.id.bottom_recording_edit),500);
    }

    private void clickFinish(){
        Log.d(TAG, "clickFinish: ");
        presenter.stopRecord();
        isRecording = false;
        mTvTime.setText("00:00:00");
        handler.removeMessages(MSG_RECORDING);
        animationTranslate(findViewById(R.id.bottom_recording_audio),findViewById(R.id.bottom_recording_edit),500);
    }


    public void animationTranslate(final View oldView, final View newView, final int duration){
        Log.d(TAG, "animationTranslate: ");
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
                newView.post(new Runnable() {
                    @Override
                    public void run() {
                        Animator animator = ViewAnimationUtils.createCircularReveal(newView,0,newView.getHeight()/2,0,newView.getWidth());
                        animator.setInterpolator(new AccelerateDecelerateInterpolator());
                        animator.setDuration(duration);
                        animator.start();
                    }
                });
            }
        });
    }

    private void cliclPhotoRecording(){
        Log.d(TAG, "cliclPhotoRecording: ");
        if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                ||checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                ToastUtils.showShort(this, "您已经拒绝过一次");
            }
            requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else {
            animationTranslate(findViewById(R.id.bottom_recording_edit),findViewById(R.id.bottom_recording_photo),500);
            isPhotoChoosing = true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: ");
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
        Log.d(TAG, "onUpdate: "+time);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        mTvTime.setText(sdf.format(new Date(time)));
    }

    @Override
    public void onStop(String filePath, String type){
        Log.d(TAG, "onStop: ");
        int index = adapter.getCurrentIndex();
        String currentType = adapter.getCurrentType();
        if( index != -1){
            HashMap<Integer,RecordingContent> map = null;
            if(currentType.equals("text")){
                List<Spanned> text =  adapter.distachText(mRecyclerView);
                Log.d(TAG, "onStop: text"+text.toString());
                map = recordingPresenter.insertRecording(mMap,text,index,filePath,type);
            }else{
                map = recordingPresenter.insertRecording(mMap,filePath,index,currentType,type);
            }
            mMap.clear();
            mMap.putAll(map);
            checkDefaultEditTex();
            if(type.equals("photo")){
                adapter.photoSetFocusable(RecordingPresenter.insertIndex);
            }else {
                adapter.setRequestFocusableArgs(RecordingPresenter.insertIndex,0,"end");
            }
            Log.d("requestFocusableIndex",RecordingPresenter.insertIndex+"");
            Log.d("requestFocusableIndex",adapter.getRequestFocusableIndex()+"");
            adapter.notifyItemRangeChanged(index,mMap.size() - index);
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    if (adapter.requestFocusableIndex < getCurrentFirstIndex() || adapter.requestFocusableIndex > getCurrentLastIndex()) {
                        updateRecyclerView(adapter.getRequestFocusableIndex());
                    }else {
                        adapter.requestFocusable(mRecyclerView);
                    }
                }
            });

        }else{
            recordingPresenter.insertRecording(mMap,filePath,type);
            Log.d("TAG", "onStop: "+mMap.toString());
            adapter.notifyItemRangeChanged(mMap.size() - 1,mMap.size());
            if(type.equals("photo")){
                adapter.photoSetFocusable(RecordingPresenter.insertIndex);
            }else {
                adapter.setRequestFocusableArgs(RecordingPresenter.insertIndex,0,"end");
            }
            if(adapter.requestFocusableIndex < getCurrentFirstIndex() || adapter.requestFocusableIndex >getCurrentLastIndex()) {
                updateRecyclerView(adapter.getRequestFocusableIndex());
            }
        }
    }

    @Override
    public void onStopActivateRecording() {
        startTime = 0;
        handler.removeMessages(MSG_RECORDING);
    }

    private void checkDefaultEditTex(){
        Log.d(TAG, "checkDefaultEditTex: ");
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
        Log.d(TAG, "getCurrentFirstIndex: ");
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
        return linearManager.findFirstVisibleItemPosition();
    }

    @Override
    public int getCurrentLastIndex() {
        Log.d(TAG, "getCurrentLastIndex: ");
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
        return linearManager.findLastVisibleItemPosition();
    }

    @Override
    public HashMap<Integer, RecordingContent> getMap() {
        Log.d(TAG, "getMap: ");
        return mMap;
    }

    @Override
    public void deleteEditText(HashMap<Integer, RecordingContent> map,int index,int position,String type) {
        Log.d(TAG, "deleteEditText: "+ mMap.toString());
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
        Log.d(TAG, "updateAdapter: "+ mMap.toString());
        adapter.notifyItemRangeChanged(index,mMap.size() - index);
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                if(adapter.requestFocusableIndex < getCurrentFirstIndex() || adapter.requestFocusableIndex >getCurrentLastIndex())
                    updateRecyclerView(adapter.requestFocusableIndex);
            }
        });
    }

    @Override
    public void recyclerViewFocusable() {
        Log.d(TAG, "recyclerViewFocusable: ");
        mRecyclerView.requestFocus();
    }

    @Override
    public void recyclerViewClearFocusable() {
        Log.d(TAG, "recyclerViewClearFocusable: ");
        mRecyclerView.clearFocus();
    }

    @Override
    public RecyclerView getRecyclerView() {

        Log.d(TAG, "getRecyclerView: ");
        return mRecyclerView;
    }

    @Override
    public void updateRecyclerView(int position) {
        Log.d("updateRecyclerView",adapter.getRequestFocusableIndex()+"");
        mRecyclerView.scrollToPosition(position);
    }

    @Override
    public void TextChanged(Spanned text, int index) {
        String html = Html.toHtml(text);
        if(html.length() > 0)
            html = html.substring(0,html.length() -1);
        mMap.get(index).setContent(adapter.parseUnicodeToStr(html));
        Log.d(TAG, "TextChanged: "+mMap.toString());
    }

    private String taskType(){
        Log.d(TAG, "taskType: ");
        for(int i = 0; i < mMap.size(); i++){
            if(mMap.get(i).getType() == "audio")
                return "audio";
        }
        return "text";
    }

    class OnTextClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.textedit_bold:
                    adapter.setTextFont(adapter.getCurrentIndex(),mRecyclerView,Typeface.BOLD);
                    mFontPopupWindow.dismiss();
                    break;
                case R.id.textedit_italy:
                    adapter.setTextFont(adapter.getCurrentIndex(),mRecyclerView,Typeface.ITALIC);
                    mFontPopupWindow.dismiss();
                    break;
                case R.id.textedit_italy_bold:
                    adapter.setTextFont(adapter.getCurrentIndex(),mRecyclerView,Typeface.BOLD_ITALIC);
                    mFontPopupWindow.dismiss();
                    break;
                case R.id.textedit_normal:
                    adapter.setTextFont(adapter.getCurrentIndex(),mRecyclerView,Typeface.NORMAL);
                    mFontPopupWindow.dismiss();
                    break;
                case R.id.textedit_deleteline:
                    adapter.setTextLine(adapter.getCurrentIndex(),mRecyclerView,0);
                    mFontPopupWindow.dismiss();
                    break;
                case R.id.textedit_underline:
                    adapter.setTextLine(adapter.getCurrentIndex(),mRecyclerView,1);
                    mFontPopupWindow.dismiss();
                    break;
                case R.id.textedit_increase:
                    adapter.setTextFontSize(adapter.getCurrentIndex(),mRecyclerView,1);
                    mFontPopupWindow.dismiss();
                    break;
                case R.id.textedit_decrease:
                    adapter.setTextFontSize(adapter.getCurrentIndex(),mRecyclerView,0);
                    mFontPopupWindow.dismiss();
                    break;
                case R.id.textedit_color_red:
                    adapter.setTextColor(adapter.getCurrentIndex(),mRecyclerView,getResources().getColor(R.color.textcolor_red));
                    mFontPopupWindow.dismiss();
                    break;
                case R.id.textedit_color_yellow:
                    adapter.setTextColor(adapter.getCurrentIndex(),mRecyclerView,getResources().getColor(R.color.textcolor_yellow));
                    mFontPopupWindow.dismiss();
                    break;
                case R.id.textedit_color_orange:
                    adapter.setTextColor(adapter.getCurrentIndex(),mRecyclerView,getResources().getColor(R.color.textcolor_orange));
                    mFontPopupWindow.dismiss();
                    break;
                case R.id.textedit_color_green:
                    adapter.setTextColor(adapter.getCurrentIndex(),mRecyclerView,getResources().getColor(R.color.textcolor_green));
                    mFontPopupWindow.dismiss();
                    break;
                case R.id.textedit_color_black:
                    adapter.setTextColor(adapter.getCurrentIndex(),mRecyclerView,getResources().getColor(R.color.textcolor_black));
                    mFontPopupWindow.dismiss();
                    break;
                case R.id.textedit_color_blue:
                    adapter.setTextColor(adapter.getCurrentIndex(),mRecyclerView,getResources().getColor(R.color.textcolor_blue));
                    mFontPopupWindow.dismiss();
                    break;
                case R.id.textedit_color_purple:
                    adapter.setTextColor(adapter.getCurrentIndex(),mRecyclerView,getResources().getColor(R.color.textcolor_purple));
                    mFontPopupWindow.dismiss();
                    break;
            }
        }
    }
}
