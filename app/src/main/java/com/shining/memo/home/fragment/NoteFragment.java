package com.shining.memo.home.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.main_note,container,false);
        initData();
        mRecycler=view.findViewById(R.id.main_note_recycler);
        LinearLayoutManager manager=new LinearLayoutManager(getActivity());
        mRecycler.setLayoutManager(manager);
        mAdapter=new NoteAdapter(getActivity(),mNotes);
        mRecycler.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
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
