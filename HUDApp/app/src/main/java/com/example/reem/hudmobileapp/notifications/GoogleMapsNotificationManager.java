package com.example.reem.hudmobileapp.notifications;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.service.notification.NotificationListenerService;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.example.reem.hudmobileapp.activities.MainActivity;

/**
 * Created by Reem on 2018-03-09.
 */

public class GoogleMapsNotificationManager extends NotificationManager {
    private final String DEBUG_TAG = this.getClass().getSimpleName();
    private RelativeLayout layout;

    public GoogleMapsNotificationManager(RelativeLayout rl) {
        layout = rl;
    }
    @Override
    public byte[] getContent() {
        parseNotification(layout);
        return new byte[0];
    }
    private void parseNotification(RelativeLayout rl) {

        ImageView turnArrow = (ImageView) rl.getChildAt(0);

        //Log.d(DEBUG_TAG, Integer.toHexString(turnArrow.hashCode()));
        //Log.d(DEBUG_TAG, Integer.toHexString(turnArrow.getDrawable().getConstantState().hashCode()));

        LinearLayout ll = (LinearLayout) ((LinearLayout) rl.getChildAt(1)).getChildAt(0);
        TextView distance = (TextView) ll.getChildAt(0);
        //
        TextView direction = (TextView) ll.getChildAt(1);
        TextView eta = (TextView) ll.getChildAt(2);

        Log.d(DEBUG_TAG, "Distance: "+distance.getText().toString());
        Log.d(DEBUG_TAG, "Directions: "+direction.getText().toString());
        Log.d(DEBUG_TAG, "ETA: "+eta.getText().toString());
    }
}
