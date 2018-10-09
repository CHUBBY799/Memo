package com.shining.memo.presenter;

import org.json.JSONObject;

import java.util.List;

public class MemoContract {
    public interface View{
//        public void initTaskRecycleView();
    }
    public interface Presenter{
        public List<JSONObject> returnTaskList();
    }
}
