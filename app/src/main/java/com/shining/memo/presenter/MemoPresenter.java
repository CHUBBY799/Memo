package com.shining.memo.presenter;

import android.content.Context;

import com.shining.memo.model.Task;
import com.shining.memo.model.TaskImpl;
import com.shining.memo.model.TextImpl;
import com.shining.memo.model.TextModel;

import org.json.JSONObject;

import java.util.List;

public class MemoPresenter implements MemoContract.Presenter{
    private MemoContract.View mView;
    private TaskImpl taskModel;

    public MemoPresenter(MemoContract.View view, TaskImpl taskModel){
        mView=view;
        this.taskModel=taskModel;
    }

    @Override
    public List<JSONObject> returnTaskList() {
       List tasks=taskModel.getAlarmTasksByUrgentDesc(1);
       tasks.addAll(taskModel.getNotAlarmTasksByUrgentDesc(1));
       tasks.addAll(taskModel.getAlarmTasksByUrgentDesc(0));
       tasks.addAll(taskModel.getNotAlarmTasksByUrgentDesc(0));
       return tasks;
    }
}
