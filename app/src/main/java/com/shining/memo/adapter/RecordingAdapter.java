package com.shining.memo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.method.KeyListener;
import android.text.method.TextKeyListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.shining.memo.R;
import com.shining.memo.model.Recording;
import com.shining.memo.model.RecordingContent;
import com.shining.memo.presenter.AudioPlayPresenter;
import com.shining.memo.view.MainActivity;


import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class RecordingAdapter extends RecyclerView.Adapter implements AudioPlayPresenter.onStopPlay {
    private String TAG ="RecordingAdapter";
    private HashMap<Integer,RecordingContent> map;
    private Context context;
    private AudioPlayPresenter presenter;
    private TextChanged textChanged;
    private int requestFocusableIndex = 0,position = 0;
    private String type = "";
    private float dpTransform;

    public RecordingAdapter(HashMap<Integer,RecordingContent> map,Context context,TextChanged textChanged) {
        this.map = map;
        this.context = context;
        presenter = new AudioPlayPresenter(context,this);
        this.textChanged =textChanged;
        calculateTransform();
    }

    public void calculateTransform(){

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);

        int mScreenWidth = outMetrics.widthPixels;
        int mScreenHeight = outMetrics.heightPixels;
        Log.d("calculateTransform",mScreenWidth+"--"+mScreenHeight);
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
            case 2:
                view = LayoutInflater.from(context).inflate(R.layout.item_recording_photo,null);
                return new PhotoViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Log.d("focusable1","focusable1"+requestFocusableIndex);
        TextViewHolder textViewHolder = null;
        AudioViewHolder audioViewHolder = null;
        PhotoViewHolder photoViewHolder = null;
        switch (map.get(i).getType()){
            case "text":
                textViewHolder = ((TextViewHolder)viewHolder);
                textViewHolder.itemView.setTag(i);
                textViewHolder.editText.setText(map.get(i).getContent());
                textViewHolder.editText.setWidth(2000);
                break;
            case "audio":
                audioViewHolder = ((AudioViewHolder)viewHolder);
                MediaPlayer mediaPlayer = new MediaPlayer();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
                try {
                    mediaPlayer.setDataSource(map.get(i).getContent());
                    mediaPlayer.prepare();
                    long time = mediaPlayer.getDuration();
                    audioViewHolder.button.setText(sdf.format(new Date(time)));
                } catch (IOException e) {
                    e.printStackTrace();
                    audioViewHolder.button.setText(sdf.format(new Date(0)));
                }
                Drawable drawable = context.getDrawable(R.drawable.play_audio_icon);
                drawable.setBounds(0,0,35,35);
                audioViewHolder.button.setCompoundDrawables(drawable,null,null,null);
                mediaPlayer.release();
                audioViewHolder.editTextEnd.setWidth(2000);
                audioViewHolder.filePath = map.get(i).getContent();
                audioViewHolder.itemView.setTag(i);
                break;
            case "photo":
                photoViewHolder = ((PhotoViewHolder)viewHolder);
                photoViewHolder.itemView.setTag(i);
                try {
                    FileInputStream in = new FileInputStream(map.get(i).getContent());
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;//图片的长宽都是原来的1/8
                    BufferedInputStream bis = new BufferedInputStream(in);
                    Bitmap bm = BitmapFactory.decodeStream(bis, null, options);
                    photoViewHolder.imageView.setImageBitmap(bm);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    photoViewHolder.imageView.setImageResource(R.drawable.alarm_clock_btn_48x48px);
                }
                break;
        }
        if(i ==  requestFocusableIndex){
            textChanged.recyclerViewFocusable();
            switch (map.get(i).getType()){
                case "text":
                    textViewHolder.itemView.requestFocus();
                    if(type.equals("end"))
                        textViewHolder.editText.setSelection(textViewHolder.editText.getText().toString().length());
                    else if(type.equals("specific"))
                        textViewHolder.editText.setSelection(position);
                    else
                        textViewHolder.editText.setSelection(0);
                    break;
                case "audio":
                    audioViewHolder.itemView.requestFocus();
                    if(type.equals("end"))
                        audioViewHolder.editTextEnd.requestFocus();
                    else
                        audioViewHolder.editTextStart.requestFocus();
                    break;
                case "photo":
                    photoViewHolder.itemView.requestFocus();
                    if(type.equals("end"))
                        photoViewHolder.editTextEnd.requestFocus();
                    else
                        photoViewHolder.editTextStart.requestFocus();
                    break;
            }
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
            case "photo":
                return 2;
        }
        return 0;
    }

    private int CurrentIndex = -1,btnIndex = -1;
    private String CurrentType = "";

    @Override
    public void onStopPlay() {
        btnIndex = -1;
        Log.d(TAG, "onStopPlay: btnIndex"+btnIndex);
    }

    public class TextViewHolder extends RecyclerView.ViewHolder implements OnFocusChangeListener,TextWatcher, View.OnKeyListener,TextView.OnEditorActionListener{

        public EditText editText;
        private View itemView;

        public TextViewHolder(View itemView) {
            super(itemView);
            editText = itemView.findViewById(R.id.item_editText);
            editText.setHorizontallyScrolling(false);
            editText.setMaxLines(Integer.MAX_VALUE);
            this.itemView = itemView;
            editText.setOnFocusChangeListener(this);
            editText.addTextChangedListener(this);
            editText.setOnKeyListener(this);
            editText.setOnEditorActionListener(this);
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                CurrentIndex = (int)itemView.getTag();
                CurrentType = "text";
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
                    if(editText.getText().toString().length() == 0 && getCurrentIndex() < map.size() && getCurrentIndex() > 0){  //删除视图
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
                                textChanged.deleteEditText(map,CurrentIndex,position,"specific");
                            }
                            else {
                                setRequestFocusableArgs(CurrentIndex -1,0,"end");
                                textChanged.updateAdapter(CurrentIndex - 1);
                            }
                        }
                    }
                }
            }
            return false;
        }

        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            if(actionId == EditorInfo.IME_ACTION_NEXT){
                List<String> text = editTextDistach(editText);
                HashMap<Integer, RecordingContent> map = textChanged.getMap();
                int index = getCurrentIndex();
                for(int i = map.size() - 1; i > index; i--)
                    map.put(i + 1,map.get(i));
                RecordingContent content = new RecordingContent();
                content.setType("text");
                content.setColor(map.get(index).getColor());
                content.setContent(text.get(0));
                map.put(index,content);
                content = new RecordingContent();
                content.setType("text");
                content.setColor(map.get(index).getColor());
                if(text.size() > 1)
                    content.setContent(text.get(1));
                else
                    content.setContent("");
                map.put(index + 1,content);
                setRequestFocusableArgs(index + 1,0,"first");
                textChanged.updateAdapter(index);
            }
            return false;
        }
    }

    private static boolean isChanged = true;
    public class AudioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,OnFocusChangeListener, View.OnKeyListener,TextWatcher,TextView.OnEditorActionListener
    {

        public Button button;
        public EditText editTextStart,editTextEnd;
        private String filePath;
        private View itemView;
        private int textChangedId;

        public AudioViewHolder(View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.item_btn);
            editTextStart = itemView.findViewById(R.id.btn_edit_strat);
            editTextEnd = itemView.findViewById(R.id.btn_edit);
            this.itemView =itemView;
            button.setOnClickListener(this);
            editTextStart.setOnFocusChangeListener(this);
            editTextStart.setOnKeyListener(this);
            editTextStart.addTextChangedListener(this);
            editTextStart.setOnEditorActionListener(this);
            editTextEnd.setOnFocusChangeListener(this);
            editTextEnd.setOnKeyListener(this);
            editTextEnd.addTextChangedListener(this);
            editTextEnd.setOnEditorActionListener(this);
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
                CurrentIndex = (int)itemView.getTag();
                if(v.getId() == R.id.btn_edit_strat)
                {
                    CurrentType = "audio_start";
                    textChangedId = R.id.btn_edit_strat;
                }
                else
                {
                    CurrentType = "audio_end";
                    textChangedId = R.id.btn_edit;
                }
                Log.d(TAG, "onFocusChange: " + CurrentIndex);
            }else {
                CurrentIndex = -1;
            }
        }
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if( keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN){
                if(v.equals(editTextEnd)) {
                    int text_index = editTextEnd.getSelectionStart();
                    if (text_index == 0) {
                        int index = (int) itemView.getTag();
                        HashMap<Integer, RecordingContent> map = textChanged.getMap();
                        String filePath = map.get(index).getContent();
                        File file = new File(filePath);
                        if (file.exists())
                            file.delete();
                        for (int i = index; i < map.size() - 1; i++)
                            map.put(i, map.get(i + 1));
                        map.remove(map.size() - 1);
                        textChanged.deleteEditText(map, index, 0, "end");
                    }
                }else if(v.equals(editTextStart)){
                    if(CurrentIndex - 1 >= 0){
                        HashMap<Integer, RecordingContent> map = textChanged.getMap();
                        if(map.get(CurrentIndex - 1).getType().equals("text") && map.get(CurrentIndex - 1).getContent().equals("")){
                            for(int i = CurrentIndex -1; i < map.size() - 1; i++)
                                map.put(i,map.get(i + 1));
                            map.remove(map.size());
                            textChanged.deleteEditText(map,CurrentIndex - 1,0,"first");
                        }else {
                            setRequestFocusableArgs(CurrentIndex - 1,0,"end");
                            textChanged.updateAdapter(CurrentIndex -1);
                        }
                    }
                }
            }
            return false;
        }
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(isChanged){
                int index = getCurrentIndex();
                isChanged = false;
                if(textChangedId == R.id.btn_edit) {
                    changedItem(editTextEnd.getText().toString(),"end",index);
                    editTextEnd.setText("");
                }
                else{
                    changedItem(editTextStart.getText().toString(),"start",index);
                    editTextStart.setText("");
                }
                textChanged.updateAdapter(index);
            }else {
                isChanged = true;
            }
        }
        @Override
        public void afterTextChanged(Editable editable) { }

        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            if(actionId == EditorInfo.IME_ACTION_NEXT){

                HashMap<Integer, RecordingContent> map = textChanged.getMap();
                int index = getCurrentIndex();
                for(int i = map.size() - 1; i > index; i--)
                    map.put(i + 1,map.get(i));
                RecordingContent content = new RecordingContent();
                content.setType("text");
                content.setColor(map.get(index).getColor());
                content.setContent("");
                if(textChangedId == R.id.btn_edit){
                    map.put(index + 1,content);
                }else{
                    map.put(index + 1,map.get(index));
                    map.put(index,content);
                }
                setRequestFocusableArgs(index + 1,0,"first");
                textChanged.updateAdapter(index);
            }
            return false;
        }
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder implements OnFocusChangeListener,View.OnKeyListener,TextWatcher,TextView.OnEditorActionListener{

        public EditText editTextStart,editTextEnd;
        private ImageView imageView;
        public View itemView;
        private int textChangedId;
        public PhotoViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            editTextStart = (EditText)itemView.findViewById(R.id.item_image_start);
            editTextEnd = (EditText)itemView.findViewById(R.id.item_image_end);
            imageView = (ImageView)itemView.findViewById(R.id.item_imageView);
            editTextStart.setOnFocusChangeListener(this);
            editTextStart.setOnKeyListener(this);
            editTextStart.addTextChangedListener(this);
            editTextStart.setOnEditorActionListener(this);
            editTextEnd.setOnFocusChangeListener(this);
            editTextEnd.setOnKeyListener(this);
            editTextEnd.addTextChangedListener(this);
            editTextEnd.setOnEditorActionListener(this);
        }
        @Override
        public void onFocusChange(View view, boolean b) {
            if(b){
                CurrentIndex = (int)itemView.getTag();
                if(((EditText)view).getId() == R.id.item_image_start){
                    CurrentType = "photo_start";
                    textChangedId = R.id.item_image_start;
                }
                else{
                    CurrentType = "photo_end";
                    textChangedId = R.id.item_image_end;
                }
                //TBD
                if(itemView.getHeight() != 0)
                    ((EditText)view).setTextSize(TypedValue.COMPLEX_UNIT_PX,(int)(itemView.getHeight()/1.36));
                else
                    ((EditText)view).setTextSize(20);
                Log.d(TAG, "onFocusChange: " + CurrentIndex);
            }else {
                CurrentIndex = -1;
            }
        }
        @Override
        public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
            if( keyCode == KeyEvent.KEYCODE_DEL && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                if(view.equals(editTextEnd)) {
                    int text_index = editTextEnd.getSelectionStart();
                    if (text_index == 0) {
                        int index = (int) itemView.getTag();
                        HashMap<Integer, RecordingContent> map = textChanged.getMap();
                        String filePath = map.get(index).getContent();
                        File file = new File(filePath);
                        if (file.exists() && filePath.contains(Environment.getExternalStorageState()+"/photo/"))
                            file.delete();
                        for (int i = index; i < map.size() - 1; i++)
                            map.put(i, map.get(i + 1));
                        map.remove(map.size() - 1);
                        textChanged.deleteEditText(map, index, 0, "end");
                    }
                }else if(view.equals(editTextStart)){
                    if(CurrentIndex - 1 >= 0){
                        HashMap<Integer, RecordingContent> map = textChanged.getMap();
                        if(map.get(CurrentIndex - 1).getType().equals("text") && map.get(CurrentIndex - 1).getContent().equals("")){
                            for(int i = CurrentIndex -1; i < map.size() - 1; i++)
                                map.put(i,map.get(i + 1));
                            map.remove(map.size());
                            textChanged.deleteEditText(map,CurrentIndex - 1,0,"first");
                        }else {
                            setRequestFocusableArgs(CurrentIndex - 1,0,"end");
                            textChanged.updateAdapter(CurrentIndex -1);
                        }
                    }
                }
            }
            return false;
        }
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(isChanged){
                int index = getCurrentIndex();
                isChanged = false;
                if(textChangedId == R.id.item_image_end) {
                    changedItem(editTextEnd.getText().toString(),"end",index);
                    editTextEnd.setText("");
                }
                else{
                    changedItem(editTextStart.getText().toString(),"start",index);
                    editTextStart.setText("");
                }
                textChanged.updateAdapter(index);
            }else {
                isChanged = true;
            }
        }
        @Override
        public void afterTextChanged(Editable editable) { }

        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            Log.d("onEditorAction","onEditorAction");
            if(actionId == EditorInfo.IME_ACTION_NEXT){
                Log.d("onEditorAction1","onEditorAction1");
                HashMap<Integer, RecordingContent> map = textChanged.getMap();
                int index = getCurrentIndex();
                for(int i = map.size() - 1; i > index; i--)
                    map.put(i + 1,map.get(i));
                RecordingContent content = new RecordingContent();
                content.setType("text");
                content.setColor(map.get(index).getColor());
                content.setContent("");
                if(textChangedId == R.id.item_image_end){
                    map.put(index + 1,content);
                }else{
                    map.put(index + 1,map.get(index));
                    map.put(index,content);
                }
                setRequestFocusableArgs(index + 1,0,"first");
                textChanged.updateAdapter(index);
            }
            return false;
        }
    }

    public void changedItem(String text,String position,int index){
        HashMap<Integer, RecordingContent> map = textChanged.getMap();
        Log.d("test",map.toString());
        for(int i = map.size() - 1; i > index; i--)
            map.put(i + 1,map.get(i));
        Log.d("test1",map.toString());
        if(position.equals("start")){
            map.put(index + 1,map.get(index));
            RecordingContent content = new RecordingContent();
            content.setColor(map.get(index).getColor());
            content.setType("text");
            content.setContent(text);
            map.put(index,content);
            requestFocusableIndex = index;
        }else if(position.equals("end")){
            RecordingContent content = new RecordingContent();
            content.setColor(map.get(index).getColor());
            content.setType("text");
            content.setContent(text);
            map.put(index + 1,content);
            requestFocusableIndex = index + 1;
        }
    }

    public void setRequestFocusableArgs(int requestIndex,int position,String positionType){
        requestFocusableIndex = requestIndex;
        this.position = position;
        type = positionType;
    }

    public void requestFocusable(RecyclerView mRecycleView){
        try {
            mRecycleView.requestFocus();
            mRecycleView.getChildAt(requestFocusableIndex);
            PhotoViewHolder PhotoViewHolder = (PhotoViewHolder)mRecycleView.getChildViewHolder(mRecycleView.getChildAt(requestFocusableIndex));
            PhotoViewHolder.editTextEnd.requestFocus();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public int getCurrentIndex(){
        return CurrentIndex;
    }

    public String getCurrentType(){
        return CurrentType;
    }

    public List<String> distachText(RecyclerView recyclerView){
        TextViewHolder textViewHolder = (TextViewHolder)recyclerView.getChildViewHolder(recyclerView.getChildAt(CurrentIndex - textChanged.getCurrentFirstIndex()));
        return editTextDistach(textViewHolder.editText);
    }

    public List<String> editTextDistach(EditText editText){
        List<String> text = new ArrayList<>();
        Editable editable = editText.getEditableText();
        int index = editText.getSelectionStart();
        String strText = editText.getText().toString();
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
        HashMap<Integer,RecordingContent> getMap();
        void deleteEditText(HashMap<Integer,RecordingContent> map,int index,int position, String type);
        void updateAdapter(int index);
        void recyclerViewFocusable();
    }



}

