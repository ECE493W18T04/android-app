package com.example.reem.hudmobileapp.notifications;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.reem.hudmobileapp.helper.ImagePHash;

import java.util.ArrayList;


/**
 * Created by Reem on 2018-03-09.
 */

public class GoogleMapsNotificationManager extends NotificationManager {
    private final String DEBUG_TAG = this.getClass().getSimpleName();
    private RelativeLayout layout;

    private Context context;
    private ArrayList<Pair<Long, Integer>> resourceArray;

    public GoogleMapsNotificationManager(RelativeLayout layout, Context context, ArrayList<Pair<Long, Integer>> array) {
        this.context = context;
        this.layout = layout;
        resourceArray = array;
    }

    @Override
    public byte[] getContent() {
        ImageView turnArrow = (ImageView) layout.getChildAt(0);
        LinearLayout ll = (LinearLayout) ((LinearLayout) layout.getChildAt(1)).getChildAt(0);
        TextView distance = (TextView) ll.getChildAt(0);
        TextView StreetName = (TextView) ll.getChildAt(1);
        //TextView eta = (TextView) ll.getChildAt(2);

        Bitmap scrappedImage = drawableToBitmap(turnArrow.getDrawable());

        long imageHash = new ImagePHash().calcPHash(scrappedImage);

        long closestMatch = Long.MAX_VALUE;
        int correspondingDrawable = 0;
        for (Pair direction: resourceArray) {
            long match = ImagePHash.distance((long)direction.first,imageHash);
            if(closestMatch > match) {
                closestMatch = match;
                correspondingDrawable = (int) direction.second;
            }
        }

        Log.d(DEBUG_TAG,"DirectionDrawable: "+context.getResources().getResourceEntryName(correspondingDrawable));
        Log.d(DEBUG_TAG, "Distance: "+distance.getText().toString());
        Log.d(DEBUG_TAG, "StreetNumber: "+StreetName.getText().toString());

        //TODO: Combine direction and Distance and pack into byte[]
        //Maps_street, Maps_distance, maps_direction, Maps_unit
        return new byte[0];
    }

    // Function taken from https://stackoverflow.com/questions/3035692/how-to-convert-a-drawable-to-a-bitmap
    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

}
