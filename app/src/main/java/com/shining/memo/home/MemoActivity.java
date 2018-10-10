package com.shining.memo.home;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shining.memo.R;
import com.shining.memo.home.fragment.NoteFragment;
import com.shining.memo.home.fragment.TaskFragment;
import com.shining.memo.model.Alarm;
import com.shining.memo.model.AlarmImpl;
import com.shining.memo.model.MemoDatabaseHelper;
import com.shining.memo.model.Task;
import com.shining.memo.model.TaskImpl;
import com.shining.memo.model.TextImpl;
import com.shining.memo.presenter.MemoContract;
import com.shining.memo.presenter.TaskPresenter;
import com.shining.memo.view.CalendarActivity;
import com.shining.memo.view.RecordingEditActivity;
import com.shining.memo.view.RecordingViewActivity;

import java.util.Random;

public class MemoActivity extends AppCompatActivity implements MemoContract.View,View.OnClickListener{
    private static final String TAG = "MemoActivity";
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
    public void initTestData(){
        MemoDatabaseHelper dbHelper=new MemoDatabaseHelper(this,"memo.db",null,1);
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        TaskImpl model = new TaskImpl(this);
        AlarmImpl alarm1=new AlarmImpl();
        int n = 10;
        int m = 6; //0~m
        int mm=30;
        for (int i = 0; i < n; i++) {
            Random r = new Random();
            int num = r.nextInt(m) + 1;
            Task task = new Task();
            task.setType(num % 2 == 1 ? "audio" : "text");
            num = r.nextInt(m) + 1;
            task.setUrgent(num % 2 == 1 ? 1 : 0);
            num = r.nextInt(m) + 1;
            task.setTitle("hello mylove" + num);
            task.setDeleted(0);
            task.setFinished(0);
            if (num % 2 == 1) {
                task.setAlarm(1);
                long taskId = model.addTask(task,db);
//                Log.d(TAG, "useAppContext: AlarmtaskId:" + taskId);
                Alarm alarm = new Alarm();
                alarm.setTaskId(taskId);
                int month=r.nextInt(20)+10;
                alarm.setDate("2018-09-"+month);
                int hour=r.nextInt(15)+10;
                int min=r.nextInt(51)+10;
//                int second=r.nextInt(51)+10;
                Log.d(TAG, "initTestData: "+"2018-09-"+month+" "+hour+":"+min);
                alarm.setTime(hour+":"+min);
                alarm.setRingtone(0);
                alarm.setPop(0);
                alarm1.addAlarm(alarm,db);
            } else {
                task.setAlarm(0);
                Log.d(TAG, "useAppContext: NotAlarmtaskId:"+model.addTask(task,db));
            }

        }
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
        if(currentFragment instanceof TaskFragment){
            Intent calendarIntent = new Intent(this, RecordingEditActivity.class);
            startActivity(calendarIntent);
            Log.d(TAG, "onClickAdd: ");
        }else if(currentFragment instanceof NoteFragment){
            //添加add note跳转
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
