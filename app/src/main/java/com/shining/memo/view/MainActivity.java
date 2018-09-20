package com.shining.memo.view;

import android.app.ActionBar;
import android.content.Intent;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.shining.memo.R;
import com.shining.memo.fragment.ListFragment;
import com.shining.memo.fragment.NoteFragment;
import com.shining.memo.fragment.TaskFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView list;
    private TextView note;
    private TextView task;

    private ListFragment listFragment;
    private NoteFragment noteFragment;
    private TaskFragment taskFragment;

    private TextView newList;
    private TextView newNote;
    private TextView newTask;

    private Button addText;
    private Button calendar;
    private Button addAudio;

    private FragmentManager fragmentManager;
    private PopupWindow pop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initComponent();

        fragmentManager = getFragmentManager();
        // 第一次启动时选中第0个tab
        setTabSelection(0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.task:
                // 当点击了Task时，选中第1个tab
                setTabSelection(0);
                break;
            case R.id.list:
                // 当点击了List时，选中第2个tab
                setTabSelection(1);
                break;
            case R.id.note:
                // 当点击了Note时，选中第3个tab
                setTabSelection(2);
                break;
            case R.id.add_text:
                addText();
                break;
            case R.id.calendar:
                calendar();
                break;
            case R.id.add_audio:
                audio();
                break;
            default:
                break;
        }
    }

    private void addText(){
        Intent textIntent = new Intent(this,TextActivity.class);
        startActivity(textIntent);
        pop.dismiss();
    }

    private void calendar(){
        Intent calendarIntent = new Intent(this,CalendarActivity.class);
        startActivity(calendarIntent);
        pop.dismiss();
    }

    private void audio(){
        Intent calendarIntent = new Intent(this,AudioRecordingActivity.class);
        startActivity(calendarIntent);
        pop.dismiss();
    }


    private void initView(){
        list = findViewById(R.id.list);
        note = findViewById(R.id.note);
        task = findViewById(R.id.task);

        newList = findViewById(R.id.list_new);
        newNote = findViewById(R.id.note_new);
        newTask = findViewById(R.id.task_new);

        addText = findViewById(R.id.add_text);
        calendar = findViewById(R.id.calendar);
        addAudio = findViewById(R.id.add_audio);
    }

    private void initComponent (){
        list.setOnClickListener(this);
        note.setOnClickListener(this);
        task.setOnClickListener(this);
    }

    /**
     * 根据传入的index参数来设置选中的tab页。
     *
     * @param index
     * 每个tab页对应的下标。0表示消息，1表示联系人，2表示动态，3表示设置。
     */
    private void setTabSelection(int index) {
        // 每次选中之前先清楚掉上次的选中状态
        clearSelection();
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (index) {
            case 0:
                task.setTextSize(30);
                if (taskFragment == null) {
                    // 如果TaskFragment为空，则创建一个并添加到界面上
                    taskFragment = new TaskFragment();
                    transaction.add(R.id.add_new, taskFragment);
                } else {
                    // 如果TaskFragment不为空，则直接将它显示出来
                    transaction.show(taskFragment);
                }
                break;
            case 1:
                list.setTextSize(30);
                if (listFragment == null) {
                    // 如果ListFragment为空，则创建一个并添加到界面上
                    listFragment = new ListFragment();
                    transaction.add(R.id.add_new, listFragment);
                } else {
                    // 如果ListFragment不为空，则直接将它显示出来
                    transaction.show(listFragment);
                }
                break;
            case 2:
                note.setTextSize(30);
                if (noteFragment == null) {
                    // 如果NoteFragment为空，则创建一个并添加到界面上
                    noteFragment = new NoteFragment();
                    transaction.add(R.id.add_new, noteFragment);
                } else {
                    // 如果NoteFragment不为空，则直接将它显示出来
                    transaction.show(noteFragment);
                }
            default:
                break;
        }
        transaction.commit();
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction
     * 用于对Fragment执行操作的事务
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (listFragment != null) {
            transaction.hide(listFragment);
        }
        if (noteFragment != null) {
            transaction.hide(noteFragment);
        }
        if (taskFragment != null) {
            transaction.hide(taskFragment);
        }
    }

    /**
     * 清除掉所有的选中状态。
     */
    private void clearSelection() {
        list.setTextSize(18);
        note.setTextSize(18);
        task.setTextSize(18);
    }

    public void onMenu(View v){
        View menu = getLayoutInflater().inflate(R.layout.main_menu,null,false);
        pop = new PopupWindow(menu, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT,true);
        pop.showAsDropDown(findViewById(R.id.menu), 0 ,0);

        addText = menu.findViewById(R.id.add_text);
        calendar = menu.findViewById(R.id.calendar);
        addAudio = menu.findViewById(R.id.add_audio);
        addText.setOnClickListener(this);
        calendar.setOnClickListener(this);
        addAudio.setOnClickListener(this);
    }
}