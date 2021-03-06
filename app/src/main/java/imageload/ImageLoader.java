package imageload;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.shining.memo.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Stack;
import java.util.WeakHashMap;

public class ImageLoader {
    // 手机中的缓存
    private Context context;
    private MemoryCache memoryCache = new MemoryCache();
    private FileCache fileCache;
    private PicturesLoader pictureLoaderThread = new PicturesLoader();
    private PicturesQueue picturesQueue = new PicturesQueue();
    private Map<ImageView, String> imageViews = Collections
            .synchronizedMap(new WeakHashMap<ImageView, String>());
    private static final String TAG = "ImageLoader";

    public ImageLoader(Context context) {
        // 设置线程的优先级
        pictureLoaderThread.setPriority(Thread.NORM_PRIORITY - 1);
        fileCache = new FileCache();
        this.context = context;
    }
    // 在找不到图片时，默认的图片
    final int stub_id = R.drawable.image_null_icon;
    public void DisplayImage(String url, Activity activity, ImageView imageView) {
        imageViews.put(imageView, url);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null)
            imageView.setImageBitmap(bitmap);
        else {// 如果手机内存缓存中没有图片，则调用任务队列，并先设置默认图片
            queuePhoto(url, activity, imageView);
            imageView.setImageResource(stub_id);
        }
    }
    private void queuePhoto(String url, Activity activity, ImageView imageView) {
        // 这ImageView可能之前被用于其它图像。所以可能会有一些旧的任务队列。我们需要清理掉它们。
        picturesQueue.Clean(imageView);
        PictureToLoad p = new PictureToLoad(url, imageView);
        synchronized (picturesQueue.picturesToLoad) {
            picturesQueue.picturesToLoad.push(p);
            picturesQueue.picturesToLoad.notifyAll();
        }
        // 如果这个线程还没有启动，则启动线程
        if (pictureLoaderThread.getState() == Thread.State.NEW)
            pictureLoaderThread.start();
    }
    /**
     * 根据url获取相应的图片的Bitmap
     *
     * @param url
     * @return
     */
    private Bitmap getBitmap(String url) {
        File f = fileCache.getFile(url);
        // 从SD卡缓存中获取
        Bitmap b = decodeFile(f);
        if (b != null)
            return b;
        else {


        }
        // 否则从网络中获取
        try {
            Bitmap bitmap = null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl
                    .openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            // 将图片写到sd卡目录中去
            ImageUtil.CopyStream(is, os);
            os.close();
            bitmap = decodeFile(f);
            return bitmap;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    // 解码图像和缩放以减少内存的消耗
    private Bitmap decodeFile(File f) {
        try {
            // 解码图像尺寸
            int angle = readPictureDegree(f.getAbsolutePath());
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
            int width_tmp = o.outWidth;
            WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
            int width = windowManager.getDefaultDisplay().getWidth();
            int scale = 1;
            if(width_tmp <= width){
                scale = 1;
            }else {
                scale = width_tmp / width;
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
            return  rotaingImageView(angle, bitmap);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    /**
     * PictureToLoad类(包括图片的地址和ImageView对象)
     *
     * @author loonggg
     *
     */
    private class PictureToLoad {
        public String url;
        public ImageView imageView;
        public PictureToLoad(String u, ImageView i) {
            url = u;
            imageView = i;
        }
    }
    public void stopThread() {
        pictureLoaderThread.interrupt();
    }
    // 存储下载的照片列表
    class PicturesQueue {
        private Stack<PictureToLoad> picturesToLoad = new Stack<PictureToLoad>();
        // 删除这个ImageView的所有实例
        public void Clean(ImageView image) {
            for (int j = 0; j < picturesToLoad.size();) {
                if (picturesToLoad.get(j).imageView == image)
                    picturesToLoad.remove(j);
                else
                    ++j;
            }
        }
    }
    // 图片加载线程
    class PicturesLoader extends Thread {
        public void run() {
            try {
                while (true) {
                    // 线程等待直到有图片加载在队列中
                    if (picturesQueue.picturesToLoad.size() == 0)
                        synchronized (picturesQueue.picturesToLoad) {
                            picturesQueue.picturesToLoad.wait();
                        }
                    if (picturesQueue.picturesToLoad.size() != 0) {
                        PictureToLoad photoToLoad;
                        synchronized (picturesQueue.picturesToLoad) {
                            photoToLoad = picturesQueue.picturesToLoad.pop();
                        }
                        Bitmap bmp = getBitmap(photoToLoad.url);
                        // 写到手机内存中
                        memoryCache.put(photoToLoad.url, bmp);
                        String tag = imageViews.get(photoToLoad.imageView);
                        if (tag != null && tag.equals(photoToLoad.url)) {
                            BitmapDisplayer bd = new BitmapDisplayer(bmp,
                                    photoToLoad.imageView);
                            Activity activity = (Activity) photoToLoad.imageView
                                    .getContext();
                            activity.runOnUiThread(bd);
                        }
                    }
                    if (Thread.interrupted())
                        break;
                }
            } catch (InterruptedException e) {
                // 在这里允许线程退出
            }
        }
    }
    // 在UI线程中显示Bitmap图像
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        ImageView imageView;
        public BitmapDisplayer(Bitmap bitmap, ImageView imageView) {
            this.bitmap = bitmap;
            this.imageView = imageView;
        }
        public void run() {
            if (bitmap != null){
                imageView.setImageBitmap(bitmap);
            }
            else
                imageView.setImageResource(stub_id);
        }
    }
    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }

    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        Bitmap returnBm = null;
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bitmap;
        }
        if (bitmap != returnBm) {
            bitmap.recycle();
        }
        return returnBm;
    }
}
