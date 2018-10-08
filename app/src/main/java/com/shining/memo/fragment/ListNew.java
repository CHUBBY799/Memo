package com.shining.memo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shining.memo.R;
import com.shining.memo.view.ListActivity;

public class ListNew extends Fragment{

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_new, container, false);
    }

    @Override
    public void onActivityCreated(Bundle saveInstanceState){
        super.onActivityCreated(saveInstanceState);
        TextView newList = getActivity().findViewById(R.id.list_new);
        newList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent textIntent = new Intent(getActivity(),ListActivity.class);
                textIntent.putExtra("title", "new");
                getActivity().startActivity(textIntent);
            }
        });
    }
}
