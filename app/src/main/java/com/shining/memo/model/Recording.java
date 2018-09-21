package com.shining.memo.model;

import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.HashMap;


public class Recording {
    private int taskId;
    private HashMap<Integer,RecordingContent> recordingMap;

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public HashMap<Integer, RecordingContent> getRecordingMap() {
        return recordingMap;
    }

    public void setRecordingMap(HashMap<Integer, RecordingContent> recordingMap) {
        this.recordingMap = recordingMap;
    }

    public String serialize(){
        try {
            ByteArrayOutputStream mem_out = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(mem_out);
            out.writeObject(recordingMap);
            out.close();
            mem_out.close();
            byte[] bytes =  mem_out.toByteArray();
            byte[] base64ByteArray= Base64.encode(bytes, Base64.DEFAULT);
            String mapString=new String(base64ByteArray);
            return mapString;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deserialize(String mapString){
        try {
            byte[] bytes=mapString.getBytes();
            byte[] base64ByteArray=Base64.decode(bytes, Base64.DEFAULT);
            ByteArrayInputStream mem_in = new ByteArrayInputStream(base64ByteArray);
            ObjectInputStream in = new ObjectInputStream(mem_in);
            recordingMap = (HashMap<Integer, RecordingContent>)in.readObject();
            in.close();
            mem_in.close();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }   catch (IOException e) {
            e.printStackTrace();
        }
    }

}
