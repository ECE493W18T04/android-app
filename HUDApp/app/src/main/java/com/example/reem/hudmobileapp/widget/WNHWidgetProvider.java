package com.example.reem.hudmobileapp.widget;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.RemoteViews;

import com.example.reem.hudmobileapp.R;
import com.example.reem.hudmobileapp.ble.BLEService;
import com.example.reem.hudmobileapp.notifications.WNHNotificationListener;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by Reem on 2018-03-08.
 */

public class WNHWidgetProvider extends AppWidgetProvider {
    private static Intent bluetoothServiceIntent;
    private static Intent notificationListenerIntent;
    private static boolean serviceRunning = false;
    private final String TOGGLE = "toggle";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] widgetIds)
    {

        final int count= widgetIds.length;
        for (int i=0;i<count;i++)
        {
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
            bluetoothServiceIntent = new Intent(context, BLEService.class);
            notificationListenerIntent = new Intent(context, WNHNotificationListener.class);
            if (isMyServiceRunning(BLEService.class, context)){
                Log.d("WNHWidget", "isMyServiceRunning=True" );
            }else {
                Log.d("WNHWidget", "isMyServiceRunning=False" );
            }
            Log.d("WNHWidget", "Button clicked");

            if (serviceRunning) {
                Log.d("WNHWidget", " Stopping Service");
                context.stopService(notificationListenerIntent);
                context.stopService(bluetoothServiceIntent);


            }else {
                Log.d("WNHWidget", " Starting Service");

                context.startService(bluetoothServiceIntent);

            }
            serviceRunning = !serviceRunning;
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


}
