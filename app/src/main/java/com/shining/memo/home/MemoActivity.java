package com.shining.memo.home;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shining.memo.R;
import com.shining.memo.home.fragment.NoteFragment;
import com.shining.memo.home.fragment.TaskFragment;
import com.shining.memo.model.Task;
import com.shining.memo.model.TaskImpl;
import com.shining.memo.presenter.MemoContract;
import com.shining.memo.presenter.TaskPresenter;
import com.shining.memo.view.CalendarActivity;

public class MemoActivity extends AppCompatActivity implements MemoContract.View,View.OnClickListener{
    private Fragment currentFragment;
    private TaskFragment taskFragment;

    private TextView task,list,note,addText,currentClickText;
    private ImageButton calendar;
    private LinearLayout add;

    private TaskPresenter taskPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_home);
        initView();
        initData();
    }
    public void initView(){
        task=findViewById(R.id.main_titlebar_task);
        list=findViewById(R.id.main_titlebar_list);
        note=findViewById(R.id.main_titlebar_note);
        calendar=findViewById(R.id.main_titlebar_calendar);
        add=findViewById(R.id.main_titlebar_add);
        addText=findViewById(R.id.main_titlebar_addtext);
    }
    public void initData(){
        taskPresenter=new TaskPresenter(this,new TaskImpl(this));
        taskFragment=new TaskFragment();
        taskFragment.setPresenter(taskPresenter);
        switchFragment(taskFragment);
        add.setTag(currentFragment);
        addText.setText(getResources().getString(R.string.main_add_task));
        onClickTitle(task);
    }


    private FragmentTransaction switchFragment(Fragment targetFragment) {

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        if (!targetFragment.isAdded()) {
            //第一次使用switchFragment()时currentFragment为null，所以要判断一下
            if (currentFragment != null&&targetFragment!=currentFragment) {
                transaction.hide(currentFragment);
            }
            transaction.add(R.id.main_page, targetFragment,targetFragment.getClass().getName());
        } else {
            transaction
                    .hide(currentFragment)
                    .show(targetFragment);
        }
        currentFragment = targetFragment;
//        transaction.addToBackStack(null);
        return   transaction;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.main_titlebar_task:
                onClickTitle((TextView)v);
                switchFragment(taskFragment);
                add.setTag(currentFragment);
                addText.setText(getResources().getString(R.string.main_add_task));
                break;
            case R.id.main_titlebar_list:
                onClickTitle((TextView)v);
                break;
            case R.id.main_titlebar_note:
                onClickTitle((TextView)v);
                break;
            case R.id.main_titlebar_add:
                onClickAdd(v);
                break;
            case R.id.main_titlebar_calendar:
                Intent calendarIntent = new Intent(this,CalendarActivity.class);
                startActivity(calendarIntent);
                break;
        }
    }
    private void onClickAdd(View v){
        if(v.getTag() instanceof TaskFragment){

        }else if(v.getTag() instanceof NoteFragment){

        }
    }
    private void onClickTitle(TextView textView){
        if(currentClickText!=null){
            currentClickText.setTextColor(getResources().getColor(R.color.main_task_noclickcolor));
            currentClickText.setTextSize(16);
        }
        textView.setTextColor(getResources().getColor(R.color.main_task_clickcolor));
        textView.setTextSize(24);
        currentClickText=textView;
    }
}
