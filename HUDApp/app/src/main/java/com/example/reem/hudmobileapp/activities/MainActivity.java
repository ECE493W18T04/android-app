package com.example.reem.hudmobileapp.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.provider.Settings;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.reem.hudmobileapp.R;
import com.example.reem.hudmobileapp.ble.BLEService;

import com.example.reem.hudmobileapp.dialogs.ColorPickerDialog;
import com.example.reem.hudmobileapp.helper.FileManager;
import com.example.reem.hudmobileapp.notifications.WNHNotificationListener;

import java.io.File;


public class MainActivity extends AppCompatActivity {

    private Intent mServiceIntent;
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private AlertDialog enableNotificationListenerAlertDialog;
    private boolean activeMode =false;

    private Button navButton;
    private static final int COARSE_LOCATION_PERMISSIONS = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navButton = (Button) findViewById(R.id.navButton);
        System.out.println(navButton);

//        File file = new File(this.getFilesDir(), "mac.sav");
//        FileManager.saveMACAddress(this,"");
//        Log.e("The address is: ",FileManager.readMACAddress(this));

        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent tempIntent = new Intent(MainActivity.this,PreferencesActivity.class);
//                startActivity(tempIntent);
                if (activeMode == false)
                    startBluetoothService();
                else
                    stopBluetoothService();
//                Intent intent = new Intent(MainActivity.this,PriorityQueueActivity.class);
//                startActivity(intent);

//                int initialColor = Color.WHITE;
//
//                ColorPickerDialog colorPickerDialog = new ColorPickerDialog(MainActivity.this, initialColor, new ColorPickerDialog.OnColorSelectedListener() {
//
//                    @Override
//                    public void onColorSelected(int color) {
//                        showToast(color);
//                    }
//
//                });
//                colorPickerDialog.show();
            }
        });
//        if(!isNotificationServiceEnabled()){
//            enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog();
//            enableNotificationListenerAlertDialog.show();
//        }

        //Intent notificationIntent = new Intent(MainActivity.this, WNHNotificationListener.class);
        //startService(notificationIntent);
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
                        // If you choose to not enable the notification listener
                        // the app. will not work as expected
                    }
                });
        return(alertDialogBuilder.create());
    }
    private void showToast(int color) {
        String rgbString = "R: " + Color.red(color) + " B: " + Color.blue(color) + " G: " + Color.green(color);
        Toast.makeText(this, rgbString, Toast.LENGTH_SHORT).show();
    }
    public void startBluetoothService()
    {



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(getApplicationContext(), "Request for permissions", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    COARSE_LOCATION_PERMISSIONS);

        }
        else
        {
            System.out.println("About to call BLE service ");
            activeMode=true;
            mServiceIntent = new Intent(MainActivity.this, BLEService.class);
            startService(mServiceIntent);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case COARSE_LOCATION_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    System.out.println("About to call BLE service ");
                    activeMode=true;
                    mServiceIntent = new Intent(MainActivity.this, BLEService.class);
                    startService(mServiceIntent);

                } else {
                    Toast.makeText(getApplicationContext(), "Coarse location permisssions not granted", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}
