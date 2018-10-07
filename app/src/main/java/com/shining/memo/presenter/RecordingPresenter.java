package com.shining.memo.presenter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;

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

    public boolean saveRecording(Task task, HashMap<Integer,RecordingContent> recordingMap,Alarm alarmObject){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try{
            long taskId = taskModel.addTask(task,db);
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
            Log.d("RecordingAdapter number", number+"");
            if(number > 1){
                RecordingContent content = new RecordingContent();
                content.setType("text");
//                String newStr = "";
//                if(text.get(number - 1).charAt(0) == '\n')
//                    newStr = text.get(number - 1).toString().substring(1);
//                else
//                    newStr = text.get(number - 1);
                content.setContent(Html.toHtml(text.get(1)).substring(0,Html.toHtml(text.get(1)).length() - 1));
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
                content.setContent(Html.toHtml(text.get(0)).substring(0,Html.toHtml(text.get(0)).length() - 1));
                content.setColor(color);
                map.put(index,content);
            }else {
                if(text.size() > 1 || (text.size() ==1 && text.get(0).toString().equals(""))){
                    RecordingContent content = new RecordingContent();
                    content.setType("text");
                    Log.d("TAG","if");
                    if(text.size() > 1)
                        content.setContent(Html.toHtml(text.get(1)).substring(0,Html.toHtml(text.get(1)).length() - 1));
                    else
                        content.setContent(Html.toHtml(text.get(0)).substring(0,Html.toHtml(text.get(0)).length() - 1));
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
                    Log.d("TAG","else");
                    RecordingContent content = new RecordingContent();
                    content.setType("text");
                    content.setContent(Html.toHtml(text.get(0)).substring(0,Html.toHtml(text.get(0)).length() - 1));
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
            color = "#000000";
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
        Log.d("insertRecording",map.toString());
        return map;
    }
}
