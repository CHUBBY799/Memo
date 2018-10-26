package com.shining.memo.view;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.shining.memo.R;
import com.shining.memo.adapter.ListItemAdapter;
import com.shining.memo.bean.ListBean;
import com.shining.memo.presenter.ListPresenter;
import com.shining.memo.utils.ShotUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class ListActivity extends AppCompatActivity implements View.OnClickListener,ViewList{

    private final static int CANCEL = 1;
    private final static int DELETE = 2;
    private final static int CONFIRM = 3;
    private final static int SHARE = 4;
    private static final int REQUEST_SHARE=0xa4;
    private static final int REQUEST_SHARE_PERMISSION=0xa1;
    private ImageButton listCancel;
    private ImageButton listConfirm;
    private ImageButton listDelete;
    private ImageButton listShare;
    private EditText listTitle;
    private RecyclerView listContent;
    private ListItemAdapter listItemAdapter;

    private int id;
    private int finished;
    private String title;
    private JSONArray itemArr;
    private String shotPath="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        finished = intent.getIntExtra("finished", 0);
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
            case R.id.list_share:
                listShare();
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
        listBean.setFinished(finished);
        listBean.setTitle(title);
        listBean.setItemArr(itemArr.toString());
        return listBean;
    }

    /**
     * 退出
     */
    private void listCancel(){
        buttonFocus(CANCEL, true);
        title = listTitle.getText().toString();
        if(title.equals("")){
            Toast.makeText(ListActivity.this, "Please input title", Toast.LENGTH_SHORT).show();
            listTitle.setFocusable(true);
            listTitle.setFocusableInTouchMode(true);
            listTitle.requestFocus();
            InputMethodManager inputManager = (InputMethodManager) listTitle.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null){
                inputManager.showSoftInput(listTitle, 0);
            }
        }else{
            ListPresenter listPresenter = new ListPresenter(ListActivity.this);

            if(id == -1){
                listPresenter.insertPresenter(formatData());
            }else {
                listPresenter.updatePresenter(formatData());
            }
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
        buttonFocus(CANCEL, false);
    }

    /**
     * 删除
     */
    private void listDelete(){
        buttonFocus(DELETE, true);
        buildDialog();
        buttonFocus(DELETE, false);
    }

    /**
     * 保存
     */
    private void listConfirm(){
        buttonFocus(CONFIRM, true);
        title = listTitle.getText().toString();
        if(title.equals("")){
            Toast.makeText(ListActivity.this, "Please input title", Toast.LENGTH_SHORT).show();
            listTitle.setFocusable(true);
            listTitle.setFocusableInTouchMode(true);
            listTitle.requestFocus();
            InputMethodManager inputManager = (InputMethodManager) listTitle.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null){
                inputManager.showSoftInput(listTitle, 0);
            }
        }else{
            ListPresenter listPresenter = new ListPresenter(ListActivity.this);

            if(id == -1){
                listPresenter.insertPresenter(formatData());
            }else {
                listPresenter.updatePresenter(formatData());
            }
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
        buttonFocus(CONFIRM, false);
    }

    /**
     * 分享
     */
    private void listShare(){
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                ||checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_SHARE_PERMISSION);
        }else {
            buttonFocus(SHARE, true);
            shotPath = ShotUtils.saveBitmap(ListActivity.this,ShotUtils.shotRecyclerView(listContent,findViewById(R.id.list_title_layout)));
            ShotUtils.shareCustom(ListActivity.this,shotPath);
            buttonFocus(SHARE, false);
        }
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
        listShare = findViewById(R.id.list_share);
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
        listShare.setOnClickListener(this);
        listDelete.setOnClickListener(this);
    }

    /**
     * 建立删除确认弹窗
     */
    private void buildDialog(){
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialog);
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

                case SHARE:
                    listShare.setFocusable(true);
                    listShare.setFocusableInTouchMode(true);
                    listShare.requestFocus();
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

                case SHARE:
                    listShare.setFocusable(false);
                    listShare.setFocusableInTouchMode(false);
                    listShare.requestFocus();
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SHARE:
                if(!shotPath.equals("")){
                    File file = new File(shotPath);
                    if(file.exists()){
                        boolean result = file.delete();
                        if (result){
                            Log.d("ListActivity", "Deleted files successfully");
                        }
                    }
                    shotPath = "";
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_SHARE_PERMISSION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    buttonFocus(SHARE, true);
                    shotPath = ShotUtils.saveBitmap(ListActivity.this,ShotUtils.shotRecyclerView(listContent,findViewById(R.id.list_title_layout)));
                    ShotUtils.shareCustom(ListActivity.this,shotPath);
                    buttonFocus(SHARE, false);
                }
                break;
        }
    }
}
