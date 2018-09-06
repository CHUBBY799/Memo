package com.shining.memo.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class TextImpl implements TextModel{
    @Override
    public JSONObject getInfo(String title){
        return null;
    }
    @Override
    public  void  setInfo(JSONObject textInfo){
        try {
            String type = textInfo.getString("type");
            String title =textInfo.getString("title");
            String content = textInfo.getString("content");
            String color = textInfo.getString("color");
            int urgent = textInfo.getInt("urgent");
            String date = textInfo.getString("date");
            String time = textInfo.getString("time");
            Log.d("TextImpl", type + " " + title + " " + content + " " + color + " " + urgent + " " + date + " " + time);
        }catch (JSONException e){
            e.printStackTrace();
        }

    }
}
