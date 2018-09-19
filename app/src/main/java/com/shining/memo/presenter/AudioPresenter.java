package com.shining.memo.presenter;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.shining.memo.model.Audio;
import com.shining.memo.model.AudioImpl;
import com.shining.memo.model.AudioModel;
import com.shining.memo.model.MemoDatabaseHelper;
import com.shining.memo.model.Task;
import com.shining.memo.model.TaskImpl;
import com.shining.memo.model.TaskModel;
import com.shining.memo.view.AudioEditActivity;
import com.shining.memo.view.AudioViewActivity;
import com.shining.memo.view.MainActivity;
import com.shining.memo.view.ViewAudioRecording;
import com.shining.memo.view.ViewAudioEdit;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AudioPresenter{

    private static String filePath = "";     //录音文件路径
    private String FolderPath = "";     //文件夹路径
    private String playFilePath = "";     //播放文件路径
    private MediaRecorder mMediaRecorder;   //音频录制API
    private static MediaPlayer mMediaPlayer;
    private static final int MAX_LENGTH = 1000 * 60 * 30;// 最大录音时长1000*60*30;
    private ViewAudioRecording viewAudioRecording;
    private ViewAudioEdit viewAudioSetting;
    private Context context;
    private long startTime,endTime;
    private static final int MSG_PROGRESS_UPDATE = 0x110;
    private static final int SEEK_CLOSEST = 0x03;
    private TaskModel taskModel;
    private AudioModel audioModel;
    private  MemoDatabaseHelper dbHelper;

    public AudioPresenter(ViewAudioRecording vAudioRec){
        this(Environment.getExternalStorageDirectory()+"/record/");
        this.viewAudioRecording = vAudioRec;
        context = viewAudioRecording.getContext();
    }

    public AudioPresenter(ViewAudioEdit vAudioSet, String filePath){
        this.viewAudioSetting = vAudioSet;
        context = viewAudioSetting.getContext();
        playFilePath = filePath;
        taskModel = new TaskImpl(context);
        audioModel = new AudioImpl(context);
        dbHelper=new MemoDatabaseHelper(context,"memo.db",null,1);
    }

    public AudioPresenter(String filePath) {

        File path = new File(filePath);
        if(!path.exists())
            path.mkdirs();
        this.FolderPath = filePath;
    }

    // 开始录音
    public void startRecord() {

        if (mMediaRecorder == null)
            mMediaRecorder = new MediaRecorder();
        try {

            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = new Date(System.currentTimeMillis());     //获取当前时间
            filePath = FolderPath + simpleDateFormat.format(date) + ".amr" ;
            mMediaRecorder.setOutputFile(filePath);
            mMediaRecorder.setMaxDuration(MAX_LENGTH);
            try {
                mMediaRecorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            /* ④开始 */
            mMediaRecorder.start();
            /* 获取开始时间* */
            startTime = System.currentTimeMillis();
            updateMicStatus();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止录音
     */
    public long stopRecord() {
        if (mMediaRecorder == null)
            return 0L;
        endTime = System.currentTimeMillis();
        try {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            if(((endTime - startTime) / 1000) < 1)
            {
                File file = new File(filePath);
                if (file.exists())
                    file.delete();
                Toast.makeText(context,"录音时长小于1S，请重新录制！",Toast.LENGTH_SHORT).show();
            }
            else {
                viewAudioRecording.onStop(filePath);
            }
            filePath = "";

        }catch (RuntimeException e){
            Log.e("Exception",e.getMessage());
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;

            File file = new File(filePath);
            if (file.exists())
                file.delete();
            filePath = "";
        }
        return endTime - startTime;
    }

    /**
     * 取消录音
     */
    public void cancelRecord(){

        try {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;

        }catch (RuntimeException e){
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
        File file = new File(filePath);
        if (file.exists())
            file.delete();
        filePath = "";

    }

    private final Handler mHandler = new Handler();
    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }
    };

    private int BASE = 10;
    private int SPACE = 500;// 间隔取样时间
    private void updateMicStatus() {

        if (mMediaRecorder != null) {
            double ratio = (double)mMediaRecorder.getMaxAmplitude() / BASE;
            Log.d("ratio",String.valueOf(ratio));
            double db = 0;// 分贝
            if (ratio > 1) {
                db = 20 * Math.log10(ratio);
                Log.d("db",String.valueOf(db));
                if(null != viewAudioRecording) {
                    viewAudioRecording.onUpdate(db,System.currentTimeMillis()-startTime);
                }
            }
            mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
        }
    }

    long start;
    public synchronized void doPlay(){
        if(!playFilePath.equals("")){
            //配置播放器 MediaPlayer
            if(mMediaPlayer == null){
                start = System.currentTimeMillis();
                mMediaPlayer = new MediaPlayer();
                try {
                    mMediaPlayer.setDataSource(playFilePath);
                    //设置监听回调
                    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            long end = System.currentTimeMillis();
                            long interval = (end - start)/1000;
                            Log.d("interval",String.valueOf(interval));
                            mMediaPlayer.reset();
                            mMediaPlayer.release();
                            mMediaPlayer = null;
                            viewAudioSetting.onStopPlay();
                        }
                    });

                    //设置出错的监听器
                    mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mp, int what, int extra) {
                            Toast.makeText(context,"play failed",Toast.LENGTH_SHORT).show();
                            //提示用户
                            mMediaPlayer.reset();
                            mMediaPlayer.release();
                            mMediaPlayer = null;
                            mHandlerProgress.removeMessages(MSG_PROGRESS_UPDATE);
                            viewAudioSetting.onStopPlay();
                            //释放播放器
                            return true;
                        }
                    });
                    //配置音量，是否循环
                    mMediaPlayer.setVolume(1,1);
                    mMediaPlayer.setLooping(false);
                    mMediaPlayer.prepare();
                    intervalCalculate();
                    roundProgress = 0;
                    viewAudioSetting.onUpdateProgress(roundProgress);
                    st = System.currentTimeMillis();
                    mHandlerProgress.sendEmptyMessageDelayed(MSG_PROGRESS_UPDATE, 100);
                    mMediaPlayer.start();

                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(context,"play failed",Toast.LENGTH_SHORT).show();
                    //提示用户
                    mMediaPlayer.reset();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                    mHandlerProgress.removeMessages(MSG_PROGRESS_UPDATE);
                    viewAudioSetting.onStopPlay();
                }
            }else {
                mMediaPlayer.start();
                mHandlerProgress.sendEmptyMessage(MSG_PROGRESS_UPDATE);

            }
        }
    }

    public void onPausePlay(){
        mMediaPlayer.pause();
        mHandlerProgress.removeMessages(MSG_PROGRESS_UPDATE);
    }

    private static int roundProgress = 0;
    private static float intervalProgress;

    private void intervalCalculate(){
        int audioTime = mMediaPlayer.getDuration() / 1000;
        intervalProgress = 100 * 1.0f / audioTime / 10;
    }

    long st,en;
    private Handler mHandlerProgress = new Handler() {
        public void handleMessage(android.os.Message msg) {
            roundProgress++;
            viewAudioSetting.onUpdateProgress((int) (roundProgress*intervalProgress));
            if ((int) (roundProgress*intervalProgress) >= 105) {
                mHandlerProgress.removeMessages(MSG_PROGRESS_UPDATE);
                viewAudioSetting.onRemoverPlay();
            }
            else
                mHandlerProgress.sendEmptyMessageDelayed(MSG_PROGRESS_UPDATE, 100);
        };
    };



    public void resetAudioPlayBeginning(int beginning){
        if(mMediaPlayer != null){
            mMediaPlayer.seekTo(beginning);
        }
    }

    public JSONObject getAudioInfo(int taskId){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        JSONObject object = new JSONObject();
        try {
            JSONObject temp = audioModel.getAudio(taskId,db);
            object.put("filePath",temp.getString("filePath"));
//            Task task = taskModel.getTask(taskId,db);
//            object.put("title",task.getTitle());
//            object.put("urgent",task.getUrgent());
//            object.put("alarm",task.getAlarm());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }


    public boolean saveAudio(Task task,String filePath){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try{
            long taskId = taskModel.addTask(task,db);
            Audio audio = new Audio();
            audio.setPath(filePath);
            audio.setTaskId(taskId);
            audioModel.saveAudio(audio,db);
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            db.endTransaction();
        }
        return true;
    }


    public boolean deleteAudio(int taskId){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try{
            audioModel.deleteAudio(taskId,db);
//            taskModel.deleteTask(taskId,db);
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            db.endTransaction();
        }
        return true;
    }

    public boolean modifyAudioPath(int taskId,String newPath){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try{
            audioModel.modifyAudio(taskId,newPath,db);
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            db.endTransaction();
        }
        return true;
    }

    public boolean modifyAudioTitle(String title){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try{
//            taskModel.modifyTaskTitle(title,db);
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            db.endTransaction();
        }
        return true;
    }


}

