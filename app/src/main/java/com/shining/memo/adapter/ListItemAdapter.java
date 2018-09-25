package com.shining.memo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shining.memo.R;
import com.shining.memo.view.TextActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ListItemAdapter extends RecyclerView.Adapter<ListItemAdapter.MyViewHolder> {

    private Context context;
    private String[] title;
    private Boolean[] state;
    private int length;

    public ListItemAdapter(Context context) {
        this.context = context;
    }

    @Override
    public @NonNull MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final TextView contentTitle = holder.contentTitle;
        final TextView contentState = holder.contentState;
        contentTitle.setText(title[position]);
        if (state[position]){
            contentState.setText(R.string.list_finish);
        }

        contentTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent textIntent = new Intent(context,TextActivity.class);
                context.startActivity(textIntent);
            }
        });

        contentState.setOnClickListener(new View.OnClickListener() {
            int index = holder.getAdapterPosition();
            @Override
            public void onClick(View v) {
                if (state[index]){
                    state[index] = false;
                    contentState.setText("");
                }else {
                    state[index] = true;
                    contentState.setText(R.string.list_finish);
                }
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
        state = new Boolean[length];
        for (int i = 0 ; i < length ; i++){
            try {
                JSONObject info = infoArr.getJSONObject(i);
                title[i] = info.getString("title");
                state[i] = info.getBoolean("state");
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView contentTitle;
        TextView contentState;
        public MyViewHolder(View itemView) {
            super(itemView);
            contentTitle = itemView.findViewById(R.id.item_title);
            contentState = itemView.findViewById(R.id.item_state);
        }
    }

}
