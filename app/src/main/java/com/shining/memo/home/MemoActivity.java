package com.shining.memo.home;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shining.memo.R;
import com.shining.memo.home.fragment.ListFragment;
import com.shining.memo.home.fragment.NoteFragment;
import com.shining.memo.home.fragment.TaskFragment;
import com.shining.memo.model.TaskImpl;
import com.shining.memo.presenter.MemoContract;
import com.shining.memo.presenter.TaskPresenter;
import com.shining.memo.view.CalendarActivity;

import com.shining.memo.view.NoteActivity;
import com.shining.memo.view.TaskActivity;

import com.shining.memo.view.ListActivity;


public class MemoActivity extends AppCompatActivity implements MemoContract.View,View.OnClickListener{
    private static final String TAG = "MemoActivity";
    private Fragment currentFragment;
    private TaskFragment taskFragment;
    private ListFragment listFragment;
    private NoteFragment noteFragment;

    private TextView task,list,note,addText,currentClickText;
    private ImageButton calendar;
    private LinearLayout add;

    private TaskPresenter taskPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_home);
        initView();
        initListener();
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

    public void initListener(){
        task.setOnClickListener(this);
        list.setOnClickListener(this);
        note.setOnClickListener(this);
        add.setOnClickListener(this);
    }

    public void initData(){
        taskPresenter=new TaskPresenter(this,new TaskImpl(this));
        task.setOnClickListener(this);
        list.setOnClickListener(this);
        note.setOnClickListener(this);
        calendar.setOnClickListener(this);
        add.setOnClickListener(this);
//        initTestData();
        taskFragment=new TaskFragment();
        taskFragment.setPresenter(taskPresenter);
        switchFragment(taskFragment).commit();
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
                switchFragment(taskFragment).commit();
                add.setTag(currentFragment);
                addText.setText(getResources().getString(R.string.main_add_task));
                break;
            case R.id.main_titlebar_list:
                onClickTitle((TextView)v);
                if(listFragment == null){
                    listFragment = new ListFragment();
                }
                switchFragment(listFragment).commit();
                addText.setText(getResources().getString(R.string.main_add_list));
                break;
            case R.id.main_titlebar_note:
                onClickTitle((TextView)v);
                if(noteFragment == null){
                    noteFragment = new NoteFragment();
                }
                switchFragment(noteFragment).commit();
                addText.setText(getResources().getString(R.string.main_add_note));
                break;
            case R.id.main_titlebar_add:
                onClickAdd(v);
                break;
            case R.id.main_titlebar_calendar:
                Intent calendarIntent = new Intent(this,CalendarActivity.class);
                startActivity(calendarIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }
    }
    private void onClickAdd(View v){
        if(currentFragment instanceof TaskFragment){
            Intent calendarIntent = new Intent(this, TaskActivity.class);
            startActivity(calendarIntent);
        }else if(currentFragment instanceof NoteFragment){
            Intent calendarIntent = new Intent(this, NoteActivity.class);
            startActivity(calendarIntent);
        }else {
            Intent listActivity = new Intent(this, ListActivity.class);
            startActivity(listActivity);
        }
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    private void onClickTitle(TextView textView){
        if(currentClickText!=null){
            currentClickText.setTextColor(getColor(R.color.main_task_not_click));
            currentClickText.setTextSize(16);
        }
        textView.setTextColor(getColor(R.color.main_task_click));
        textView.setTextSize(24);
        currentClickText=textView;
    }


}
