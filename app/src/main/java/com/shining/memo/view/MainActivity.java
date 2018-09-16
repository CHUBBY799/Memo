package com.shining.memo.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.shining.memo.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button addText;
    private Button calendar;
    private Button audio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initComponent();
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.add_text:
                addText();
                break;
            case R.id.calendar:
                calendar();
                break;
            case R.id.add_audio:
                audio();
                break;
            default:
                break;
        }
    }

    private void addText(){
        Intent textIntent = new Intent(this,TextActivity.class);
        startActivity(textIntent);
    }

    private void calendar(){
        Intent calendarIntent = new Intent(this,CalendarActivity.class);
        startActivity(calendarIntent);
    }

    private void audio(){
        Intent calendarIntent = new Intent(this,AudioRecordingActivity.class);
        startActivity(calendarIntent);
    }


    private void initView(){
        addText = findViewById(R.id.add_text);
        calendar = findViewById(R.id.calendar);
        audio = findViewById(R.id.add_audio);
    }

    private void initComponent (){
        addText.setOnClickListener(this);
        calendar.setOnClickListener(this);
        audio.setOnClickListener(this);
    }
}