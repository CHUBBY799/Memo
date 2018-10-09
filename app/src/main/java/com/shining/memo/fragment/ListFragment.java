package com.shining.memo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shining.memo.R;
import com.shining.memo.adapter.ListContent;
import com.shining.memo.bean.ListBean;
import com.shining.memo.model.ListImpl;
import com.shining.memo.model.ListModel;

public class ListFragment extends Fragment {

    private Context context;
    private RecyclerView listFragment;
    private ListContent listContent;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle saveInstanceState){
        super.onActivityCreated(saveInstanceState);
        context = getActivity();
        listFragment = getActivity().findViewById(R.id.list_fragment);
        listFragment.setLayoutManager(new LinearLayoutManager(context));
        listContent = new ListContent(context);
    }

    @Override
    public void onResume(){
        super.onResume();
        ListModel listModel = new ListImpl(context);
        ListBean[] listBeans = listModel.queryAllData();
        if (listBeans != null){
            listContent.setInfo(listBeans);
            listFragment.setAdapter(listContent);
        }
    }
}