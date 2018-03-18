package com.example.reem.hudmobileapp.notifications;

import android.app.Notification;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.Log;

/**
 * Created by Reem on 2018-03-09.
 */

public class CallNotificationManager extends NotificationManager {
    private StatusBarNotification n;

    public CallNotificationManager(StatusBarNotification sbn) {
        n=sbn;
    }

    @Override
    public byte[] getContent() {
        //String pack = n.getPackageName();
        //String ticker ="";
        //if(n.getNotification().tickerText !=null) {
            //ticker = n.getNotification().tickerText.toString();
        //}

        Bundle extras = n.getNotification().extras;
        String title = extras.getString("android.title");

        //String text = extras.getCharSequence("android.text").toString();
        //int id1 = extras.getInt(Notification.EXTRA_SMALL_ICON);
        //Bitmap id = n.getNotification().largeIcon;

        //Log.d("Package",pack);
        //Log.d("Ticker",ticker);

        //Title contains Contact Name/Phone number
        //FORMAT -> 'Contact Name'
        Log.d("Title",title);

        //Text: Incoming call
        //Log.d("Text",text);

        return title.getBytes();
    }
}
