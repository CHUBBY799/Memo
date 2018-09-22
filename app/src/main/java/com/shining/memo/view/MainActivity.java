package com.shining.memo.view;

import android.app.ActionBar;
import android.content.Intent;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.constraint.ConstraintLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.shining.memo.R;
import com.shining.memo.fragment.ListContent;
import com.shining.memo.fragment.ListNew;
import com.shining.memo.fragment.NoteNew;
import com.shining.memo.fragment.TaskNew;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView list;
    private TextView note;
    private TextView task;

    private ListNew listNew;
    private NoteNew noteNew;
    private TaskNew taskNew;
    private ListContent listContent;

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
            case R.id.list_new:
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
    }

    private void initComponent (){
        list.setOnClickListener(this);
        note.setOnClickListener(this);
        task.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case 1:
                if (resultCode == RESULT_OK){
                    JSONObject info = new JSONObject();
                    String title = data.getStringExtra("title");
                    //Boolean state = data.getBooleanExtra("state");
                    try{
                        info.put("title", title);
                        info.put("state", false);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    listContent.addInfo(info);
                }
                break;
            default:
        }
    }

    /**
     * 根据传入的index参数来设置选中的tab页。
     *
     * @param index
     * 每个tab页对应的下标。0表示task，1表示list，2表示note。
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
                if (taskNew == null) {
                    // 如果TaskFragment为空，则创建一个并添加到界面上
                    taskNew = new TaskNew();
                    transaction.add(R.id.add_new, taskNew);
                } else {
                    // 如果TaskFragment不为空，则直接将它显示出来
                    transaction.show(taskNew);
                }
                break;
            case 1:
                list.setTextSize(30);
                if (listNew == null) {
                    // 如果ListFragment为空，则创建一个并添加到界面上
                    listNew = new ListNew();
                    transaction.add(R.id.add_new, listNew);

                } else {
                    // 如果ListFragment不为空，则直接将它显示出来
                    transaction.show(listNew);
                }

                if (listContent == null) {
                    // 如果ListFragment为空，则创建一个并添加到界面上
                    listContent = new ListContent();
                    transaction.add(R.id.content_list, listContent);

                } else {
                    // 如果ListFragment不为空，则直接将它显示出来
                    transaction.show(listContent);
                }

                break;
            case 2:
                note.setTextSize(30);
                if (noteNew == null) {
                    // 如果NoteFragment为空，则创建一个并添加到界面上
                    noteNew = new NoteNew();
                    transaction.add(R.id.add_new, noteNew);
                } else {
                    // 如果NoteFragment不为空，则直接将它显示出来
                    transaction.show(noteNew);
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
        if (listNew != null) {
            transaction.hide(listNew);
        }
        if (noteNew != null) {
            transaction.hide(noteNew);
        }
        if (taskNew != null) {
            transaction.hide(taskNew);
        }
        if (listContent != null){
            transaction.hide(listContent);
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
        View menu = getLayoutInflater().inflate(R.layout.main_menu, (ConstraintLayout)findViewById(R.id.constraintLayout), false);
        pop = new PopupWindow(menu, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT,true);
        pop.showAsDropDown(findViewById(R.id.menu), 0 ,0);

        Button addText = menu.findViewById(R.id.add_text);
        Button calendar = menu.findViewById(R.id.calendar);
        Button addAudio = menu.findViewById(R.id.add_audio);
        addText.setOnClickListener(this);
        calendar.setOnClickListener(this);
        addAudio.setOnClickListener(this);
    }
}