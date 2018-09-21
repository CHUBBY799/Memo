package com.shining.memo.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.constraint.Constraints;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.shining.memo.R;
import com.shining.memo.presenter.AudioPresenter;
import com.shining.memo.widget.RoundProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class AudioViewActivity extends Activity implements ViewAudioEdit,View.OnClickListener{

    private Button mBtnBack,mBtnShare,mBtnAlarm,mBtnDelete,mBtnUrgent,mBtnReRecording;
    private Button mBtnCancel,mBtnUrgentEdit,mBtnAlarmEdit,mBtnEdit,mBtnConfirm;
    private ImageView micImage;
    private EditText editTitle;
    private String filePath = "",title;
    private int taskId = 0;
    private int urgent = 0;
    private int alarm = 0;
    public  static boolean isplaying = false;
    private boolean isView = true;
    private AudioPresenter audioPresenter;
    private RoundProgressBar mRoundProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_view);
        init();
        if(savedInstanceState != null){
            taskId = savedInstanceState.getInt("taskId");
            alarm = savedInstanceState.getInt("alarm");
            urgent = savedInstanceState.getInt("urgent");
            filePath = savedInstanceState.getString("filePath");
            title = savedInstanceState.getString("title");
            isplaying = savedInstanceState.getBoolean("isplaying");
            isView = savedInstanceState.getBoolean("isView");
            if(isplaying)
                audioPresenter.doPlay();
            if(!isView)
                nonView();
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(isplaying)
            audioPresenter.onPausePlay();
        outState.putInt("taskId",taskId);
        outState.putInt("urgent",urgent);
        outState.putInt("alarm",alarm);
        outState.putString("filePath",filePath);
        outState.putString("title",title);
        outState.putBoolean("isplaying",isplaying);
        outState.putBoolean("isView",isView);
        if(editTitle.hasFocus())
            editTitle.clearFocus();
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
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (RecordingEditActivity.isShouldHideInput(v, ev)) {
                if(RecordingEditActivity.hideInputMethod(this, v)) {
                    editTitle.clearFocus();
                    return true; //隐藏键盘时，其他控件不响应点击事件==》注释则不拦截点击事件
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
    public void onUpdateInfo(int taskId) {
        JSONObject object = audioPresenter.getAudioInfo(taskId);
        try {
            title = object.getString("title");
            alarm = object.getInt("alram");
            urgent = object.getInt("urgent");
            filePath = object.getString("filePath");
            editTitle.setText(title);
            if(urgent == 0)
                mBtnUrgent.setActivated(false);
            else
                mBtnUrgent.setActivated(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.title_view_back:
                clickBack();
                break;
            case R.id.title_view_delete:
                clickDelete();
                break;
            case R.id.title_view_alarm:
                clickAlarm();
                break;
            case R.id.title_view_share:
                clickShare();
                break;
            case R.id.title_view_urgent:
                clickUrgent();
                break;
            case R.id.audio_view_rerecording:
                clickRerecording();
                break;
            case R.id.image_mic_audio_view:
                clickMicphone();
                break;
            case R.id.title_edit_cancel:
                clickCancel();
                break;
            case R.id.title_edit_urgent:
                clickUrgent();
                break;
            case R.id.title_edit_alarm:
                clickAlarm();
                break;
            case R.id.title_edit_edit:
                clickEdit();
                break;
            case R.id.title_edit_confirm:
                clickConfirm();
                break;
        }
    }
    private void init(){
        View view =  (View)findViewById(R.id.audio_view_title);
        mBtnBack = (Button)view.findViewById(R.id.title_view_back);
        mBtnShare = (Button)view.findViewById(R.id.title_view_share);
        mBtnAlarm = (Button)view.findViewById(R.id.title_view_alarm);
        mBtnDelete = (Button)view.findViewById(R.id.title_view_delete);
        mBtnUrgent = (Button)view.findViewById(R.id.title_view_urgent);
        mBtnReRecording = (Button)findViewById(R.id.audio_view_rerecording);
        view = (View)findViewById(R.id.audio_view_edit_title);
        mBtnCancel = (Button)view.findViewById(R.id.title_edit_cancel);
        mBtnUrgentEdit =(Button)view.findViewById(R.id.title_edit_urgent);
        mBtnAlarmEdit =(Button)view.findViewById(R.id.title_edit_alarm);
        mBtnEdit =(Button)view.findViewById(R.id.title_edit_edit);
        mBtnConfirm = (Button)view.findViewById(R.id.title_edit_confirm);

        micImage = (ImageView)findViewById(R.id.image_mic_audio_view);
        editTitle = (EditText)findViewById(R.id.edit_audio_view_title);
 //       taskId = Integer.parseInt(getIntent().getStringExtra("taskId"));
        filePath = getIntent().getStringExtra("filePath");
        audioPresenter = new AudioPresenter(this,filePath);
        mRoundProgressBar = (RoundProgressBar)findViewById(R.id.roundProgress_view);

        mBtnBack.setOnClickListener(this);
        mBtnShare.setOnClickListener(this);
        mBtnAlarm.setOnClickListener(this);
        mBtnDelete.setOnClickListener(this);
        mBtnUrgent.setOnClickListener(this);
        mBtnReRecording.setOnClickListener(this);
        micImage.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
        mBtnUrgentEdit.setOnClickListener(this);
        mBtnAlarmEdit.setOnClickListener(this);
        mBtnEdit.setOnClickListener(this);
        mBtnConfirm.setOnClickListener(this);
        editTitle.setOnFocusChangeListener( new FocusChangeListener());
    }
    private void clickBack(){
        audioPresenter.setMediaPlayerNull();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    private void clickDelete(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure you want to delete the current recording");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(audioPresenter.deleteAudio(taskId))
                {
                    if(!filePath.equals("")){
                        File file = new File(filePath);
                        if (file.exists())
                            file.delete();
                        filePath = "";
                    }
                    Intent intent = new Intent();
                    intent.setClass(AudioViewActivity.this,MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(AudioViewActivity.this,"delete successful",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(AudioViewActivity.this,"delete failed",Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }
    private void clickShare(){

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
                break;
        }
    }
    private void clickUrgent(){
        if (urgent == 0){
            urgent = 1;
            if(isView)
                mBtnUrgent.setActivated(true);
            else
                mBtnUrgentEdit.setActivated(true);
        }else {
            urgent = 0;
            if(isView)
                mBtnUrgent.setActivated(false);
            else
                mBtnUrgentEdit.setActivated(false);
        }
    }
    private void clickRerecording(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure you want to make an rerecording");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(!filePath.equals("")){
                    File file = new File(filePath);
                    if (file.exists())
                        file.delete();
                    filePath = "";
                }
//                Intent intent = new Intent();
//                intent.setClass(AudioViewActivity.this,AudioRecordingActivity.class);
//                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }
    private void clickMicphone(){
        if(!isplaying)
        {
            mRoundProgressBar.setVisibility(View.VISIBLE);
            audioPresenter.doPlay();
            isplaying = true;
        }
        else{
            audioPresenter.onPausePlay();
            isplaying = false;
        }
    }
    private void clickCancel(){
        View rect = findViewById(R.id.audio_view_edit_title);
        Animator animator = ViewAnimationUtils.createCircularReveal(rect,rect.getWidth()/2,rect.getHeight()/2,rect.getWidth()/2,0);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(500);
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                findViewById(R.id.audio_view_edit_title).setVisibility(View.INVISIBLE);
                View rect = (View)findViewById(R.id.audio_view_title);
                rect.setVisibility(View.VISIBLE);
                Animator animator = ViewAnimationUtils.createCircularReveal(rect,rect.getWidth()/2,rect.getHeight()/2,0,rect.getWidth()/2);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.setDuration(500);
                animator.start();
                editTitle.clearFocus();
                isView = true;
//                onUpdateInfo(taskId);
            }
        });
    }
    private void clickEdit(){

    }
    private void clickConfirm(){
        if(audioPresenter.modifyAudioTitle(editTitle.getText().toString())) {
            Toast.makeText(this,"save successful",Toast.LENGTH_SHORT).show();
            clickCancel();
        }else {
            Toast.makeText(this,"save failed",Toast.LENGTH_SHORT).show();
        }
        audioPresenter.setMediaPlayerNull();
    }
    private void nonView(){
        View view = findViewById(R.id.audio_view_title);
        view.setVisibility(View.GONE);
        view = (View)findViewById(R.id.audio_view_edit_title);
        view.setVisibility(View.VISIBLE);
    }
    class FocusChangeListener implements View.OnFocusChangeListener{

        @Override
        public void onFocusChange(View view, boolean b) {
            if(b && isView){
                View rect = findViewById(R.id.audio_view_title);
                Animator animator = ViewAnimationUtils.createCircularReveal(rect,rect.getWidth()/2,rect.getHeight()/2,rect.getWidth()/2,0);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.setDuration(500);
                animator.start();
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        findViewById(R.id.audio_view_title).setVisibility(View.GONE);
                        View rect = (View)findViewById(R.id.audio_view_edit_title);
                        rect.setVisibility(View.VISIBLE);
                        Animator animator = ViewAnimationUtils.createCircularReveal(rect,rect.getWidth()/2,rect.getHeight()/2,0,rect.getWidth()/2);
                        animator.setInterpolator(new AccelerateDecelerateInterpolator());
                        animator.setDuration(500);
                        animator.start();
                        isView = false;
                    }
                });
            }
        }
    }
}

