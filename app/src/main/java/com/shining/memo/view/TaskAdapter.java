package com.shining.memo.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shining.memo.R;

import org.json.JSONObject;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final String TAG = "TaskAdapter";
    private Context mContext;
    private List<JSONObject> tasks;
    private static final int TEXT_TYPE=1;
    private static final int  AUDIO_TYPE=2;
    public TaskAdapter(Context context,List<JSONObject> tasks){
        mContext=context;
        this.tasks=tasks;
    }
    static class TextViewHolder extends RecyclerView.ViewHolder{
          Button typeText,alarmText,deleteText;
          TextView titleText,timeText,urgentText;
          public TextViewHolder(View view){
              super(view);
              typeText=view.findViewById(R.id.memo_type_text);
              alarmText=view.findViewById(R.id.memo_alarm_text);
              deleteText=view.findViewById(R.id.memo_text_delete);
              titleText=view.findViewById(R.id.memo_text_title);
              timeText=view.findViewById(R.id.memo_text_time);
              urgentText=view.findViewById(R.id.memo_text_urgent);
          }
    }
    static class AudioViewHolder extends RecyclerView.ViewHolder{
          Button typeAudio,alarmAudio,deleteAudio;
          TextView titleAudio,timeAudio,urgentAudio;
          public AudioViewHolder(View view){
              super(view);
              typeAudio=view.findViewById(R.id.memo_type_audio);
              alarmAudio=view.findViewById(R.id.memo_alarm_audio);
              deleteAudio=view.findViewById(R.id.memo_audio_delete);
              timeAudio=view.findViewById(R.id.memo_audio_time);
              titleAudio=view.findViewById(R.id.memo_audio_title);
              urgentAudio=view.findViewById(R.id.memo_audio_urgent);
          }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        switch (i){
            case TEXT_TYPE:
                View viewText= LayoutInflater.from(viewGroup.getContext()).inflate
                        (R.layout.memo_recycle_text,viewGroup,false);
                return new TextViewHolder(viewText);
            case AUDIO_TYPE:
                View viewAudio=LayoutInflater.from(viewGroup.getContext()).inflate
                        (R.layout.memo_recycle_audio,viewGroup,false);
                return new AudioViewHolder(viewAudio);
            default:
                Log.d(TAG, "onCreateViewHolder: Type error");
                return null;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try{
            JSONObject task=tasks.get(position);
            if(holder instanceof TextViewHolder){
                ((TextViewHolder) holder).typeText.setText("1");
                ((TextViewHolder) holder).titleText.setText(task.getString("title"));
                if(task.getInt("urgent")==0){
                    ((TextViewHolder) holder).urgentText.setBackgroundColor(mContext.getResources().getColor(R.color.alarm_false,null));
                }else {
                    ((TextViewHolder) holder).urgentText.setBackgroundColor(mContext.getResources().getColor(R.color.alarm_true, null));
                }
                if(task.getInt("alarm")==0){
                    ((TextViewHolder) holder).timeText.setText("not alarm");
                }else{
                    ((TextViewHolder) holder).timeText.setText(task.getString("alarmDate")+" "+
                    task.getString("alarmTime"));
                }

            }else if(holder instanceof AudioViewHolder){
                ((AudioViewHolder) holder).typeAudio.setText("2");
                ((AudioViewHolder) holder).titleAudio.setText(task.getString("title"));
                if(task.getInt("urgent")==0){
                    ((AudioViewHolder) holder).urgentAudio.setBackgroundColor(mContext.getResources().getColor(R.color.alarm_false,null));
                }else {
                    ((AudioViewHolder) holder).urgentAudio.setBackgroundColor(mContext.getResources().getColor(R.color.alarm_true, null));
                }
                if(task.getInt("alarm")==0){
                    ((AudioViewHolder) holder).timeAudio.setText("not alarm");
                }else{
                    ((AudioViewHolder) holder).timeAudio.setText(task.getString("alarmDate")+" "+
                            task.getString("alarmTime"));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemViewType(int position) {
        try{
            switch (tasks.get(position).getString("type")){
                case "text":
                    return TEXT_TYPE;
                case "audio":
                    return AUDIO_TYPE;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }


    @Override
    public int getItemCount() {
        return tasks.size();
    }
}
