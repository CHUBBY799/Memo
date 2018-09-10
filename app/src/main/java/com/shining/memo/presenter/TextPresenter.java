package com.shining.memo.presenter;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.shining.memo.model.TextImpl;
import com.shining.memo.model.TextModel;
import com.shining.memo.view.MainActivity;
import com.shining.memo.view.TextView;

import org.json.JSONObject;

public class TextPresenter {

    private TextModel mModel;
    private TextView mView;
    private Context context;


    public TextPresenter(TextView mView) {
        this.mView = mView;
        context = mView.getContext();
        mModel = new TextImpl(context);
    }

    public void requestTextInfo(String title){
        JSONObject TextInfo = getTextInfo(title);
        updateTextInfo(TextInfo);
    }

    public void responseTextInfo(){
        JSONObject textInfo = saveTextInfo();
        setTextInfo(textInfo);
        Toast.makeText(context, "添加成功", Toast.LENGTH_SHORT).show();
        Intent mainIntent = new Intent(context, MainActivity.class);
        context.startActivity(mainIntent);
    }

    private void updateTextInfo(JSONObject textInfo){
        if (mView != null) {
            mView.onInfoUpdate(textInfo);
        }
    }

    private JSONObject saveTextInfo(){
        if (mView != null) {
            return mView.onInfoSave();
        }
        return null;
    }

    private JSONObject getTextInfo(String title){
        return mModel.getInfo(title);
    }

    private void setTextInfo(JSONObject textInfo){
        mModel.setInfo(textInfo);
    }
}
