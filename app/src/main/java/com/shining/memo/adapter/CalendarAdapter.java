package com.shining.memo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.shining.memo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.MyViewHolder> {

    private Context context;
    private int[] task_id;
    private int[] task_finished;
    private String[] task_title;
    private String[] task_day;
    private String[] task_month;
    private int length;

    public CalendarAdapter(Context context) {
        this.context = context;
    }

    @Override
    public @NonNull MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.calendar_item, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final TextView taskFinished = holder.taskFinished;
        final TextView taskTitle = holder.taskTitle;
        final TextView taskDate = holder.taskDate;
        final TextView taskMonth = holder.taskMonth;
        if (task_finished[position] == 0){
            taskFinished.setBackground(null);
        }
        taskTitle.setText(task_title[position]);
        taskDate.setText(task_day[position]);
        taskMonth.setText(task_month[position]);

        holder.taskTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,task_id[holder.getAdapterPosition()] + " " + taskTitle.getText(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return length;
    }

    public void setInfo(JSONArray taskDataArr, int length){
        this.length = length;
        task_id = new int[length];
        task_finished = new int[length];
        task_title = new String[length];
        task_day = new String[length];
        task_month = new String[length];
        for (int i = 0 ; i < length ; i++){
            try {
                JSONObject taskData = taskDataArr.getJSONObject(i);
                task_id[i] = taskData.getInt("id");
                task_finished[i] = taskData.getInt("finished");
                task_title[i] = taskData.getString("title");
                task_day[i] = taskData.getString("day");
                task_month[i] = taskData.getString("month");
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView taskFinished;
        TextView taskTitle;
        TextView taskDate;
        TextView taskMonth;

        public MyViewHolder(View itemView) {
            super(itemView);
            taskFinished = itemView.findViewById(R.id.task_finished);
            taskTitle = itemView.findViewById(R.id.task_title);
            taskDate = itemView.findViewById(R.id.task_date);
            taskMonth = itemView.findViewById(R.id.task_month);
        }
    }

}




