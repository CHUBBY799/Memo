package com.shining.memo.view;

import android.content.Context;

public interface ViewRecord {
    Context getContext();
    void onUpdateProgress(int progress);
    void onStopPlay();
    void onRemoverPlay();
    void onUpdate(double db, long time);
    void onStop(String filePath);
}
