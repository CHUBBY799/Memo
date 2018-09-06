package com.shining.memo.view;

import org.json.JSONObject;

public interface TextView {
    JSONObject onInfoSave();
    void onInfoUpdate(JSONObject textInfo);
}
