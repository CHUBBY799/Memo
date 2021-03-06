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
import android.widget.EditText;
import android.widget.ImageButton;

import com.shining.memo.R;
import com.shining.memo.adapter.ListItemAdapter;
import com.shining.memo.bean.ListBean;
import com.shining.memo.presenter.ListPresenter;
import com.shining.memo.utils.ShotUtils;
import com.shining.memo.utils.ToastUtils;

import org.joda.time.LocalDate;
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
        initListener();
        initData();
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.list_cancel:
                buttonFocus(CANCEL, true);
                buttonFocus(CANCEL, false);
                listCancel();
                break;
            case R.id.list_confirm:
                buttonFocus(CONFIRM, true);
                buttonFocus(CONFIRM, false);
                listConfirm();
                break;
            case R.id.list_delete:
                buttonFocus(DELETE, true);
                buttonFocus(DELETE, false);
                listDelete();
                break;
            case R.id.list_share:
                buttonFocus(SHARE, true);
                buttonFocus(SHARE, false);
                listShare();
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
        finished = 1;
        for (int i = 0 ; i < itemArr.length(); i++){
            try{
                if (!itemArr.getJSONObject(i).getBoolean("state")){
                    finished = 0;
                    break;
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        listBean.setFinished(finished);
        listBean.setDate(LocalDate.now().toString());
        return listBean;
    }

    /**
     * 退出
     */
    private void listCancel(){
        title = listTitle.getText().toString();
        ListPresenter listPresenter = new ListPresenter(ListActivity.this);
        if (title.equals("") && itemArr.length() == 0){
            if (id != -1){
                listPresenter.deletePresenter(String.valueOf(id));
            }

        }else {
            if(id == -1){
                listPresenter.insertPresenter(formatData());
            }else {
                listPresenter.updatePresenter(formatData());
            }
        }
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    /**
     * 删除
     */
    private void listDelete(){
        buildDialog();
    }

    /**
     * 保存
     */
    private void listConfirm(){
        title = listTitle.getText().toString();
        ListPresenter listPresenter = new ListPresenter(ListActivity.this);
        if (title.equals("") && itemArr.length() == 0){
            if (id != -1){
                listPresenter.deletePresenter(String.valueOf(id));
            }
            ToastUtils.showShort(ListActivity.this,getString(R.string.empty_text_notice));
        }else {
            if(id == -1){
                listPresenter.insertPresenter(formatData());
            }else {
                listPresenter.updatePresenter(formatData());
            }
            ToastUtils.showShort(ListActivity.this,getString(R.string.save_successful_notice));
        }
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
            shotPath = ShotUtils.saveBitmap(ListActivity.this,ShotUtils.shotRecyclerView(this,listContent,findViewById(R.id.list_title_layout)));
            ShotUtils.shareCustom(ListActivity.this,shotPath);
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
    private void initListener(){
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
        builder.setTitle(getContext().getString(R.string.main_delete));
        builder.setMessage(getContext().getString(R.string.list_delete_tip));
        builder.setNegativeButton(getContext().getString(R.string.cancel), null);
        builder.setPositiveButton(getContext().getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ListPresenter listPresenter = new ListPresenter(ListActivity.this);
                if(id != -1){
                    listPresenter.deletePresenter(String.valueOf(id));
                }
                ToastUtils.showShort(ListActivity.this,getString(R.string.delete_success_notice));
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
        listTitle.setFocusable(true);
        listTitle.setFocusableInTouchMode(true);
        listTitle.requestFocus();

        title = listTitle.getText().toString();
        ListPresenter listPresenter = new ListPresenter(ListActivity.this);
        if (title.equals("") && itemArr.length() == 0){
            if (id != -1){
                listPresenter.deletePresenter(String.valueOf(id));
            }

        }else {
            if(id == -1){
                listPresenter.insertPresenter(formatData());
            }else {
                listPresenter.updatePresenter(formatData());
            }
        }
        finish();
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
            default:
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_SHARE_PERMISSION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    buttonFocus(SHARE, true);
                    shotPath = ShotUtils.saveBitmap(ListActivity.this,ShotUtils.shotRecyclerView(this,listContent,findViewById(R.id.list_title_layout)));
                    ShotUtils.shareCustom(ListActivity.this,shotPath);
                    buttonFocus(SHARE, false);
                }
                break;
        }
    }
}
