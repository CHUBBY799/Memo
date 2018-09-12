package com.shining.memo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shining.memo.R;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.MyViewHolder> {

    private Context context;

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
        textView.setText(String.format(context.getResources().getString(R.string.selected_day),position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return 50;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_);
        }
    }

}




