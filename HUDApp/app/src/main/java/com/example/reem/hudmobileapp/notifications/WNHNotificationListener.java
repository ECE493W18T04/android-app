package com.example.reem.hudmobileapp.notifications;

import android.service.notification.StatusBarNotification;

/**
 * Created by Reem on 2018-03-09.
 */

public class WNHNotificationListener extends android.service.notification.NotificationListenerService
{

    @Override
    public void onCreate()
    {

    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn)
    {
        NotificationManager notificationManager;
        if (sbn.getPackageName().equals("com.google.android.apps.maps") && !sbn.isClearable()) {
            // initialize ble stuff here
            notificationManager = new GoogleMapsNotificationManager();
            byte[] content=notificationManager.getContent();
        }
    }

}
