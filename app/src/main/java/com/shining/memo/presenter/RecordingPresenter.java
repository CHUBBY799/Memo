package com.shining.memo.presenter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.widget.Toast;

import com.shining.memo.adapter.RecordingAdapter;
import com.shining.memo.model.Alarm;
import com.shining.memo.model.AlarmImpl;
import com.shining.memo.model.AlarmModel;
import com.shining.memo.model.MemoDatabaseHelper;
import com.shining.memo.model.Recording;
import com.shining.memo.model.RecordingContent;
import com.shining.memo.model.RecordingImpl;
import com.shining.memo.model.RecordingModel;
import com.shining.memo.model.Task;
import com.shining.memo.model.TaskImpl;
import com.shining.memo.model.TaskModel;
import com.shining.memo.model.Task_Recording;
import com.shining.memo.utils.ToastUtils;

import java.util.HashMap;
import java.util.List;

public class RecordingPresenter {

    private Context context;
    private TaskModel taskModel;
    private AlarmModel alarmModel;
    private RecordingModel recordingModel;
    private MemoDatabaseHelper dbHelper;

    public RecordingPresenter(Context context) {
        this.context = context;
        this.taskModel = new TaskImpl(context);
        this.recordingModel = new RecordingImpl();
        this.alarmModel = new AlarmImpl();
        dbHelper=new MemoDatabaseHelper(context,"memo.db",null,1);
    }

    public long saveRecording(Task task, HashMap<Integer,RecordingContent> recordingMap,Alarm alarmObject){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        long taskId = -1;
        try{
            taskId = taskModel.addTask(task,db);
            ToastUtils.showShort(context,taskId+"");
            Recording recording = new Recording();
            recording.setTaskId((int)taskId);
            recording.setRecordingMap(recordingMap);
            recordingModel.addRecording(recording,db);
            if(task.getAlarm() == 1 && alarmObject != null){
                alarmObject.setTaskId(taskId);
                alarmModel.addAlarm(alarmObject,db);
            }
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }finally {
            db.endTransaction();
        }
        return taskId;
    }

    public Task_Recording getTaskRecording(int taskId){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        Task_Recording task_recording = null;
        try{
            task_recording = new Task_Recording();
            task_recording.setTask(taskModel.getTask(taskId,db));
            task_recording.setRecording(recordingModel.getRecording(taskId,db));
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            db.endTransaction();
        }
        return task_recording;
    }

    public boolean modifyRecording(Task task, HashMap<Integer,RecordingContent> recordingMap,Alarm alarmObject){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try{
            taskModel.modifyTask(task,db);
            Alarm alarm = alarmModel.getAlarm((int)task.getId(),db);
            if(alarm == null){
                if(task.getAlarm() == 1 && alarmObject != null)
                    alarmModel.addAlarm(alarmObject,db);
            }else {
                if(task.getAlarm() == 0)
                    alarmModel.deleteAlarm((int)task.getId(),db);
                else
                    alarmModel.modifyAlarm(alarmObject,db);
            }
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
        }
        return true;
    }

    public boolean modifyUrgent(int taskId,int urgent){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try{
            taskModel.modifyTaskUrgent(taskId,urgent,db);
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            db.endTransaction();
        }
        return true;
    }

    public boolean modifyDeleted(int taskId,int deleted){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try{
            taskModel.modifyTaskDeleted(taskId,deleted,db);
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            db.endTransaction();
        }
        return true;
    }

    public static int insertIndex = -1;
    public HashMap<Integer, RecordingContent> insertRecording(HashMap<Integer, RecordingContent> oldMap, List<Spanned> text, int index, String filePath, String type) {
        HashMap<Integer, RecordingContent> map = new HashMap<>();
        Log.d("RecordingAdapter oldMap", oldMap.toString());
        int number = 0;
        String html = "";
        if(text != null && oldMap != null){
            if(!text.get(0).toString().equals(""))
                number = text.size();
            else
                number = 1;
            String color = oldMap.get(index).getColor();
            for(int i = oldMap.size() - 1; i > index; i--){
                map.put(i + number,oldMap.get(i));
            }
            for(int i = 0; i < index; i++){
                map.put(i,oldMap.get(i));
            }
            Log.d("RecordingAdapter oldMap", number+"");
            if(number > 1){
                RecordingContent content = new RecordingContent();
                content.setType("text");
                html = Html.toHtml(text.get(1));
                if(html.length() > 0)
                    html = html.substring(0,html.length() -1);
                content.setContent(RecordingAdapter.parseUnicodeToStr(html));
                content.setColor(color);
                map.put(index + number,content);
                content = new RecordingContent();
                content.setType(type);
                content.setContent(filePath);
                content.setColor(color);
                map.put(index + 1,content);
                insertIndex = index + 1;
                content = new RecordingContent();
                content.setType("text");
                html = Html.toHtml(text.get(0));
                if(html.length() > 0)
                    html = html.substring(0,html.length() -1);
                content.setContent(RecordingAdapter.parseUnicodeToStr(html));
                content.setColor(color);
                map.put(index,content);
            }else {
                if(text.size() > 1 || (text.size() ==1 && text.get(0).toString().equals(""))){
                    RecordingContent content = new RecordingContent();
                    content.setType("text");
                    if(text.size() > 1){
                        html = Html.toHtml(text.get(1));
                        if(html.length() > 0)
                            html = html.substring(0,html.length() -1);
                        content.setContent(RecordingAdapter.parseUnicodeToStr(html));
                    }
                    else{
                        html = Html.toHtml(text.get(0));
                        if(html.length() > 0)
                            html = html.substring(0,html.length() -1);
                        content.setContent(RecordingAdapter.parseUnicodeToStr(html));
                    }
                    content.setColor(color);
                    map.put(index + 1,content);
                    content = new RecordingContent();
                    content.setType(type);
                    content.setContent(filePath);
                    content.setColor(color);
                    map.put(index,content);
                    insertIndex = index;
                }
                else {
                    RecordingContent content = new RecordingContent();
                    content.setType("text");
                    html = Html.toHtml(text.get(0));
                    if(html.length() > 0)
                        html = html.substring(0,html.length() -1);
                    content.setContent(RecordingAdapter.parseUnicodeToStr(html));
                    content.setColor(color);
                    map.put(index,content);
                    content = new RecordingContent();
                    content.setType(type);
                    content.setContent(filePath);
                    content.setColor(color);
                    map.put(index + 1,content);
                    insertIndex = index + 1;
                }
            }
        }
        return map;
    }

    public void insertRecording(HashMap<Integer, RecordingContent> map,String filePath,String type) {
        int index = map.size();
        String color,strContent = map.get(index - 1).getContent();
        if(index >= 1)
            color = map.get(index - 1).getColor();
        else
            color = "#666666";
        strContent = strContent.replace("\n","");
        if(strContent.equals(""))
        {
            RecordingContent content = map.get(index -1);
            content.setContent("");
            map.put(index,content);
            content = new RecordingContent();
            content.setType(type);
            content.setContent(filePath);
            content.setColor(color);
            map.put(index - 1,content);
            insertIndex = index - 1;
        }
        else {
            RecordingContent content = new RecordingContent();
            content.setType(type);
            content.setContent(filePath);
            content.setColor(color);
            map.put(index, content);
            insertIndex = index;
            content = new RecordingContent();
            content.setType("text");
            content.setContent("");
            content.setColor(color);
            map.put(index + 1, content);
        }
    }

    public HashMap<Integer, RecordingContent> insertRecording(HashMap<Integer, RecordingContent> mMap,String filePath,int index,String position,String type){
        HashMap<Integer, RecordingContent> map = new HashMap<>();
        for(int i = 0; i < index; i++)
            map.put(i,mMap.get(i));
        for(int i = mMap.size() - 1; i > index; i--)
            map.put(i + 1,mMap.get(i));
        Log.d("insertRecording",map.toString());
        if(position.contains("start")){
            map.put(index + 1,mMap.get(index));
            RecordingContent content = new RecordingContent();
            content.setColor(map.get(index + 1).getColor());
            content.setType(type);
            content.setContent(filePath);
            map.put(index,content);
            insertIndex = index;
        }else if(position.contains("end")){
            map.put(index,mMap.get(index));
            RecordingContent content = new RecordingContent();
            content.setColor(map.get(index).getColor());
            content.setType(type);
            content.setContent(filePath);
            map.put(index + 1,content);
            insertIndex = index + 1;
        }
        return map;
    }
}
