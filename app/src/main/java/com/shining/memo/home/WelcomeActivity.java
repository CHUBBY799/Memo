package com.shining.memo.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.shining.memo.R;

public class WelcomeActivity extends AppCompatActivity implements Runnable{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        new  Thread(this).start();
    }

    public void run(){
        try{
            //延迟秒时间
            Thread.sleep(300);
            SharedPreferences preferences= getSharedPreferences("count", 0);// 存在则打开它，否则创建新的Preferences
            int count = preferences.getInt("count", 0);// 取出数据

            if (count == 0) {
                Intent intent = new Intent();
                intent.setClass(WelcomeActivity.this, GuideActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent();
                intent.setClass(WelcomeActivity.this, MemoActivity.class);
                startActivity(intent);
            }
            finish();
            //实例化Editor对象
            SharedPreferences.Editor editor = preferences.edit();
            //存入数据
            editor.putInt("count", 1);
            //提交修改
            editor.apply();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
