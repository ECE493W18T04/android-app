package com.example.reem.hudmobileapp.notifications;

import android.app.Notification;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Reem on 2018-03-09.
 */

public class WNHNotificationListener extends NotificationListenerService
{
    private final String DEBUG_TAG = this.getClass().getSimpleName();

    private static final String GOOGLE_MAPS = "com.google.android.apps.maps";
    private static final String GOOGLE_CALLER = "com.android.dialer";
    private static final String GOOGLE_SMS = "com.google.android.apps.messaging";
    private static final String SPOTIFY = "com.spotify.music";



    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn)
    {
        Log.d(DEBUG_TAG, "Notification Posted: " + sbn.getPackageName());

        NotificationManager notificationManager;
        if (sbn.getPackageName().equals(GOOGLE_MAPS) && !sbn.isClearable()) {
            // initialize ble stuff here

            RemoteViews rv = sbn.getNotification().bigContentView;
            RelativeLayout rl = (RelativeLayout) rv.apply(getApplicationContext(), null);
            notificationManager = new GoogleMapsNotificationManager(rl);
            byte[] content=notificationManager.getContent();
            Bitmap b = BitmapFactory.decodeByteArray(content, 0, content.length);


        }
        if (sbn.getPackageName().equals(GOOGLE_CALLER) && !sbn.isClearable())  {
            notificationManager = new CallNotificationManager(sbn);
            byte[] content=notificationManager.getContent();
        }
        if (sbn.getPackageName().equals(SPOTIFY) && !sbn.isClearable()) {
            notificationManager = new SpotifyMusicNotificationManager(sbn);
            byte[] content=notificationManager.getContent();
        }
        if (sbn.getPackageName().equals(GOOGLE_SMS)) {
            notificationManager = new SMSNotificationManager(sbn);
            byte[] content=notificationManager.getContent();
        }
        super.onNotificationPosted(sbn);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){

    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }
}
