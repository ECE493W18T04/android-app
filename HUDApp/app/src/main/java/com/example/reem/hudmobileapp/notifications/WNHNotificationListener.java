package com.example.reem.hudmobileapp.notifications;

import android.app.Notification;
import android.content.Intent;
import android.graphics.Bitmap;
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


            Bundle extras = sbn.getNotification().extras;
            String text;
            if (extras.get(Notification.EXTRA_TEXT) instanceof String) {
                text = (String) extras.get(Notification.EXTRA_TEXT);
            } else {
                text = extras.get(Notification.EXTRA_TEXT).toString();
            }
            Log.d(DEBUG_TAG, text);

            RemoteViews rv = sbn.getNotification().bigContentView;
            RelativeLayout rl = (RelativeLayout) rv.apply(getApplicationContext(), null);
            notificationManager = new GoogleMapsNotificationManager(rl);
            byte[] content=notificationManager.getContent();

        }
        if (sbn.getPackageName().equals(GOOGLE_CALLER) && !sbn.isClearable())  {
            Log.d(DEBUG_TAG,"Caller Notification");

            notificationManager = new CallNotificationManager(sbn);
            byte[] content=notificationManager.getContent();

        }
        if (sbn.getPackageName().equals(SPOTIFY) && !sbn.isClearable()) {
            notificationManager = new SpotifyMusicNotificationManager(sbn);
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
