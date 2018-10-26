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
import android.widget.ImageView;
import android.widget.TextView;

import com.shining.memo.R;
import com.shining.memo.home.adapter.ListAdapter;
import com.shining.memo.bean.ListBean;
import com.shining.memo.model.ListImpl;
import com.shining.memo.model.ListModel;

public class ListFragment extends Fragment {

    private Context context;
    private RecyclerView listFragment;
    private ListAdapter listAdapter;
    private View mNodata;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        mNodata = view.findViewById(R.id.main_no_data);
        ImageView mNodataIv = mNodata.findViewById(R.id.main_no_data_iv);
        mNodataIv.setImageResource(R.drawable.list_icon);
        TextView mNodataTvTop = mNodata.findViewById(R.id.main_no_data_tv_top);
        mNodataTvTop.setText(getResources().getString(R.string.main_no_data_list_top));
        TextView mNodataTvBottom = mNodata.findViewById(R.id.main_no_data_tv_bottom);
        mNodataTvBottom.setText(getResources().getString(R.string.main_no_data_list_bottom));
        return  view;
    }

    @Override
    public void onActivityCreated(Bundle saveInstanceState){
        super.onActivityCreated(saveInstanceState);
        context = getActivity();
        if (getActivity() != null){
            listFragment = getActivity().findViewById(R.id.list_fragment);
        }
        listFragment.setLayoutManager(new LinearLayoutManager(context));
        listAdapter = new ListAdapter(context, getActivity());
    }

    @Override
    public void onResume(){
        super.onResume();
        ListModel listModel = new ListImpl(context);
        ListBean[] listBeans = listModel.queryAllData();
        if (listBeans != null){
            listAdapter.setInfo(listBeans);
            listFragment.setAdapter(listAdapter);
        }
        if(listBeans.length == 0){
            mNodata.setVisibility(View.VISIBLE);
        }else {
            mNodata.setVisibility(View.INVISIBLE);
        }

    }


    /**
     * 解除 Fragment 与 Activity 的关联
     */
    @Override
    public void onPause() {
        super.onPause();
        ListBean[] listBeans = listAdapter.getInfo();
        if (listBeans != null){
            ListModel listModel = new ListImpl(context);
            listModel.updateAllDataById(listBeans);
        }
        super.onDetach();
    }
}