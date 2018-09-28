package com.shining.memo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.shining.memo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ListItemAdapter extends RecyclerView.Adapter<ListItemAdapter.MyViewHolder> {

    private Context context;
    private int length;
    private JSONArray itemArr;

    public ListItemAdapter(Context context) {
        this.context = context;
    }

    @Override
    public @NonNull MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_edit, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final EditText itemContent = holder.itemContent;
        final TextView itemState = holder.itemState;

        final JSONObject itemInfo;
        String content = null;
        boolean state = false;
        try {
            itemInfo = itemArr.getJSONObject(position);
            content = itemInfo.getString("content");
            state = itemInfo.getBoolean("state");
        }catch (JSONException e){
            e.printStackTrace();
        }

        itemContent.setText(content);
        if (state){
            itemState.setText(R.string.list_finish);
        }

        itemState.setOnClickListener(new View.OnClickListener() {
            int stateIndex = holder.getAdapterPosition();
            boolean tempState;
            @Override
            public void onClick(View v) {
                if (itemState.getText().toString().equals("")){
                    itemState.setText(R.string.list_finish);
                    tempState = true;
                }else {
                    itemState.setText("");
                    tempState = false;
                }
                try {
                    JSONObject itemInfo = new JSONObject();
                    itemInfo.put("state", tempState);
                    itemInfo.put("content", itemContent.getText().toString());
                    itemArr.put(stateIndex, itemInfo);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });

        itemContent.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (holder.getAdapterPosition() == length - 1){
                        try {
                            JSONObject itemInfo = new JSONObject();
                            itemInfo.put("state", false);
                            itemInfo.put("content", "");
                            itemArr.put(itemInfo);
                            setInfo(itemArr, itemArr.length());
                            notifyItemChanged(length);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                } else {
                    int contentIndex = holder.getAdapterPosition();
                    if(itemContent.getText().toString().equals("")){
                        itemArr.remove(contentIndex);
                        setInfo(itemArr, itemArr.length());
                        notifyItemRemoved(contentIndex);
                        notifyItemRangeChanged(contentIndex,length - contentIndex);
                    }else {
                        try {
                            boolean tempState = (!itemState.getText().toString().equals(""));
                            JSONObject itemInfo = new JSONObject();
                            itemInfo.put("state", tempState);
                            itemInfo.put("content", itemContent.getText().toString());
                            itemArr.put(contentIndex, itemInfo);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return length;
    }

    public void setInfo(JSONArray itemArr, int length){
        this.length = length;
        this.itemArr = itemArr;
    }

    public JSONArray getItemArr(){
        return itemArr;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        EditText itemContent;
        TextView itemState;
        public MyViewHolder(View itemView) {
            super(itemView);
            itemContent = itemView.findViewById(R.id.item_content);
            itemState = itemView.findViewById(R.id.item_state);
        }
    }

}
