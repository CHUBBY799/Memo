package com.shining.memo.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.shining.memo.R;
import com.shining.memo.adapter.ListItemAdapter;
import com.shining.memo.bean.ListBean;
import com.shining.memo.presenter.ListPresenter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ListActivity extends AppCompatActivity implements View.OnClickListener,ViewList{

    private final static int CANCEL = 1;
    private final static int DELETE = 2;
    private final static int CONFIRM = 3;

    private ImageButton listCancel;
    private ImageButton listConfirm;
    private ImageButton listDelete;
    private EditText listTitle;
    private RecyclerView listContent;
    private ListItemAdapter listItemAdapter;

    private int id;
    private int selected;
    private String title;
    private JSONArray itemArr;
    private String initItemArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        selected = intent.getIntExtra("selected", 0);
        if(id != -1){
            title = intent.getStringExtra("title");
            try{
                itemArr = new JSONArray(intent.getStringExtra("itemArr"));
            }catch (JSONException e){
                e.printStackTrace();
            }
        }else {
            title = "";
            itemArr = new JSONArray();
        }
        initItemArr = itemArr.toString();

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
            case R.id.list_delete:
                listDelete();
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
        listBean.setSelected(selected);
        listBean.setTitle(title);
        listBean.setItemArr(itemArr.toString());
        return listBean;
    }

    /**
     * 退出
     */
    private void listCancel(){
        buttonFocus(CANCEL, true);
        if (title.equals(listTitle.getText().toString())&& initItemArr.equals(itemArr.toString())){
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }else {
            buildDialog(CANCEL);
        }
        buttonFocus(CANCEL, false);
    }

    /**
     * 删除
     */
    private void listDelete(){
        buttonFocus(DELETE, true);
        buildDialog(DELETE);
        buttonFocus(DELETE, false);
    }

    /**
     * 保存
     */
    private void listConfirm(){
        buttonFocus(CONFIRM, true);
        buildDialog(CONFIRM);
        buttonFocus(CONFIRM, false);
    }

    /**
     * 添加
     */
    private void addItem(){
        try {
            JSONObject itemInfo = new JSONObject();
            itemInfo.put("state", false);
            itemInfo.put("content", "");
            listItemAdapter.addInfo(itemInfo);
            listContent.scrollToPosition(itemArr.length());
        }catch (JSONException e){
            e.printStackTrace();
        }
        listTitle.setFocusable(true);
        listTitle.setFocusableInTouchMode(true);
        listTitle.requestFocus();
    }

    /**
     * 初始化视图
     */
    private void initView(){
        listCancel = findViewById(R.id.list_cancel);
        listConfirm = findViewById(R.id.list_confirm);
        listDelete = findViewById(R.id.list_delete);
        listTitle = findViewById(R.id.list_title);

        listContent = findViewById(R.id.list_content);
        listContent.setLayoutManager(new LinearLayoutManager(this));
        listItemAdapter = new ListItemAdapter(this);
        listItemAdapter.setOnItemClickListener(new ListItemAdapter.OnItemClickListener(){
            @Override
            public void onClick(int position) {
                addItem();
            }
         });
    }

    /**
     * 初始化组件
     */
    private void initComponent(){
        listCancel.setOnClickListener(this);
        listConfirm.setOnClickListener(this);
        listDelete.setOnClickListener(this);
    }

    /**
     * 建立确认弹窗
     * @param buttonType 点击的button类型
     */
    private void buildDialog(int buttonType){
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialog);
        switch (buttonType){
            case CANCEL:
                builder.setTitle("Back");
                builder.setMessage("Do you want to discard changes");
                builder.setNegativeButton("Cancel", null);
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    }
                });
                dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                break;

            case DELETE:
                builder.setTitle("Delete");
                builder.setMessage("Do you want to delete the list");
                builder.setNegativeButton("Cancel", null);
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ListPresenter listPresenter = new ListPresenter(ListActivity.this);
                        if(id != -1){
                            listPresenter.deletePresenter(String.valueOf(id));
                        }
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    }
                });
                dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                break;

            case CONFIRM:
                title = listTitle.getText().toString();
                if(title.equals("")){
                    Toast.makeText(ListActivity.this, "Please input title", Toast.LENGTH_SHORT).show();
                    listTitle.setFocusable(true);
                    listTitle.setFocusableInTouchMode(true);
                    listTitle.requestFocus();
                }else{
                    builder.setTitle("Save");
                    builder.setMessage("Do you want to save the list");
                    builder.setNegativeButton("Cancel", null);
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ListPresenter listPresenter = new ListPresenter(ListActivity.this);

                            if(id == -1){
                                listPresenter.insertPresenter(formatData());
                            }else {
                                listPresenter.updatePresenter(formatData());
                            }
                            finish();
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        }
                    });
                    dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }
                break;

            default:
                break;
        }
    }

    /**
     * button 得到和失去焦点
     * @param buttonType button的类型
     * @param focusType true为得到焦点， false为失去焦点
     */
    private void buttonFocus(int buttonType, boolean focusType){
        if (focusType){
            switch (buttonType){
                case CANCEL:
                    listCancel.setFocusable(true);
                    listCancel.setFocusableInTouchMode(true);
                    listCancel.requestFocus();
                    break;

                case DELETE:
                    listDelete.setFocusable(true);
                    listDelete.setFocusableInTouchMode(true);
                    listDelete.requestFocus();
                    break;

                case CONFIRM:
                    listConfirm.setFocusable(true);
                    listConfirm.setFocusableInTouchMode(true);
                    listConfirm.requestFocus();
                    break;

                default:
                    break;
            }
        }else {
            switch (buttonType){
                case CANCEL:
                    listCancel.setFocusable(false);
                    listCancel.setFocusableInTouchMode(false);
                    listCancel.requestFocus();
                    break;

                case DELETE:
                    listDelete.setFocusable(false);
                    listDelete.setFocusableInTouchMode(false);
                    listDelete.requestFocus();
                    break;

                case CONFIRM:
                    listConfirm.setFocusable(false);
                    listConfirm.setFocusableInTouchMode(false);
                    listConfirm.requestFocus();
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * 初始化数据
     */
    private void initData(){
        listTitle.setText(title);
        listItemAdapter.setInfo(itemArr, itemArr.length());
        listContent.setAdapter(listItemAdapter);
    }

    /**
     * 添加返回动画
     */
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
