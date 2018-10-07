package com.shining.memo.view;

import android.content.Context;

import java.io.IOException;

public interface ViewRecord {
    Context getContext();
    void onUpdate(double db, long time);
    void onStop(String filePath,String type);
    void onStopActivateRecording();
}
