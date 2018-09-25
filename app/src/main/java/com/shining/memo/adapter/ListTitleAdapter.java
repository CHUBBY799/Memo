package com.shining.memo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shining.memo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ListTitleAdapter extends RecyclerView.Adapter<ListTitleAdapter.MyViewHolder> {

    private Context context;
    private String[] title;
    private Boolean[] state;
    private String[] itemStr;
    private Boolean[] selected;
    private int length;

    public ListTitleAdapter(Context context) {
        this.context = context;
    }

    @Override
    public @NonNull MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.list_title, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ListTitleAdapter.MyViewHolder holder, final int position) {
        final TextView contentTitle = holder.contentTitle;
        final TextView contentState = holder.contentState;
        final ImageView listCheckbox = holder.listCheckbox;
        contentTitle.setText(title[position]);
        if (state[position]){
            contentState.setText(R.string.list_finish);
        }

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

        final LinearLayout listItem = holder.listItem;
        JSONArray itemArr;
        try{
            itemArr = new JSONArray(itemStr[position]);
            int itemArrLen = itemArr.length();
            for (int i = 0 ; i < itemArrLen ; i ++){
                JSONObject itemInfo = itemArr.getJSONObject(i);
                String item_title = itemInfo.getString("title");
                final Boolean item_state = itemInfo.getBoolean("state");
                View listItemLayout = View.inflate(context, R.layout.list_item, null);
                final TextView itemState = listItemLayout.findViewById(R.id.item_state);
                TextView itemTitle = listItemLayout.findViewById(R.id.item_title);
                if (item_state){
                    itemState.setText(R.string.list_finish);
                }
                itemState.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (itemState.getText() == ""){
                            itemState.setText(R.string.list_finish);
                        }else {
                            itemState.setText("");
                        }
                    }
                });

                itemTitle.setText(item_title);
                listItem.addView(listItemLayout);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        listCheckbox.setOnClickListener(new View.OnClickListener() {
            int index = holder.getAdapterPosition();
            @Override
            public void onClick(View v) {
                if (selected[index]){
                    listCheckbox.setSelected(false);
                    listItem.setVisibility(View.GONE);
                    selected[index] = false;
                }else {
                    listCheckbox.setSelected(true);
                    listItem.setVisibility(View.VISIBLE);
                    selected[index] = true;
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
        itemStr = new String[length];
        selected = new Boolean[length];
        for (int i = 0 ; i < length ; i++){
            try {
                JSONObject info = infoArr.getJSONObject(i);
                title[i] = info.getString("title");
                state[i] = info.getBoolean("state");
                itemStr[i] = info.getString("itemArr");
                selected[i] = false;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView contentTitle;
        TextView contentState;
        LinearLayout listItem;
        ImageView listCheckbox;
        public MyViewHolder(View itemView) {
            super(itemView);
            contentTitle = itemView.findViewById(R.id.list_title);
            contentState = itemView.findViewById(R.id.list_state);
            listItem = itemView.findViewById(R.id.list_item);
            listCheckbox = itemView.findViewById(R.id.list_checkbox);
        }
    }

}