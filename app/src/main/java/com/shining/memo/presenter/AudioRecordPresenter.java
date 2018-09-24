package com.shining.memo.presenter;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.shining.memo.model.AudioModel;
import com.shining.memo.model.MemoDatabaseHelper;
import com.shining.memo.model.RecordingContent;
import com.shining.memo.model.TaskModel;
import com.shining.memo.view.ViewAudioRecording;
import com.shining.memo.view.ViewRecord;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AudioRecordPresenter {

    private static String filePath = "";     //录音文件路径
    private String FolderPath = "";     //文件夹路径
    private MediaRecorder mMediaRecorder;   //音频录制API
    private static final int MAX_LENGTH = 1000 * 60 * 30;// 最大录音时长1000*60*30;
    private ViewRecord viewAudioRecording;
    private Context context;
    private long startTime,endTime;
    public AudioRecordPresenter(ViewRecord vAudioRec){
        this(Environment.getExternalStorageDirectory()+"/record/");
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
            if(((endTime - startTime) / 1000) < 1) {
                File file = new File(filePath);
                if (file.exists())
                    file.delete();
                Toast.makeText(context, "The recording time is less than 1S. Please rerecord it.", Toast.LENGTH_SHORT).show();
            }
            else {
                viewAudioRecording.onStop(filePath);
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
        } catch (IOException e) {
            e.printStackTrace();
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

    private int BASE = 100;
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

    public static int insertIndex = -1;
    public HashMap<Integer, RecordingContent> insertAudioRecording(HashMap<Integer, RecordingContent> oldMap, List<String> text, int index,String filePath) {
        HashMap<Integer, RecordingContent> map = new HashMap<>();
        int number = 0;
        if(text != null && oldMap != null){
            if(!text.get(0).equals(""))
                number = text.size();
            else
                number = 1;
            String color = oldMap.get(index).getColor();
            for(int i = oldMap.size() - 1; i > index; i--){
                map.put(i + number,oldMap.get(i));
            }
            for(int i = 0; i < index; i++){
                map.put(i,oldMap.get(i));
            }
            if(number > 1){
                RecordingContent content = new RecordingContent();
                content.setType("text");
                content.setContent(text.get(number - 1));
                content.setColor(color);
                map.put(index + number,content);
                content = new RecordingContent();
                content.setType("audio");
                content.setContent(filePath);
                content.setColor(color);
                map.put(index + 1,content);
                insertIndex = index + 1;
                Log.d("TAG", "onStop: "+ index);
                Log.d("TAG", "onStop: "+ insertIndex);
                content = new RecordingContent();
                content.setType("text");
                content.setContent(text.get(number - 2));
                content.setColor(color);
                map.put(index,content);
            }else {
                if(text.size() > 1 || (text.size() ==1 && text.get(0).equals(""))){
                    Log.d("EDG", "insertAudioRecording: if");
                    RecordingContent content = new RecordingContent();
                    content.setType("text");
                    if(text.size() > 1)
                        content.setContent(text.get(1));
                    else
                        content.setContent(text.get(0));
                    content.setColor(color);
                    map.put(index + 1,content);
                    content = new RecordingContent();
                    content.setType("audio");
                    content.setContent(filePath);
                    content.setColor(color);
                    map.put(index,content);
                    insertIndex = index;
                }
                else {
                    RecordingContent content = new RecordingContent();
                    content.setType("text");
                    content.setContent(text.get(0));
                    content.setColor(color);
                    map.put(index,content);
                    content = new RecordingContent();
                    content.setType("audio");
                    content.setContent(filePath);
                    content.setColor(color);
                    map.put(index + 1,content);
                    insertIndex = index + 1;
                }
            }
            String newStr = "";
            for(int i = 0;i < viewAudioRecording.getDefaultNumber() - 1;i++)
                newStr += "\n";
            map.get(map.size() - 1).setContent(newStr);
            viewAudioRecording.DefaultEditText(number - 1, map.size() - 1);
        }
        return map;
    }

    public void insertAudioRecording(HashMap<Integer, RecordingContent> map,String filePath,int number) {
        int index = map.size();
        String color,strContent = map.get(index - 1).getContent();
        if(index > 1)
            color = map.get(index - 1).getColor();
        else
            color = "#000000";
        strContent = strContent.replace("\n","");
        if(strContent.equals(""))
        {
            RecordingContent content = map.get(index -1);
            StringBuffer strBuf = new StringBuffer();
            for (int i = 0; i < number - 1; i++)
                strBuf.append("\n");
            content.setContent(strBuf.toString());
            map.put(index,content);
            content = new RecordingContent();
            content.setType("audio");
            content.setContent(filePath);
            content.setColor(color);
            map.put(index - 1,content);
            insertIndex = index - 1;
            viewAudioRecording.DefaultEditText(number - 1,index);
        }
    }
}
