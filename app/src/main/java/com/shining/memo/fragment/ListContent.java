package com.shining.memo.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shining.memo.R;
import com.shining.memo.adapter.ListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

public class ListContent extends Fragment {

    private RecyclerView recyclerView;
    private ListAdapter listAdapter;
    private JSONArray infoArr;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        infoArr = new JSONArray();
        return inflater.inflate(R.layout.content_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle saveInstanceState){
        super.onActivityCreated(saveInstanceState);
        Context context = getActivity();
        recyclerView = getActivity().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        listAdapter = new ListAdapter(context);
    }

    public void addInfo(JSONObject info){
        infoArr.put(info);
    }

    @Override
    public void onResume(){
        super.onResume();
        listAdapter.setInfo(infoArr, infoArr.length());
        recyclerView.setAdapter(listAdapter);
    }
}