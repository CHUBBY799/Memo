package com.shining.memo.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.shining.memo.R;
import com.shining.memo.presenter.TextPresenter;

import org.json.JSONException;
import org.json.JSONObject;

public class TextActivity extends AppCompatActivity implements View.OnClickListener,TextView{

    private Button textCancel;
    private Button textUrgent;
    private Button textClock;
    private Button textEdit;
    private Button textConfirm;
    private EditText editTitle;
    private EditText editContent;

    private String title;
    private String content;
    private String color;
    private int urgent;
    //private int alarm;
    //private int deleted;
    //private Date date;
    //private Date time;

    private TextPresenter textPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        initView();
        initListener();

        textPresenter = new TextPresenter(this);
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        if(title != null){
            textPresenter.requestTextInfo(title);
        }
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.text_cancel:
                clickCancel();
                break;
            case R.id.text_urgent:
                clickUrgent();
                break;
            case R.id.text_clock:
                clickClock();
                break;
            case R.id.text_edit:
                clickEdit();
                break;
            case R.id.text_confirm:
                clickConfirm();
                break;
            default:
                break;
        }
    }

    private void clickCancel(){
        Intent cancelIntent = new Intent(this,MainActivity.class);
        startActivity(cancelIntent);
    }

    private void clickUrgent(){
        if (urgent == 0){
            urgent = 1;
            textUrgent.setActivated(true);
        }else {
            urgent = 0;
            textUrgent.setActivated(false);
        }
    }

    private void clickClock(){

    }

    private void clickEdit(){

    }

    private void clickConfirm(){
        textPresenter.responseTextInfo();
    }

    @Override
    public JSONObject onInfoSave(){
        JSONObject textInfo = new JSONObject();
        String type = "text";
        title = editTitle.getText().toString();
        content = editContent.getText().toString();
        color = "#" + Integer.toHexString(editContent.getCurrentTextColor()).substring(2);
        try {
            textInfo.put("type", type);
            textInfo.put("title", title);
            textInfo.put("content", content);
            textInfo.put("color", color);
            textInfo.put("urgent", urgent);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return textInfo;
    }

    @Override
    public void onInfoUpdate(JSONObject textInfo){
        try {
            title = textInfo.getString("title");
            content = textInfo.getString("content");
            color = textInfo.getString("color");
            urgent = textInfo.getInt("urgent");
        }catch (JSONException e){
            e.printStackTrace();
        }
        editTitle.setText(title);
        editContent.setText(content);
        editTitle.setTextColor(Color.parseColor(color));
        editContent.setTextColor(Color.parseColor(color));
    }

    private void initView(){
        textCancel = findViewById(R.id.text_cancel);
        textUrgent = findViewById(R.id.text_urgent);
        textClock = findViewById(R.id.text_clock);
        textEdit = findViewById(R.id.text_edit);
        textConfirm = findViewById(R.id.text_confirm);
        editTitle = findViewById(R.id.edit_title);
        editContent = findViewById(R.id.edit_content);
    }

    private void initListener(){
        textCancel.setOnClickListener(this);
        textUrgent.setOnClickListener(this);
        textClock.setOnClickListener(this);
        textEdit.setOnClickListener(this);
        textConfirm.setOnClickListener(this);
    }
}
