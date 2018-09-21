package com.shining.memo.adapter;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.shining.memo.R;
import com.shining.memo.model.RecordingContent;
import com.shining.memo.presenter.AudioPlayPresenter;
import com.shining.memo.presenter.AudioPresenter;
import com.shining.memo.view.RecordingEditActivity;
import com.shining.memo.widget.NoteEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecordingAdapter extends RecyclerView.Adapter{

    private HashMap<Integer,RecordingContent> map;
    private Context context,parentContext;

    public RecordingAdapter(HashMap<Integer,RecordingContent> map,Context context) {
        this.map = map;
        this.context = context;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        switch (i){
            case 0:
                View view = LayoutInflater.from(context).inflate(R.layout.item_recording_text,null);
                return new TextViewHolder(view);
            case 1:
                view = LayoutInflater.from(context).inflate(R.layout.item_recording_audio,null);
                return new AudioViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        switch (map.get(i).getType()){
            case "text":
                ((TextViewHolder)viewHolder).editText.setText(map.get(i).getContent());
                ((TextViewHolder)viewHolder).editText.setWidth(2000);
                ((TextViewHolder)viewHolder).itemView.setTag(i);
                break;
            case "audio":
                ((AudioViewHolder)viewHolder).button.setText(map.get(i).getTime());
                ((AudioViewHolder)viewHolder).filePath = map.get(i).getContent();
                ((AudioViewHolder)viewHolder).itemView.setTag(i);
                break;
        }
    }

    @Override
    public int getItemCount() { return map.size(); }

    @Override
    public int getItemViewType(int position){
        switch (map.get(position).getType()){
            case "text":
                return 0;
            case "audio":
                return 1;
        }
        return 0;
    }

    public int CurrentIndex = -1;
    public class TextViewHolder extends RecyclerView.ViewHolder{

        private NoteEditText editText;

        public TextViewHolder(final View itemView) {
            super(itemView);
            editText = itemView.findViewById(R.id.item_editText);
            editText.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(b){
                        CurrentIndex = (int)itemView.getTag();
                    }
                    else {
                        CurrentIndex = -1;
                    }
                }
            });
        }
    }

    public class AudioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Button button;
        private String filePath;
        private AudioPresenter audioPresenter;

        public AudioViewHolder(View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.item_btn);
            button.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            AudioPlayPresenter presenter = new AudioPlayPresenter(filePath);
            presenter.doPlay();
        }
    }

    public int getCurrentIndex(){
        return CurrentIndex;
    }

    public List<String> distachText(RecyclerView recyclerView){
        List<String> text = new ArrayList<>();
        TextViewHolder textViewHolder = (TextViewHolder)recyclerView.getChildViewHolder(recyclerView.getChildAt(CurrentIndex));
        Editable editable = textViewHolder.editText.getEditableText();
        int index = textViewHolder.editText.getSelectionStart();
        String strText = textViewHolder.editText.getText().toString();
        if(index < strText.length()){
            text.add(strText.substring(0,index));
            text.add(strText.substring(index));
        }else
            text.add(strText.substring(0,index));
        return text;
    }

}
