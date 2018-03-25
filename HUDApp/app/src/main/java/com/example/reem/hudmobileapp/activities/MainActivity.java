package com.example.reem.hudmobileapp.activities;

import android.Manifest;
import android.app.AlertDialog;

import android.app.Dialog;
import android.app.DialogFragment;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;

import android.provider.Settings;

import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.CheckBox;
import android.widget.ListView;

import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reem.hudmobileapp.R;
import com.example.reem.hudmobileapp.ble.BLEService;

import com.example.reem.hudmobileapp.constants.HUDObject;
import com.example.reem.hudmobileapp.constants.PreferencesEnum;
import com.example.reem.hudmobileapp.dialogs.BrightnessDialog;
import com.example.reem.hudmobileapp.dialogs.ColorPickerDialog;
import com.example.reem.hudmobileapp.helper.FileManager;
import com.example.reem.hudmobileapp.notifications.WNHNotificationListener;

import java.io.File;
import java.util.ArrayList;



public class MainActivity extends AppCompatActivity  implements BrightnessDialog.BrightnessDialogListener{

    private Intent mServiceIntent;
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private AlertDialog enableNotificationListenerAlertDialog;
    private boolean activeMode =false;
    private HUDObject hud;
    private Button navButton;
    private static final int COARSE_LOCATION_PERMISSIONS = 0;
    private static final int RECORD_AUDIO_PERMISSION = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navButton = (Button) findViewById(R.id.navButton);

        getHudItem();
        checkPreviousConnection();

        String[] options = new String[]{"Priority Queue","Brightness Control","HUD Color"};
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, R.layout.list_item, options);
        String[] restore = new String[]{"Restore Default HUD Settings"};
        ArrayAdapter<String> restoreAdaptor =
                new ArrayAdapter<String>(this, R.layout.list_item, restore);

        final ListView preferencesView = (ListView) findViewById(R.id.preferencesList);
        final ListView restoreView = (ListView) findViewById(R.id.restoreList);
        preferencesView.setAdapter(itemsAdapter);
        restoreView.setAdapter(restoreAdaptor);
        final Drawable mDrawable = this.getDrawable(android.R.drawable.ic_lock_power_off);


        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeMode == false)
                {
                    startBluetoothService();
//                    navButton.setText("Stop");
//                    navButton.setBackgroundColor(getResources().getColor(R.color.gray));
                    mDrawable.setColorFilter(new
                            PorterDuffColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.MULTIPLY));
                    navButton.setBackground(mDrawable);
                    activeMode = true;
                }
                else {
                    stopBluetoothService();
//                    navButton.setText("Activate");
                    mDrawable.setColorFilter(new
                            PorterDuffColorFilter(getResources().getColor(R.color.gray), PorterDuff.Mode.MULTIPLY));
                    navButton.setBackground(mDrawable);
                    activeMode=false;
                }

            }
        });
        preferencesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getItemClicked(parent,view,position,id);
            }
        });
        restoreView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                restore();
            }
        });
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, COARSE_LOCATION_PERMISSIONS);

        }



        if(!isNotificationServiceEnabled()){
            enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog();
            enableNotificationListenerAlertDialog.show();
        }

        //Intent notificationIntent = new Intent(MainActivity.this, WNHNotificationListener.class);
        //startService(notificationIntent);

    }

    public void getHudItem()
    {

        File hudFile = new File(this.getFilesDir(),"hud.sav");
        if (!hudFile.exists())
        {
            hud = new HUDObject();
            FileManager.saveToFile(this,hud);
        }else
        {
            hud = FileManager.loadFromFile(this);
        }
    }

    public void checkPreviousConnection()
    {
        File file = new File(this.getFilesDir(), "mac.sav");
        if (!file.exists())
        {
            FileManager.saveMACAddress(this,null);
        }
        ArrayList<String> value = FileManager.readMACAddress(this);
        Log.e("MACADDRESSFAILURE",value.toString());
    }

    public void restore()
    {
        HUDObject hudObject= new HUDObject();
        FileManager.saveToFile(this,hudObject);
        hud = hudObject;
    }

    public void getItemClicked(AdapterView<?> parent,View view, int position,long id)
    {
           if (position == PreferencesEnum.PRIORITY_QUEUE.getValue())
           {
                Intent intent = new Intent(MainActivity.this,PriorityQueueActivity.class);
                startActivity(intent);
           }else if (position == PreferencesEnum.COLOR_CONTROL.getValue())
           {
               int initialColor = Color.WHITE;

                ColorPickerDialog colorPickerDialog = new ColorPickerDialog(MainActivity.this, initialColor, new ColorPickerDialog.OnColorSelectedListener() {

                    @Override
                    public void onColorSelected(int color) {
                        saveColor(color);
                    }

                });
                colorPickerDialog.show();
                colorPickerDialog.getWindow().setBackgroundDrawableResource(R.color.darkGray);
           }else if (position == PreferencesEnum.BRIGHTNESS_CONTROL.getValue())
           {
               DialogFragment dialogFragment = new BrightnessDialog();
               dialogFragment.show(getFragmentManager(),"BrightnessDialog");
           }
    }

    public void stopBluetoothService()
    {

        stopService(mServiceIntent);

        activeMode=false;
    }
    /**
     * Is Notification Service Enabled.
     * Verifies if the notification listener service is enabled.
     * Got it from: https://github.com/kpbird/NotificationListenerService-Example/blob/master/NLSExample/src/main/java/com/kpbird/nlsexample/NLService.java
     * @return True if eanbled, false otherwise.
     */
    private boolean isNotificationServiceEnabled(){
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
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
    private AlertDialog buildNotificationServiceAlertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Notification Listener Service");
        alertDialogBuilder.setMessage("For the the app. to work you need to enable the Notification Listener Service. Enable it now?");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
                    }
                });
        alertDialogBuilder.setNegativeButton("no",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(MainActivity.this,"Notification Listening not granted. App will now shut down.",Toast.LENGTH_SHORT).show();
//                        finish();
                        // If you choose to not enable the notification listener
                        // the app. will not work as expected
                    }
                });
        return(alertDialogBuilder.create());
    }





    private void saveColor(int color) {
        HUDObject hudObject = FileManager.loadFromFile(this);
        float[] hsv = new float[3];
        Color.colorToHSV(color,hsv);
        int hue = Math.round(hsv[0]);
        int saturation = Math.round(hsv[1]);
        if (saturation >= 360 )
            saturation = 360;
        if (hue >= 100)
            hue = 100;
        hudObject.setHue(hue);
        hudObject.setSaturation(saturation);
        FileManager.saveToFile(this,hudObject);
        String rgbString = "R: " + Color.red(color) + " B: " + Color.blue(color) + " G: " + Color.green(color);
        Toast.makeText(this, rgbString, Toast.LENGTH_SHORT).show();
    }





    public void startBluetoothService()
    {

        System.out.println("About to call BLE service ");
        activeMode=true;
        mServiceIntent = new Intent(MainActivity.this, BLEService.class);
        startService(mServiceIntent);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case COARSE_LOCATION_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Permission is not granted
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},RECORD_AUDIO_PERMISSION);
                    }
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
//                    System.out.println("About to call BLE service ");
//                    activeMode=true;
//                    mServiceIntent = new Intent(MainActivity.this, BLEService.class);
//                    startService(mServiceIntent);

                } else {
                    Toast.makeText(getApplicationContext(), "Coarse location permisssions not granted. App will now shut down", Toast.LENGTH_SHORT).show();
                    finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case RECORD_AUDIO_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("RECORD AUDIO:", "Permission Granted");

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
            // this is for brightness control
        HUDObject hudObject = FileManager.loadFromFile(this);
        Dialog view = dialog.getDialog();
        SeekBar seekBar = (SeekBar)view.findViewById(R.id.brightnessSeekBar);
        CheckBox autoBrightness = (CheckBox) view.findViewById(R.id.autoBrightness);
        if (autoBrightness.isChecked())
        {
            hudObject.setAuto_brightness(true);
        }else
        {
            hudObject.setAuto_brightness(false);
        }
        Integer brightness = seekBar.getProgress();
        hudObject.setBrightness(brightness);
        FileManager.saveToFile(this,hudObject);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
}
