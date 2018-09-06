package com.shining.memo.model;

import org.json.JSONObject;

public interface TextModel {
    JSONObject getInfo(String title);
    void setInfo(JSONObject textInfo);
}
