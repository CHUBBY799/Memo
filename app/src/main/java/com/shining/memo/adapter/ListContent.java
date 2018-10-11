package com.shining.memo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shining.memo.R;
import com.shining.memo.bean.ListBean;
import com.shining.memo.view.ListActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.view.Gravity.CENTER;

public class ListContent  extends RecyclerView.Adapter<ListContent.MyViewHolder>{

    private Context context;
    private int length;
    private int id[];
    private int selected[];
    private String[] list_title;
    private Boolean[] expandState;
    private Boolean[][] itemState;
    private String[][] itemContent;

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
        final LinearLayout listItem = holder.listItem;

        for(int i = 0 ; i < itemContent[position].length ; i ++){
            final int index = i;
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.HORIZONTAL);

            final TextView content = new TextView(context);
            content.setPadding(32,0,0,0);
            content.setTextColor(context.getColor(R.color.recording_title));
            content.setTextSize(20);
            content.setText(itemContent[position][i]);

            final TextView state = new TextView(context);
            state.setTextColor(context.getColor(R.color.calendar_select));
            state.setWidth(15);
            state.setHeight(15);
            state.setGravity(CENTER);
            state.setBackground(context.getDrawable(R.drawable.non_oval_background));
            if (itemState[holder.getAdapterPosition()][index]){
                state.setText("√");
            }
            state.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemState[holder.getAdapterPosition()][index]){
                        state.setText("");
                        content.setTextColor(context.getColor(R.color.recording_title));
                        itemState[holder.getAdapterPosition()][index] = false;
                    }else {
                        state.setText("√");
                        content.setTextColor(context.getColor(R.color.calendar_unselected));
                        itemState[holder.getAdapterPosition()][index] = true;
                    }
                }
            });

            layout.addView(state);
            layout.addView(content);
            listItem.addView(layout);
        }


        listTitle.setText(list_title[holder.getAdapterPosition()]);
        listTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ListActivity.class);
                intent.putExtra("id", id[holder.getAdapterPosition()]);
                intent.putExtra("selected", selected[holder.getAdapterPosition()]);
                intent.putExtra("title", list_title[holder.getAdapterPosition()]);

                JSONArray itemArr = new JSONArray();
                for (int j = 0 ; j < itemContent[holder.getAdapterPosition()].length ; j++){
                    try{
                        JSONObject item = new JSONObject();
                        item.put("state", itemState[holder.getAdapterPosition()][j]);
                        item.put("content", itemContent[holder.getAdapterPosition()][j]);
                        itemArr.put(item);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                intent.putExtra("itemArr", itemArr.toString());
                context.startActivity(intent);
            }
        });

        expandIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expandState[holder.getAdapterPosition()]){
                    listItem.setVisibility(View.GONE);
                    expandIcon.setBackground(context.getDrawable(R.drawable.fold_icon));
                    expandState[holder.getAdapterPosition()] = false;
                }else{
                    listItem.setVisibility(View.VISIBLE);
                    expandIcon.setBackground(context.getDrawable(R.drawable.expand_icon));
                    expandState[holder.getAdapterPosition()] = true;
                }
            }
        });

        if (selected[holder.getAdapterPosition()] == 1){
            startIcon.setBackground(context.getDrawable(R.drawable.star_selected_icon));
        }
        startIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selected[holder.getAdapterPosition()] == 1){
                    startIcon.setBackground(context.getDrawable(R.drawable.star_default_icon));
                    selected[holder.getAdapterPosition()] = 0;
                }else{
                    startIcon.setBackground(context.getDrawable(R.drawable.star_selected_icon));
                    selected[holder.getAdapterPosition()] = 1;
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
        id = new int[length];
        selected = new int[length];
        list_title = new String[length];
        expandState = new Boolean[length];
        itemState = new Boolean[length][];
        itemContent = new String[length][];
        JSONArray itemArr;
        for (int i = 0 ; i < length ; i++){
            id[i] = (int)listBeans[i].getId();
            selected[i] = listBeans[i].getSelected();
            list_title[i] = listBeans[i].getTitle();
            expandState[i] = false;
            try{
                itemArr = new JSONArray(listBeans[i].getItemArr());
                itemState[i] = new Boolean[itemArr.length()];
                itemContent[i] = new String[itemArr.length()];
                for (int j = 0 ; j < itemArr.length() ; j++){
                    JSONObject item = itemArr.getJSONObject(j);
                    itemState[i][j] = item.getBoolean("state");
                    itemContent[i][j] = item.getString("content");
                }

            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    public ListBean[] getInfo(){
        ListBean[] listBeans = new ListBean[length];
        for (int i = 0 ; i < length ; i++){
            ListBean listBean = new ListBean();
            listBean.setId(id[i]);
            listBean.setSelected(selected[i]);
            JSONArray itemArr = new JSONArray();
            try{
                for (int j = 0 ; j < itemState[i].length ; j++){
                    JSONObject item = new JSONObject();
                    item.put("state", itemState[i][j]);
                    item.put("content", itemContent[i][j]);
                    itemArr.put(item);

                }

            }catch (JSONException e){
                e.printStackTrace();
            }
            listBean.setItemArr(itemArr.toString());
            listBeans[i] = listBean;
        }
        return listBeans;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView expandIcon;
        TextView listTitle;
        ImageView startIcon;
        LinearLayout listItem;

        public MyViewHolder(View contentView) {
            super(contentView);
            listTitle = contentView.findViewById(R.id.list_title);
            expandIcon = contentView.findViewById(R.id.expand_icon);
            startIcon = contentView.findViewById(R.id.start_default);
            listItem = contentView.findViewById(R.id.list_item);
        }
    }
}