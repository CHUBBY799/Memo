package com.shining.memo.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.shining.memo.R;

public class ListDialog extends Dialog {

    private Context context;

    private Button cancer;
    private Button confirm;
    private EditText titleView;
    private Spinner stateView;

    private String title;
    private Boolean state;

    private onNoOnclickListener noOnclickListener;       //取消按钮被点击了的监听器
    private onYesOnclickListener yesOnclickListener;     //确定按钮被点击了的监听器

    public ListDialog(Context context) {
        super(context, R.style.ListDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_list_item);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);

        //初始化界面控件
        initView();

        //初始化界面控件的事件
        initEvent();

        //初始化界面控件内容
        initData();

    }

    public String getTitle(){
        return title;
    }

    public void setTitle(){
        title = String.valueOf(titleView.getText());
    }

    public Boolean getState(){
        return state;
    }

    public void setNoOnclickListener(onNoOnclickListener onNoOnclickListener) {
        this.noOnclickListener = onNoOnclickListener;
    }

    /**
     * 设置确定按钮的显示内容和监听
     *
     * @param onYesOnclickListener
     * 取消按钮监听
     */
    public void setYesOnclickListener(onYesOnclickListener onYesOnclickListener) {
        this.yesOnclickListener = onYesOnclickListener;
    }

    /**
     * 设置确定按钮和取消被点击的接口
     */
    public interface onYesOnclickListener {
        void onYesClick();
    }

    public interface onNoOnclickListener {
        void onNoClick();
    }

    private void initData(){

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,R.layout.spinner_item);
        adapter.add(String.valueOf(true));
        adapter.add(String.valueOf(false));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateView.setAdapter(adapter);

        stateView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                state = Boolean.valueOf(stateView.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yesOnclickListener != null) {
                    yesOnclickListener.onYesClick();
                }
            }
        });

        //设置取消按钮被点击后，向外界提供监听
        cancer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noOnclickListener != null) {
                    noOnclickListener.onNoClick();
                }
            }
        });
    }

    private void initView(){
        cancer = findViewById(R.id.cancer);
        confirm = findViewById(R.id.confirm);
        titleView = findViewById(R.id.title);
        stateView = findViewById(R.id.state);
    }

}
