package com.shining.memo.adapter;

import android.content.Context;

import android.os.Build;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shining.memo.R;
import com.shining.memo.view.RecordingViewActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.support.v4.text.HtmlCompat.FROM_HTML_MODE_COMPACT;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.MyViewHolder> {

    private Context context;
    private int[] task_id;
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final TextView taskTitle = holder.taskTitle;
        final TextView taskDate = holder.taskDate;
        final TextView taskMonth = holder.taskMonth;
        final ConstraintLayout calendarItem = holder.calendarItem;

        taskTitle.setText(task_title[position]);
        taskDate.setText(task_day[position]);
        taskMonth.setText(task_month[position]);

        calendarItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RecordingViewActivity.class);
                intent.putExtra("taskId", task_id[holder.getAdapterPosition()]);
                context.startActivity(intent);
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
        task_title = new String[length];
        task_day = new String[length];
        task_month = new String[length];
        for (int i = 0 ; i < length ; i++){
            try {
                JSONObject taskData = taskDataArr.getJSONObject(i);
                task_id[i] = taskData.getInt("id");
                Spanned spanned = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    spanned = Html.fromHtml(taskData.getString("title"), FROM_HTML_MODE_COMPACT);
                }
                if(spanned != null && spanned.length() > 0)
                    task_title[i] = (spanned.subSequence(0,spanned.length() -1)).toString();
                else if(spanned != null){
                    task_title[i] = spanned.toString();
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

        public MyViewHolder(View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.task_title);
            taskDate = itemView.findViewById(R.id.task_date);
            taskMonth = itemView.findViewById(R.id.task_month);
            calendarItem = itemView.findViewById(R.id.calendar_item);
        }
    }

}




