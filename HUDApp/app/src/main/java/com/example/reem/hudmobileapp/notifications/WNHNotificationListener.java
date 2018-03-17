package com.example.reem.hudmobileapp.notifications;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Pair;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;

import com.example.reem.hudmobileapp.helper.ImagePHash;
import com.example.reem.hudmobileapp.R;

import java.lang.reflect.Field;
import java.util.ArrayList;

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

    private ArrayList<Pair<Long, Integer>> resArray;



    @Override
    public void onCreate() {

        Field[] ID_Fields = R.drawable.class.getFields();
        resArray = new ArrayList<>();
        ImagePHash pHash = new ImagePHash();

        for(int i = 0; i < ID_Fields.length; i++) {
            try {
                if(getResources().getResourceEntryName(ID_Fields[i].getInt(null)).toString().substring(0,2).equalsIgnoreCase("da")) {
                    Bitmap b = BitmapFactory.decodeResource(getResources(), ID_Fields[i].getInt(null));
                    b = b.createScaledBitmap(b,126,126,false);
                    resArray.add(Pair.create(pHash.calcPHash(b),ID_Fields[i].getInt(null)));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        super.onCreate();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn)
    {
        //Log.d(DEBUG_TAG, "Notification Posted: " + sbn.getPackageName());

        NotificationManager notificationManager;
        byte[] content;
        switch (sbn.getPackageName()) {
            case GOOGLE_MAPS:
                // initialize ble stuff here if not already initialized

                //no useful information in extras
                //Bundle extras = sbn.getNotification().extras;
                //Log.d(DEBUG_TAG, extras.toString());

                RemoteViews rv = sbn.getNotification().bigContentView;
                RelativeLayout rl = (RelativeLayout) rv.apply(getApplicationContext(), null);
                notificationManager = new GoogleMapsNotificationManager(rl, this, resArray);
                content=notificationManager.getContent();
                break;
            case GOOGLE_CALLER:
                notificationManager = new CallNotificationManager(sbn);
                content=notificationManager.getContent();
                break;
            case GOOGLE_SMS:
                notificationManager = new SMSNotificationManager(sbn);
                content=notificationManager.getContent();
                break;
            case SPOTIFY:
                notificationManager = new SpotifyMusicNotificationManager(sbn);
                content=notificationManager.getContent();
                break;
            default:

        }

        //TODO: package content with proper BLE Characteristic  and send with BLEService

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
