package com.example.reem.hudmobileapp.ble;


import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;
/**
 * Created by Reem on 2018-03-02.
 */

public class BLEService extends Service {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothManager bluetoothManager;
    private BluetoothDevice bluetoothDevice;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattService bluetoothGattService;
    private final IBinder mBinder = new BLEBinder();


    private String bluetoothDeviceMACAdress=null;
    private int lastTransmittedCode = 0;
    private Thread blueToothScanThread = null;


    public class BLEBinder extends Binder {
        BLEService getService() {
            // Return this instance of LocalService so clients can call public methods
            return BLEService.this;
        }
    }


    private boolean isScanActive=false;
    private boolean isConnected= false;
    private boolean inFlight=false;

    private Set<BluetoothDevice> devices = new HashSet<>();

    private final String DEBUG_TAG = this.getClass().getSimpleName();

    private final BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
            if (!devices.contains(bluetoothDevice)) {
                Log.d(DEBUG_TAG, "Discovered " + bluetoothDevice.getName() + " : " + bluetoothDevice.getAddress());

                if ("LED".equals(bluetoothDevice.getName())) {

                    bluetoothDevice = bluetoothDevice;
                    connect(bluetoothDevice.getAddress());
                    Log.d(DEBUG_TAG, "Found device name with address: " + bluetoothDevice.getAddress());
                    stopScan();
                } else {
                    devices.add(bluetoothDevice);
                }
            }
        }
    };


    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                isConnected = true;
                bluetoothGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(DEBUG_TAG, "Disconnected from Bluetooth");
                isConnected = false;
                bluetoothDevice = null;
                //scanForDevices();
            }
        }


        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            inFlight = false;
            if (status != GATT_SUCCESS) {
                Log.e(DEBUG_TAG, "Failed to write characteristic ");
            } else {
                Log.d(DEBUG_TAG, "SUCCESS");
            }
            super.onCharacteristicWrite(gatt, characteristic, status);
        }


        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic charactertistic, int status)
        {
            if (status != GATT_SUCCESS){
                Log.e(DEBUG_TAG, "Failed to read characteristic");
            }else
            {
                Log.e(DEBUG_TAG, "SUCCESS");
            }

        }


    };

    @Override
    public void onCreate()
    {
        super.onCreate();
        Toast.makeText(getApplicationContext(), "Check if Bluetooth is enabled", Toast.LENGTH_SHORT).show();
        Log.i("","About to find bluetooth settings");
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        Toast.makeText(getApplicationContext(), "Check if Bluetooth is enabled", Toast.LENGTH_SHORT).show();
        if (bluetoothAdapter == null) {
            Log.e(DEBUG_TAG, "Bluetooth adapter is null, failure.");
            Toast.makeText(getApplicationContext(), "Bluetooth isn't avaialable", Toast.LENGTH_SHORT).show();
            return;
        } else if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(getApplicationContext(), "BLE not supported", Toast.LENGTH_SHORT).show();
            return;
        } else if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            Toast.makeText(getApplicationContext(), "BLE is supported, about to start Activity", Toast.LENGTH_SHORT).show();
            getApplicationContext().startActivity(enableBtIntent);


        }
        System.out.println("About to scan for devices");

        scanForDevices();
    }




    private boolean connect(final String address)
    {

        if (bluetoothDeviceMACAdress!= null && address.equals(bluetoothDeviceMACAdress) && bluetoothGatt!=null)
        {
            return bluetoothGatt.connect();
        }

        final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        if (device == null)
        {
            Log.d(DEBUG_TAG, "Device " + address + " not found, unable to connect");
            return false;
        }
        bluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(DEBUG_TAG, "Trying to create a new connection.");
        bluetoothDeviceMACAdress = address;
        bluetoothDevice = device;

        return true;

    }

    private void scanForDevices() {
        if (blueToothScanThread != null) {
            Log.e(DEBUG_TAG, "Scan is currently running, error");
            return;
        }

        isScanActive = true;

        blueToothScanThread = new Thread() {
            @Override
            public void run() {
                Log.d(DEBUG_TAG, "Started LE Scan");
                bluetoothAdapter.startLeScan(null, mLeScanCallback);

                for (long startTime = System.nanoTime() + TimeUnit.SECONDS.toNanos(3); startTime > System.nanoTime();) {
                    if (!isScanActive)
                        break;
                }

                bluetoothAdapter.stopLeScan(mLeScanCallback);

                if (bluetoothDevice == null) {
                    run();
                }
            }
        };

        blueToothScanThread.start();
    }

    private void stopScan() {
        isScanActive = false;
        blueToothScanThread = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bluetoothGatt.disconnect();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(), "Figuring out where the problem is", Toast.LENGTH_SHORT).show();
        return mBinder;
    }
}
