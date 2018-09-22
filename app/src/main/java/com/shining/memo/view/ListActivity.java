package com.shining.memo.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.shining.memo.R;

public class ListActivity extends AppCompatActivity implements View.OnClickListener{

    private Button listCancel;
    private Button listThumbtack;
    private Button listDelete;
    private Button listConfirm;
    private EditText listTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        initView();
        initComponent();
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.list_cancel:
                break;
            case R.id.list_thumbtack:
                break;
            case R.id.list_delete:
                break;
            case R.id.list_confirm:
                listConfirm();
                break;
            default:
                break;
        }
    }

    private void listConfirm(){
        Intent textIntent = new Intent();
        String title = listTitle.getText().toString();
        textIntent.putExtra("title", title);
        setResult(RESULT_OK, textIntent);
        finish();
    }

    private void initView(){
        listCancel = findViewById(R.id.list_cancel);
        listThumbtack = findViewById(R.id.list_thumbtack);
        listDelete = findViewById(R.id.list_delete);
        listConfirm = findViewById(R.id.list_confirm);
        listTitle = findViewById(R.id.list_title);
    }

    private void initComponent(){
        listCancel.setOnClickListener(this);
        listThumbtack.setOnClickListener(this);
        listDelete.setOnClickListener(this);
        listConfirm.setOnClickListener(this);
    }
}
