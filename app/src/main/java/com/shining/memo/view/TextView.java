package com.shining.memo.view;

import android.content.Context;

import org.json.JSONObject;

public interface TextView {
    Context getContext();
    JSONObject onInfoSave();
    void onInfoUpdate(JSONObject textInfo);
}
