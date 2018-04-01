package com.example.reem.hudmobileapp.notifications;

import android.app.ActivityManager;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.util.Pair;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;

import com.example.reem.hudmobileapp.ble.BLEService;
import com.example.reem.hudmobileapp.constants.CharacteristicUUIDs;
import com.example.reem.hudmobileapp.helper.ImagePHash;
import com.example.reem.hudmobileapp.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Reem on 2018-03-09.
 */

public class WNHNotificationListener extends NotificationListenerService
{
    private final String DEBUG_TAG = this.getClass().getSimpleName();

    private static final String GOOGLE_MAPS = "com.google.android.apps.maps";
    private static final String GOOGLE_CALLER = "com.android.dialer";
    private static final String SAMSUNG_CALLER = "com.samsung.android.incallui";
    private static final String SAMSUNG_SMS = "com.samsung.android.messaging";
    private static final String GOOGLE_SMS = "com.google.android.apps.messaging";
    private static final String SPOTIFY = "com.spotify.music";
    private boolean bluetoothServiceConnected = false;
    private boolean Fuck =false;
    private ArrayList<Pair<Long, Integer>> resArray;
    BLEService bleService;
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
            bleService = mBinder.getService();
            bluetoothServiceConnected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(DEBUG_TAG, "ON Service Disconnected");
            bluetoothServiceConnected = false;
        }
    };
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d(DEBUG_TAG, "Notification Posted: " + sbn.getPackageName());

        NotificationManager notificationManager;
        byte[] content;

        if (isMyServiceRunning(BLEService.class) && !bluetoothServiceConnected){
            bluetoothServiceIntent = new Intent(this, BLEService.class);
            bindService(bluetoothServiceIntent, mConnection, BIND_AUTO_CREATE);
        }

        if(sbn.getPackageName().equalsIgnoreCase(GOOGLE_MAPS) && !sbn.isClearable()) {

            // initialize ble stuff here if not already initialized

            if (!bluetoothServiceConnected) {
                Log.d(DEBUG_TAG, "Binding Bluetooth Service");
                bluetoothServiceIntent = new Intent(this, BLEService.class);
                bindService(bluetoothServiceIntent, mConnection, BIND_AUTO_CREATE);
            }else if (bleService.isConnectedToDevice()) {
                RemoteViews rv = sbn.getNotification().bigContentView;
                RelativeLayout rl = (RelativeLayout) rv.apply(getApplicationContext(), null);
                notificationManager = new GoogleMapsNotificationManager(rl, this, resArray);
                content = notificationManager.getContent();
                bleService.getWriter().writeNavigationInfo(content);
            }

        }
        if (sbn.getPackageName().equals(GOOGLE_CALLER)||sbn.getPackageName().equalsIgnoreCase(SAMSUNG_CALLER)) {

            if (!bluetoothServiceConnected) { //Listener is not connecte to BLEService
                //do nothing
            }else if(bleService.isConnectedToDevice()) { //BLEService is connected to a device.
                notificationManager = new CallNotificationManager(sbn);
                content=notificationManager.getContent();
                bleService.getWriter().writeCallInfo(content);
            }


        }
        if (sbn.getPackageName().equalsIgnoreCase(GOOGLE_SMS)||sbn.getPackageName().equalsIgnoreCase(SAMSUNG_SMS)) {
            /* Not in scope of project*/
//                notificationManager = new SMSNotificationManager(sbn);
//                content=notificationManager.getContent();
        }
        if (sbn.getPackageName().equalsIgnoreCase(SPOTIFY)) {
            Log.d(DEBUG_TAG, "Spotify");

            if (!bluetoothServiceConnected) {
                //do nothing
            }else if (bleService.isConnectedToDevice()) {
                notificationManager = new SpotifyMusicNotificationManager(sbn);
                content = notificationManager.getContent();
                Log.d(DEBUG_TAG,"Writing Music");
                bleService.getWriter().writeMusicInfo(content);
            }

        }

        super.onNotificationPosted(sbn);
    }



    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){
        if(sbn.getPackageName().equalsIgnoreCase(SAMSUNG_CALLER)||sbn.getPackageName().equalsIgnoreCase(GOOGLE_CALLER)){
            if (!bluetoothServiceConnected) { //Listener is not connected to BLEService
                //do nothing
            }else if(bleService.isConnectedToDevice()) { //BLEService is connected to a device.
                bleService.getWriter().writeCallInfo(("\0").getBytes());
            }
        }
    }



    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(DEBUG_TAG, "Destroyed");
    }

}
