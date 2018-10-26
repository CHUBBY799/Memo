package com.shining.memo.presenter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.shining.memo.model.MemoDatabaseHelper;
import com.shining.memo.model.Recording;
import com.shining.memo.model.RecordingContent;
import com.shining.memo.model.RecordingImpl;
import com.shining.memo.model.RecordingModel;
import com.shining.memo.model.Task;
import com.shining.memo.model.TaskImpl;
import com.shining.memo.model.TaskModel;

import java.util.HashMap;

public class NotePresenter {

    private Context context;
    private TaskModel taskModel;
    private RecordingModel recordingModel;
    private MemoDatabaseHelper dbHelper;

    public NotePresenter(Context context) {
        this.context = context;
        this.taskModel = new TaskImpl(context);
        this.recordingModel = new RecordingImpl();
        dbHelper=new MemoDatabaseHelper(context,"memo.db",null,1);
    }

    public boolean saveNote(Task task, HashMap<Integer,RecordingContent> recordingMap){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try{
            long taskId = taskModel.addTask(task,db);
            Recording recording = new Recording();
            recording.setTaskId((int)taskId);
            recording.setRecordingMap(recordingMap);
            recordingModel.addRecording(recording,db);
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            db.endTransaction();
            db.close();
        }
        return true;
    }

    public boolean modifyRecording(Task task, HashMap<Integer,RecordingContent> recordingMap){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try{
            taskModel.modifyTask(task,db);
            Recording recording = new Recording();
            recording.setTaskId((int)task.getId());
            recording.setRecordingMap(recordingMap);
            recordingModel.modifyRecording(recording,db);
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            db.endTransaction();
            db.close();
        }
        return true;
    }

    public boolean deleteRecording(int taskId){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try{
            recordingModel.deleteRecording(taskId,db);
            taskModel.deleteTask(taskId,db);
            AlarmPresenter presenter = new AlarmPresenter(context);
            presenter.alarmCancel(taskId);
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            db.endTransaction();
            db.close();
        }
        return true;
    }
}
