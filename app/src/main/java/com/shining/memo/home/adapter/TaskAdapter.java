package com.shining.memo.home.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.shining.memo.R;
import com.shining.memo.utils.DialogUtils;
import com.shining.memo.utils.ToastUtils;
import com.shining.memo.view.TaskActivity;

import org.json.JSONObject;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder>{
    private static final String TAG = "TaskAdapter";
//    private List<Task> tasks;
//    private List<Boolean> hasAudio;
//    private List<String> alarms;

    private List<JSONObject> tasks;
    private boolean click=false;
    private Context mContext;

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
    public TaskAdapter(List<JSONObject> tasks, Context context){
        this.tasks=tasks;
        mContext=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_taskitem,viewGroup,false);
        final ViewHolder holder=new ViewHolder(view);
        holder.mView.setClickable(true);
        holder.complete.setClickable(true);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getLayoutPosition();
                JSONObject task=tasks.get(position);
                try{
                    int id=task.getInt("taskId");
                    Log.d("helo", "onClick: "+id);
                    Intent intent=new Intent(viewGroup.getContext(), TaskActivity.class);
                    intent.putExtra("taskId",id);
                    viewGroup.getContext().startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
//        holder.complete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!click) {
//                    click=true;
//                    final int position = holder.getLayoutPosition();
//                    final JSONObject task = tasks.get(position);
//                    try {
//                        final int id = task.getInt("taskId");
//                        Log.d("hh", "onClick: "+id);
//                        holder.confirm.setVisibility(View.VISIBLE);
//                        callback.finishTaskById(id);
//                        holder.complete.setClickable(false);
//                        holder.mView.setClickable(false);
//                        tasks.remove(position);
//                        android.os.Handler handler = new android.os.Handler();
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                notifyItemRemoved(position);
//                                notifyItemRangeRemoved(position,tasks.size());
//                                click=false;
//                            }
//                        }, 1000);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }
//        });
        holder.complete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action=event.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        holder.complete.setBackgroundResource(R.color.main_confirm_focus);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        holder.complete.setBackgroundResource(R.color.colorWhite);
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d(TAG, "onTouch: up");
                        holder.complete.setBackgroundResource(R.color.colorWhite);
                        DialogUtils.showDialog(mContext, mContext.getString(R.string.main_finish_dialog_title)
                                , mContext.getString(R.string.main_finish_dialog_message), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (!click) {
                                            click = true;
                                            final int position = holder.getLayoutPosition();
                                            final JSONObject task = tasks.get(position);
                                            try {
                                                final int id = task.getInt("taskId");
                                                Log.d("hh", "onClick: " + id);
                                                holder.confirm.setVisibility(View.VISIBLE);
                                                callback.finishTaskById(id);
                                                holder.complete.setClickable(false);
                                                holder.mView.setClickable(false);
                                                tasks.remove(position);
                                                android.os.Handler handler = new android.os.Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        notifyItemRemoved(position);
                                                        notifyItemRangeRemoved(position, tasks.size());
                                                        callback.checkShowNoData();
                                                        click = false;
                                                    }
                                                }, 500);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }
                                }, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ToastUtils.showShort(mContext,"Cancel");
                                    }
                                });
                        break;
                }
                return true;
            }
        });
        return holder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.complete.setBackgroundResource(R.color.colorWhite);
        holder.complete.setClickable(true);
        holder.mView.setClickable(true);
        int p=holder.getLayoutPosition();
        try{
            holder.confirm.setVisibility(View.INVISIBLE);
            JSONObject task=tasks.get(p);
            if(task.getString("type").equalsIgnoreCase("audio") && (task.getString("title")==null
                    ||task.getString("title").equals(""))){
                holder.title.setVisibility(View.INVISIBLE);
                holder.audioTitle1.setVisibility(View.VISIBLE);
                holder.audioTitle2.setVisibility(View.VISIBLE);
            }else if(task.getString("type").equalsIgnoreCase("text") && (task.getString("title")==null
                    ||task.getString("title").equals(""))){
                holder.audioTitle1.setVisibility(View.INVISIBLE);
                holder.audioTitle2.setVisibility(View.INVISIBLE);
                holder.title.setVisibility(View.VISIBLE);
                holder.title.setText(mContext.getString(R.string.main_task_no_title));
            } else {
                holder.audioTitle1.setVisibility(View.INVISIBLE);
                holder.audioTitle2.setVisibility(View.INVISIBLE);
                holder.title.setVisibility(View.VISIBLE);
                holder.title.setText(task.getString("title"));
            }
            if(task.getString("type").equalsIgnoreCase("audio")){
                holder.type.setImageResource(R.drawable.audio_type_icon);
            }else {
                holder.type.setImageResource(R.drawable.text_type_icon);
            }
            if(task.getInt("urgent")==0){
                holder.urgent.setVisibility(View.INVISIBLE);
            }else if(task.getInt("urgent")==1){
                holder.urgent.setVisibility(View.VISIBLE);
            }
            if(task.getInt("alarm")==0){
                holder.alarm.setText(mContext.getString(R.string.main_task_no_alarm));
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
        void checkShowNoData();
    }
    private Callback callback;
    public void setCallback(Callback callback){
        this.callback=callback;
    }

}
