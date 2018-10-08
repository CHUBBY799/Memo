package com.shining.memo.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.shining.memo.R;
import com.shining.memo.utils.ToastUtils;
import com.shining.memo.view.MainActivity;
import com.shining.memo.view.RecordingEditActivity;
import com.shining.memo.view.TestView;

import java.io.File;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("com.shining.memo.alarmandnotice")){
            Log.d("AlarmReceiver", "onReceive: ");
            String id = "my_channel_04";
            String name="my_channel_name4";
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            Notification notification = null;
            Intent intent1 = new Intent(context, TestView.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
                mChannel.setDescription("memo notice");
                mChannel.enableLights(true);
                mChannel.setLightColor(Color.RED);
                mChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100,200,300,400,500,600});
                mChannel.setSound( RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),Notification.AUDIO_ATTRIBUTES_DEFAULT);
                mChannel.setBypassDnd(true);
                notificationManager.createNotificationChannel(mChannel);
                notification = new Notification.Builder(context,id)
                        .setTicker("Memo has new recording notice")
                        .setContentTitle("Memo has new recording notice")
                        .setContentText("hahaha")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setOngoing(false)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.import_from_gallery_icon))
                        .setContentIntent(pendingIntent)
                        .setOnlyAlertOnce(true)
                        .setWhen(System.currentTimeMillis())
                        .build();
            }else {
                notification = new Notification.Builder(context)
                        .setTicker("Memo has new recording notice")
                        .setContentTitle("Memo has new recording notice")
                        .setContentText("hahaha")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setOngoing(false)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.import_from_gallery_icon))
                        .setContentIntent(pendingIntent)
                        .setOnlyAlertOnce(true)
                        .setWhen(System.currentTimeMillis())
                        .build();
            }
            Log.d("TAG", "onReceive:"+notification.toString());
            notificationManager.notify(0x1a42, notification);
//            Ringtone r = RingtoneManager.getRingtone(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
//            r.play();
            PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
            if (!pm.isScreenOn()) {
                PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "con.shinging.memo:WakeAndLock");
                wakeLock.acquire();  //点亮屏幕
                wakeLock.release();  //任务结束后释放
            }
        }
    }
}
