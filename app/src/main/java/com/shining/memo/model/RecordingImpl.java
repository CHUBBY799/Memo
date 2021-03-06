package com.shining.memo.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class RecordingImpl implements RecordingModel{

    @Override
    public void addRecording(Recording recording, SQLiteDatabase db) {
        db.execSQL("insert into recording(taskId,recordingInfo) " +
                "values(?,?)",new Object[]{recording.getTaskId(),recording.serialize()});
    }

    @Override
    public void modifyRecording(Recording recording, SQLiteDatabase db) {
        db.execSQL("update recording "
                +"set recordingInfo = ? "
                + "where taskId = ?",new Object[]{recording.serialize(),recording.getTaskId()});
    }

    @Override
    public void deleteRecording(int taskId, SQLiteDatabase db) {
        db.execSQL("delete from recording "
                +"where taskId = ?",new Object[]{taskId});
    }

    @Override
    public Recording getRecording(int taskId, SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select recordingInfo "
                +"from recording "
                +"where taskId = ?",new String[]{String.valueOf(taskId)});
        if(cursor.moveToFirst()){
            Recording recording = new Recording();
            recording.setTaskId(taskId);
            recording.deserialize(cursor.getString(cursor.getColumnIndex("recordingInfo")));
            return recording;
        }
        return null;
    }
}
