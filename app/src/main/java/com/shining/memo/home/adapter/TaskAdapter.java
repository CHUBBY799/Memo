package com.shining.memo.home.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.shining.memo.R;
import com.shining.memo.model.Task;
import com.shining.memo.utils.Utils;

import org.json.JSONObject;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder>{
//    private List<Task> tasks;
//    private List<Boolean> hasAudio;
//    private List<String> alarms;

    private List<JSONObject> tasks;

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView type,urgent,confirm;
        TextView title,alarm;
        ImageButton complete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            type=itemView.findViewById(R.id.main_task_type);
            urgent=itemView.findViewById(R.id.main_task_urgent);
            confirm=itemView.findViewById(R.id.main_task_confirm);
            title=itemView.findViewById(R.id.main_task_title);
            alarm=itemView.findViewById(R.id.main_task_alarm);
            complete=itemView.findViewById(R.id.main_task_complete);
        }
    }

//    public TaskAdapter(List<Task> tasks,List<Boolean> hasAudio,List<String> alarms){
//        this.tasks=tasks;
//        this.hasAudio=hasAudio;
//        this.alarms=alarms;
//    }
    public TaskAdapter(List<JSONObject> tasks){
        this.tasks=tasks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_taskitem,viewGroup,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int p=holder.getLayoutPosition();
//        Task task=tasks.get(p);
//        if(hasAudio.get(p)){
//            holder.type.setImageResource(R.drawable.audio_type_icon);
//        }else {
//            holder.type.setImageResource(R.drawable.text_type_icon);
//        }
//        if(task.getUrgent()==0){
//            holder.urgent.setVisibility(View.INVISIBLE);
//        }else {
//            holder.urgent.setVisibility(View.VISIBLE);
//        }
//        holder.title.setText(task.getTitle());
//        holder.alarm.setText(alarms.get(p));
        try{
            JSONObject task=tasks.get(p);
            if(task.getString("type").equalsIgnoreCase("audio")){
                holder.type.setImageResource(R.drawable.audio_type_icon);
            }else {
                holder.type.setImageResource(R.drawable.text_type_icon);
            }
            if(task.getInt("urgent")==0){
                holder.urgent.setVisibility(View.INVISIBLE);
            }else{
                holder.urgent.setVisibility(View.VISIBLE);
            }
            holder.title.setText(task.getString("title"));
            if(task.getInt("alarm")==0){
                holder.alarm.setText("no alarm");
            }else {
                String alarmHelp= Utils.formatToMain(task.getString("alarmDate")
                        ,task.getString("alarmTime"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
}
