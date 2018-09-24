package com.shining.memo.model;

import java.io.Serializable;

public class RecordingContent implements Serializable{
    private String type;
    private String content;
    private String color;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "RecordingContent{" +
                "type='" + type + '\'' +
                ", content='" + content + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
