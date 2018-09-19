package com.shining.memo.view;

import android.content.Context;

import org.json.JSONObject;

public interface ViewAudioEdit {
    Context getContext();
    void onUpdateProgress(int progress);
    void onStopPlay();
    void onRemoverPlay();
}
