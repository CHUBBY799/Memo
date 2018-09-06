package com.shining.memo.presenter;

import com.shining.memo.model.TextImpl;
import com.shining.memo.model.TextModel;
import com.shining.memo.view.TextView;

import org.json.JSONObject;

public class TextPresenter {

    private TextModel mModel;
    private TextView mView;


    public TextPresenter(TextView mView) {
        this.mView = mView;
        mModel = new TextImpl();
    }

    public void requestTextInfo(String title){
        JSONObject TextInfo = getTextInfo(title);
        updateTextInfo(TextInfo);
    }

    public void responseTextInfo(){
        JSONObject textInfo = saveTextInfo();
        setTextInfo(textInfo);
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
