package com.shining.memo.view;

import android.app.Activity;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.shining.memo.R;
import com.shining.memo.model.TaskImpl;
import com.shining.memo.model.TextImpl;
import com.shining.memo.model.TextModel;
import com.shining.memo.presenter.MemoContract;
import com.shining.memo.presenter.MemoPresenter;

import org.json.JSONObject;

import java.util.List;

public class MemoActivity extends Activity implements MemoContract.View {
    private static final String TAG = "MemoActivity";
    private TaskAdapter taskAdapter;
    private RecyclerView memoRecyclerView;
    private TaskImpl taskModel;
    private MemoPresenter mPresenter;
    private List<JSONObject> mtasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);
        taskModel=new TaskImpl(this);
        mPresenter=new MemoPresenter(this,taskModel);
        initTaskRecycleView();
    }

    @Override
    public void initTaskRecycleView() {
        mtasks=mPresenter.returnTaskList();
        taskAdapter=new TaskAdapter(this,mtasks);
        memoRecyclerView=findViewById(R.id.memo_recycler);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        memoRecyclerView.setLayoutManager(layoutManager);
        memoRecyclerView.setAdapter(taskAdapter);
    }
}
