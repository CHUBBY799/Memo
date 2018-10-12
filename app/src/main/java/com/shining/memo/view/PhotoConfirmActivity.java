package com.shining.memo.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.shining.memo.R;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class PhotoConfirmActivity extends Activity implements View.OnClickListener{

    private Button mBtnCancel,mBtnConfirm;
    private ImageView mImageView;
    private String photoPath ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_confirm);
        mBtnCancel = (Button)findViewById(R.id.photo_cancel);
        mBtnConfirm = (Button)findViewById(R.id.photo_confirm);
        mBtnConfirm.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
        mImageView = (ImageView)findViewById(R.id.photo_image);
        photoPath = getIntent().getStringExtra("photoPath");
        Log.d("PhotoConfirmActivity", "onCreate: "+photoPath);
        try {
            FileInputStream in = new FileInputStream(photoPath);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;//图片的长宽都是原来的1/8
            BufferedInputStream bis = new BufferedInputStream(in);
            Bitmap bm = BitmapFactory.decodeStream(bis, null, options);
            mImageView.setImageBitmap(bm);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            mImageView.setImageResource(R.drawable.alarm_clock_btn_48x48px);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.photo_cancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.photo_confirm:
                Intent intent = new Intent();
                intent.putExtra("photoPath",photoPath);
                setResult(RESULT_OK,intent);
                finish();
                break;
        }
    }
}
