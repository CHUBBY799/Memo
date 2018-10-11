package com.shining.memo.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shining.memo.R;
import com.shining.memo.model.RecordingContent;
import com.shining.memo.presenter.AudioPlayPresenter;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.text.Html.FROM_HTML_MODE_COMPACT;


public class RecordingAdapter extends RecyclerView.Adapter implements AudioPlayPresenter.onStopPlay {
    private String TAG ="RecordingAdapter";
    private HashMap<Integer,RecordingContent> map;
    private Context context;
    private AudioPlayPresenter presenter;
    private TextChanged textChanged;
    public int requestFocusableIndex = -1,position = 0;
    private String type = "";
    private HashMap<Integer,shelterSize> shelter = new HashMap<>();
    private int CurrentIndex = -1,btnIndex = -1;
    private String CurrentType = "";
    private boolean isView = false,isViewEdit = false;
    public static int currentColor,colorPos;

    private ViewChange viewChange;
    public List<String> deletePath;

    public RecordingAdapter(HashMap<Integer,RecordingContent> map,Context context,ViewChange viewChange) {
        this.map = map;
        this.context = context;
        presenter = new AudioPlayPresenter(context,this);
        isView = true;
        this.viewChange = viewChange;
        textChanged = new TextChanged() {
            @Override
            public void TextChanged(Spanned text, int index) {

            }

            @Override
            public Context getContext() {
                return null;
            }

            @Override
            public int getCurrentFirstIndex() {
                return 0;
            }

            @Override
            public int getCurrentLastIndex() {
                return 0;
            }

            @Override
            public HashMap<Integer, RecordingContent> getMap() {
                return null;
            }

            @Override
            public void deleteEditText(HashMap<Integer, RecordingContent> map, int index, int position, String type) {

            }

            @Override
            public void updateAdapter(int index) {

            }

            @Override
            public void recyclerViewFocusable() {

            }

            @Override
            public void recyclerViewClearFocusable() {

            }

            @Override
            public RecyclerView getRecyclerView() {
                return null;
            }

            @Override
            public void updateRecyclerView(int position) {

            }
        };
    }

    public RecordingAdapter(HashMap<Integer,RecordingContent> map,Context context,TextChanged textChanged) {
        this.map = map;
        this.context = context;
        presenter = new AudioPlayPresenter(context,this);
        isView = false;
        this.textChanged =textChanged;
        currentColor = context.getColor(R.color.recording_title);
        colorPos = 0;
    }

    public int getRequestFocusableIndex() {
        return requestFocusableIndex;
    }
    public void setViewEdit(boolean viewEdit){
        this.isViewEdit = viewEdit;
        if(viewEdit){
            deletePath = new ArrayList<>();
        }else {
            deletePath = null;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.d("onCreateViewHolder","onCreateViewHolder" + requestFocusableIndex);
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

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        Log.d("onBindViewHolder","onBindViewHolder" + requestFocusableIndex);
        TextViewHolder textViewHolder = null;
        AudioViewHolder audioViewHolder = null;
        PhotoViewHolder photoViewHolder = null;
        switch (map.get(i).getType()){
            case "text":
                textViewHolder = ((TextViewHolder)viewHolder);
                textViewHolder.itemView.setTag(i);
                Spanned spanned = Html.fromHtml(map.get(i).getContent(),FROM_HTML_MODE_COMPACT);
                if(spanned.length() > 0)
                    textViewHolder.editText.setText(spanned.subSequence(0,spanned.length() -1));
                else
                    textViewHolder.editText.setText(spanned);
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
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    audioViewHolder.button.setText(sdf.format(new Date(time)));
                } catch (IOException e) {
                    e.printStackTrace();
                    audioViewHolder.button.setText(sdf.format(new Date(0)));
                }
                Drawable drawable = context.getDrawable(R.drawable.play_audio_icon);
                drawable.setBounds(0,0,35,35);
                audioViewHolder.button.setCompoundDrawables(drawable,null,null,null);
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
                        options.inSampleSize = 8;       //图片的长宽都是原来的1/8
                        BufferedInputStream bis = new BufferedInputStream(in);
                        Bitmap bm = BitmapFactory.decodeStream(bis, null, options);
                        photoViewHolder.imageView.setImageBitmap(bm);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        photoViewHolder.imageView.setImageResource(R.drawable.alarm_clock_btn_48x48px);
                    }
                    if(shelter.containsKey(i)){
                        photoViewHolder.imageView.setVisibility(View.GONE);
                        photoViewHolder.relativeLayout.getLayoutParams().width = shelter.get(i).width;
                        photoViewHolder.relativeLayout.getLayoutParams().height = shelter.get(i).height;
                        photoViewHolder.relativeLayout.setVisibility(View.VISIBLE);
                    }else {
                        photoViewHolder.imageView.setVisibility(View.VISIBLE);
                        photoViewHolder.relativeLayout.setVisibility(View.GONE);
                    }
                break;
        }
        if(i ==  requestFocusableIndex){
            textChanged.recyclerViewFocusable();
            switch (map.get(i).getType()){
                case "text":
                    Log.d(TAG, "onBindViewHolder: requestFocusableIndex  text"+ position+"---"+requestFocusableIndex +type);
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
            }
        }
        if(map.size() == 1 && map.get(0).getType().equals("text")){
            SpannableString ss = new SpannableString(context.getResources().getString(R.string.item_text_hint));
            textViewHolder.editText.setHint(ss);
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

    @Override
    public void onStopPlay() {
        btnIndex = -1;
        Log.d(TAG, "onStopPlay: btnIndex"+btnIndex);
    }

    public class TextViewHolder extends RecyclerView.ViewHolder implements OnFocusChangeListener,TextWatcher, View.OnKeyListener,TextView.OnEditorActionListener{

        public EditText editText;
        private View itemView;
        private SpannableString mSpanned;

        public TextViewHolder(View itemView) {
            super(itemView);
            editText = itemView.findViewById(R.id.item_editText);
            editText.setHorizontallyScrolling(false);
            editText.setMaxLines(Integer.MAX_VALUE);
            this.itemView = itemView;
            editText.setOnFocusChangeListener(this);
            editText.addTextChangedListener(this);
            editText.setOnKeyListener(this);
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                if(isView){
                    viewChange.changedView();
                    isView = false;
                    return;
                }
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
            Log.d("onTextChanged",s+"-----"+start);
           // mSpanned = updateAllTypeSpan(dispatchPositionColor(new SpannableString(editText.getText()),start,count));
            mSpanned = updateAllTypeSpan(new SpannableString(editText.getText()));
            textChanged.TextChanged(mSpanned,(int)itemView.getTag());
        }
        @Override
        public void afterTextChanged(Editable s) {
            if(!Html.toHtml(new SpannableString(editText.getText())).equals(Html.toHtml(mSpanned))){
                editText.removeTextChangedListener(this);
                int index = editText.getSelectionStart();
                editText.setText(mSpanned);
                editText.setSelection(index);
                editText.addTextChangedListener(this);
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if(v.equals(editText) && keyCode == KeyEvent.KEYCODE_DEL&& event.getAction() == KeyEvent.ACTION_DOWN){
                int index = editText.getSelectionStart();
                if(index == 0){
                    HashMap<Integer, RecordingContent> map = textChanged.getMap();
                    if(editText.getText().toString().length() == 0 && getCurrentIndex() < map.size() - 1 && getCurrentIndex() > 0){  //删除视图
                        for(int i = CurrentIndex; i < map.size() - 1;i++)
                            map.put(i,map.get(i + 1));
                        map.remove(map.size() - 1);
                        textChanged.deleteEditText(map,CurrentIndex,0,"end");
                    }else {  //光标返回上一层
                        if(CurrentIndex - 1 >= 0){
                            if(map.get(CurrentIndex - 1).getType().equals("text")){
                                int position = 0;
                                if(!map.get(CurrentIndex - 1).getContent().equals("") && !map.get(CurrentIndex).getContent().equals("")){
                                    position = Html.fromHtml(map.get(CurrentIndex - 1).getContent(),FROM_HTML_MODE_COMPACT).length() - 1;
                                    map.get(CurrentIndex - 1).setContent(map.get(CurrentIndex - 1).getContent().substring(0,map.get(CurrentIndex - 1).getContent().length()-4) +
                                            map.get(CurrentIndex).getContent().substring(13));
                                }else if(!map.get(CurrentIndex - 1).getContent().equals("")){
                                    position = Html.fromHtml(map.get(CurrentIndex - 1).getContent(),FROM_HTML_MODE_COMPACT).length() - 1;
                                }else if(!map.get(CurrentIndex).getContent().equals("")){
                                    position = Html.fromHtml(map.get(CurrentIndex).getContent(),FROM_HTML_MODE_COMPACT).length() - 1;
                                    map.get(CurrentIndex - 1).setContent(map.get(CurrentIndex).getContent());
                                }else {
                                    position = 0;
                                }
                                for(int i = CurrentIndex; i < map.size() - 1;i++)
                                    map.put(i,map.get(i + 1));
                                map.remove(map.size() - 1);
                                textChanged.deleteEditText(map,CurrentIndex,position,"specific");
                            } else if(map.get(CurrentIndex - 1).getType().equals("photo")){
                                try{
                                    final PhotoViewHolder photoViewHolder = (PhotoViewHolder)textChanged.getRecyclerView().findViewHolderForAdapterPosition(CurrentIndex-1);
                                    photoImageShelter(photoViewHolder.relativeLayout,photoViewHolder.imageView,photoViewHolder.itemView);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            } else if(map.get(CurrentIndex - 1).getType().equals("audio")){
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
                List<Spanned> text = editTextDistach(editText);
                HashMap<Integer, RecordingContent> map = textChanged.getMap();
                int index = getCurrentIndex();
                if(index != -1){
                    for(int i = map.size() - 1; i > index; i--)
                        map.put(i + 1,map.get(i));
                    RecordingContent content = new RecordingContent();
                    content.setType("text");
                    content.setColor(map.get(index).getColor());
                    content.setContent(Html.toHtml(text.get(0)));
                    map.put(index,content);
                    content = new RecordingContent();
                    content.setType("text");
                    content.setColor(map.get(index).getColor());
                    if(text.size() > 1)
                        content.setContent(Html.toHtml(text.get(1)));
                    else
                        content.setContent("");
                    map.put(index + 1,content);
                    setRequestFocusableArgs(index + 1,0,"first");
                    textChanged.updateAdapter(index);
                }
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
            if(isView){
                viewChange.changedView();
                isView = false;
                return;
            }
            itemView.requestFocus();
            editTextEnd.requestFocus();
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
                if(isView){
                    viewChange.changedView();
                    isView = false;
                    return;
                }
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
                        if(isViewEdit)
                            deletePath.add(filePath);
                        else{
                            File file = new File(filePath);
                            if (file.exists())
                            file.delete();
                        }
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

    public class PhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private RelativeLayout relativeLayout;
        private Button delete;
        private ImageView imageView;
        public View itemView;
        public PhotoViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            imageView = (ImageView)itemView.findViewById(R.id.item_imageView);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.photo_shelter);
            delete = (Button)itemView.findViewById(R.id.photo_delete);
            imageView.setOnClickListener(this);
            delete.setOnClickListener(this);
            relativeLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.photo_shelter:
                    if(isView){
                        viewChange.changedView();
                        isView = false;
                        return;
                    }
                    photoImageShelterRemove(relativeLayout,imageView,itemView);
                    break;
                case R.id.item_imageView:
                    if(isView){
                        viewChange.changedView();
                        isView = false;
                        return;
                    }
                    photoImageShelter(relativeLayout,imageView,itemView);
                    break;
                case R.id.photo_delete:
                    int index = (int)itemView.getTag();
                    HashMap<Integer, RecordingContent> map = textChanged.getMap();
                    String filePath = map.get(index).getContent();
                    if(isViewEdit && filePath.contains(Environment.getExternalStorageDirectory()+"/OhMemo/photo/"))
                        deletePath.add(filePath);
                    else{
                        File file = new File(filePath);
                        if (file.exists()&&filePath.contains(Environment.getExternalStorageDirectory()+"/OhMemo/photo/"))
                            file.delete();
                    };
                    for (int i = index; i < map.size() - 1; i++)
                        map.put(i, map.get(i + 1));
                    map.remove(map.size() - 1);
                    textChanged.deleteEditText(map, index, 0, "end");
                    imageView.setVisibility(View.VISIBLE);
                    relativeLayout.setVisibility(View.GONE);
                    shelter.remove(index);
                    break;
            }
        }
    }

    public void photoImageShelter(RelativeLayout relativeLayout,final ImageView imageView,View itemView){
        Log.d("photoImageShelter","photoImageShelter");
        View v = (textChanged.getRecyclerView()).getFocusedChild();
        if(v != null){
            InputMethodManager imm = (InputMethodManager)context.getSystemService( Context.INPUT_METHOD_SERVICE );
            imm.hideSoftInputFromWindow( v.getApplicationWindowToken( ) , 0 );
        }
        textChanged.recyclerViewClearFocusable();
        relativeLayout.getLayoutParams().height = imageView.getHeight();
        relativeLayout.getLayoutParams().width = imageView.getWidth();
        if(textChanged.getRecyclerView() != null)
            (textChanged.getRecyclerView()).scrollToPosition((int)itemView.getTag());
        AlphaAnimation appearAnimation = new AlphaAnimation(0, 1);
        appearAnimation.setDuration(500);
        AlphaAnimation disappearAnimation = new AlphaAnimation(1, 0);
        disappearAnimation.setDuration(500);
        imageView.startAnimation(disappearAnimation);
        disappearAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                imageView.setVisibility(View.GONE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        relativeLayout.startAnimation(appearAnimation);
        relativeLayout.setVisibility(View.VISIBLE);
        shelterSize size = new shelterSize();
        size.width = imageView.getWidth();
        size.height = imageView.getHeight();
        shelter.put((int)itemView.getTag(),size);
    }

    public void photoImageShelterRemove(final RelativeLayout relativeLayout, ImageView imageView, View itemView){
        Log.d("photoImageShelterRemove","photoImageShelterRemove");
        View v = (textChanged.getRecyclerView()).getFocusedChild();
        if(v != null){
            InputMethodManager imm = (InputMethodManager)context.getSystemService( Context.INPUT_METHOD_SERVICE );
            imm.hideSoftInputFromWindow( v.getApplicationWindowToken( ) , 0 );
        }
        textChanged.recyclerViewClearFocusable();
        if(textChanged.getRecyclerView() != null)
            (textChanged.getRecyclerView()).scrollToPosition((int)itemView.getTag());
        AlphaAnimation appearAnimation = new AlphaAnimation(0.5f, 1);
        appearAnimation.setDuration(500);
        AlphaAnimation disappearAnimation = new AlphaAnimation(1, 0.5f);
        disappearAnimation.setDuration(500);
        relativeLayout.startAnimation(disappearAnimation);
        disappearAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                relativeLayout.setVisibility(View.GONE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        imageView.startAnimation(appearAnimation);
        imageView.setVisibility(View.VISIBLE);
        shelter.remove((int)itemView.getTag());
    }


    public void changedItem(String text,String position,int index){
        HashMap<Integer, RecordingContent> map = textChanged.getMap();
        for(int i = map.size() - 1; i > index; i--)
            map.put(i + 1,map.get(i));
        String html =  Html.toHtml(new SpannedString(text));
        if(html.length() > 0)
            html = html.substring(0,html.length() - 1);
        if(position.equals("start")){
            map.put(index + 1,map.get(index));
            RecordingContent content = new RecordingContent();
            content.setColor(map.get(index).getColor());
            content.setType("text");
            content.setContent(RecordingAdapter.parseUnicodeToStr(html));
            map.put(index,content);
            requestFocusableIndex = index;
            type = "end";
        }else if(position.equals("end")){
            RecordingContent content = new RecordingContent();
            content.setColor(map.get(index).getColor());
            content.setType("text");
            content.setContent(RecordingAdapter.parseUnicodeToStr(html));
            map.put(index + 1,content);
            requestFocusableIndex = index + 1;
            type = "end";
        }
        Log.d("changedItem",map.toString());
    }

    public void requestFocusable(RecyclerView mRecycleView){
        try {
            HashMap<Integer,RecordingContent> map = textChanged.getMap();
            mRecycleView.requestFocus();
            mRecycleView.getChildAt(requestFocusableIndex -textChanged.getCurrentFirstIndex());
            Log.d("req", "requestFocusable: "+map.toString());
            if(map.get(requestFocusableIndex).getType().equals("text")){
                Log.d("req",requestFocusableIndex+"--"+textChanged.getCurrentFirstIndex());
                TextViewHolder textViewHolder = (TextViewHolder)mRecycleView.findViewHolderForAdapterPosition(requestFocusableIndex);
                textViewHolder.editText.requestFocus();
                if(type.equals("end"))
                    textViewHolder.editText.setSelection(textViewHolder.editText.getText().toString().length());
                else if(type.equals("specific"))
                    textViewHolder.editText.setSelection(position);
                else
                    textViewHolder.editText.setSelection(0);
            }else {
                AudioViewHolder audioViewHolder = (AudioViewHolder)mRecycleView.getChildViewHolder(mRecycleView.getChildAt(requestFocusableIndex-textChanged.getCurrentFirstIndex()));
                if(type.equals("end"))
                    audioViewHolder.editTextEnd.requestFocus();
                else
                    audioViewHolder.editTextStart.requestFocus();
            }
        }catch (Exception e){
           e.printStackTrace();
        }
    }

    public void setRequestFocusableArgs(int requestIndex,int position,String positionType){
        requestFocusableIndex = requestIndex;
        this.position = position;
        type = positionType;
        Log.d(TAG, "setRequestFocusableArgs: "+requestIndex+"--"+position +"---"+type);
    }


    public void photoSetFocusable(int index){
        HashMap<Integer,RecordingContent> map = textChanged.getMap();
        for(int i = index + 1; i < map.size(); i++)
            if(!map.get(i).getType().equals("photo")){
                requestFocusableIndex = i;
                break;
            }
        if(map.get(requestFocusableIndex).getType().equals("text")){
            type = "first";
            position = 0;
        }else {
            type = "end";
            position = 0;
        }
    }

    public TextViewHolder getTextViewHolder(RecyclerView mRecycleRView){
        HashMap<Integer,RecordingContent> map = textChanged.getMap();
        TextViewHolder holder = null;
        for(int i = textChanged.getCurrentLastIndex(); i >= textChanged.getCurrentFirstIndex(); i--) {
            if (map.get(i).getType().equals("text")) {
                holder = (TextViewHolder) mRecycleRView.getChildViewHolder(mRecycleRView.getChildAt(i));
                break;
            }
        }
        return holder;
    }

    public int getCurrentIndex(){
        return CurrentIndex;
    }

    public String getCurrentType(){
        return CurrentType;
    }

    public List<Spanned> distachText(RecyclerView recyclerView){
        TextViewHolder textViewHolder = (TextViewHolder)recyclerView.getChildViewHolder(recyclerView.getChildAt(CurrentIndex - textChanged.getCurrentFirstIndex()));
        return editTextDistach(textViewHolder.editText);
    }

    public List<Spanned> editTextDistach(EditText editText){
        List<Spanned> text = new ArrayList<>();
        Editable editable = editText.getEditableText();
        int index = editText.getSelectionStart();
        SpannableString foreSpannaleString =  new SpannableString(editable.subSequence(0,index)),
                backSpannalbleString =  new SpannableString(editable.subSequence(index,editable.length()));
        if(index < editable.length()){
            Object[] spans = editable.getSpans(0,editable.length(),Object.class);
            for(int i = 0; i < spans.length; i++){
                int start = editable.getSpanStart(spans[i]),end = editable.getSpanEnd(spans[i]);
                Log.d(TAG, "editTextDistach: "+spans[i].toString()+"---index"+index+"---start"+start+"---end"+end);
                if(end < index){
                    Log.d(TAG, "editTextDistach: end < index");
                    foreSpannaleString.setSpan(spans[i],start,end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }else if(index <= start){
                    Log.d(TAG, "editTextDistach: index <= start");
                    backSpannalbleString.setSpan(spans[i],start - foreSpannaleString.length(),end - foreSpannaleString.length(),
                            Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }else{
                    Log.d(TAG, "editTextDistach: other");
                    foreSpannaleString.setSpan(spans[i],start,index, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    backSpannalbleString.setSpan(spans[i],index -foreSpannaleString.length(),end - foreSpannaleString.length()
                            , Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
            }
            text.add(foreSpannaleString);
            text.add(backSpannalbleString);
        }else
            text.add((Spanned)editable);
        return text;
    }

    public SpannableString dispatchPositionColor(SpannableString spannableString,int position,int count){
        ForegroundColorSpan[] spans = spannableString.getSpans(0,spannableString.length(), ForegroundColorSpan.class);
        for(int i=0; i < spans.length; i++){
            Log.d("dispatchPositionColor","end"+spannableString.getSpanEnd(spans[i]) +"---start"+spannableString.getSpanStart(spans[i]) );
            if(spannableString.getSpanEnd(spans[i]) == position && spans[i].getForegroundColor() == currentColor){
                spannableString.setSpan(spans[i],spannableString.getSpanStart(spans[i]),position + count,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }else if((spannableString.getSpanStart(spans[i]) == position + count) && spans[i].getForegroundColor() == currentColor){
                spannableString.setSpan(spans[i],position,spannableString.getSpanEnd(spans[i]),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }else if(spannableString.getSpanStart(spans[i]) < position && spannableString.getSpanEnd(spans[i]) > position){
                int start = spannableString.getSpanStart(spans[i]),end = spannableString.getSpanEnd(spans[i]),color = spans[i].getForegroundColor();
                spannableString.removeSpan(spans[i]);
                spannableString.setSpan(new ForegroundColorSpan(color),start,position,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(currentColor),position,position+count,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(color),position+count,end,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }else if(currentColor != context.getColor(R.color.recording_title)){
                spannableString.setSpan(new ForegroundColorSpan(currentColor),position,position+count,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableString;
    }



    public void setTextColor(int index,RecyclerView recyclerView,int color,EditText editText){
        if(index != -1 && (index - textChanged.getCurrentFirstIndex() >= 0)){
            TextViewHolder textViewHolder = (TextViewHolder)recyclerView.getChildViewHolder(recyclerView.getChildAt(index - textChanged.getCurrentFirstIndex()));
            int startIndex = textViewHolder.editText.getSelectionStart(),endIndex = textViewHolder.editText.getSelectionEnd();
            SpannableString spannableString = new SpannableString(textViewHolder.editText.getText());
            spannableString = updateTextColor(spannableString,startIndex,endIndex,color);
            textViewHolder.editText.setText(spannableString);
            textViewHolder.editText.setSelection(startIndex,endIndex);
            textChanged.TextChanged(spannableString,index);
        }else if(editText != null){
            int startIndex = editText.getSelectionStart(),endIndex = editText.getSelectionEnd();
            SpannableString spannableString = new SpannableString(editText.getText());
            spannableString = updateTextColor(spannableString,startIndex,endIndex,color);
            editText.setText(spannableString);
            editText.setSelection(startIndex,endIndex);
        }
    }
    private SpannableString updateTextColor(SpannableString spannableString,int start,int end,int insertColor){
        ForegroundColorSpan[] spans = spannableString.getSpans(0,spannableString.length(), ForegroundColorSpan.class);
        boolean changed = false;
        for(int i=0; i < spans.length; i++){
            if(!(spannableString.getSpanStart(spans[i]) > end || spannableString.getSpanEnd(spans[i]) < start)){
                int spanStart = spannableString.getSpanStart(spans[i]),spanEnd = spannableString.getSpanEnd(spans[i]),size = spans[i].getForegroundColor();
                spannableString.removeSpan(spans[i]);
                if(spanStart < start)
                    spannableString.setSpan(new ForegroundColorSpan(size),spanStart,start,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                if(spanEnd > end)
                    spannableString.setSpan(new ForegroundColorSpan(size),end,spanEnd,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(insertColor),start,end,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                changed = true;
            }
        }
        if(!changed){
            spannableString.setSpan(new ForegroundColorSpan(insertColor),start,end,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }


    public void setTextLine(int index,RecyclerView recyclerView,int type,EditText editText){
        if(index != -1 && (index - textChanged.getCurrentFirstIndex() >= 0)){
            TextViewHolder textViewHolder = (TextViewHolder)recyclerView.getChildViewHolder(recyclerView.getChildAt(index - textChanged.getCurrentFirstIndex()));
            int startIndex = textViewHolder.editText.getSelectionStart(),endIndex = textViewHolder.editText.getSelectionEnd();
            SpannableString spannableString = new SpannableString(textViewHolder.editText.getText());
            if(type == 1)
                spannableString = updateText(spannableString,startIndex,endIndex,new UnderlineSpan());
            else
                spannableString = updateText(spannableString,startIndex,endIndex,new StrikethroughSpan());
            textViewHolder.editText.setText(spannableString);
            textViewHolder.editText.setSelection(startIndex,endIndex);
            textChanged.TextChanged(spannableString,index);
        }else if(editText != null){
            int startIndex = editText.getSelectionStart(),endIndex = editText.getSelectionEnd();
            SpannableString spannableString = new SpannableString(editText.getText());
            if(type == 1)
                spannableString = updateText(spannableString,startIndex,endIndex,new UnderlineSpan());
            else
                spannableString = updateText(spannableString,startIndex,endIndex,new StrikethroughSpan());
            editText.setText(spannableString);
            editText.setSelection(startIndex,endIndex);
        }
    }


    public void setTextBold(int index,RecyclerView recyclerView,EditText editText){
        if(index != -1 && (index - textChanged.getCurrentFirstIndex() >= 0)){
            TextViewHolder textViewHolder = (TextViewHolder)recyclerView.getChildViewHolder(recyclerView.getChildAt(index - textChanged.getCurrentFirstIndex()));
            int startIndex = textViewHolder.editText.getSelectionStart(),endIndex = textViewHolder.editText.getSelectionEnd();
            SpannableString spannableString = new SpannableString(textViewHolder.editText.getText());
            spannableString = updateText(spannableString,startIndex,endIndex,new StyleSpan(Typeface.BOLD));
            textViewHolder.editText.setText(spannableString);
            textViewHolder.editText.setSelection(startIndex,endIndex);
            textChanged.TextChanged(spannableString,index);
        }else if(editText != null){
            int startIndex = editText.getSelectionStart(),endIndex = editText.getSelectionEnd();
            SpannableString spannableString = new SpannableString(editText.getText());
            spannableString = updateText(spannableString,startIndex,endIndex,new StyleSpan(Typeface.BOLD));
            editText.setText(spannableString);
            editText.setSelection(startIndex,endIndex);
        }
    }

    public SpannableString updateText(SpannableString spannableString,int start,int end,Object object){
        Object[] spans = spannableString.getSpans(0,spannableString.length(),object.getClass());
        int i = 0;
        for( i = 0; i < spans.length; i++){
            if(spannableString.getSpanStart(spans[i]) <= start && spannableString.getSpanEnd(spans[i]) >= end){
                int spanStart = spannableString.getSpanStart(spans[i]),spanEnd = spannableString.getSpanEnd(spans[i]);
                spannableString.removeSpan(spans[i]);
                if(spanStart < start){
                    Object newObject = null;
                    if(object.getClass().getSimpleName().equals("StyleSpan"))
                        newObject = new StyleSpan(Typeface.BOLD);
                    else if(object.getClass().getSimpleName().equals("UnderlineSpan"))
                        newObject = new UnderlineSpan();
                    else if(object.getClass().getSimpleName().equals("StrikethroughSpan"))
                        newObject = new StrikethroughSpan();
                    if(newObject != null)
                        spannableString.setSpan(newObject,spanStart,start,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                if(spanEnd > end)
                    spannableString.setSpan(object,end,spanEnd,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            }
        }
        if(i == spans.length){
            spannableString.setSpan(object,start,end,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            for(i = 0; i < spans.length; i++){
                if(!(spannableString.getSpanStart(spans[i]) > end || spannableString.getSpanEnd(spans[i]) < start)){
                    int spanStart = spannableString.getSpanStart(spans[i]),spanEnd = spannableString.getSpanEnd(spans[i]);
                    if(start <= spanStart && end >= spanEnd)
                        spannableString.removeSpan(spans[i]);
                    else if(spanStart < start){
                        spannableString.removeSpan(spans[i]);
                            spannableString.setSpan(object,spanStart,spannableString.getSpanEnd(object), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        isChanged = true;
                    }else if(spanEnd > end){
                        spannableString.removeSpan(spans[i]);
                        spannableString.setSpan(object,spannableString.getSpanStart(object),spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        isChanged = true;
                    }
                }
            }
        }
        spans = spannableString.getSpans(0,spannableString.length(),object.getClass());
        for(i=0; i < spans.length - 1; i++){
            if((spannableString.getSpanEnd(spans[i])) == spannableString.getSpanStart(spans[i+1])){
                int startMeger = spannableString.getSpanStart(spans[i]),endMerge = spannableString.getSpanEnd(spans[i+1]);
                spannableString.removeSpan(spans[i]);
                spannableString.setSpan(spans[i+1],startMeger,endMerge,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableString;
    }

    public static String parseUnicodeToStr(String unicodeStr) {
        String regExp = "&#\\d*;";
        Matcher m = Pattern.compile(regExp).matcher(unicodeStr);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String s = m.group(0);
            s = s.replaceAll("(&#)|;", "");
            char c = (char) Integer.parseInt(s);
            m.appendReplacement(sb, Character.toString(c));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private SpannableString updateAllTypeSpan(SpannableString spannableString){
        Object[] spans = spannableString.getSpans(0,spannableString.length(),Object.class);
        for(int i = 0; i < spans.length; i++){
            if(spannableString.getSpanStart(spans[i]) == spannableString.getSpanEnd(spans[i]))
                spannableString.removeSpan(spans[i]);
        }
        return spannableString;
    }

    public interface TextChanged{
        void TextChanged(Spanned text, int index);
        Context getContext();
        int getCurrentFirstIndex();
        int getCurrentLastIndex();
        HashMap<Integer,RecordingContent> getMap();
        void deleteEditText(HashMap<Integer,RecordingContent> map,int index,int position, String type);
        void updateAdapter(int index);
        void recyclerViewFocusable();
        void recyclerViewClearFocusable();
        RecyclerView getRecyclerView();
        void updateRecyclerView(int position);
    }

    public interface ViewChange{
         void changedView();
    }

    class shelterSize{
        int width;
        int height;
    }

}

