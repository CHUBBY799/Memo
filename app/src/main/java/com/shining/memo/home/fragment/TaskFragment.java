package com.shining.memo.home.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.shining.memo.R;
import com.shining.memo.home.adapter.TaskAdapter;
import com.shining.memo.presenter.AlarmPresenter;
import com.shining.memo.presenter.TaskPresenter;

import org.json.JSONObject;

import java.util.List;

public class TaskFragment extends Fragment{
    private static final String TAG = "TaskFragment";
    private List<JSONObject> mTasks;
    private TaskAdapter mAdapter;
    private TaskPresenter mPresenter;
    private AlarmPresenter alarmPresenter;
    private RecyclerView mRecycleView;
    private View mNodata;
    private LinearLayoutManager layoutManager;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public @Nullable View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view=inflater.inflate(R.layout.main_task,container,false);
        initData();
        mRecycleView=view.findViewById(R.id.main_task_recycler);
        layoutManager = new LinearLayoutManager(getActivity());
        mRecycleView.setLayoutManager(layoutManager);
        mAdapter=new TaskAdapter(mTasks,getActivity());

        mRecycleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        smoothClose();
                        break;
                }
                return false;
            }

        });

        mAdapter.setCallback(new TaskAdapter.Callback() {
            @Override
            public void finishTaskById(int id) {
                mPresenter.finishTaskById(id);
                alarmPresenter.alarmCancel(id);
            }

            @Override
            public void checkShowNoData() {
                if(mTasks.size() == 0){
                    mNodata.setVisibility(View.VISIBLE);
                }else {
                    mNodata.setVisibility(View.INVISIBLE);
                }
            }
        });
        mRecycleView.setAdapter(mAdapter);
        mNodata = view.findViewById(R.id.main_no_data);
        ImageView mNodataIv = mNodata.findViewById(R.id.main_no_data_iv);
        mNodataIv.setImageResource(R.drawable.task_icon);
        TextView mNodataTvTop = mNodata.findViewById(R.id.main_no_data_tv_top);
        mNodataTvTop.setText(getResources().getString(R.string.main_no_data_task_top));
        TextView mNodataTvBottom = mNodata.findViewById(R.id.main_no_data_tv_bottom);
        mNodataTvBottom.setText(getResources().getString(R.string.main_no_data_task_bottom));
        return view;
    }

    /**
     * 点击recycleView的空白处关闭侧滑删除菜单
     */
    private void smoothClose(){
        int itemCount = mAdapter.getItemCount();
        for (int i = 0 ; i < itemCount ; i++){
            View view = layoutManager.findViewByPosition(i);
            if (view != null){
                SwipeMenuLayout swipeMenuLayout = view.findViewById(R.id.swipeMenuLayout);
                ImageView taskType = view.findViewById(R.id.main_task_type);
                int[] location = new int[2] ;
                taskType.getLocationOnScreen(location);
                if (location[0] < 0){
                    swipeMenuLayout.smoothClose();
                }
            }
        }
    }

    public void initData(){
        if(mPresenter != null)
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
    public void setAlarmPresenter(AlarmPresenter presenter){alarmPresenter=presenter;}
    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        refreshData();
        if(mTasks.size() == 0){
            mNodata.setVisibility(View.VISIBLE);
        }else {
            mNodata.setVisibility(View.INVISIBLE);
        }
    }
}
