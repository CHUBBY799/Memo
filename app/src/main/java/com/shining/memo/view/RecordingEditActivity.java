package com.shining.memo.view;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.shining.memo.R;
import com.shining.memo.adapter.RecordingAdapter;
import com.shining.memo.home.MemoActivity;
import com.shining.memo.model.Alarm;
import com.shining.memo.model.RecordingContent;
import com.shining.memo.model.Task;
import com.shining.memo.model.Task_Recording;
import com.shining.memo.presenter.AlarmPresenter;
import com.shining.memo.presenter.AudioRecordPresenter;
import com.shining.memo.presenter.PhotoPresenter;
import com.shining.memo.presenter.RecordingPresenter;
import com.shining.memo.utils.ToastUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static android.text.Html.FROM_HTML_MODE_COMPACT;


public class RecordingEditActivity extends Activity implements View.OnClickListener,ViewRecord, RecordingAdapter.TextChanged,Switch.OnCheckedChangeListener,OnFocusChangeListener{
    private final static  String TAG = "RecordingEditActivity";
    private static final int MSG_RECORDING = 0x110;
    private static final int REQUEST_ALARM=0xb3;
    private static final int REQUEST_CAMERA=0xa1;
    private static final int REQUEST_GALLERY=0xa3;
    private Button mBtnCancel,mBtnConfirm,mBtnAlarm,mBtnEdit,mBtnRecord,mBtnPhoto,mBtnAudioCancel,mBtnFinish,mBtnGallery,mBtnCamera;
    private Button mBtnBold,mBtnUnderLine,mBtnDeleteLine,mBtnColor,mBtnTextBack;
    private Button mBtnColBack,mBtnColRed,mBtnColOrange,mBtnColBlue,mBtnColPurple,mBtnColGray,mBtnColBlack;
    private Button mBtnViewBack,mBtnViewDelete,mBtnViewShare,mBtnViewAlarm;
    private ConstraintLayout layout;
    private Switch mSwitchUrgent,mBtnViewUrgent;
    private TextView mTvTime,dialogTv;
    private EditText editTitle;
    private AlertDialog dialog;
    private PopupWindow volumePopWindow;
    private ImageView volumeImage;
    private RecyclerView mRecyclerView;
    private static boolean isRecording = false,isPhotoChoosing = false,isTextEdit = false,isColorPick = false ,titleOnFocus = false,noBackKey = false,isView = false;
    private String photoPath="";
    private int urgent = 0,alarm = 0,taskId = -1;
    private boolean isNotification;
    private OonClickView onClickView;
    private Alarm alarmObject;
    private RecordingAdapter adapter;
    private HashMap<Integer,RecordingContent> mMap;
    private AudioRecordPresenter presenter;
    private RecordingPresenter recordingPresenter;
    private AlarmPresenter alarmPresenter;
    private PhotoPresenter photoPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_edit);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        init();
        initData();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        if(isRecording) {
            if(volumePopWindow != null){
                noBackKey = true;
                volumePopWindow.dismiss();
                volumeImage = null;
            }
            animationTranslate(findViewById(R.id.bottom_recording_audio),findViewById(R.id.bottom_recording_edit));
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
            animationTranslate(findViewById(R.id.bottom_recording_photo),findViewById(R.id.bottom_recording_edit));
        }else if(isRecording){
            presenter.cancelRecord();
            mTvTime.setText("00:00:00");
            if(volumePopWindow != null){
                volumePopWindow.dismiss();
                volumeImage = null;
            }
            handler.removeMessages(MSG_RECORDING);
            isRecording = false;
            animationTranslate(findViewById(R.id.bottom_recording_audio),findViewById(R.id.bottom_recording_edit));
        }else if(isColorPick){
            isColorPick = false;
            animationTranslate(findViewById(R.id.bottom_recording_colorpick),findViewById(R.id.bottom_recording_textedit));
        }else if(isTextEdit){
            isTextEdit = false;
            animationTranslate(findViewById(R.id.bottom_recording_textedit),findViewById(R.id.bottom_recording_edit));
        }else if(taskId != -1 && !isView){
            isView = true;
            initData();
            animationTranslate(findViewById(R.id.bottom_recording_edit),findViewById(R.id.bottom_recording_view));
        }else {
            finish();
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
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
        mBtnTextBack = (Button)view.findViewById(R.id.bottom_textedit_back);
        mBtnBold = (Button)view.findViewById(R.id.bottom_bold);
        mBtnUnderLine = (Button)view.findViewById(R.id.bottom_underline);
        mBtnDeleteLine = (Button)view.findViewById(R.id.bottom_deleteline);
        mBtnColor = (Button)view.findViewById(R.id.bottom_color);
        view =  (View)findViewById(R.id.bottom_recording_colorpick);
        mBtnColBack = (Button)view.findViewById(R.id.bottom_colorpick_back);
        mBtnColBlack = (Button)view.findViewById(R.id.colorpick_black);
        mBtnColRed = (Button)view.findViewById(R.id.colorpick_red);
        mBtnColOrange = (Button)view.findViewById(R.id.colorpick_orange);
        mBtnColBlue = (Button)view.findViewById(R.id.colorpick_blue);
        mBtnColPurple = (Button)view.findViewById(R.id.colorpick_purple);
        mBtnColGray = (Button)view.findViewById(R.id.colorpick_gray);

        mSwitchUrgent = (Switch)findViewById(R.id.bottom_urgent);
        mBtnRecord = (Button)findViewById(R.id.bottom_audio);
        mBtnPhoto = (Button)findViewById(R.id.bottom_photo);
        mRecyclerView = (RecyclerView)findViewById(R.id.recording_recyclerView);
        editTitle = (EditText)findViewById(R.id.recording_title);
        editTitle.setOnFocusChangeListener(this);
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
        mBtnTextBack.setOnClickListener(this);
        mBtnBold.setOnClickListener(this);
        mBtnUnderLine.setOnClickListener(this);
        mBtnDeleteLine.setOnClickListener(this);
        mBtnColor.setOnClickListener(this);
        mBtnColBack.setOnClickListener(this);
        mBtnColBlack.setOnClickListener(this);
        mBtnColRed.setOnClickListener(this);
        mBtnColOrange.setOnClickListener(this);
        mBtnColBlue.setOnClickListener(this);
        mBtnColPurple.setOnClickListener(this);
        mBtnColGray.setOnClickListener(this);
        presenter = new AudioRecordPresenter(this);
        photoPresenter = new PhotoPresenter(this);
        recordingPresenter = new RecordingPresenter(this);
        alarmPresenter = new AlarmPresenter(this);
        taskId = getIntent().getIntExtra("taskId",1);
        isNotification = getIntent().getBooleanExtra("isNotification",false);
        if(taskId != -1){
            isView = true;
            View v = findViewById(R.id.bottom_recording_view);
            v.setVisibility(View.VISIBLE);
            findViewById(R.id.bottom_recording_edit).setVisibility(View.GONE);
            mBtnViewBack = (Button)v.findViewById(R.id.bottom_back);
            mBtnViewDelete = (Button)v.findViewById(R.id.bottom_delete);
            mBtnViewShare = (Button)v.findViewById(R.id.bottom_share);
            mBtnViewAlarm = (Button)v.findViewById(R.id.bottom_view_alarm);
            mBtnViewUrgent = (Switch)v.findViewById(R.id.bottom_view_urgent);
            onClickView = new OonClickView();
            mBtnViewBack.setOnClickListener(onClickView);
            mBtnViewDelete.setOnClickListener(onClickView);
            mBtnViewShare.setOnClickListener(onClickView);
            mBtnViewAlarm.setOnClickListener(onClickView);
            mBtnViewUrgent.setOnCheckedChangeListener(this);
        }
    }

    private void initData(){
        if(taskId == -1){
            if(mMap == null || mMap.isEmpty()){
                mMap = new HashMap<>();
                RecordingContent content = new RecordingContent();
                content.setType("text");
                content.setColor("#666666");
                content.setContent("");
                mMap.put(mMap.size(),content);
            }
        }else{
            Task_Recording task_recording = recordingPresenter.getTaskRecording(taskId);
            Spanned spanned = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                spanned = Html.fromHtml(task_recording.getTask().getTitle(),FROM_HTML_MODE_COMPACT);
            }
            if(spanned != null && spanned.length() > 0)
                editTitle.setText(spanned.subSequence(0,spanned.length() -1));
            else
                editTitle.setText(spanned);
            alarm = task_recording.getTask().getAlarm();
            if(alarm == 1){
                alarmObject = alarmPresenter.getAlarm(taskId);
            }
            urgent = task_recording.getTask().getUrgent();
            if(urgent == 0)
                mBtnViewUrgent.setChecked(false);
            else
                mBtnViewUrgent.setChecked(true);
            mMap = task_recording.getRecording().getRecordingMap();
        }
        adapter = new RecordingAdapter(mMap,this,this);
        if(taskId != -1){
            adapter.setView(true);
            adapter.setViewEdit(true);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
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
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = findViewById(R.id.linearLayout);
            if (!isShouldHideInput(v, ev) && (isPhotoChoosing)) {
                isPhotoChoosing = false;
                animationTranslate(findViewById(R.id.bottom_recording_photo), findViewById(R.id.bottom_recording_edit));
                return true;
            }else if (isShouldHideInput(mRecyclerView, ev)) {
                try {
                    v = mRecyclerView.getChildAt(getCurrentLastIndex());
                    int[] leftTop = { 0, 0 };
                    if(v != null){
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
            case R.id.bottom_textedit_back:
                isTextEdit = false;
                animationTranslate(findViewById(R.id.bottom_recording_textedit),findViewById(R.id.bottom_recording_edit));
                layout.requestFocus();
                break;
            case R.id.bottom_bold:
                clickBold();
                break;
            case R.id.bottom_underline:
                clickUnderLine();
                break;
            case R.id.bottom_deleteline:
                clickDeleteLine();
                break;
            case R.id.bottom_color:
                isColorPick = true;
                animationTranslate(findViewById(R.id.bottom_recording_textedit),findViewById(R.id.bottom_recording_colorpick));
                break;
            case R.id.bottom_colorpick_back:
                clickColorBcak();
                break;
            case R.id.colorpick_red:
                clickColorChanged(1);
                break;
            case R.id.colorpick_orange:
                clickColorChanged(2);
                break;
            case R.id.colorpick_blue:
                clickColorChanged(3);
                break;
            case R.id.colorpick_purple:
                clickColorChanged(4);
                break;
            case R.id.colorpick_gray:
                clickColorChanged(5);
                break;
            case R.id.colorpick_black:
                clickColorChanged(0);
                break;
        }
    }


    private void clickCancel(){
        Log.d(TAG, "clickCancel: ");
        if(taskId == -1){
            for(int i = 0; i < mMap.size(); i++){
                if((mMap.get(i).getType().equals("audio"))||(mMap.get(i).getType().equals("photo")
                        && mMap.get(i).getContent().contains(Environment.getExternalStorageDirectory()+"/OhMemo/photo/"))){
                        File file = new File(mMap.get(i).getContent());
                        if (file.exists())
                            file.delete();
                }
            }
            setResult(RESULT_CANCELED);
            finish();
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        }else {
            animationTranslate(findViewById(R.id.bottom_recording_edit),findViewById(R.id.bottom_recording_view));
            isView = true;
            initData();
        }
    }
    private void clickConfirm(){
        Log.d(TAG, "clickConfirm: ");
        Task task = new Task();
        task.setType(taskType());
        task.setUrgent(urgent);
        task.setAlarm(alarm);
        String html = Html.toHtml(new SpannedString(editTitle.getText()));
        if(html.length() > 0)
            html = html.substring(0,html.length() -1);
        String title = adapter.parseUnicodeToStr(html);
        task.setTitle(title);
        if(taskId == -1){
            long id = 0;
            if( (id = recordingPresenter.saveRecording(task,mMap,alarmObject)) != -1){
                alarmPresenter.setAlarmNotice((int)id);
                Toast.makeText(this,"save successful",Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }else{
                Toast.makeText(this,"save failed",Toast.LENGTH_SHORT).show();
            }
        }else {
            task.setId(taskId);
            if(alarmObject != null)
                alarmObject.setTaskId(taskId);
            if(recordingPresenter.modifyRecording(task,mMap,alarmObject)){
                Toast.makeText(this,"save successful",Toast.LENGTH_SHORT).show();
                alarmPresenter.setAlarmNotice(taskId);
                if(adapter.deletePath != null && adapter.deletePath.size() > 0){
                    for(int i=0; i < adapter.deletePath.size(); i++){
                        File file = new File(adapter.deletePath.get(i));
                        if(file.exists())
                            file.delete();
                    }
                }
                animationTranslate(findViewById(R.id.bottom_recording_edit),findViewById(R.id.bottom_recording_view));
                isView = true;
//                adapter.setView(true);
//                mRecyclerView.clearFocus();
                initData();
            }else{
                Toast.makeText(this,"save failed",Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void clickEdit(){
        isTextEdit = true;
        if(layout == null)
            layout = (ConstraintLayout)findViewById(R.id.recroding_edit_root);
        animationTranslate(findViewById(R.id.bottom_recording_edit),findViewById(R.id.bottom_recording_textedit));
    }
    private void clickAlarm(){
        Log.d(TAG, "clickAlarm: ");
        Intent alarmIntent = new Intent(this, AlarmActivity.class);
        if(isView){
            Log.d(TAG,"alarm"+alarm + "taskId" +taskId);
            Log.d(TAG,alarmObject.toString());
            alarmIntent.putExtra("taskId",taskId);
            alarmIntent.putExtra("alarm",alarm);
        } else {
            if(alarm == 1){
                alarmIntent.putExtra("date",alarmObject.getDate());
                alarmIntent.putExtra("time",alarmObject.getTime());
                alarmIntent.putExtra("pop",alarmObject.getPop());
                alarmIntent.putExtra("ringtone",alarmObject.getRingtone());
                alarmIntent.putExtra("alarm",1);
            }
        }
        startActivityForResult(alarmIntent, REQUEST_ALARM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d(TAG, "onActivityResult: ");
        switch (requestCode){
            case REQUEST_ALARM:
                if (resultCode == RESULT_OK){
                    alarmObject = new Alarm();
                    alarmObject.setDate(data.getStringExtra("date"));
                    alarmObject.setTime(data.getStringExtra("time"));
                    alarmObject.setPop(data.getIntExtra("pop",0));
                    alarmObject.setRingtone(data.getIntExtra("ringtone",0));
                    alarm =  data.getIntExtra("alarm",1);
                }
                break;
            case REQUEST_CAMERA:
                isPhotoChoosing = false;
                animationTranslate(findViewById(R.id.bottom_recording_photo),findViewById(R.id.bottom_recording_edit));
                if(resultCode == RESULT_OK && !photoPath.equals("")){
                    Log.d("uri", photoPath.toString());
                    onStop(photoPath,"photo");
                }else {
                    photoPath = "";
                }
                break;
            case REQUEST_GALLERY:
                isPhotoChoosing = false;
                animationTranslate(findViewById(R.id.bottom_recording_photo),findViewById(R.id.bottom_recording_edit));
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
        if(isView)
            recordingPresenter.modifyUrgent(taskId,urgent);
    }

    private void clickRerecording(){
        Log.d(TAG, "clickRerecording: ");
        try {
        //    hideInputMethod(this,getCurrentFocus());
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)
                        && shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    ToastUtils.showShort(this, "您已经拒绝过一次");
                }
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                isRecording = true;
                animationTranslate(findViewById(R.id.bottom_recording_edit),findViewById(R.id.bottom_recording_audio));
                handler.sendEmptyMessageDelayed(MSG_RECORDING,600);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    long startTime = 0;
    Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            startTime++;
            if(startTime == 1)
                onCreateDialog();
            else if(startTime < 4){
                if(dialogTv != null)
                    dialogTv.setText(String.valueOf(4 - startTime));
            }
            if (startTime == 4) {
                handler.removeMessages(MSG_RECORDING);
                startTime = 0;
                presenter.startRecord();
                if(dialog != null)
                    dialog.dismiss();
                onCreateVolumePopWindow();
            }
            else
                handler.sendEmptyMessageDelayed(MSG_RECORDING, 1000);
        };
    };

    private void onCreateDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Theme_AppCompat_Light_Dialog);
        builder.setView(R.layout.countdown);
        dialog = builder.show();dialogTv = dialog.findViewById(R.id.countdown_view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_anim;
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                handler.removeMessages(MSG_RECORDING);
                startTime = 0;
                onBackPressed();
            }
        });
    }
    private void onCreateVolumePopWindow(){
        View view = LayoutInflater.from(this).inflate(R.layout.volumedisplay,null);
        volumePopWindow = new PopupWindow(view, ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT,true);
        volumeImage = (ImageView)view.findViewById(R.id.volume_view);
        volumePopWindow.setTouchable(false);
        volumePopWindow.setBackgroundDrawable(new ColorDrawable());
        volumePopWindow.setAnimationStyle(R.style.anim);
        volumePopWindow.showAtLocation(view,Gravity.BOTTOM,0,-140);
        noBackKey = false;
        volumePopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if(!noBackKey)
                    onBackPressed();
            }
        });
    }

    private void clickAudioCancel(){
        Log.d(TAG, "clickAudioCancel: ");
        presenter.cancelRecord();
        isRecording = false;
        mTvTime.setText("00:00:00");
        if(volumePopWindow != null){
            noBackKey = true;
            volumePopWindow.dismiss();
            volumeImage = null;
        }
        animationTranslate(findViewById(R.id.bottom_recording_audio),findViewById(R.id.bottom_recording_edit));
    }

    private void clickFinish(){
        Log.d(TAG, "clickFinish: ");
        presenter.stopRecord();
        isRecording = false;
        mTvTime.setText("00:00:00");
        if(volumePopWindow != null){
            noBackKey = true;
            volumePopWindow.dismiss();
            volumeImage = null;
        }
        handler.removeMessages(MSG_RECORDING);
        animationTranslate(findViewById(R.id.bottom_recording_audio),findViewById(R.id.bottom_recording_edit));
    }


    public void animationTranslate(final View oldView, final View newView){
        Log.d(TAG, "animationTranslate: ");
        final int duration = 500;
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
            animationTranslate(findViewById(R.id.bottom_recording_edit),findViewById(R.id.bottom_recording_photo));
            isPhotoChoosing = true;
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onUpdate(double db, long time) {
        if(volumeImage != null)
            volumeImage.getDrawable().setLevel((int)db);
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
        mMap.get(index).setContent(RecordingAdapter.parseUnicodeToStr(html));
        Log.d(TAG, "TextChanged: "+mMap.toString());
    }

    private String taskType(){
        Log.d(TAG, "taskType: ");
        for(int i = 0; i < mMap.size(); i++){
            if(mMap.get(i).getType().equals("audio"))
                return "audio";
        }
        return "text";
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if(b){
            titleOnFocus = true;
        }else {
            titleOnFocus = false;
        }
        if(isView)
            viewToEdit();
    }

    private void clickBold(){
        if(titleOnFocus){
            adapter.setTextBold(adapter.getCurrentIndex(),mRecyclerView,editTitle);
        }else{
            adapter.setTextBold(adapter.getCurrentIndex(),mRecyclerView,null);
        }
    }
    private void clickUnderLine(){
        if(titleOnFocus){
            adapter.setTextLine(adapter.getCurrentIndex(),mRecyclerView,1,editTitle);
        }else{
            adapter.setTextLine(adapter.getCurrentIndex(),mRecyclerView,1,null);
        }
    }
    private void clickDeleteLine(){
        if(titleOnFocus){
            adapter.setTextLine(adapter.getCurrentIndex(),mRecyclerView,0,editTitle);
        }else{
            adapter.setTextLine(adapter.getCurrentIndex(),mRecyclerView,0,null);
        }
    }

    private void clickColorBcak(){
        isColorPick = false;
        animationTranslate(findViewById(R.id.bottom_recording_colorpick),findViewById(R.id.bottom_recording_textedit));
    }

    private void clickColorChanged(int pos){
        int color = 0;
        switch (pos){
            case 0:
                color = getResources().getColor(R.color.textcolor_black,null);
                break;
            case 1:
                color = getResources().getColor(R.color.textcolor_red,null);
                break;
            case 2:
                color = getResources().getColor(R.color.textcolor_orange,null);
                break;
            case 3:
                color = getResources().getColor(R.color.textcolor_blue,null);
                break;
            case 4:
                color = getResources().getColor(R.color.textcolor_purple,null);
                break;
            case 5:
                color = getResources().getColor(R.color.textcolor_gray,null);
                break;
        }
        if(titleOnFocus){
            adapter.setTextColor(adapter.getCurrentIndex(),mRecyclerView,color,editTitle);
        }else if(adapter.getCurrentIndex() != -1){
            adapter.setTextColor(adapter.getCurrentIndex(),mRecyclerView,color,null);
        }
            resetColorBackground();
            RecordingAdapter.currentColor = color;
            switch (pos){
                case 0:
                    mBtnColBlack.setBackground(getResources().getDrawable(R.drawable.color_oval_black,null));
                    RecordingAdapter.colorPos = 0;
                    break;
                case 1:
                    mBtnColRed.setBackground(getResources().getDrawable(R.drawable.color_oval_red,null));
                    RecordingAdapter.colorPos = 1;
                    break;
                case 2:
                    mBtnColOrange.setBackground(getResources().getDrawable(R.drawable.color_oval_orange,null));
                    RecordingAdapter.colorPos = 2;
                    break;
                case 3:
                    mBtnColBlue.setBackground(getResources().getDrawable(R.drawable.color_oval_blue,null));
                    RecordingAdapter.colorPos = 3;
                    break;
                case 4:
                    mBtnColPurple.setBackground(getResources().getDrawable(R.drawable.color_oval_purple,null));
                    RecordingAdapter.colorPos = 4;
                    break;
                case 5:
                    mBtnColGray.setBackground(getResources().getDrawable(R.drawable.color_oval_gray,null));
                    RecordingAdapter.colorPos = 5;
                    break;
            }

    }

    private void resetColorBackground(){
        switch (RecordingAdapter.colorPos){
            case 0:
                mBtnColBlack.setBackground(getResources().getDrawable(R.drawable.color_ring_black,null));
                break;
            case 1:
                mBtnColRed.setBackground(getResources().getDrawable(R.drawable.color_ring_red,null));
                break;
            case 2:
                mBtnColOrange.setBackground(getResources().getDrawable(R.drawable.color_ring_orange,null));
                break;
            case 3:
                mBtnColBlue.setBackground(getResources().getDrawable(R.drawable.color_ring_blue,null));
                break;
            case 4:
                mBtnColPurple.setBackground(getResources().getDrawable(R.drawable.color_ring_purple,null));
                break;
            case 5:
                mBtnColGray.setBackground(getResources().getDrawable(R.drawable.color_ring_gray,null));
                break;
        }
    }

    class OonClickView implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.bottom_back:
                    returnHomePage();
                    break;
                case R.id.bottom_delete:
                    recordingPresenter.modifyDeleted(taskId,1);
                    returnHomePage();
                    break;
                case R.id.bottom_share:
                    ToastUtils.showShort(RecordingEditActivity.this,"TBD");
                    break;
                case R.id.bottom_view_alarm:
                    clickAlarm();
                    break;
            }
        }
    }

    private void returnHomePage(){
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

    @Override
    public void viewToEdit(){
        animationTranslate(findViewById(R.id.bottom_recording_view),findViewById(R.id.bottom_recording_edit));
        isView = false;
        adapter.setView(false);
        if(urgent == 1)
            mSwitchUrgent.setChecked(true);
        else
            mSwitchUrgent.setChecked(false);
    }

}
