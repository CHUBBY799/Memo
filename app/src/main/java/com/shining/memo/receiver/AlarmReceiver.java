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
import android.media.RingtoneManager;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import com.shining.memo.R;
import com.shining.memo.view.RecordingViewActivity;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("com.shining.memo.alarmandnotice")){
            int taskId = intent.getIntExtra("taskId",-1);
            int pop = intent.getIntExtra("pop",-1);
            int ringtone = intent.getIntExtra("ringtone",-1);
            String title = "";
            try{
                if(intent.getStringExtra("title").equals(""))
                    title = "无标题!";
                else
                    title = intent.getStringExtra("title");
            }catch (Exception e){
                e.printStackTrace();
            }
            Log.d("AlarmReceiver", "onReceive: pop"+ pop +"---ringtone"+ ringtone);
            if(pop == 1){
                String id = "my_channel_04";
                String name="my_channel_name4";
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                Notification notification = null;
                Intent contentIntent = new Intent(context, RecordingViewActivity.class);
                contentIntent.putExtra("taskId",taskId);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, contentIntent, 0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
                    notificationManager.createNotificationChannel(mChannel);
                    notification = new Notification.Builder(context,id)
                            .setTicker("Memo has new recording notice")
                            .setContentTitle("Memo has new recording notice")
                            .setContentText(title)
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
                notificationManager.notify(0x1a42, notification);
            }
            if(ringtone == 1){

            }
//            Ringtone r = RingtoneManager.getRingtone(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
//            r.play();
            if(ringtone ==1 || pop == 1){
                PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
                if (!pm.isScreenOn()) {
                    PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "con.shinging.memo:WakeAndLock");
                    wakeLock.acquire();  //点亮屏幕
                    wakeLock.release();  //任务结束后释放
                }
            }
        }
    }
}
