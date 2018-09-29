package com.shining.memo.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;

import com.shining.memo.model.RecordingContent;
import com.shining.memo.view.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PhotoPresenter {

    private static final String TAG = "PhotoPresenter";
    private String FolderPath = "";     //文件夹路径

    private Uri imageUri;
    private Context context;

    public PhotoPresenter(Context context) {
        this(Environment.getExternalStorageDirectory()+"/photo/");
        this.context = context;
    }

    public PhotoPresenter(String filePath) {

        File path = new File(filePath);
        if(!path.exists())
            path.mkdirs();
        this.FolderPath = filePath;
    }


    public String takePicture(Activity activity,int requestCode) {
        if(hasSdcard()){
            //创建File对象，用于存储拍照后的照片
            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String fileName = timeStamp + ".jpg";
            File outputImage=new File(FolderPath,fileName);
            String filePath = FolderPath + fileName;
            try{
                if(outputImage.exists())
                    outputImage.delete();
                outputImage.createNewFile();
                Uri imageUri = Uri.fromFile(outputImage);
                //启动相机程序
                Intent intentCamera = new Intent();
                intentCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                activity.startActivityForResult(intentCamera,requestCode);
                return filePath;
            }catch (Exception e){
                e.printStackTrace();
                ToastUtils.showShort(context, "没有找到储存目录！");
                return null;
            }
        }else {
            ToastUtils.showShort(context, "设备没有SD卡！");
            return null;
        }
    }

    //打开相册
    public void openAlbum(Activity activity,int requestCode){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        activity.startActivityForResult(intent, requestCode); // 打开相册
    }

    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

}
