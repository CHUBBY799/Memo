package com.shining.memo.home.adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.shining.memo.R;
import com.shining.memo.model.Task;
import com.shining.memo.utils.Utils;
import com.shining.memo.view.RecordingViewActivity;

import org.json.JSONObject;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder>{
//    private List<Task> tasks;
//    private List<Boolean> hasAudio;
//    private List<String> alarms;

    private List<JSONObject> tasks;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View mView;
        ImageView type,urgent,confirm,audioTitle1,audioTitle2;
        TextView title,alarm;
        ImageButton complete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            type=itemView.findViewById(R.id.main_task_type);
            urgent=itemView.findViewById(R.id.main_task_urgent);
            confirm=itemView.findViewById(R.id.main_task_confirm);
            title=itemView.findViewById(R.id.main_task_title);
            alarm=itemView.findViewById(R.id.main_task_alarm);
            complete=itemView.findViewById(R.id.main_task_complete);
            audioTitle1=itemView.findViewById(R.id.main_task_audio1);
            audioTitle2=itemView.findViewById(R.id.main_task_audio2);
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
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_taskitem,viewGroup,false);
        final ViewHolder holder=new ViewHolder(view);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getLayoutPosition();
                JSONObject task=tasks.get(position);
                try{
                    int id=task.getInt("taskId");
                    Log.d("helo", "onClick: "+id);
                    Intent intent=new Intent(viewGroup.getContext(), RecordingViewActivity.class);
                    intent.putExtra("taskId",id);
                    viewGroup.getContext().startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        holder.complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position=holder.getLayoutPosition();
                JSONObject task=tasks.get(position);
                try{
                    final int id=task.getInt("taskId");
                    holder.confirm.setVisibility(View.VISIBLE);
                    callback.finishTaskById(id);
                    tasks.remove(position);
                    holder.confirm.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            notifyItemRemoved(position);
                            notifyItemRangeRemoved(position,tasks.size());
                        }
                    },1000);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
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
            holder.confirm.setVisibility(View.INVISIBLE);
            JSONObject task=tasks.get(p);
            if(task.getString("type").equalsIgnoreCase("audio")){
                holder.type.setImageResource(R.drawable.audio_type_icon);
                holder.title.setVisibility(View.INVISIBLE);
                holder.audioTitle1.setVisibility(View.VISIBLE);
                holder.audioTitle2.setVisibility(View.VISIBLE);
            }else {
                holder.type.setVisibility(View.VISIBLE);
                holder.audioTitle1.setVisibility(View.INVISIBLE);
                holder.audioTitle2.setVisibility(View.INVISIBLE);
                holder.type.setImageResource(R.drawable.text_type_icon);
                String title=task.getString("title");
                Spanned spanned= Html.fromHtml(title,Html.FROM_HTML_MODE_COMPACT);
                if(spanned.length()>0){
                    title=spanned.subSequence(0,spanned.length()-1).toString();
                }
                holder.title.setText(title);
            }
            if(task.getInt("urgent")==0){
                holder.urgent.setVisibility(View.INVISIBLE);
            }else if(task.getInt("urgent")==1){
                holder.urgent.setVisibility(View.VISIBLE);
            }
            if(task.getInt("alarm")==0){
                holder.alarm.setText("no alarm");
            }else {
//                String alarmHelp= Utils.formatToMain(task.getString("alarmDate")
//                        ,task.getString("alarmTime"));
                holder.alarm.setText(task.getString("alarmDate")+" "+task.getString("alarmTime"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public interface Callback{
        void finishTaskById(int id);
    }
    private Callback callback;
    public void setCallback(Callback callback){
        this.callback=callback;
    }
}
