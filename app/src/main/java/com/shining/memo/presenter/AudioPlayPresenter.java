package com.shining.memo.presenter;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Button;

import com.shining.memo.utils.ToastUtils;

public class AudioPlayPresenter {

    private String playFilePath = "";     //录音文件路径
    private static MediaPlayer mMediaPlayer;
    private Context context;
    private onStopPlay onStopPlay;
    public Button currentButton;

    public AudioPlayPresenter(Context context, onStopPlay onStop) {
        this.context = context;
        onStopPlay = onStop;
    }

    public void setPlayFilePath(String playFilePath) {
        this.playFilePath = playFilePath;
    }

    public synchronized void doPlay(){
        if(!playFilePath.equals("")){
            //配置播放器 MediaPlayer
            if(mMediaPlayer == null){
                mMediaPlayer = new MediaPlayer();
                try {
                    mMediaPlayer.setDataSource(playFilePath);
                    //设置监听回调
                    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mMediaPlayer.reset();
                            mMediaPlayer.release();
                            mMediaPlayer = null;
                            playFilePath = "";
                            onStopPlay.onStopPlay(currentButton);
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
                            //释放播放器
                            onStopPlay.onStopPlay(currentButton);
                            return true;
                        }
                    });
                    //配置音量，是否循环
                    mMediaPlayer.setVolume(1,1);
                    mMediaPlayer.setLooping(false);
                    mMediaPlayer.prepare();
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
                    onStopPlay.onStopPlay(currentButton);
                }
            }else {
                mMediaPlayer.start();
            }
        }
    }

    public synchronized void onStop(){
        if(mMediaPlayer != null){
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            playFilePath = "";
            onStopPlay.onStopPlay(currentButton);
        }
    }

    public void onPausePlay(){
        mMediaPlayer.pause();
    }


    public interface onStopPlay{
        void onStopPlay(Button btn);
    }
}
