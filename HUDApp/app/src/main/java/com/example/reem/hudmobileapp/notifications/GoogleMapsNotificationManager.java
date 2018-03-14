package com.example.reem.hudmobileapp.notifications;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.service.notification.NotificationListenerService;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.example.reem.hudmobileapp.R;
import com.example.reem.hudmobileapp.activities.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Reem on 2018-03-09.
 */

public class GoogleMapsNotificationManager extends NotificationManager {
    private final String DEBUG_TAG = this.getClass().getSimpleName();
    private RelativeLayout layout;
    private Bitmap bMap;

    public GoogleMapsNotificationManager(RelativeLayout rl) {
        layout = rl;
    }

    @Override
    public byte[] getContent() {
<<<<<<< HEAD

        return new byte[0];
=======
        parseNotification(layout);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bMap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    private void parseNotification(RelativeLayout rl) {

        ImageView turnArrow = (ImageView) rl.getChildAt(0);
        //Log.d(DEBUG_TAG, Integer.toHexString(turnArrow.hashCode()));
        //Log.d(DEBUG_TAG, Integer.toHexString(turnArrow.getDrawable().getConstantState().hashCode()));
        BitmapDrawable bitmapDrawable = ((BitmapDrawable) turnArrow.getDrawable());
        bMap = bitmapDrawable.getBitmap();

        LinearLayout ll = (LinearLayout) ((LinearLayout) rl.getChildAt(1)).getChildAt(0);
        TextView distance = (TextView) ll.getChildAt(0);

        TextView eta = (TextView) ll.getChildAt(1);
        TextView StreetName = (TextView) ll.getChildAt(2);

        Log.d(DEBUG_TAG, "Distance: "+distance.getText().toString());
        Log.d(DEBUG_TAG, "ETA: "+eta.getText().toString());
        Log.d(DEBUG_TAG, "StreetNumber: "+StreetName.getText().toString());
>>>>>>> eefd6fe45e6370f560ed358cef9aa8835c489c70
    }


}
