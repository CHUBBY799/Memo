package com.shining.memo.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.shining.memo.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button addText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initEvent();
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.add_text:
                addText();
                break;
            default:
                break;
        }
    }

    private void addText(){
        Intent textIntent = new Intent(this,TextActivity.class);
        startActivity(textIntent);
    }

    private void initView(){
        addText = findViewById(R.id.add_text);
    }

    private void initEvent(){
        addText.setOnClickListener(this);
    }
}