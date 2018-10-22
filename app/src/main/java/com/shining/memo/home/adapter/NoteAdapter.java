package com.shining.memo.home.adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shining.memo.R;
import com.shining.memo.model.Task;
import com.shining.memo.view.NoteActivity;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder>{
    private List<Task> mNotes;
    static class ViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView mTitle,mTime;
        LinearLayout mAudio;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            mTitle=mView.findViewById(R.id.main_note_title);
            mTime=mView.findViewById(R.id.main_note_time);
            mAudio=mView.findViewById(R.id.main_note_audio);
        }
    }
    public NoteAdapter(List<Task> notes){
        mNotes=notes;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_noteitem,viewGroup,false);
        final ViewHolder holder= new ViewHolder(view);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long id=mNotes.get(holder.getLayoutPosition()).getId();
                Log.d("helo", "onClick: "+id);
                Intent intent=new Intent(viewGroup.getContext(), NoteActivity.class);
                intent.putExtra("taskId",id);
                viewGroup.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        Task task = mNotes.get(i);
        String type=task.getType();
        String title=task.getTitle();
        if(type.equalsIgnoreCase("audio") && (title==null || title.equals(""))){
            holder.mAudio.setVisibility(View.VISIBLE);
            holder.mTitle.setVisibility(View.GONE);
        }else {
            holder.mTitle.setVisibility(View.VISIBLE);
            holder.mAudio.setVisibility(View.GONE);
            holder.mTitle.setText(title);
        }
        holder.mTime.setText(task.getDate()+" "+task.getTime());
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }
}
