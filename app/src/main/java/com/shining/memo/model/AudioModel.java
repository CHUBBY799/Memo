package com.shining.memo.model;

import org.json.JSONObject;

public interface AudioModel {
    JSONObject getAudio();
    void saveAudio(Audio audio);
}
