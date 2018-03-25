package com.example.reem.hudmobileapp.notifications;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Pair;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.reem.hudmobileapp.activities.MainActivity;
import com.example.reem.hudmobileapp.ble.BLEService;
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
    private boolean connected = false;
    private boolean Fuck =false;
    private ArrayList<Pair<Long, Integer>> resArray;
    BLEService mService;
    Intent bluetoothServiceIntent;


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

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(DEBUG_TAG, "On Service Connected");

            BLEService.BLEBinder mBinder = (BLEService.BLEBinder) iBinder;
            mService = mBinder.getService();
            connected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(DEBUG_TAG, "ON Service Disconnected");
            connected = false;
        }
    };
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        //Log.d(DEBUG_TAG, "Notification Posted: " + sbn.getPackageName());

        NotificationManager notificationManager;
        byte[] content;
        switch (sbn.getPackageName()) {
            case GOOGLE_MAPS:
                if(!sbn.isClearable()) {
                    // initialize ble stuff here if not already initialized

                    if (!Fuck) {
                        //TODO Start BLE Service
                        Log.d(DEBUG_TAG, "Binding Bluetooth Service");
                        bluetoothServiceIntent = new Intent(this, BLEService.class);
                        bindService(bluetoothServiceIntent, mConnection, BIND_AUTO_CREATE);
                        Fuck = true;
                    }

                    //Log.d(DEBUG_TAG, "mservice initialized");
                    //no useful information in extras
                    //Bundle extras = sbn.getNotification().extras;
                    //Log.d(DEBUG_TAG, extras.toString());
                    if(connected) {
                        RemoteViews rv = sbn.getNotification().bigContentView;
                        RelativeLayout rl = (RelativeLayout) rv.apply(getApplicationContext(), null);
                        notificationManager = new GoogleMapsNotificationManager(rl, this, resArray);
                        content = notificationManager.getContent();

                    }

                }
                break;
            case GOOGLE_CALLER:
                /*
                if (isMyServiceRunning(BLEService.class)) {
                    Intent BLEIntent = new Intent(this, BLEService.class);
                    bindService(BLEIntent, mConnection, BIND_AUTO_CREATE);

                    notificationManager = new CallNotificationManager(sbn);
                    content=notificationManager.getContent();
                }

*/
                break;
            case GOOGLE_SMS:
                /*
                if (isMyServiceRunning(BLEService.class)) {
                    bluetoothServiceIntent = new Intent(this, BLEService.class);
                    bindService(BLEIntent, mConnection, BIND_AUTO_CREATE);
                    notificationManager = new SMSNotificationManager(sbn);
                    content=notificationManager.getContent();
                }
*/
                break;
            case SPOTIFY:
                /*
                if(isMyServiceRunning(BLEService.class)) {
                    notificationManager = new SpotifyMusicNotificationManager(sbn);
                    content = notificationManager.getContent();
                }
                */
                break;

            default:
                break;
        }
        //TODO: package content with proper BLE Characteristic  and send with BLEService

        super.onNotificationPosted(sbn);
    }

    public void startBluetoothService()
    {
        System.out.println("About to call BLE service ");
        bluetoothServiceIntent = new Intent(this, BLEService.class);
        startService(bluetoothServiceIntent);
    }

    public void stopBluetoothService()
    {
        stopService(bluetoothServiceIntent);
    }


    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){
        if(sbn.getPackageName().equalsIgnoreCase(GOOGLE_MAPS)){
            Log.d(DEBUG_TAG, "Google Maps notification disabled");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }
}
