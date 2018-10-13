package com.shining.memo.view;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;

import com.shining.memo.R;

public class NoteActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_edit);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

    }
}
