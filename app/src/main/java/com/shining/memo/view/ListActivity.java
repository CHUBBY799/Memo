package com.shining.memo.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.shining.memo.R;
import com.shining.memo.adapter.ListItemAdapter;
import com.shining.memo.bean.ListBean;
import com.shining.memo.presenter.ListPresenter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ListActivity extends AppCompatActivity implements View.OnClickListener,ViewList{

    private Button listCancel;
    private Button listConfirm;
    private EditText listTitle;
    private RecyclerView listContent;
    private ListItemAdapter listItemAdapter;

    private int id;
    private String title;
    private JSONArray itemArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        if(id != -1){
            title = intent.getStringExtra("title");
            try{
                itemArr = new JSONArray(intent.getStringExtra("itemArr"));
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        initView();
        initComponent();
        initData();
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.list_cancel:
                listCancel();
                break;
            case R.id.list_confirm:
                listConfirm();
                break;
            default:
                break;
        }
    }

    @Override
    public Context getContext(){
        return this;
    }

    /**
     * 格式化数据,以便插入数据库
     * @return listData
     */
    @Override
    public ListBean formatData(){
        ListBean listBean = new ListBean();
        listBean.setId(id);
        listBean.setTitle(title);
        listBean.setItemArr(itemArr.toString());
        return listBean;
    }

    /**
     * 退出
     */
    private void listCancel(){
        Intent listIntent = new Intent();
        setResult(RESULT_CANCELED, listIntent);
        finish();
    }

    /**
     * 保存并向数据库中插入数据
     */
    private void listConfirm(){
        listConfirm.setFocusable(true);
        listConfirm.setFocusableInTouchMode(true);
        listConfirm.requestFocus();

        title = listTitle.getText().toString();
        itemArr = listItemAdapter.getItemArr();
        itemArr.remove(itemArr.length() - 1);
        ListPresenter listPresenter = new ListPresenter(this);

        if(id == -1){
            listPresenter.insertPresenter(formatData());
        }else {
            listPresenter.updatePresenter(formatData());
        }
        finish();
    }

    /**
     * 初始化视图
     */
    private void initView(){
        listCancel = findViewById(R.id.list_cancel);
        listConfirm = findViewById(R.id.list_confirm);
        listTitle = findViewById(R.id.list_title);

        listContent = findViewById(R.id.list_content);
        listContent.setLayoutManager(new LinearLayoutManager(this));
        listItemAdapter = new ListItemAdapter(this);
    }

    /**
     * 初始化组件
     */
    private void initComponent(){
        listCancel.setOnClickListener(this);
        listConfirm.setOnClickListener(this);
    }

    /**
     * 初始化数据
     */
    private void initData(){
        if(id == -1){
            itemArr = new JSONArray();
            JSONObject itemInfo = new JSONObject();
            try{
                itemInfo.put("state", false);
                itemInfo.put("content", "");
            }catch (JSONException e){
                e.printStackTrace();
            }
            itemArr.put(itemInfo);
        }else {
            listTitle.setText(title);
        }
        listItemAdapter.setInfo(itemArr, itemArr.length());
        listContent.setAdapter(listItemAdapter);
    }
}
