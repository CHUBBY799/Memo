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
import org.json.JSONException;
import org.json.JSONObject;

public class ListContent extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle saveInstanceState){
        super.onActivityCreated(saveInstanceState);
        Context context = getActivity();
        RecyclerView recyclerView = getActivity().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        ListAdapter listAdapter = new ListAdapter(context);
        JSONArray infoArr = new JSONArray();
        for(int i = 0 ; i < 10 ; i++){
            String title = "购物清单" + i;
            JSONObject info = new JSONObject();
            try {
                info.put("title", title);
                info.put("state",true);
            }catch (JSONException e){
                e.printStackTrace();
            }
            infoArr.put(info);
        }
        listAdapter.setInfo(infoArr, infoArr.length());
        recyclerView.setAdapter(listAdapter);
    }
}