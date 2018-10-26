package com.shining.memo.adapter;

import android.content.Context;

import android.os.Build;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shining.memo.R;
import com.shining.memo.view.CalendarActivity;
import com.shining.memo.view.ListActivity;
import com.shining.memo.view.NoteActivity;
import com.shining.memo.view.TaskActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.MyViewHolder> {

    private Context context;
    private CalendarActivity calendarActivity;
    private int[] task_id;
    private String[] task_title;
    private String[] task_day;
    private String[] task_month;
    private String[] itemArr;
    private int length;
    private String type;

    public CalendarAdapter(Context context, CalendarActivity calendarActivity) {
        this.context = context;
        this.calendarActivity = calendarActivity;
    }

    @Override
    public @NonNull MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.calendar_item, parent,false));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final TextView taskTitle = holder.taskTitle;
        final TextView taskDate = holder.taskDate;
        final TextView taskMonth = holder.taskMonth;
        final ConstraintLayout calendarItem = holder.calendarItem;

        if (task_title[position].equals("")){
            taskTitle.setText(context.getString(R.string.main_task_no_title));
        }else {
            taskTitle.setText(task_title[position]);
        }
        taskDate.setText(task_day[position]);
        taskMonth.setText(task_month[position]);

        if(type.equals("task")){
            calendarItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, TaskActivity.class);
                    intent.putExtra("taskId", task_id[holder.getAdapterPosition()]);
                    context.startActivity(intent);
                    calendarActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
        }
        if (type.equals("note")){
            calendarItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, NoteActivity.class);
                    intent.putExtra("noteId", task_id[holder.getAdapterPosition()]);
                    context.startActivity(intent);
                    calendarActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
        }
        if (type.equals("list")){
            calendarItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ListActivity.class);
                    intent.putExtra("id", task_id[holder.getAdapterPosition()]);
                    intent.putExtra("title", task_title[holder.getAdapterPosition()]);
                    intent.putExtra("itemArr", itemArr[holder.getAdapterPosition()]);
                    context.startActivity(intent);
                    calendarActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return length;
    }

    public void setInfo(JSONArray taskDataArr, int length, String type){
        this.length = length;
        this.type = type;
        task_id = new int[length];
        task_title = new String[length];
        itemArr = new String[length];
        task_day = new String[length];
        task_month = new String[length];
        for (int i = 0 ; i < length ; i++){
            try {
                JSONObject taskData = taskDataArr.getJSONObject(i);
                task_id[i] = taskData.getInt("id");
                task_title[i] = taskData.getString("title");
                if (type.equals("list")){
                    itemArr[i] = taskData.getString("itemArr");
                }
                task_day[i] = taskData.getString("day");
                task_month[i] = taskData.getString("month");
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle;
        TextView taskDate;
        TextView taskMonth;
        ConstraintLayout calendarItem;

        MyViewHolder(View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.task_title);
            taskDate = itemView.findViewById(R.id.task_date);
            taskMonth = itemView.findViewById(R.id.task_month);
            calendarItem = itemView.findViewById(R.id.calendar_item);
        }
    }

}




