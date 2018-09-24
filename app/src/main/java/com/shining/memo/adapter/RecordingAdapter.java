package com.shining.memo.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.shining.memo.model.Recording;
import com.shining.memo.model.RecordingContent;
import com.shining.memo.presenter.AudioPlayPresenter;


import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;



public class RecordingAdapter extends RecyclerView.Adapter implements AudioPlayPresenter.onStopPlay {
    private String TAG ="RecordingAdapter";
    private HashMap<Integer,RecordingContent> map;
    private Context context;
    private AudioPlayPresenter presenter;
    private TextChanged textChanged;

    public RecordingAdapter(HashMap<Integer,RecordingContent> map,Context context,TextChanged textChanged) {
        this.map = map;
        this.context = context;
        presenter = new AudioPlayPresenter(context,this);
        this.textChanged =textChanged;
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
                ((TextViewHolder)viewHolder).itemView.setTag(i);
                ((TextViewHolder)viewHolder).editText.setText(map.get(i).getContent());
                ((TextViewHolder)viewHolder).editText.setWidth(2000);
                break;
            case "audio":
                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(map.get(i).getContent());
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                long time = mediaPlayer.getDuration();
                mediaPlayer.release();
                ((AudioViewHolder)viewHolder).button.setText(new SimpleDateFormat("mm:ss").format(new Date(time)));
                ((AudioViewHolder)viewHolder).editText.setWidth(2000);
                ((AudioViewHolder)viewHolder).filePath = map.get(i).getContent();
                ((AudioViewHolder)viewHolder).itemView.setTag(i);
                break;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
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

    private int CurrentIndex = -1,btnIndex = -1;

    @Override
    public void onStopPlay() {
        btnIndex = -1;
        Log.d(TAG, "onStopPlay: btnIndex"+btnIndex);
    }

    public class TextViewHolder extends RecyclerView.ViewHolder implements OnFocusChangeListener,TextWatcher, View.OnKeyListener {

        public EditText editText;
        private View itemView;

        public TextViewHolder(final View itemView) {
            super(itemView);
            editText = itemView.findViewById(R.id.item_editText);
            this.itemView = itemView;
            editText.setOnFocusChangeListener(this);
            editText.addTextChangedListener(this);
            editText.setOnKeyListener(this);
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                CurrentIndex = (int)itemView.getTag();
                Log.d(TAG, "onFocusChange: " + CurrentIndex);
            }
            else {
                CurrentIndex = -1;
            }
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            textChanged.TextChanged(editText.getText().toString(),(int)itemView.getTag());
        }
        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if(v.equals(editText) && keyCode == KeyEvent.KEYCODE_DEL&& event.getAction() == KeyEvent.ACTION_DOWN){
                int index = editText.getSelectionStart();
                if(index == 0){
                    HashMap<Integer, RecordingContent> map = textChanged.getMap();
                    if(editText.getText().toString().length() == 0 && getCurrentIndex() < map.size()){  //删除视图
                        for(int i = CurrentIndex; i < map.size() - 1;i++)
                            map.put(i,map.get(i + 1));
                        map.remove(map.size() - 1);
                        textChanged.deleteEditText(map,CurrentIndex,0,"end");
                    }else {  //光标返回上一层
                        if(CurrentIndex - 1 >= 0){
                            if(map.get(CurrentIndex - 1).getType().equals("text")){
                                int position = map.get(CurrentIndex - 1).getContent().length();
                                map.get(CurrentIndex - 1).setContent(map.get(CurrentIndex - 1).getContent() + map.get(CurrentIndex).getContent());
                                for(int i = CurrentIndex; i < map.size() - 1;i++)
                                    map.put(i,map.get(i + 1));
                                map.remove(map.size() - 1);
                                Log.d(TAG, "onKey: "+ map.toString());
                                textChanged.deleteEditText(map,CurrentIndex,position,"specific");
                            }
                            else {
                                textChanged.requestFocusable(CurrentIndex -1,0,"first");
                            }
                        }
                    }
                }
            }
            return false;
        }
    }

    public class AudioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,OnFocusChangeListener, View.OnKeyListener{

        public Button button;
        public EditText editText;
        private String filePath;
        private View itemView;

        public AudioViewHolder(View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.item_btn);
            editText = itemView.findViewById(R.id.btn_edit);
            button.setOnClickListener(this);
            editText.setOnFocusChangeListener(this);
            editText.setOnKeyListener(this);
            editText.setMaxEms(0);
            this.itemView =itemView;
        }

        @Override
        public void onClick(View view) {
            itemView.requestFocus();
            if(btnIndex == -1){
                presenter.setPlayFilePath(filePath);
                presenter.doPlay();
                btnIndex = (int)itemView.getTag();
            }else if(btnIndex == (int)itemView.getTag()){
                presenter.onStop();
            }else {
                presenter.onStop();
                presenter.setPlayFilePath(filePath);
                presenter.doPlay();
            }
        }
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                CurrentIndex = -1;
                Log.d(TAG, "onFocusChange: " + CurrentIndex);
            }
        }
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if(v.equals(editText) && keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN){
                int text_index = editText.getSelectionStart();
                if(text_index == 0) {
                    int index = (int) itemView.getTag();
                    HashMap<Integer, RecordingContent> map = textChanged.getMap();
                    String filePath = map.get(index).getContent();
//                    File file = new File(filePath);
//                    if (file.exists())
//                        file.delete();
                    for (int i = index; i < map.size() - 1; i++)
                        map.put(i, map.get(i + 1));
                    map.remove(map.size() - 1);
                    textChanged.deleteEditText(map, index,0,"end");
                }
            }
            return false;
        }

    }

    public int getCurrentIndex(){
        return CurrentIndex;
    }

    public List<String> distachText(RecyclerView recyclerView){
        List<String> text = new ArrayList<>();
        TextViewHolder textViewHolder = textViewHolder = (TextViewHolder)recyclerView.getChildViewHolder(recyclerView.getChildAt(CurrentIndex - textChanged.getCurrentFirstIndex()));
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

    public interface TextChanged{
        void TextChanged(String text,int index);
        Context getContext();
        int getCurrentFirstIndex();
        void requestFocusable(int index,int position,String type);
        HashMap<Integer,RecordingContent> getMap();
        void deleteEditText(HashMap<Integer,RecordingContent> map,int index,int position, String type);
    }
}

