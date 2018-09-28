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

    private String title;
    private JSONArray itemArr;

    private ListPresenter listPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
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
        listBean.setState(false);
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
     * 保存并向主界面返回数据
     */
    private void listConfirm(){
        listConfirm.setFocusable(true);
        listConfirm.setFocusableInTouchMode(true);
        listConfirm.requestFocus();

        title = listTitle.getText().toString();
        itemArr = listItemAdapter.getItemArr();
        itemArr.remove(itemArr.length() - 1);

        listPresenter = new ListPresenter(this);
        listPresenter.insertPresenter();

        Intent listIntent = new Intent();
        listIntent.putExtra("state",false);
        listIntent.putExtra("title", title);
        listIntent.putExtra("itemArr", itemArr.toString());
        setResult(RESULT_OK, listIntent);
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
        itemArr = new JSONArray();
        JSONObject itemInfo = new JSONObject();
        try{
            itemInfo.put("state", false);
            itemInfo.put("content", "");
        }catch (JSONException e){
            e.printStackTrace();
        }
        itemArr.put(itemInfo);
        listItemAdapter.setInfo(itemArr, itemArr.length());
        listContent.setAdapter(listItemAdapter);
    }
}
