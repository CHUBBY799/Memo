package com.shining.memo.view;

import android.content.Context;

public interface ViewAudioRecording {
    Context getContext();
    void onUpdate(double db, long time);
    void onStop(String filePath);
}
