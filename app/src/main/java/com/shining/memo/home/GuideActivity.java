package com.shining.memo.home;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.shining.memo.R;
import com.shining.memo.home.adapter.GuideAdapter;

import java.util.ArrayList;

public class GuideActivity extends AppCompatActivity {

    private ViewPager guidePager;
    private ImageView guideImage;
    private TextView guideTitle;
    private TextView guideContent;
    private Button guideButton;
    private TextView guideDot_1;
    private TextView guideDot_2;
    private TextView guideDot_3;
    private ArrayList<ImageView> imageViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initView();
        initListener();
        initImages();
        GuideAdapter guideAdapter = new GuideAdapter(imageViews);
        guidePager.setAdapter(guideAdapter);
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener(){

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        @Override
        public void onPageSelected(int position) {

            if(position == 0){
                guideImage.setBackground(getDrawable(R.drawable.guide_page_one_image));
                guideTitle.setText(getString(R.string.guide_title_1));
                guideContent.setText(getString(R.string.guide_content_1));
                guideDot_1.setBackground(getDrawable(R.drawable.orange_dot));
                guideDot_2.setBackground(getDrawable(R.drawable.white_dot));
                guideDot_3.setBackground(getDrawable(R.drawable.white_dot));
                guideButton.setVisibility(View.GONE);
            }else if(position == 1){
                guideImage.setBackground(getDrawable(R.drawable.guide_page_two_image));
                guideTitle.setText(getString(R.string.guide_title_2));
                guideContent.setText(getString(R.string.guide_content_2));
                guideDot_1.setBackground(getDrawable(R.drawable.white_dot));
                guideDot_2.setBackground(getDrawable(R.drawable.orange_dot));
                guideDot_3.setBackground(getDrawable(R.drawable.white_dot));
                guideButton.setVisibility(View.GONE);
            }else {
                guideImage.setBackground(getDrawable(R.drawable.guide_page_three_image));
                guideTitle.setText(getString(R.string.guide_title_3));
                guideContent.setText(getString(R.string.guide_content_3));
                guideDot_1.setBackground(getDrawable(R.drawable.white_dot));
                guideDot_2.setBackground(getDrawable(R.drawable.white_dot));
                guideDot_3.setBackground(getDrawable(R.drawable.orange_dot));
                guideButton.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {}
    };

    private void initImages(){
        //设置每一张图片都填充窗口
        ViewPager.LayoutParams mParams = new ViewPager.LayoutParams();
        imageViews = new ArrayList<>();
        for(int i=0 ; i<3 ; i++) {
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(mParams);//设置布局
            iv.setImageResource(R.drawable.guide_pager);//为ImageView添加图片资源
            iv.setScaleType(ImageView.ScaleType.FIT_XY);//这里也是一个图片的适配
            imageViews.add(iv);
        }
    }

    private void initView(){
        guidePager = findViewById(R.id.guide_pager);
        guideImage = findViewById(R.id.guide_image);
        guideTitle = findViewById(R.id.guide_title);
        guideContent = findViewById(R.id.guide_content);
        guideButton = findViewById(R.id.guide_button);
        guideDot_1 = findViewById(R.id.guide_dot_1);
        guideDot_2 = findViewById(R.id.guide_dot_2);
        guideDot_3 = findViewById(R.id.guide_dot_3);
    }

    private void initListener(){
        guidePager.addOnPageChangeListener(viewPagerPageChangeListener);
        guideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuideActivity.this, MemoActivity.class);
                startActivity(intent);
            }
        });
    }
}
