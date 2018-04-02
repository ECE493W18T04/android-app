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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
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
//
//        Log.d(DEBUG_TAG,"DirectionDrawable: "+context.getResources().getResourceEntryName(correspondingDrawable));
//        Log.d(DEBUG_TAG, "Distance: "+distance.getText().toString());
//        Log.d(DEBUG_TAG, "StreetNumber: "+StreetName.getText().toString());

        //TODO: Combine direction and Distance and pack into byte[]
        //Maps_street, Maps_distance, maps_direction, Maps_unit



        return PackGoogleMapsData(distance.getText().toString(), StreetName.getText().toString(),context.getResources().getResourceEntryName(correspondingDrawable));
    }

    // Function taken from https://stackoverflow.com/questions/3035692/how-to-convert-a-drawable-to-a-bitmap
    private  Bitmap drawableToBitmap (Drawable drawable) {
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

    private byte[] PackGoogleMapsData(String distance, String streetName, String direction) {
        Log.d(DEBUG_TAG,"DirectionDrawable: "+direction);
        Log.d(DEBUG_TAG, "Distance: "+distance);
        Log.d(DEBUG_TAG, "StreetNumber: "+streetName);

        byte[] rawStreetName = (streetName+"\0").getBytes();
        ByteBuffer.wrap(rawStreetName).order(ByteOrder.LITTLE_ENDIAN);
        byte[] rawDistance = new byte[4];
        int unit = 0;
        int dir = 0;
        if (distance.length() > 1) {
            /* DISTANCE VALUE */
            String distanceValue = distance.split("\\s")[0];
            int dist = (int) Math.round(Double.parseDouble(distanceValue)*10);
            ByteBuffer.wrap(rawDistance).order(ByteOrder.LITTLE_ENDIAN).putInt(dist);

            /* DISTANCE UNIT */
            String distanceUnit = distance.split("\\s")[1];
            switch (distanceUnit.toLowerCase()) {
                case "km":
                    unit = 0;
                    break;
                case "m":
                    unit = 1;
                    break;
                case "mi":
                    unit = 2;
                    break;
                case "yd":
                    unit = 3;
                    break;
                default:
                    unit = 5;
                    break;
            }
            dir = ParseDirection(direction);
        }
        byte dirDistU = CombineDirectionAndDistanceUnits(dir, unit);
        byte[] fullContent = new byte[5+rawStreetName.length];

        fullContent[0] = dirDistU;
        fullContent[1] = rawDistance[0];
        fullContent[2] = rawDistance[1];
        fullContent[3] = rawDistance[2];
        fullContent[4] = rawDistance[3];
        for(int i=5;i<5+rawStreetName.length;i++){
            fullContent[i] = rawStreetName[i-5];
        }
        return fullContent;
    }

    private byte CombineDirectionAndDistanceUnits(int direction, int distanceUnit){
        byte[] unit = new byte[4];
        Log.d(DEBUG_TAG, "direction: "+Integer.toBinaryString(direction));
        Log.d(DEBUG_TAG, "distanceUnit: "+Integer.toBinaryString(distanceUnit));
        direction = direction << 4;
        int combined = direction | distanceUnit;
        Log.d(DEBUG_TAG, "Combined: "+Integer.toBinaryString(combined));
        //ByteBuffer.wrap(unit).order(ByteOrder.LITTLE_ENDIAN).putInt(distanceUnit);
        ByteBuffer.wrap(unit).order(ByteOrder.LITTLE_ENDIAN).putInt(combined);
        Log.d(DEBUG_TAG,"Byte Value: "+unit[0]);
        return unit[0];
    }


    private int ParseDirection(String direction){
        /* DIRECTION ARROW */
        int dir = 0;
        if (direction.matches(
                "da_turn_uturn_right_white" +
                        "||da_turn_roundabout_8_right_white")){
            Log.d(DEBUG_TAG,"Direction Uturn for rightside road");
            dir = 1;
        }else  if (direction.matches(
                "da_turn_roundabout_1_left_white" +
                        "||da_turn_roundabout_7_right_white" +
                        "||da_turn_sharp_left_white")) {
            Log.d(DEBUG_TAG,"Direction Sharp Left");
            dir = 2;
        }else if (direction.matches(
                "da_turn_left_white" +
                        "||da_turn_roundabout_2_left_white" +
                        "||da_turn_roundabout_6_right_white")){
            Log.d(DEBUG_TAG,"Direction Left");
            dir  = 3;
        }else if (direction.matches(
                "da_turn_fork_left_white" +
                        "||da_turn_ramp_left_white" +
                        "||da_turn_roundabout_3_left_white" +
                        "||da_turn_roundabout_5_right_white" +
                        "||da_turn_slight_left_white")){
            Log.d(DEBUG_TAG,"Direction Slight Left");
            dir = 4;
        }else if (direction.matches(
                "da_turn_depart_white" +
                        "||da_turn_roundabout_4_right_white" +
                        "||da_turn_roundabout_4_left_white" +
                        "||da_turn_straight_white" +
                        "||da_turn_roundabout_exit_right_white" +
                        "||da_turn_roundabout_exit_left_white" +
                        "||da_turn_generic_merge_right") ){
            Log.d(DEBUG_TAG, "Direction Straight");
            dir = 5;

        }else if (direction.matches(
                "da_turn_fork_right_white" +
                        "||da_turn_ramp_right_white" +
                        "||da_turn_roundabout_3_right_white" +
                        "||da_turn_roundabout_5_left_white" +
                        "||da_turn_slight_right_white")){
            Log.d(DEBUG_TAG,"Direction Slight Rught");
            dir = 6;
        }else if(direction.matches(
                "da_turn_right_white" +
                        "||da_turn_roundabout_2_right_white" +
                        "||da_turn_roundabout_6_left_white" +
                        "||")) {
            Log.d(DEBUG_TAG, "Direction Right");
            dir = 7;

        }else if (direction.matches(
                "da_turn_roundabout_1_right_white" +
                        "||da_turn_roundabout_7_left_white" +
                        "||da_turn_sharp_right_white")){
            Log.d(DEBUG_TAG,"Direction Sharp Right");
            dir = 8;
        }else if (direction.matches(
                "da_turn_uturn_right_white" +
                        "||da_turn_roundabout_8_right_white")) {
            Log.d(DEBUG_TAG,"Direction Uturn");
            dir = 9;

        }
        return dir;
    }


}
