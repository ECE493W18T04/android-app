package com.example.reem.hudmobileapp.widget;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

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
    private static Intent vehicleMonitorIntent;
    private static boolean initialized = false;
    private static boolean connectedToDevice = false;
    private AlertDialog enableNotificationListenerAlertDialog;
    private ComponentName watchWidget;
    private RemoteViews views;

    private final String TOGGLE = "toggle";
    BLEService bleService;

    int testState;


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] widgetIds) {
        //context.getApplicationContext().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        testState = 0;
        final int count= widgetIds.length;
        for (int i=0;i<count;i++) {
            int widgetId = widgetIds[i];
            watchWidget = new ComponentName( context, WNHWidgetProvider.class );

            views = new RemoteViews(context.getPackageName(), R.layout.wnh_widget);
            Intent intent = new Intent(context, WNHWidgetProvider.class);
            intent.setAction(TOGGLE);
            intent.putExtra("appWidgetId", widgetId);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,intent,0);

            views.setOnClickPendingIntent(R.id.actionButton,pendingIntent);
            appWidgetManager.updateAppWidget(widgetId,views);
        }

    }

    @Override
    public void onReceive(Context context, Intent intent){

        if (intent.getAction().equalsIgnoreCase(TOGGLE)) {
            Log.d("Widget", "Button pressed");
            int appWidgetId = intent.getIntExtra("appWidgetId", -1);

            views = new RemoteViews( context.getPackageName(), R.layout.wnh_widget );
            if (testState == 0){
                views.setImageViewResource(R.id.actionButton, R.drawable.wnh_toggle_transition);
                testState = 1;

            }else if (testState == 1) {
                views.setImageViewResource(R.id.actionButton, R.drawable.wnh_toggle_on);
                testState = 2;
            }else {
                views.setImageViewIcon(R.id.actionButton, R.drawable.wnh_toggle_off);
                testState = 0;
            }
            /*
            RemoteViews views = new RemoteViews( context.getPackageName(), R.layout.wnh_widget);
            views.setImageViewResource(R.id.actionButton, R.drawable.wnh_toggle_transition);

            if(!isNotificationServiceEnabled(context)){
                enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog(context);
                enableNotificationListenerAlertDialog.show();
            }
            if (isMyServiceRunning(VehicleMonitoringService.class, context)) {
                vehicleMonitorIntent = new Intent(context, VehicleMonitoringService.class);
                context.startService(vehicleMonitorIntent);
            }
            if (bleService == null) {
                startBluetoothService(context);
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
            */
            (AppWidgetManager.getInstance(context)).updateAppWidget( appWidgetId, views );

        }


        super.onReceive(context, intent);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    /**
     * Is Notification Service Enabled.
     * Verifies if the notification listener service is enabled.
     * Got it from: https://github.com/kpbird/NotificationListenerService-Example/blob/master/NLSExample/src/main/java/com/kpbird/nlsexample/NLService.java
     * @return True if eanbled, false otherwise.
     */
    private boolean isNotificationServiceEnabled(Context context){
        String pkgName = context.getPackageName();
        final String flat = Settings.Secure.getString(context.getContentResolver(),
                "enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Build Notification Listener Alert Dialog.
     * Builds the alert dialog that pops up if the user has not turned
     * the Notification Listener Service on yet.
     * @return An alert dialog which leads to the notification enabling screen
     */
    private AlertDialog buildNotificationServiceAlertDialog(final Context context){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Notification Listener Service");
        alertDialogBuilder.setMessage("For the the app. to work you need to enable the Notification Listener Service. Enable it now?");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        context.startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                    }
                });
        alertDialogBuilder.setNegativeButton("no",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(context,"Notification Listening not granted. App will now shut down.",Toast.LENGTH_SHORT).show();
//                        finish();
                        // If you choose to not enable the notification listener
                        // the app. will not work as expected
                    }
                });
        return(alertDialogBuilder.create());
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
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("SERVICECONNECTION", "ON Service Disconnected");
            bleService = null;
        }
    };



    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BLEService.ACTION_GATT_CONNECTED.equals(action)) {
                connectedToDevice = true;
                RemoteViews views = new RemoteViews( context.getPackageName(), R.layout.wnh_widget);
                views.setImageViewResource(R.id.actionButton, R.drawable.wnh_toggle_on);
            } else if (BLEService.ACTION_GATT_DISCONNECTED.equals(action)) {
                connectedToDevice = false;
                RemoteViews views = new RemoteViews( context.getPackageName(), R.layout.wnh_widget);
                views.setImageViewResource(R.id.actionButton, R.drawable.wnh_toggle_off);
            } else if (BLEService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {

                Log.e("WORKED","yay it worked");

            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                if (bleService.getDevice().getBondState() == BluetoothDevice.BOND_BONDED) {
                    connectedToDevice = true;
                    RemoteViews views = new RemoteViews( context.getPackageName(), R.layout.wnh_widget);
                    views.setImageViewResource(R.id.actionButton, R.drawable.wnh_toggle_on);
                    Log.e("BONDER", "properly bonded");
                }else if (bleService.getDevice().getBondState() == BluetoothDevice.BOND_NONE){
                    connectedToDevice = false;
                    RemoteViews views = new RemoteViews( context.getPackageName(), R.layout.wnh_widget);
                    views.setImageViewResource(R.id.actionButton, R.drawable.wnh_toggle_off);

                }

            }else if (BLEService.ACTION_GATT_NO_DEVICE_FOUND.equals(action)) {
                connectedToDevice = false;
                RemoteViews views = new RemoteViews( context.getPackageName(), R.layout.wnh_widget);
                views.setImageViewResource(R.id.actionButton, R.drawable.wnh_toggle_off);
                Log.e("NODEVICE","no device was found");
            }
        }
    };

/*
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BLEService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BLEService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BLEService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BLEService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BLEService.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BLEService.ACTION_GATT_NO_DEVICE_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BLEService.CLOSE_DIALOG);
        return intentFilter;
    }
*/

}
