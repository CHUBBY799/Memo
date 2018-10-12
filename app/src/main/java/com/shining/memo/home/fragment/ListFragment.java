package com.shining.memo.home.fragment;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
        if (getActivity() != null){
            listFragment = getActivity().findViewById(R.id.list_fragment);
        }
        listFragment.setLayoutManager(new LinearLayoutManager(context));
        listContent = new ListContent(context, getActivity());
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


    /**
     * 解除 Fragment 与 Activity 的关联
     */
    @Override
    public void onDetach() {
        ListBean[] listBeans = listContent.getInfo();
        if (listBeans != null){
            ListModel listModel = new ListImpl(context);
            listModel.updateAllDataById(listBeans);
        }
        super.onDetach();
    }
}