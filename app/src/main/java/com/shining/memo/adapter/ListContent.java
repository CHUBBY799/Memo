package com.shining.memo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shining.memo.R;
import com.shining.memo.bean.ListBean;

public class ListContent  extends RecyclerView.Adapter<ListContent.MyViewHolder>{

    private Context context;
    private int length;
    private String[] list_title;
    private Boolean[] expandState;
    private Boolean[] startState;

    public ListContent(Context context) {
        this.context = context;
    }

    @Override
    public @NonNull MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.list_content, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final ImageView expandIcon = holder.expandIcon;
        TextView listTitle = holder.listTitle;
        final ImageView startIcon = holder.startIcon;

        listTitle.setText(list_title[holder.getAdapterPosition()]);
        expandIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expandState[holder.getAdapterPosition()]){
                    expandIcon.setBackground(context.getDrawable(R.drawable.fold_icon));
                    expandState[holder.getAdapterPosition()] = false;
                }else{
                    expandIcon.setBackground(context.getDrawable(R.drawable.expand_icon));
                    expandState[holder.getAdapterPosition()] = true;
                }
            }
        });

        startIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(startState[holder.getAdapterPosition()]){
                    startIcon.setBackground(context.getDrawable(R.drawable.star_default_icon));
                    startState[holder.getAdapterPosition()] = false;
                }else{
                    startIcon.setBackground(context.getDrawable(R.drawable.star_selected_icon));
                    startState[holder.getAdapterPosition()] = true;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return length;
    }

    public void setInfo(ListBean[] listBeans){
        this.length = listBeans.length;
        list_title = new String[length];
        expandState = new Boolean[length];
        startState = new Boolean[length];
        for (int i = 0 ; i < length ; i++){
            list_title[i] = listBeans[i].getTitle();
            expandState[i] = false;
            startState[i] = false;
        }
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView expandIcon;
        TextView listTitle;
        ImageView startIcon;

        public MyViewHolder(View contentView) {
            super(contentView);
            listTitle = contentView.findViewById(R.id.list_title);
            expandIcon = contentView.findViewById(R.id.expand_icon);
            startIcon = contentView.findViewById(R.id.start_default);
        }
    }
}
