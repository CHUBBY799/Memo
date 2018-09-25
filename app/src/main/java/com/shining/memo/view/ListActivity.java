package com.shining.memo.view;

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
import com.shining.memo.widget.CustomFAB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ListActivity extends AppCompatActivity implements View.OnClickListener{

    private Button listCancel;
    private Button listThumbtack;
    private Button listDelete;
    private Button listConfirm;
    private EditText listTitle;
    private RecyclerView listContent;
    private ListItemAdapter listItemAdapter;
    private CustomFAB addNewItem;

    private JSONArray itemArr;

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
            case R.id.add_new_item:
                addNewItem();
                break;
            default:
                break;
        }
    }

    /**
     * 添加新的子菜单
     */
    private void addNewItem(){
        try{
            JSONObject item = new JSONObject();
            item.put("title", "跑步");
            item.put("state", false);
            itemArr.put(item);
        }catch (JSONException e){
            e.printStackTrace();
        }
        listItemAdapter.setInfo(itemArr, itemArr.length());
        listContent.setAdapter(listItemAdapter);
    }

    /**
     * 返回数据
     */
    private void listConfirm(){
        Intent textIntent = new Intent();
        String title = listTitle.getText().toString();
        textIntent.putExtra("title", title);
        textIntent.putExtra("itemArr", itemArr.toString());
        setResult(RESULT_OK, textIntent);
        finish();
    }

    private void initView(){
        listCancel = findViewById(R.id.list_cancel);
        listThumbtack = findViewById(R.id.list_thumbtack);
        listDelete = findViewById(R.id.list_delete);
        listConfirm = findViewById(R.id.list_confirm);
        listTitle = findViewById(R.id.list_title);

        listContent = findViewById(R.id.list_content);
        listContent.setLayoutManager(new LinearLayoutManager(this));
        listItemAdapter = new ListItemAdapter(this);

        addNewItem = findViewById(R.id.add_new_item);

        itemArr = new JSONArray();
    }

    private void initComponent(){
        listCancel.setOnClickListener(this);
        listThumbtack.setOnClickListener(this);
        listDelete.setOnClickListener(this);
        listConfirm.setOnClickListener(this);
        addNewItem.setOnClickListener(this);
    }
}
