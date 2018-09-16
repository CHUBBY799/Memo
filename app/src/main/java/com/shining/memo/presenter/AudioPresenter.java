package com.shining.memo.presenter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.shining.memo.model.Audio;
import com.shining.memo.model.AudioImpl;
import com.shining.memo.model.AudioModel;
import com.shining.memo.model.Task;
import com.shining.memo.model.TaskImpl;
import com.shining.memo.model.TaskModel;
import com.shining.memo.view.AudioSettingActivity;
import com.shining.memo.view.MainActivity;
import com.shining.memo.view.ViewAudioRecording;
import com.shining.memo.view.ViewAudioSetting;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AudioPresenter {

    private static String filePath = "";     //录音文件路径
    private String FolderPath = "";     //文件夹路径
    private String playFilePath = "";     //播放文件路径
    private MediaRecorder mMediaRecorder;   //音频录制API
    private MediaPlayer mMediaPlayer;
    private static final int MAX_LENGTH = 1000 * 60 * 30;// 最大录音时长1000*60*30;
    private ViewAudioRecording viewAudioRecording;
    private ViewAudioSetting viewAudioSetting;
    private Context context;
    private long startTime,endTime;
    private static final int MSG_PROGRESS_UPDATE = 0x110;
    private TaskModel taskModel;
    private AudioModel audioModel;

    public AudioPresenter(ViewAudioRecording vAudioRec){
        this(Environment.getExternalStorageDirectory()+"/record/");
        this.viewAudioRecording = vAudioRec;
        context = viewAudioRecording.getContext();
    }

    public AudioPresenter(ViewAudioSetting vAudioSet, String filePath){
        this.viewAudioSetting = vAudioSet;
        context = viewAudioSetting.getContext();
        playFilePath = filePath;
        taskModel = new TaskImpl(context);
        audioModel = new AudioImpl(context);
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
                            AudioSettingActivity.isplaying = false;
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
                            AudioSettingActivity.isplaying = false;
                            //释放播放器
                            return true;
                        }
                    });
                    //配置音量，是否循环
                    mMediaPlayer.setVolume(1,1);
                    mMediaPlayer.setLooping(false);
                    mMediaPlayer.prepare();
                    intervalCalculate();
                    Log.d("intervalProgress",String.valueOf(intervalProgress));
                    roundProgress = 0;
                    viewAudioSetting.onUpdateProgress(roundProgress);
                    Log.d("reset progress", "roundprogress"+roundProgress);
                    mHandlerProgress.sendEmptyMessage(MSG_PROGRESS_UPDATE);
                    mMediaPlayer.start();

                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(context,"play failed",Toast.LENGTH_SHORT).show();
                    //提示用户
                    mMediaPlayer.reset();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                    AudioSettingActivity.isplaying = false;
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

    int roundProgress = 0;
    float intervalProgress;

    private void intervalCalculate(){
        int audioTime = mMediaPlayer.getDuration() / 1000;
        intervalProgress = (float)(100 * 1.0 / audioTime /10);
    }

    private Handler mHandlerProgress = new Handler() {
        public void handleMessage(android.os.Message msg) {
            roundProgress += intervalProgress;
            viewAudioSetting.onUpdateProgress(roundProgress);
            if (roundProgress >= 100) {
                mHandlerProgress.removeMessages(MSG_PROGRESS_UPDATE);
            }
            mHandlerProgress.sendEmptyMessageDelayed(MSG_PROGRESS_UPDATE, 100);
        };
    };

    public void saveAudio(Task task,String filePath){
        long taskId = taskModel.addTask(task);
        Audio audio = new Audio();
        audio.setTaskId(taskId);
        audio.setPath(filePath);
        audioModel.saveAudio(audio);
        Intent intent = new Intent();
        intent.setClass(context, MainActivity.class);
        context.startActivity(intent);
    }
}

