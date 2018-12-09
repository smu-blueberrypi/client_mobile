package com.example.user.mainapp;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class NotificationHelper extends ContextWrapper {
    public static final String sendID = "sendID";
    public static final String sendName = "send";

    private NotificationManager mManager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createsends();
        }
    }
    @TargetApi(Build.VERSION_CODES.O)
    public void createsends(){
        NotificationChannel send = new NotificationChannel(sendID,sendName,NotificationManager.IMPORTANCE_DEFAULT);
        send.enableLights(true);
        send.enableVibration(true);
        send.setLightColor(R.color.colorPrimary);
        send.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(send);
    }
    public NotificationManager getManager(){
        if (mManager == null){
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }
    public NotificationCompat.Builder getsendNotification(String title, String message){
        return new NotificationCompat.Builder(getApplicationContext(),sendID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.sos);
    }
}
