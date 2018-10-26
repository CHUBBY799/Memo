package com.shining.memo.presenter;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.shining.memo.R;
import com.shining.memo.utils.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class AudioPlayPresenter {
    private static final int MSG_PROGRESS_UPDATE = 0xa1;
    private String playFilePath = "";     //录音文件路径
    public static MediaPlayer mMediaPlayer;
    private Context context;
    private onStopPlay onStopPlay;
    public ImageButton currentButton;
    public TextView mTvTime;
    public SeekBar seekBar;
    public String timeDuration;
    public AudioManager audioManager;

    public AudioPlayPresenter(Context context, onStopPlay onStop) {
        this.context = context;
        onStopPlay = onStop;
        audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
    }

    public void setPlayFilePath(String playFilePath) {
        this.playFilePath = playFilePath;
    }

    public synchronized void doPlay(){
        if(!playFilePath.equals("")){
            //配置播放器 MediaPlayer
            if(mMediaPlayer == null){
                mMediaPlayer = new MediaPlayer();
                audioManager.requestAudioFocus(afChangeListener,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
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
                            mHandlerProgress.removeMessages(MSG_PROGRESS_UPDATE);
                            onStopPlay.onStopPlay(currentButton);
                            mTvTime.setText(timeDuration);
                            seekBar.setProgress(0);
                            return true;
                        }
                    });
                    //配置音量，是否循环
                    mMediaPlayer.setVolume(1,1);
                    mMediaPlayer.setLooping(false);
                    mMediaPlayer.prepare();
                    totalTime = mMediaPlayer.getDuration();
                    mMediaPlayer.start();
                    progress = 0;
                    mHandlerProgress.sendEmptyMessage(MSG_PROGRESS_UPDATE);
                }catch (Exception e){
                    e.printStackTrace();
                    ToastUtils.showShort(context,context.getResources().getString(R.string.audio_play_failed));
                    //提示用户
                    mMediaPlayer.reset();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                    playFilePath = "";
                    mHandlerProgress.removeMessages(MSG_PROGRESS_UPDATE);
                    onStopPlay.onStopPlay(currentButton);
                    mTvTime.setText(timeDuration);
                    seekBar.setProgress(0);
                }
            }else {
                mMediaPlayer.start();
                mHandlerProgress.sendEmptyMessage(MSG_PROGRESS_UPDATE);
            }
        }
    }

    public synchronized void onStop(){
        if(mMediaPlayer != null){
            audioManager.abandonAudioFocus(afChangeListener);
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            playFilePath = "";
            onStopPlay.onStopPlay(currentButton);
            if(mTvTime != null)
                mTvTime.setText(timeDuration);
            if(seekBar != null)
                seekBar.setProgress(0);
        }
    }

    public void onPausePlay(){
        if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
            mHandlerProgress.removeMessages(MSG_PROGRESS_UPDATE);
        }
    }


    public interface onStopPlay{
        void onStopPlay(ImageButton btn);
        void onInterruptPause(ImageButton btn);
    }

    private int progress,totalTime;
    private Handler mHandlerProgress = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (mMediaPlayer == null) {
                mHandlerProgress.removeMessages(MSG_PROGRESS_UPDATE);
                mTvTime.setText(timeDuration);
                seekBar.setProgress(0);
            }
            else{
                updateProgress();
                seekBar.setProgress(progress);
                mTvTime.setText(getRemaining());
                mHandlerProgress.sendEmptyMessageDelayed(MSG_PROGRESS_UPDATE, 100);
            }
        };
    };


    private String getRemaining() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        if(mMediaPlayer.getCurrentPosition() < totalTime){
            return sdf.format(new Date((int)Math.ceil((double)totalTime - mMediaPlayer.getCurrentPosition()+mMediaPlayer.getCurrentPosition()%1000)));
        }
        else
            return sdf.format(new Date((int)Math.ceil((double)totalTime - mMediaPlayer.getCurrentPosition())));
    }

    private void updateProgress(){
        if((int)(mMediaPlayer.getCurrentPosition() * 1.0f/totalTime * 100) > progress)
            progress =  (int)(mMediaPlayer.getCurrentPosition() * 1.0f/totalTime * 100);
    }

    public void seekTo(int progress){
        mMediaPlayer.seekTo((int)(progress*1.0f/100*totalTime));
        this.progress = progress;
        mHandlerProgress.sendEmptyMessage(MSG_PROGRESS_UPDATE);
    }

    public void removeHandler(){
        mHandlerProgress.removeMessages(MSG_PROGRESS_UPDATE);
    }

    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if(focusChange == AudioManager.AUDIOFOCUS_LOSS){
                onStopPlay.onInterruptPause(currentButton);
            }
        }
    };

}
