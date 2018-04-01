package com.example.reem.hudmobileapp.widget;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;
import android.widget.RemoteViews;

import com.example.reem.hudmobileapp.R;
import com.example.reem.hudmobileapp.VehicleMonitoringService;
import com.example.reem.hudmobileapp.activities.MainActivity;
import com.example.reem.hudmobileapp.ble.BLEService;
import com.example.reem.hudmobileapp.helper.FileManager;
import com.example.reem.hudmobileapp.notifications.WNHNotificationListener;
import com.openxc.VehicleManager;

import java.io.File;
import java.util.ArrayList;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by Reem on 2018-03-08.
 */

public class WNHWidgetProvider extends AppWidgetProvider {
    private static Intent bluetoothServiceIntent;
    private static Intent notificationListenerIntent;
    private static boolean initialized = false;
    private static boolean connectedToDevice = false;
    private final String TOGGLE = "toggle";
    BLEService bleService;


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] widgetIds) {

        final int count= widgetIds.length;
        for (int i=0;i<count;i++) {
            int widgetId = widgetIds[i];
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.wnh_widget);
            Intent listenerIntent = new Intent(context, WNHWidgetProvider.class);
            listenerIntent.setAction(TOGGLE);
            PendingIntent pendingListenerIntent = PendingIntent.getBroadcast(context,0,listenerIntent,0);

            views.setOnClickPendingIntent(R.id.actionButton,pendingListenerIntent);
            appWidgetManager.updateAppWidget(widgetId,views);
        }

    }

    @Override
    public void onReceive(Context context, Intent intent){

        if (intent.getAction().equalsIgnoreCase(TOGGLE)) {
            if (bleService == null) {
                //startBluetoothService();
            }

            if (!connectedToDevice) {
                Log.d("WNHWidget", " Starting Service");
                if (bleService!=null)
                    if (!bleService.initialize()){
                        return;
                    }
                bleService.connectToDevice();
            } else{
                //stopBluetoothService();
                if (initialized){
                    Log.d("WNHWidget", " Stopping Service");
                    bleService.disconnectFromDevice();
                }
            }
        }
        super.onReceive(context, intent);
    }

    public void startBluetoothService(Context context)
    {

        Log.d("BINDINGSERVICE", "Binding Bluetooth Service");
        if (bleService == null)
        {
            bluetoothServiceIntent = new Intent(context, BLEService.class);
            context.bindService(bluetoothServiceIntent, mConnection, BIND_AUTO_CREATE);
        }



    }

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("SERVICECONNECTION", "On Service Connected");

            BLEService.BLEBinder mBinder = (BLEService.BLEBinder) iBinder;
            bleService = mBinder.getService();
            initialized = true;
//            if (!bleService.initialize()){
//                Log.e("UNABLETOINITIALIZEBLE", "Unable to initialize Bluetooth");
//                initialized=false;
//                finish();
//            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("SERVICECONNECTION", "ON Service Disconnected");
            bleService = null;
//            initialized = false;
        }
    };

    public void checkPreviousConnection(Context context)
    {
        File file = new File(context.getFilesDir(), "mac.sav");
        if (!file.exists())
        {
            FileManager.saveMACAddress(context,null);
        }
        ArrayList<String> value = FileManager.readMACAddress(context);

        if (value==null){
            Log.e("MACADDRESSFAILURE","MAc address contains null");
        }
    }
/*
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BLEService.ACTION_GATT_CONNECTED.equals(action)) {
                connectedToDevice = true;
            } else if (BLEService.ACTION_GATT_DISCONNECTED.equals(action)) {
                connectedToDevice = false;
            } else if (BLEService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                ArrayList<String> addresses=FileManager.readMACAddress(context);
                if (addresses==null){
                    ArrayList<String> newList = new ArrayList<>();
                    newList.add(bleService.getDevice().getAddress());
                    FileManager.saveMACAddress(context,newList);
                    Log.e("MAKING A NEW LIST",newList.toString());
                }else{
                    if (!addresses.contains(bleService.getDevice().getAddress()))
                    {
                        Log.e("What it lookslikebefore",addresses.toString());
                        addresses.add(bleService.getDevice().getAddress());
                        FileManager.saveMACAddress(context,addresses);
                        Log.e("Continue with old list",addresses.toString());
                    }
                }
                Log.e("WORKED","yay it worked");
                vMonitor = new VehicleMonitoringService(bleService.getWriter());
                if(vMonitor.VehicleManager == null) {
                    Intent vehicleIntent = new Intent(context, VehicleManager.class);
                    bindService(vehicleIntent, vMonitor.connection, Context.BIND_AUTO_CREATE);
                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                if (bleService.getDevice().getBondState() == BluetoothDevice.BOND_BONDED) {
                    connectedToDevice = true;
                    Log.e("BONDER", "properly bonded");
                }else if (bleService.getDevice().getBondState() == BluetoothDevice.BOND_NONE){
                    connectedToDevice = false;
                }

            }else if (BLEService.ACTION_GATT_NO_DEVICE_FOUND.equals(action)) {
                connectedToDevice = false;
                Log.e("NODEVICE","no device was found");
            }
        }
    };
    */


}
