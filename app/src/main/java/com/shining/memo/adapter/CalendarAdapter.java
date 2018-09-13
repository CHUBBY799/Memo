package com.shining.memo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shining.memo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.MyViewHolder> {

    private Context context;
    private String[] title;
    private int length;

    public CalendarAdapter(Context context) {
        this.context = context;
    }

    @Override
    public @NonNull MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        TextView textView = holder.textView;
        textView.setText(title[position]);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return length;
    }

    public void setInfo(JSONArray infoArr, int length){
        this.length = length;
        title = new String[length];
        for (int i = 0 ; i < length ; i++){
            try {
                JSONObject info = infoArr.getJSONObject(i);
                title[i] = info.getString("title");
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_);
        }
    }

}




