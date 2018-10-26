package com.shining.memo.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder>{

    private Context context;
    private FragmentActivity listFragment;
    private int length;
    private int id[];
    private int finished[];
    private String[] list_title;
    private Boolean[] expandState;
    private String[] listDate;
    private Boolean[][] itemState;
    private String[][] itemContent;

    public ListAdapter(Context context, FragmentActivity listFragment) {
        this.context = context;
        this.listFragment = listFragment;
    }

    @Override
    public @NonNull MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.list_main, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final ImageButton expandIcon = holder.expandIcon;
        TextView listTitle = holder.listTitle;
        final ImageView finishedIcon = holder.finishedIcon;
        final LinearLayout listItem = holder.listItem;

        for(int i = 0 ; i < itemContent[position].length ; i ++){
            final int index = i;
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.HORIZONTAL);

            final TextView state = new TextView(context);
            final TextView content = new TextView(context);
            layout.addView(state);
            layout.addView(content);
            listItem.addView(layout);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layout.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, 30);
            layout.setLayoutParams(layoutParams);
            layout.setPadding(0, 10, 0, 10);

            content.setPadding(40,-6,0,0);
            content.setTextSize(16);
            content.setText(itemContent[position][i]);

            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)state.getLayoutParams();
            lp.width = 44;
            lp.height= 44;
            lp.gravity = CENTER;
            state.setLayoutParams(lp);
            if (itemState[holder.getAdapterPosition()][index]){
                state.setBackground(context.getDrawable(R.drawable.group));
                content.setTextColor(context.getColor(R.color.calendar_unselected));
            }else {
                state.setBackground(context.getDrawable(R.drawable.group_2));
                content.setTextColor(context.getColor(R.color.recording_title));
            }

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemState[holder.getAdapterPosition()][index]){
                        state.setBackground(context.getDrawable(R.drawable.group_2));
                        content.setTextColor(context.getColor(R.color.recording_title));
                        itemState[holder.getAdapterPosition()][index] = false;
                        if (finished[holder.getAdapterPosition()] == 1){
                            finished[holder.getAdapterPosition()] = 0;
                            finishedIcon.setImageResource(R.color.white);
                        }

                    }else {
                        state.setBackground(context.getDrawable(R.drawable.group));
                        content.setTextColor(context.getColor(R.color.calendar_unselected));
                        itemState[holder.getAdapterPosition()][index] = true;
                        finished[holder.getAdapterPosition()] = 1;
                        for (int j = 0 ; j < itemState[holder.getAdapterPosition()].length ; j++){
                            if (!itemState[holder.getAdapterPosition()][j]){
                                finished[holder.getAdapterPosition()] = 0;
                                break;
                            }
                        }
                        if (finished[holder.getAdapterPosition()] == 1){
                            finishedIcon.setImageResource(R.drawable.finish_icon);
                        }else {
                            finishedIcon.setImageResource(R.color.white);
                        }
                    }
                }
            });
        }


        if (list_title[holder.getAdapterPosition()].equals("")){
            listTitle.setText(context.getString(R.string.main_task_no_title));
        }else {
            listTitle.setText(list_title[holder.getAdapterPosition()]);
        }
        listTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ListActivity.class);
                intent.putExtra("id", id[holder.getAdapterPosition()]);
                intent.putExtra("finished", finished[holder.getAdapterPosition()]);
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
                listFragment.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        expandIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expandState[holder.getAdapterPosition()]){
                    listItem.setVisibility(View.GONE);
                    expandIcon.setImageResource(R.drawable.fold_icon);
                    expandState[holder.getAdapterPosition()] = false;
                }else{
                    listItem.setVisibility(View.VISIBLE);
                    expandIcon.setImageResource(R.drawable.expand_icon);
                    expandState[holder.getAdapterPosition()] = true;
                }
            }
        });

        if (finished[holder.getAdapterPosition()] == 1){
            finishedIcon.setImageResource(R.drawable.finish_icon);
        }else {
            finishedIcon.setImageResource(R.color.white);
        }
    }

    @Override
    public int getItemCount() {
        return length;
    }

    public void setInfo(ListBean[] listBeans){
        this.length = listBeans.length;
        id = new int[length];
        finished = new int[length];
        list_title = new String[length];
        expandState = new Boolean[length];
        listDate = new String[length];
        itemState = new Boolean[length][];
        itemContent = new String[length][];
        JSONArray itemArr;
        for (int i = 0 ; i < length ; i++){
            id[i] = (int)listBeans[i].getId();
            finished[i] = listBeans[i].getFinished();
            list_title[i] = listBeans[i].getTitle();
            listDate[i] = listBeans[i].getDate();
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
            listBean.setFinished(finished[i]);
            listBean.setDate(listDate[i]);
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
        ImageButton expandIcon;
        TextView listTitle;
        ImageView finishedIcon;
        LinearLayout listItem;

        MyViewHolder(View contentView) {
            super(contentView);
            listTitle = contentView.findViewById(R.id.list_title);
            expandIcon = contentView.findViewById(R.id.expand_icon);
            finishedIcon = contentView.findViewById(R.id.finished_icon);
            listItem = contentView.findViewById(R.id.list_item);
        }
    }
}
