package com.shining.memo.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import com.shining.memo.R;
import com.shining.memo.utils.ToastUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoPresenter {

    private String FolderPath = "";     //文件夹路径
    private Uri imageUri;
    private Context context;

    public PhotoPresenter(Context context) {
        this(Environment.getExternalStorageDirectory()+"/OhMemo/photo/");
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

                imageUri = Uri.fromFile(outputImage);
                //启动相机程序
                Intent intentCamera = new Intent();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
                }
                intentCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                activity.startActivityForResult(intentCamera,requestCode);
                return filePath;
            }catch (Exception e){
                e.printStackTrace();
                ToastUtils.showShort(context, context.getResources().getString(R.string.no_memory_dir));
                return null;
            }
        }else {
            ToastUtils.showShort(context, context.getResources().getString(R.string.no_sdcard));
            return null;
        }
    }

    //打开相册
    public void openAlbum(Activity activity,int requestCode){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
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
