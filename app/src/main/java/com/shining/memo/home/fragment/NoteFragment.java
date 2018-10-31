package com.shining.memo.home.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.shining.memo.R;
import com.shining.memo.home.adapter.NoteAdapter;
import com.shining.memo.model.Task;
import com.shining.memo.presenter.TaskPresenter;

import java.util.List;

public class NoteFragment extends Fragment{
    private TaskPresenter mPresenter;
    private RecyclerView mRecycler;
    private List<Task> mNotes;
    private NoteAdapter mAdapter;
    private View mNodata;
    private LinearLayoutManager manager;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public @Nullable View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.main_note,container,false);
        initData();
        mRecycler=view.findViewById(R.id.main_note_recycler);
        manager=new LinearLayoutManager(getActivity());
        mRecycler.setLayoutManager(manager);
        mAdapter=new NoteAdapter(getActivity(),mNotes);
        mRecycler.setAdapter(mAdapter);
        mRecycler.setOnTouchListener(new View.OnTouchListener() {
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

        mNodata = view.findViewById(R.id.main_no_data);
        ImageView mNodataIv = mNodata.findViewById(R.id.main_no_data_iv);
        mNodataIv.setImageResource(R.drawable.notes_icon);
        TextView mNodataTvTop = mNodata.findViewById(R.id.main_no_data_tv_top);
        mNodataTvTop.setText(getResources().getString(R.string.main_no_data_note_top));
        TextView mNodataTvBottom = mNodata.findViewById(R.id.main_no_data_tv_bottom);
        mNodataTvBottom.setText(getResources().getString(R.string.main_no_data_note_bottom));
        return view;
    }

    /**
     * 点击recycleView的空白处关闭侧滑删除菜单
     */
    private void smoothClose(){
        int itemCount = mAdapter.getItemCount();
        for (int i = 0 ; i < itemCount ; i++){
            View view = manager.findViewByPosition(i);
            if (view != null){
                SwipeMenuLayout swipeMenuLayout = view.findViewById(R.id.swipeMenuLayout);
                TextView taskType = view.findViewById(R.id.main_note_title);
                int[] location = new int[2] ;
                taskType.getLocationOnScreen(location);
                if (location[0] < 0){
                    swipeMenuLayout.smoothClose();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
        if(mNotes.size() == 0){
            mNodata.setVisibility(View.VISIBLE);
        }else {
            mNodata.setVisibility(View.INVISIBLE);
        }
    }

    public void initData(){
        mNotes=mPresenter.getNotes();
    }
    public void refreshData(){
        mNotes.clear();
        mNotes.addAll(mPresenter.getNotes());
        mAdapter.notifyDataSetChanged();
    }
    public void setPresenter(TaskPresenter presenter){
        mPresenter=presenter;
    }

}
