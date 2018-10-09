package com.shining.memo.presenter;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.shining.memo.utils.ToastUtils;
import com.shining.memo.view.ViewRecord;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AudioRecordPresenter {

    private static String filePath = "";     //录音文件路径
    private String FolderPath = "";     //文件夹路径
    private MediaRecorder mMediaRecorder;   //音频录制API
    private static final int MAX_LENGTH = 1000 * 60 * 120;// 最大录音时长1000*60*30;
    private ViewRecord viewAudioRecording;
    private Context context;
    private long startTime,endTime;
    public AudioRecordPresenter(ViewRecord vAudioRec){
        this(Environment.getExternalStorageDirectory()+"/OhMemo/recording/");
        this.viewAudioRecording = vAudioRec;
        context = viewAudioRecording.getContext();
    }

    public AudioRecordPresenter(String filePath) {

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
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            Date date = new Date(System.currentTimeMillis());     //获取当前时间
            filePath = FolderPath + "RCD_" + simpleDateFormat.format(date) + ".amr" ;
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
            if(((endTime - startTime) / 1000) < 1) {
                File file = new File(filePath);
                if (file.exists())
                    file.delete();
                Toast.makeText(context, "The recording time is less than 1S. Please rerecord it.", Toast.LENGTH_SHORT).show();
            }
            else {
                viewAudioRecording.onStop(filePath,"audio");
            }
            filePath = "";
        }catch (RuntimeException e){
            Log.e("Exception",e.getMessage());
            if(mMediaRecorder != null){
                mMediaRecorder.reset();
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
            File file = new File(filePath);
            if (file.exists())
                file.delete();
            filePath = "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return endTime - startTime;
    }

    /**
     * 取消录音
     */
    public void cancelRecord(){
        if(mMediaRecorder != null){
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
        else {
            ToastUtils.showShort(context,"Recording has not started yet!");
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
    private int SPACE = 1000;// 间隔取样时间
    private void updateMicStatus() {

        if (mMediaRecorder != null) {
            double ratio = (double)mMediaRecorder.getMaxAmplitude() / BASE;
            Log.d("ratio",String.valueOf(ratio));
            double db = 0;// 分贝
            if (ratio > 0) {
                db = 20 * Math.log10(ratio);
                Log.d("db",String.valueOf(db));
                if(null != viewAudioRecording) {
                    viewAudioRecording.onUpdate(db,System.currentTimeMillis()-startTime);
                }
            }
            mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
        }
    }

}
