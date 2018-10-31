package com.shining.memo.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shining.memo.R;
import com.shining.memo.model.Task;
import com.shining.memo.presenter.CalendarPresenter;
import com.shining.memo.view.NoteActivity;

import org.json.JSONObject;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder>{
    private Context mContext;
    private List<Task> mNotes;
    private FragmentActivity noteFragment;
    static class ViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView mTitle,mTime,homeDelete;
        LinearLayout mAudio;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView.findViewById(R.id.main_note_content);
            mTitle=itemView.findViewById(R.id.main_note_title);
            mTime=itemView.findViewById(R.id.main_note_time);
            mAudio=itemView.findViewById(R.id.main_note_audio);
            homeDelete=itemView.findViewById(R.id.home_delete);
        }
    }
    public NoteAdapter(Context context,List<Task> notes){
        mContext = context;
        mNotes=notes;
        this.noteFragment = (FragmentActivity) context;
    }

    @Override
    public @NonNull ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_noteitem,viewGroup,false);
        final ViewHolder holder= new ViewHolder(view);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id=(int)mNotes.get(holder.getLayoutPosition()).getId();
                Intent intent=new Intent(viewGroup.getContext(), NoteActivity.class);
                intent.putExtra("noteId",id);
                viewGroup.getContext().startActivity(intent);
                noteFragment.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        holder.homeDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getLayoutPosition();
                Task task = mNotes.get(position);
                CalendarPresenter calendarPresenter = new CalendarPresenter(mContext);
                calendarPresenter.deleteData(String.valueOf(task.getId()), "task");
                mNotes.remove(holder.getAdapterPosition());
                notifyItemRemoved(position);
                notifyItemRangeRemoved(position, mNotes.size());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        Task task = mNotes.get(i);
        String type=task.getType();
        String title=task.getTitle();
        if( title==null || title.equals("")){
            if(type.equalsIgnoreCase("audio")){
                holder.mAudio.setVisibility(View.VISIBLE);
                holder.mTitle.setVisibility(View.GONE);
            }else {
                holder.mTitle.setVisibility(View.VISIBLE);
                holder.mAudio.setVisibility(View.GONE);
                holder.mTitle.setText(mContext.getString(R.string.main_task_no_title));
            }
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
