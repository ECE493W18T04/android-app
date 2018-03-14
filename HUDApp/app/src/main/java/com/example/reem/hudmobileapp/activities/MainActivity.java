package com.example.reem.hudmobileapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.reem.hudmobileapp.R;
import com.example.reem.hudmobileapp.ble.BLEService;
import com.example.reem.hudmobileapp.dialogs.ColorPickerDialog;

public class MainActivity extends AppCompatActivity {

    private Intent mServiceIntent;
    private Button navButton;
    private static final int COARSE_LOCATION_PERMISSIONS = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navButton = (Button) findViewById(R.id.navButton);
        System.out.println(navButton);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startBluetoothService();
//                Intent intent = new Intent(MainActivity.this,PriorityQueueActivity.class);
//                startActivity(intent);

                int initialColor = Color.WHITE;

                ColorPickerDialog colorPickerDialog = new ColorPickerDialog(MainActivity.this, initialColor, new ColorPickerDialog.OnColorSelectedListener() {

                    @Override
                    public void onColorSelected(int color) {
                        showToast(color);
                    }

                });
                colorPickerDialog.show();
            }
        });

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
