package com.shining.memo.home;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.shining.memo.R;
import com.shining.memo.home.adapter.GuideAdapter;

import java.util.ArrayList;

public class GuideActivity extends AppCompatActivity {

    private ViewPager guidePager;
    private ArrayList<View> guideLayouts;
    private TextView guideDot_1;
    private TextView guideDot_2;
    private TextView guideDot_3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initView();
        initLayout();
        GuideAdapter guideAdapter = new GuideAdapter(guideLayouts);
        guidePager.setAdapter(guideAdapter);
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener(){

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        @Override
        public void onPageSelected(int position) {

            switch (position){
                case 0:
                    guideDot_1.setBackground(getDrawable(R.drawable.dot_orange));
                    guideDot_2.setBackground(getDrawable(R.drawable.dot_white));
                    guideDot_3.setBackground(getDrawable(R.drawable.dot_white));
                    break;
                case 1:
                    guideDot_1.setBackground(getDrawable(R.drawable.dot_white));
                    guideDot_2.setBackground(getDrawable(R.drawable.dot_orange));
                    guideDot_3.setBackground(getDrawable(R.drawable.dot_white));
                    break;
                case 2:
                    guideDot_1.setBackground(getDrawable(R.drawable.dot_white));
                    guideDot_2.setBackground(getDrawable(R.drawable.dot_white));
                    guideDot_3.setBackground(getDrawable(R.drawable.dot_orange));
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {}
    };

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
        guideDot_1 = findViewById(R.id.guide_dot_1);
        guideDot_2 = findViewById(R.id.guide_dot_2);
        guideDot_3 = findViewById(R.id.guide_dot_3);

        guidePager.addOnPageChangeListener(viewPagerPageChangeListener);
    }

    public void buttonClick(){
        Intent intent = new Intent(GuideActivity.this, MemoActivity.class);
        startActivity(intent);
        finish();
    }
}
