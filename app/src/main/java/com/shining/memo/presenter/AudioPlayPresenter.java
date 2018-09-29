package com.shining.memo.presenter;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import com.shining.memo.adapter.RecordingAdapter;

public class AudioPlayPresenter {

    private String playFilePath = "";     //录音文件路径
    private static MediaPlayer mMediaPlayer;
    private Context context;
    private onStopPlay onStopPlay;

    public AudioPlayPresenter(Context context, onStopPlay onStop) {
        this.context = context;
        onStopPlay = onStop;
    }

    public String getPlayFilePath() {
        return playFilePath;
    }

    public void setPlayFilePath(String playFilePath) {
        this.playFilePath = playFilePath;
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
                            playFilePath = "";
                            onStopPlay.onStopPlay();
                  //          viewAudioSetting.onStopPlay();
                        }
                    });

                    //设置出错的监听器
                    mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mp, int what, int extra) {
                            //提示用户
                            mMediaPlayer.reset();
                            mMediaPlayer.release();
                            mMediaPlayer = null;
                 //           mHandlerProgress.removeMessages(MSG_PROGRESS_UPDATE);
                //            viewAudioSetting.onStopPlay();
                            //释放播放器
                            onStopPlay.onStopPlay();
                            return true;
                        }
                    });
                    //配置音量，是否循环
                    mMediaPlayer.setVolume(1,1);
                    mMediaPlayer.setLooping(false);
                    mMediaPlayer.prepare();
             //       intervalCalculate();
              //      roundProgress = 0;
             //       viewAudioSetting.onUpdateProgress(roundProgress);
             //       st = System.currentTimeMillis();
            //        mHandlerProgress.sendEmptyMessageDelayed(MSG_PROGRESS_UPDATE, 100);
                    playFilePath = "";
                    mMediaPlayer.start();

                }catch (Exception e){
                    e.printStackTrace();
                    ToastUtils.showShort(context,"failed to play audio");
                    //提示用户
                    mMediaPlayer.reset();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                    playFilePath = "";
                    onStopPlay.onStopPlay();
                //    mHandlerProgress.removeMessages(MSG_PROGRESS_UPDATE);
               //     viewAudioSetting.onStopPlay();
                }
            }else {
                mMediaPlayer.start();
               // mHandlerProgress.sendEmptyMessage(MSG_PROGRESS_UPDATE);

            }
        }
    }

    public synchronized void onStop(){
        if(mMediaPlayer != null){
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            playFilePath = "";
            onStopPlay.onStopPlay();
        }
    }


    public interface onStopPlay{
        void onStopPlay();
    }
}
