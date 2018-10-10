package com.shining.memo.presenter;

import com.shining.memo.model.TaskImpl;

import org.json.JSONObject;

import java.util.List;

public class TaskPresenter implements MemoContract.Presenter{
    private MemoContract.View mView;
    private TaskImpl taskModel;

    public TaskPresenter(MemoContract.View view, TaskImpl taskModel){
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
//    public boolean hasAudioById(long id){
//        return taskModel.hasAudioById(id);
//    }
}
