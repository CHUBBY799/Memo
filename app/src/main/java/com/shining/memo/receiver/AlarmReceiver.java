package com.shining.memo.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;


import com.shining.memo.R;
import com.shining.memo.view.AlarmClockActivity;
import com.shining.memo.view.TaskActivity;

public class AlarmReceiver extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("com.shining.memo.alarmandnotice")){
            int taskId = intent.getIntExtra("taskId",-1);
            int pop = intent.getIntExtra("pop",-1);
            int ringtone = intent.getIntExtra("ringtone",-1);
            String title = "";
            try{
                title = intent.getStringExtra("title");
                if(title.equals(""))
                    title = "No title!";
            }catch (Exception e){
                title = "No title!";
                e.printStackTrace();
            }
            if(pop == 1){
                String id = "my_channel";
                String name="my_channel_name";
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                Notification notification = null;
                Intent contentIntent = new Intent(context, TaskActivity.class);
                contentIntent.putExtra("taskId",taskId);
                contentIntent.putExtra("isNotification",true);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, taskId, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
                    notificationManager.createNotificationChannel(mChannel);
                    notification = new Notification.Builder(context,id)
                            .setStyle(new Notification.BigTextStyle())
                            .setTicker("Memo has new recording notice")
                            .setContentTitle("Memo has new recording notice")
                            .setContentText(title)
                            .setSmallIcon(R.drawable.finish_icon)
                            .setPriority(Notification.PRIORITY_HIGH)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setAutoCancel(true)
                            .setOngoing(false)
                            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.app_launch_icon))
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
                String time = "None";
                try{
                    time = intent.getStringExtra("time");
                }catch (Exception e){
                    e.printStackTrace();
                }
                Intent alarmIntent = new Intent(context,AlarmClockActivity.class);
                alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                alarmIntent.putExtra("time",time);
                alarmIntent.putExtra("title",title);
                alarmIntent.putExtra("urgent",intent.getIntExtra("urgent",0));
                context.startActivity(alarmIntent);
            }
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
