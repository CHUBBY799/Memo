package com.shining.memo.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.shining.memo.R;
import com.shining.memo.adapter.RecordingAdapter;
import com.shining.memo.home.MemoActivity;
import com.shining.memo.model.RecordingContent;
import com.shining.memo.model.Task_Recording;
import com.shining.memo.presenter.RecordingPresenter;
import com.shining.memo.utils.ToastUtils;

import java.util.HashMap;

import static android.text.Html.FROM_HTML_MODE_COMPACT;

public class RecordingViewActivity extends Activity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener,View.OnFocusChangeListener {
    private static final String TAG = RecordingViewActivity.class.getSimpleName();
    private static final int REQUEST_ALARM = 0xa11;
    private static final int REQUEST_EDIT = 0xa12;
    private Button mBtnBack,mBtnDelete,mBtnShare,mBtnAlarm;
    private Switch mBtnUrgent;
    private RecyclerView mRecyclerView;
    private EditText mEditTitle;
    private int taskId;
    private int urgent = 0,alarm = 0;
    private boolean isNotification = false;
    private RecordingPresenter presenter;
    private HashMap<Integer,RecordingContent> mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_view);
        init();
    }

    @Override
    protected void onResume() {
        initData();
        super.onResume();
    }


    @Override
    public void onBackPressed(){
        finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }


    private void init(){
        View view = (View)findViewById(R.id.bottom_recording_view);
        mBtnBack = (Button) view.findViewById(R.id.bottom_back);
        mBtnDelete = (Button) view.findViewById(R.id.bottom_delete);
        mBtnShare = (Button) view.findViewById(R.id.bottom_share);
        mBtnAlarm = (Button) view.findViewById(R.id.bottom_view_alarm);
        mBtnUrgent = (Switch)view.findViewById(R.id.bottom_view_urgent);
        mEditTitle = (EditText)findViewById(R.id.recording_title);
        mRecyclerView = (RecyclerView)findViewById(R.id.recording_recyclerView);

        mBtnBack.setOnClickListener(this);
        mBtnDelete.setOnClickListener(this);
        mBtnShare.setOnClickListener(this);
        mBtnAlarm.setOnClickListener(this);
        mBtnUrgent.setOnCheckedChangeListener(this);
        mEditTitle.setOnFocusChangeListener(this);

        SpannableString ss = new SpannableString(mEditTitle.getHint());
        ss.setSpan(new StyleSpan(Typeface.BOLD),0,ss.length(),Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        mEditTitle.setHint(ss);
        presenter = new RecordingPresenter(this);
        taskId = getIntent().getIntExtra("taskId",6);
        isNotification = getIntent().getBooleanExtra("isNotification",false);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bottom_back:
                returnListPage();
                break;
            case R.id.bottom_delete:
                presenter.modifyDeleted(taskId,1);
                returnListPage();
                break;
            case R.id.bottom_share:
                ToastUtils.showShort(this,"TBD");
                break;
            case R.id.bottom_alarm:
                clickAlarm();
                break;
        }
    }

    private void returnListPage(){
        finish();
        if(isNotification){
            Intent intent = new Intent();
            intent.setClass(this,MemoActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        }else {
            finish();
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        }
    }

    private void clickAlarm(){
        Intent alarmIntent = new Intent(this, AlarmActivity.class);
        alarmIntent.putExtra("taskId",taskId);
        alarmIntent.putExtra("alarm",alarm);
        startActivityForResult(alarmIntent, REQUEST_ALARM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: ");
        switch (requestCode) {
            case REQUEST_ALARM:
                if (resultCode == RESULT_OK) {
                    alarm = data.getIntExtra("alarm", 1);
                }
                break;
            case REQUEST_EDIT:
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
        presenter.modifyUrgent(taskId,urgent);
    }

    private void initData(){
        Task_Recording task_recording = presenter.getTaskRecording(taskId);
        Spanned spanned = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            spanned = Html.fromHtml(task_recording.getTask().getTitle(), FROM_HTML_MODE_COMPACT);
        }
        if(spanned != null && spanned.length() > 0)
            mEditTitle.setText(spanned.subSequence(0,spanned.length() -1));
        else
            mEditTitle.setText(spanned);
        alarm = task_recording.getTask().getAlarm();
        urgent = task_recording.getTask().getUrgent();
        if(urgent == 0)
            mBtnUrgent.setChecked(false);
        mMap = task_recording.getRecording().getRecordingMap();
        RecordingAdapter adapter = null;//new RecordingAdapter(mMap,this,this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if(b){

            Log.d("TAG",view.getId()+"");
        }
    }
}
