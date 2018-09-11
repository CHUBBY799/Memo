package com.shining.memo.view;

import android.content.Context;

import org.json.JSONObject;

public interface ViewText {
    Context getContext();
    JSONObject onInfoSave();
    void onInfoUpdate(JSONObject textInfo);
}
