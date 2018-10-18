package com.shining.memo.home;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.shining.memo.R;
import com.shining.memo.home.adapter.GuideAdapter;

import java.util.ArrayList;

public class GuideActivity extends AppCompatActivity {

    private ViewPager guidePager;
    private ArrayList<View> guideLayouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initView();
        //initListener();
        initLayout();
        GuideAdapter guideAdapter = new GuideAdapter(guideLayouts);
        guidePager.setAdapter(guideAdapter);
    }

    @SuppressWarnings("all")
    private void initLayout(){

        guideLayouts = new ArrayList<>();
        View layoutView;

        layoutView = LayoutInflater.from(this).inflate(R.layout.guide_one, null, false);
        guideLayouts.add(layoutView);

        layoutView = LayoutInflater.from(this).inflate(R.layout.guide_two, null, false);
        guideLayouts.add(layoutView);

        layoutView = LayoutInflater.from(this).inflate(R.layout.guide_three, null, false);
        Button guideButton = layoutView.findViewById(R.id.guide_button);
        guideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClick();
            }
        });
        guideLayouts.add(layoutView);
    }

    private void initView(){
        guidePager = findViewById(R.id.guide_pager);
    }

    public void buttonClick(){
        Intent intent = new Intent(GuideActivity.this, MemoActivity.class);
        startActivity(intent);
        finish();
    }
}
