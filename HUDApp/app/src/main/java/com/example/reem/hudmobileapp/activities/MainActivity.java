package com.example.reem.hudmobileapp.activities;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;

import android.app.Dialog;
import android.app.DialogFragment;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;

import android.os.IBinder;
import android.provider.Settings;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reem.hudmobileapp.R;
import com.example.reem.hudmobileapp.VehicleMonitoringService;
import com.example.reem.hudmobileapp.ble.BLEService;

import com.example.reem.hudmobileapp.constants.HUDObject;
import com.example.reem.hudmobileapp.constants.PreferencesEnum;
import com.example.reem.hudmobileapp.dialogs.BrightnessDialog;
import com.example.reem.hudmobileapp.dialogs.ColorPickerDialog;
import com.example.reem.hudmobileapp.dialogs.MaxCurrentDialog;
import com.example.reem.hudmobileapp.helper.FileManager;
import com.example.reem.hudmobileapp.helper.VoiceCommandManager;
import com.example.reem.hudmobileapp.notifications.WNHNotificationListener;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Main Activity of the screen. The main activity will contain features for brightness control
 * , priotity queue brightness and control control. This is required to meet requirments:
 * REQ-A-4.5.3.2 - HUD brightness control
 * REQ-A-4.5.3.3 - Priority Queue
 * REQ-A-4.5.3.4 - HUD color control
 */
public class MainActivity extends AppCompatActivity implements BrightnessDialog.BrightnessDialogListener, MaxCurrentDialog.MaxCurrentDialogListener{

//    private Intent mServiceIntent;
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private AlertDialog enableNotificationListenerAlertDialog;
    private HUDObject hud;
    private Switch navButton;
    private static final int COARSE_LOCATION_PERMISSIONS = 0;
    private static final int RECORD_AUDIO_PERMISSION = 1;
    private Intent vehicleMonitorIntent;
    private ListView preferencesView;
    private ListView restoreView;
    BLEService bleService;
    Intent bluetoothServiceIntent;

    private boolean connectedToDevice;
    private boolean initialized = false;
    ProgressDialog dialog = null;
    private String[] options;
    private String[]  restore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navButton = (Switch) findViewById(R.id.toggle);

        options = new String[]{"Priority Queue","Brightness Control","HUD Color", "Maximum Current"};
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, R.layout.list_item, options);
        restore = new String[]{"Restore Default HUD Settings"};
        ArrayAdapter<String> restoreAdaptor =
                new ArrayAdapter<String>(this, R.layout.list_item, restore);

        preferencesView = (ListView) findViewById(R.id.preferencesList);
        restoreView = (ListView) findViewById(R.id.restoreList);
        preferencesView.setAdapter(itemsAdapter);
        restoreView.setAdapter(restoreAdaptor);
        //mDrawable = this.getDrawable(android.R.drawable.ic_lock_power_off);


        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (navButton.isChecked()){
                    if (bleService!=null)
                        if (!bleService.initialize()){
                            bleNotInitializedDialog();
                            return;
                        }
                    createLoadingDialog();
                    bleService.connectToDevice();
                }else{
                    if (initialized){
                        bleService.disconnectFromDevice();
                    }
                }
            }
        } );

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

        Log.e("servicecheck","about to check if service running");



    }

    public void bleNotInitializedDialog()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this,R.style.Theme_AlertDialog).create();
        alertDialog.setTitle("Unable to connect");
        alertDialog.setMessage("Please make sure Bluetooth is turned on.");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    public void onStart()
    {
        super.onStart();

        getHudItem();
        View view = findViewById(R.id.rectangle_at_the_top);
        view.setBackgroundColor(Color.HSVToColor(getColor()));
        checkPreviousConnection();
        TextView brightness = (TextView)findViewById(R.id.brightness_text);
        if (hud.isAuto_brightness())
            brightness.setText("Auto");
        else
            brightness.setText(hud.getBrightness()+"%");
        TextView maxCurrentView = (TextView)findViewById(R.id.maxCurrentText);
        maxCurrentView.setText(hud.getCurrent()+"mA");
        startBluetoothService();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        updateConnectionState();

        if(!isNotificationServiceEnabled()){
            enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog();
            enableNotificationListenerAlertDialog.show();
        }
        Log.d("MainActivity", "Starting Vehicle Monitor");
        vehicleMonitorIntent = new Intent(this, VehicleMonitoringService.class);
        startService(vehicleMonitorIntent);

    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (bleService!=null)
        {
            if (bleService.isConnectedToDevice()){
                connectedToDevice = true;
            }else{
                connectedToDevice = false;
            }
            updateConnectionState();
        }

    }


    public void createLoadingDialog()
    {
        dialog=new ProgressDialog(MainActivity.this,R.style.ProgressDialog);
        dialog.setMessage("Scanning for WNH Device");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();
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

        if (value==null){
            Log.e("MACADDRESSFAILURE","MAc address contains null");
        }
    }

    public void restore()
    {

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this,R.style.Theme_AlertDialog).create();
        alertDialog.setTitle("Restore");
        alertDialog.setMessage("Are you sure you want to restore to default settings?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                HUDObject hudObject= new HUDObject();
                FileManager.saveToFile(MainActivity.this,hudObject);
                hud = hudObject;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        HUDObject hudObject = FileManager.loadFromFile(MainActivity.this);
                        TextView maxCurrentView = (TextView)findViewById(R.id.maxCurrentText);
                        maxCurrentView.setText(hudObject.getCurrent()+"mA");
                        TextView brightnessview = (TextView)findViewById(R.id.brightness_text);
                        if (hudObject.isAuto_brightness()){
                            brightnessview.setText("Auto");
                        }else{
                            brightnessview.setText(hudObject.getBrightness()+"%");
                        }
                        View view = (View)findViewById(R.id.rectangle_at_the_top);
                        view.setBackgroundColor(Color.HSVToColor(getColor()));
                    }
                });
                dialog.dismiss();
            }

        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                });
        alertDialog.show();

    }

    public void getItemClicked(AdapterView<?> parent,View view, int position,long id)
    {
           if (position == PreferencesEnum.PRIORITY_QUEUE.getValue())
           {
                Intent intent = new Intent(MainActivity.this,PriorityQueueActivity.class);
                startActivity(intent);
           }else if (position == PreferencesEnum.COLOR_CONTROL.getValue())
           {

               int  color = Color.HSVToColor(getColor());
               String rgbString = "Color Saved: "+"R: " + Color.red(color) + " B: " + Color.blue(color) + " G: " + Color.green(color);
//               Toast.makeText(this, rgbString, Toast.LENGTH_SHORT).show();
               int initialColor = color;

                ColorPickerDialog colorPickerDialog = new ColorPickerDialog(MainActivity.this, initialColor, new ColorPickerDialog.OnColorSelectedListener() {

                    @Override
                    public void onColorSelected(int color) {
                        saveColor(color);
                        View view = (View)findViewById(R.id.rectangle_at_the_top);
                        view.setBackgroundColor(color);
                    }

                });
                colorPickerDialog.show();
                colorPickerDialog.getWindow().setBackgroundDrawableResource(R.color.darkGray);
           }else if (position == PreferencesEnum.BRIGHTNESS_CONTROL.getValue())
           {


               HUDObject hudObject = FileManager.loadFromFile(this);
               int brightness = hudObject.getBrightness();
               boolean autoBrightness = hudObject.isAuto_brightness();
               Bundle bundle = new Bundle();
               bundle.putBoolean("autobrighness",autoBrightness);
               bundle.putInt("brightnessLevel",brightness);

               DialogFragment dialogFragment = new BrightnessDialog();
               dialogFragment.setArguments(bundle);
               dialogFragment.show(getFragmentManager(),"BrightnessDialog");

           }else if (position == PreferencesEnum.MAX_CURRENT.getValue())
           {
               HUDObject hudObject = FileManager.loadFromFile(this);
               int maxCurrent = hudObject.getCurrent();
               Bundle bundle = new Bundle();
               bundle.putInt("maxLevel",maxCurrent);
               DialogFragment dialogFragment = new MaxCurrentDialog();
               dialogFragment.setArguments(bundle);
               dialogFragment.show(getFragmentManager(),"MaxCurrentDialog");

           }
    }

    public float[] getColor(){
        HUDObject hudObject = FileManager.loadFromFile(this);
        float hue=hudObject.getHue();
        DecimalFormat df = new DecimalFormat(".00");
        float saturation = hudObject.getSaturation();
        saturation = Float.parseFloat(df.format(saturation/100));

        float brightness = hudObject.getHsvBrightness();

        float[] hsv = {hue,saturation,brightness};
        return hsv;
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




    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("SERVICECONNECTION", "On Service Connected");

            BLEService.BLEBinder mBinder = (BLEService.BLEBinder) iBinder;
            bleService = mBinder.getService();
            updateConnectionState();
            initialized = true;
//            if (!bleService.initialize()){
//                Log.e("UNABLETOINITIALIZEBLE", "Unable to initialize Bluetooth");
//                initialized=false;
//                finish();
//            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("SERVICECONNECTION", "ON Service Disconnected");
            bleService = null;
//            initialized = false;
        }
    };



    private void saveColor(int color) {
        HUDObject hudObject = FileManager.loadFromFile(this);
        float[] hsv = new float[3];
        Color.colorToHSV(color,hsv);
        float hue = hsv[0];
        float saturation = hsv[1]*100;
        float value = hsv[2];
        if (saturation >= 100 )
            saturation = 100;
//        hue = 360-hue;
        if (hue >= 360)
            hue = 360;
        hudObject.setHue(hue);
        hudObject.setSaturation(saturation);
        hudObject.setHsvBrightness(value);
        FileManager.saveToFile(this,hudObject);
        String rgbString = "R: " + Color.red(color) + " B: " + Color.blue(color) + " G: " + Color.green(color)+ "Hue: "+hue+" Saturation: "+saturation+" Brightness: "+value;
//        Toast.makeText(this, rgbString, Toast.LENGTH_SHORT).show();
    }





    public void startBluetoothService()
    {

        Log.d("BINDINGSERVICE", "Binding Bluetooth Service");
        if (bleService == null)
        {
            bluetoothServiceIntent = new Intent(this, BLEService.class);
            bindService(bluetoothServiceIntent, mConnection, BIND_AUTO_CREATE);
        }



    }


    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BLEService.ACTION_GATT_CONNECTED.equals(action)) {
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();//                if (dialog.isShowing())
//                    dialog.hide();
                connectedToDevice = true;

                updateConnectionState();
                invalidateOptionsMenu();
            } else if (BLEService.ACTION_GATT_DISCONNECTED.equals(action)) {
                connectedToDevice = false;
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
                updateConnectionState();

            } else if (BLEService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // need to stop the loading
                if (dialog!= null && dialog.isShowing())
                    dialog.dismiss();
                ArrayList<String> addresses=FileManager.readMACAddress(MainActivity.this);

                    if (addresses==null){
                        ArrayList<String> newList = new ArrayList<String>();
                        newList.add(bleService.getDevice().getAddress());
                        FileManager.saveMACAddress(MainActivity.this,newList);
                        Log.e("MAKING A NEW LIST",newList.toString());
                    }else{
                        if (!addresses.contains(bleService.getDevice().getAddress()))
                        {
                            Log.e("What it lookslikebefore",addresses.toString());
                            addresses.add(bleService.getDevice().getAddress());
                            FileManager.saveMACAddress(MainActivity.this,addresses);
                            Log.e("Continue with old list",addresses.toString());
                        }


                }

                Log.e("WORKED","yay it worked");
                if (bleService.getDevice().getBondState() == BluetoothDevice.BOND_BONDED)
                {

                }


            }  else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action))
            {
                if (bleService.getDevice().getBondState() == BluetoothDevice.BOND_BONDED) {
                    {
                        connectedToDevice = true;
                        updateConnectionState();
                        Log.e("BONDER", "properly bonded");
                        try {
                            bleService.initialWriteCharacteristics();

                            if (dialog != null && dialog.isShowing())
                                dialog.dismiss();

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }else if (bleService.getDevice().getBondState() == BluetoothDevice.BOND_NONE){
                    connectedToDevice = false;
                    if ( dialog!= null && dialog.isShowing())
                        dialog.dismiss();
                    updateConnectionState();
                }
                // need to stop the loading page
                // get not create pairing bond
            }else if (BLEService.ACTION_GATT_NO_DEVICE_FOUND.equals(action))
            {
                connectedToDevice = false;
                if (dialog!= null && dialog.isShowing())
                    dialog.dismiss();
                updateConnectionState();
                Log.e("NODEVICE","no device was found");
                // need to stop the loading page
            }else if (BLEService.CLOSE_DIALOG.equals(action)){
                if (dialog!= null && dialog.isShowing())
                    dialog.dismiss();
            }else if (VoiceCommandManager.VOICE_COMMAND_UPDATE.equals(action))
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        HUDObject hudObject = FileManager.loadFromFile(MainActivity.this);
                        TextView maxCurrentView = (TextView)findViewById(R.id.maxCurrentText);
                        maxCurrentView.setText(hudObject.getCurrent()+"mA");
                        TextView brightnessview = (TextView)findViewById(R.id.brightness_text);
                        if (hudObject.isAuto_brightness()){
                            brightnessview.setText("Auto");
                        }else{
                            brightnessview.setText(hudObject.getBrightness()+"%");
                        }
                        View view = (View)findViewById(R.id.rectangle_at_the_top);
                        view.setBackgroundColor(Color.HSVToColor(getColor()));
                    }
                });
            }
        }
    };



    private void updateConnectionState() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (connectedToDevice) {
                    navButton.setChecked(true);
                    ArrayAdapter<String> itemsAdapter =
                            new ArrayAdapter<String>(MainActivity.this, R.layout.list_item, options){
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent){
                                    View view = super.getView(position, convertView, parent);
                                    TextView tv = (TextView) view.findViewById(R.id.list_item);
                                    tv.setTextColor(Color.GRAY);
                                    return view;
                                }
                            };
                    ArrayAdapter<String> restoreAdaptor =
                            new ArrayAdapter<String>(MainActivity.this, R.layout.list_item, restore){
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent){
                                    View view = super.getView(position, convertView, parent);
                                    TextView tv = (TextView) view.findViewById(R.id.list_item);
                                    tv.setTextColor(Color.GRAY);
                                    return view;
                                }
                            };
                    preferencesView.setAdapter(itemsAdapter);
                    restoreView.setAdapter(restoreAdaptor);
                    preferencesView.setOnItemClickListener(null);
                    restoreView.setOnItemClickListener(null);


                }
                else{
                    navButton.setChecked(false);
                    ArrayAdapter<String> itemsAdapter =
                            new ArrayAdapter<String>(MainActivity.this, R.layout.list_item, options);
                    ArrayAdapter<String> restoreAdaptor =
                            new ArrayAdapter<String>(MainActivity.this, R.layout.list_item, restore);
                    preferencesView.setAdapter(itemsAdapter);
                    restoreView.setAdapter(restoreAdaptor);
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
                }

            }
        });
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

                } else {
                    Toast.makeText(getApplicationContext(), "Coarse location permisssions not granted. App will now shut down", Toast.LENGTH_SHORT).show();
                    finish();
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
        TextView brightnessview = (TextView)findViewById(R.id.brightness_text);
        if (hudObject.isAuto_brightness()){
            brightnessview.setText("Auto");
        }else{
            brightnessview.setText(hudObject.getBrightness()+"%");
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    public void onMaxCurrentDialogPositiveClick(DialogFragment dialog) {
        HUDObject hudObject = FileManager.loadFromFile(this);
        Dialog view = dialog.getDialog();
        EditText maxCurrentText = (EditText) view.findViewById(R.id.editMax);
        Integer maxCurrent =  Integer.parseInt(maxCurrentText.getText().toString());
        hudObject.setCurrent(maxCurrent);
        FileManager.saveToFile(this,hudObject);
        TextView maxCurrentView = (TextView)findViewById(R.id.maxCurrentText);
        maxCurrentView.setText(hudObject.getCurrent()+"mA");
    }

    @Override
    public void onMaxCurrentDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (dialog!=null){
            dialog.dismiss();
        }
        unregisterReceiver(mGattUpdateReceiver);
        unbindService(mConnection);
        if (bleService!=null)
        {
            bleService.disconnectFromDevice();

        }
        stopService(vehicleMonitorIntent);
        stopService(bluetoothServiceIntent);
        stopService(new Intent(this, WNHNotificationListener.class));
    }



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
        intentFilter.addAction(VoiceCommandManager.VOICE_COMMAND_UPDATE);
        return intentFilter;
    }
}
