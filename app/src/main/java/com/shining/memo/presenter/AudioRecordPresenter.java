package com.shining.memo.presenter;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;

import com.shining.memo.R;
import com.shining.memo.utils.ToastUtils;
import com.shining.memo.view.ViewRecord;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AudioRecordPresenter {

    private static String filePath = "";     //录音文件路径
    private String FolderPath = "";     //文件夹路径
    public MediaRecorder mMediaRecorder;   //音频录制API
    private static final int MAX_LENGTH = 1000 * 60 * 120;// 最大录音时长1000*60*30;
    private ViewRecord viewAudioRecording;
    private Context context;
    private long startTime, endTime;
    private AudioManager audioManager;

    public AudioRecordPresenter(ViewRecord vAudioRec) {
        this(Environment.getExternalStorageDirectory() + "/OhMemo/recording/");
        this.viewAudioRecording = vAudioRec;
        context = viewAudioRecording.getContext();
        audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
    }

    public AudioRecordPresenter(String filePath) {

        File path = new File(filePath);
        if (!path.exists())
            path.mkdirs();
        this.FolderPath = filePath;
    }

    // 开始录音
    public void startRecord() {
        if (mMediaRecorder == null)
            mMediaRecorder = new MediaRecorder();
        try {
            audioManager.requestAudioFocus(null,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = new Date(System.currentTimeMillis());     //获取当前时间
            filePath = FolderPath + simpleDateFormat.format(date) + ".amr";
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
            audioManager.abandonAudioFocus(null);
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            if (((endTime - startTime) / 1000) < 1) {
                File file = new File(filePath);
                if (file.exists())
                    file.delete();
                ToastUtils.showFailedShort(context, context.getResources().getString(R.string.audio_length_limit));
            } else {
                viewAudioRecording.onStop(filePath, "audio");
            }
            filePath = "";
        } catch (Exception e) {
             e.printStackTrace();
            if (mMediaRecorder != null) {
                mMediaRecorder.reset();
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
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
    public void cancelRecord() {
        if (mMediaRecorder != null) {
            try {
                mMediaRecorder.stop();
                mMediaRecorder.reset();
                mMediaRecorder.release();
                mMediaRecorder = null;

            } catch (RuntimeException e) {
                mMediaRecorder.reset();
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
            File file = new File(filePath);
            if (file.exists())
                file.delete();
            filePath = "";
        } else {
            ToastUtils.showFailedShort(context, context.getResources().getString(R.string.audio_prepare));
            viewAudioRecording.onStopActivateRecording();
            long startTime = 0;
        }
    }

    private final Handler mHandler = new Handler();
    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }
    };

    private int BASE = 100;
    private int SPACE = 100;// 间隔取样时间

    private void updateMicStatus() {

        if (mMediaRecorder != null) {
            double ratio = (double) mMediaRecorder.getMaxAmplitude() / BASE;
            double db = 0;// 分贝
            if (ratio > 0) {
                db = 20 * Math.log10(ratio);
                if (null != viewAudioRecording) {
                    viewAudioRecording.onUpdate(db, System.currentTimeMillis() - startTime);
                }
            }
            mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
        }
    }


}
