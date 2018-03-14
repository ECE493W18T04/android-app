package com.example.reem.hudmobileapp.notifications;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

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
        parseNotification(layout);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bMap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    private void parseNotification(RelativeLayout rl) {

        ImageView turnArrow = (ImageView) rl.getChildAt(0);
        //String turnString = turnArrow.getDrawable().toString();
        //Log.d(DEBUG_TAG,turnString);

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
    }


}
