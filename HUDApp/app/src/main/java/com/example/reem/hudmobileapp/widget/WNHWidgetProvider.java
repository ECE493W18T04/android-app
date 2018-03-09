package com.example.reem.hudmobileapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.reem.hudmobileapp.R;
import com.example.reem.hudmobileapp.ble.BLEService;

/**
 * Created by Reem on 2018-03-08.
 */

public class WNHWidgetProvider extends AppWidgetProvider {


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] widgetIds)
    {
        final int count= widgetIds.length;
        for (int i=0;i<count;i++)
        {
            int widgetId = widgetIds[i];

            Intent intent = new Intent(context, BLEService.class);
            PendingIntent pendingIntent = PendingIntent.getService(context,0,intent,0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.wnh_widget);
            views.setOnClickPendingIntent(R.id.actionButton,pendingIntent);

            appWidgetManager.updateAppWidget(widgetId,views);
        }

    }

}
