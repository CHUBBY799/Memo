package com.shining.memo.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.util.LruCache;
import android.view.View;

import com.shining.memo.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShotUtils {

    public static Bitmap shotRecyclerView(Context context,RecyclerView view,View title) {
        Log.d("view",view.getHeight()+"");
        RecyclerView.Adapter adapter = view.getAdapter();
        Bitmap bigBitmap = null;
        int stampHeight = 50;
        if (adapter != null) {
            int size = adapter.getItemCount();
            int height = 0;
            if(title != null){
                height = title.getHeight();
            }
            Paint paint = new Paint();
            int iHeight = 0;
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = maxMemory / 8;
            LruCache<String, Bitmap> bitmaCache = new LruCache<>(cacheSize);
            for (int i = 0; i < size; i++) {
                RecyclerView.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(i));
                adapter.onBindViewHolder(holder, i);
                holder.itemView.measure(
                        View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(),
                        holder.itemView.getMeasuredHeight());
                holder.itemView.setDrawingCacheEnabled(true);
                holder.itemView.buildDrawingCache();
                Bitmap drawingCache = holder.itemView.getDrawingCache();
                if (drawingCache != null) {
                    bitmaCache.put(String.valueOf(i), drawingCache);
                }
                height += holder.itemView.getMeasuredHeight();
            }

            bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), height + stampHeight, Bitmap.Config.ARGB_8888);
            Canvas bigCanvas = new Canvas(bigBitmap);
            Drawable lBackground = view.getBackground();
            if (lBackground instanceof ColorDrawable) {
                ColorDrawable lColorDrawable = (ColorDrawable) lBackground;
                int lColor = lColorDrawable.getColor();
                bigCanvas.drawColor(lColor);
            }
            if(title != null){
                Bitmap bitmap = getViewBitmap(title);
                if(bitmap != null){
                    bigCanvas.drawBitmap(bitmap, 0f, iHeight, paint);
                    iHeight += bitmap.getHeight();
                }
            }
            for (int i = 0; i < size; i++) {
                try {
                    Bitmap bitmap = bitmaCache.get(String.valueOf(i));
                    if(bitmap != null){
                        bigCanvas.drawBitmap(bitmap, 0f, iHeight, paint);
                        iHeight += bitmap.getHeight();
                        bitmap.recycle();
                    }else {
                        RecyclerView.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(i));
                        adapter.onBindViewHolder(holder, i);
                        holder.itemView.measure(
                                View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                        holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(),
                                holder.itemView.getMeasuredHeight());
                        bitmap = getBitmap(holder.itemView);
                        bigCanvas.drawBitmap(bitmap, 0f, iHeight, paint);
                        iHeight += bitmap.getHeight();
                        bitmap.recycle();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            //分享水印
            Bitmap stamp = Bitmap.createBitmap(view.getMeasuredWidth(),stampHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(stamp);
            canvas.drawBitmap(stamp, 0, 0, null);
            TextPaint textPaint = new TextPaint();
            textPaint.setAntiAlias(true);
            textPaint.setTextSize(28.0F);
            textPaint.setColor(context.getColor(R.color.item_btn_text));
            StaticLayout sl= new StaticLayout(context.getResources().getString(R.string.share_stamp),
                    textPaint, stamp.getWidth(), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
            sl.draw(canvas);
            bigCanvas.drawBitmap(stamp,0f,iHeight,paint);
        }
        return bigBitmap;
    }

    public static Bitmap getViewBitmap(View view){
        view.measure(
                View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        return  view.getDrawingCache();
    }


    public static Bitmap getBitmap(View view)
    {
        Bitmap bitmap= Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }


    public static String saveBitmap(Context context, Bitmap mBitmap) {
        String savePath = "";
        File file;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            savePath = Environment.getExternalStorageDirectory()+"/OhMemo/photo/";
            file = new File(savePath);
            if (!file.exists()) {
                file.mkdirs();
            }
        }else {
            return null;
        }
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String fileName = timeStamp + ".jpg";
            file = new File(savePath,fileName);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return file.getAbsolutePath();
    }


    public static boolean isAppAvilible(Context context,String packName){
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        for(int i = 0; i < packageInfos.size(); i++){
            if(packageInfos.get(i).packageName.equals(packName))
                return true;
        }
        return false;
    }

    public static void shareCustom(Context context,String sharePath){
        try{
            List<Intent> targetIntents = new ArrayList<>();
            if(isAppAvilible(context,"com.tencent.mm")){
                Intent target = new Intent();
                ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
                target.setComponent(comp);
                target.setAction(Intent.ACTION_SEND);
                target.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(sharePath)));
                target.setType("image/*");
                targetIntents.add(target);
                target = new Intent();
                comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
                target.setComponent(comp);
                target.setAction(Intent.ACTION_SEND);
                target.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(sharePath)));
                target.setType("image/*");
                targetIntents.add(target);
            }
            if(isAppAvilible(context,"com.tencent.mobileqq")){
                Intent target = new Intent();
                ComponentName comp = new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity");
                target.setComponent(comp);
                target.setAction(Intent.ACTION_SEND);
                target.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(sharePath)));
                target.setType("image/*");
                targetIntents.add(target);
            }
            if(targetIntents.size() > 0){
                Intent chooserIntent = Intent.createChooser(targetIntents.remove(0),"分享图片");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,targetIntents.toArray(new Parcelable[]{}));
                ((Activity)context).startActivityForResult(chooserIntent,0xa4);
            }else {
                if(sharePath != null){
                    Uri imageUri = Uri.fromFile(new File(sharePath));
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    shareIntent.setType("image/*");
                    ((Activity)context).startActivityForResult(Intent.createChooser(shareIntent, "分享图片"),0xa4);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
