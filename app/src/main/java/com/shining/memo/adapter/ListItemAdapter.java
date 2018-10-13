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
    private boolean addItem;
    private JSONArray itemArr;
    private boolean[] state;
    private String[] content;

    public ListItemAdapter(Context context) {
        this.context = context;
    }

    @Override
    public @NonNull MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_edit, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final TextView itemState = holder.itemState;
        final EditText itemContent = holder.itemContent;

        if (state[position]){
            itemState.setBackground(context.getDrawable(R.drawable.group));
            itemContent.setTextColor(context.getColor(R.color.calendar_unselected));
        }else {
            itemState.setBackground(context.getDrawable(R.drawable.group_2));
            itemContent.setTextColor(context.getColor(R.color.recording_title));
        }
        itemContent.setText(content[position]);

        itemState.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int stateIndex = holder.getLayoutPosition();
                if (state[stateIndex]){
                    itemState.setBackground(context.getDrawable(R.drawable.group_2));
                    itemContent.setTextColor(context.getColor(R.color.recording_title));
                    state[stateIndex] = false;
                }else {
                    itemState.setBackground(context.getDrawable(R.drawable.group));
                    itemContent.setTextColor(context.getColor(R.color.calendar_unselected));
                    state[stateIndex] = true;
                }
                try {
                    JSONObject itemInfo = new JSONObject();
                    itemInfo.put("state", state[stateIndex]);
                    itemInfo.put("content", content[stateIndex]);
                    itemArr.put(stateIndex, itemInfo);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });

        itemContent.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    int contentIndex = holder.getLayoutPosition();
                    content[contentIndex] = itemContent.getText().toString();
                    if(content[contentIndex].equals("")){
                        itemArr.remove(contentIndex);
                        setInfo(itemArr, itemArr.length());
                        notifyItemRemoved(contentIndex);
                        if (contentIndex != length - 1 || contentIndex == 0){
                            notifyItemRangeChanged(contentIndex,length - contentIndex);
                        }else {
                            notifyItemChanged(length - 1);
                        }
                    }else {
                        try {
                            JSONObject itemInfo = new JSONObject();
                            itemInfo.put("state", state[contentIndex]);
                            itemInfo.put("content", content[contentIndex]);
                            itemArr.put(contentIndex, itemInfo);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        if(holder.getAdapterPosition() == length - 1 && addItem){
            itemContent.setFocusable(true);
            itemContent.setFocusableInTouchMode(true);
            itemContent.requestFocus();
            addItem = false;
        }
    }

    @Override
    public int getItemCount() {
        return length;
    }

    public void setInfo(JSONArray itemArr, int length){
        this.length = length;
        this.itemArr = itemArr;
        state = new boolean[length];
        content = new String[length];
        for (int i = 0 ; i < length ; i++){
            try {
                JSONObject itemInfo = itemArr.getJSONObject(i);
                state[i] = itemInfo.getBoolean("state");
                content[i] = itemInfo.getString("content");
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    public void addInfo(JSONObject item){
        itemArr.put(item);
        setInfo(itemArr, itemArr.length());
        addItem = true;
        notifyItemChanged(length);
    }

    public JSONArray getItemArr(){
        return itemArr;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView itemState;
        public EditText itemContent;
        public MyViewHolder(View itemView) {
            super(itemView);
            itemState = itemView.findViewById(R.id.item_state);
            itemContent = itemView.findViewById(R.id.item_content);
        }
    }
}
