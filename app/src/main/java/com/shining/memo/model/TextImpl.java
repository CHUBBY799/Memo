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
            String title =textInfo.getString("title");
            String content = textInfo.getString("content");
            Log.d("TextImpl",title+"  "+content);
        }catch (JSONException e){
            e.printStackTrace();
        }

    }
}
