package com.shining.memo.home.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shining.memo.R;
import com.shining.memo.home.adapter.TaskAdapter;
import com.shining.memo.model.Task;
import com.shining.memo.presenter.TaskPresenter;

import org.json.JSONObject;

import java.util.List;

public class TaskFragment extends Fragment{
    private static final String TAG = "TaskFragment";
    private List<JSONObject> mTasks;
    private TaskAdapter mAdapter;
    private TaskPresenter mPresenter;
    private RecyclerView mRecycleView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view=inflater.inflate(R.layout.main_task,container,false);
        initData();
        mRecycleView=view.findViewById(R.id.main_task_recycler);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        mRecycleView.setLayoutManager(linearLayoutManager);
        mAdapter=new TaskAdapter(mTasks);
        mAdapter.setCallback(new TaskAdapter.Callback() {
            @Override
            public void finishTaskById(int id) {
                mPresenter.finishTaskById(id);
//                refreshData();
            }
        });
        mRecycleView.setAdapter(mAdapter);
        return view;
    }
    public void initData(){
        mTasks=mPresenter.returnTaskList();
    }
    public void refreshData(){
        Log.d(TAG, "refreshData: ");
        mTasks.clear();
        mTasks.addAll(mPresenter.returnTaskList());
        mAdapter.notifyDataSetChanged();
    }

    public void setPresenter(TaskPresenter presenter){
        mPresenter=presenter;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        refreshData();
    }
}
