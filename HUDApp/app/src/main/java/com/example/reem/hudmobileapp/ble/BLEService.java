package com.example.reem.hudmobileapp.ble;


import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;

import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;
/**
 * Created by Reem on 2018-03-02.
 */

public class BLEService extends Service {
    public static String TEXT_VALUE_CHARACTERISTIC = "0000a002-0000-1000-8000-00805f9b34fb";
    public static String LED_CHARACTERISTIC = "0000a001-0000-1000-8000-00805f9b34fb";
    public static String SERVICE = "0000a000-0000-1000-8000-00805f9b34fb";

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothManager bluetoothManager;
    private BluetoothDevice bluetoothDevice;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattService bluetoothGattService;
    private final IBinder mBinder = new BLEBinder();
    private Handler mHandler;

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
    private boolean enabled=true;
    private Set<BluetoothDevice> devices = new HashSet<>();

    private final String DEBUG_TAG = this.getClass().getSimpleName();

    private final BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int i, byte[] bytes) {
            if (!devices.contains(device)) {
                Log.d(DEBUG_TAG, "Discovered " + device.getName() + " : " + device.getAddress());

                if ("WNH".equals(device.getName())) {

                    Log.i("BLUETOOTH DEVICE FOUND","wnh found");
                    bluetoothDevice = device;
                    connect(device.getAddress());
                    Log.d(DEBUG_TAG, "Found device name with address: " + device.getAddress());

                    stopScan();
                } else {
                    devices.add(device);
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
                Log.d(DEBUG_TAG, "Connected to bluetooth");
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(DEBUG_TAG, "Disconnected from Bluetooth");
                isConnected = false;
                bluetoothDevice = null;
                //scanForDevices();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == GATT_SUCCESS) {
                Log.d(DEBUG_TAG, "GATT Status success");
                BluetoothGattService service = bluetoothGatt.getService(UUID.fromString(SERVICE));
                if (service != null) {
                    bluetoothGattService = service;
                    Log.d(DEBUG_TAG, "Discovered service!");
                    setCharacteristicNotification(gatt);
//                    BluetoothGattCharacteristic readCr = bluetoothGattService.getCharacteristic(UUID.fromString(READ_CHARACTERISTIC));
//                    bluetoothGatt.readCharacteristic(readCr);

//                    Log.d(DEBUG_TAG,"About to write CR");
//                    BluetoothGattCharacteristic writeCr = bluetoothGattService.getCharacteristic(UUID.fromString(WRITE_CHARACTERTISTIC));
//                    writeCr.setValue(valueRead.getBytes());
//                    Log.d(DEBUG_TAG,writeCr.toString());
//                    bluetoothGatt.writeCharacteristic(writeCr);
                }
            } else {
                Log.w(DEBUG_TAG, "onServicesDiscovered received: " + status);
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
//            bluetoothGatt.setCharacteristicNotification(characteristic, enabled);
            super.onCharacteristicWrite(gatt, characteristic, status);
        }


        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic charactertistic, int status)
        {
            if (status != GATT_SUCCESS){
                Log.e(DEBUG_TAG, "Failed to read characteristic");
            }else
            {
                Log.e(DEBUG_TAG, "SUCCESSFULLY READ");
            }
            charactertistic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            Log.i("Value of c:",charactertistic.getStringValue(0));
            valueRead=charactertistic.getStringValue(0);
            setCharacteristicNotification(gatt);
            super.onCharacteristicRead(gatt,charactertistic,status);
        }

        @Override
        // Characteristic notification
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
//            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            Log.i(DEBUG_TAG, "Characteristic Changed");
            Log.i(DEBUG_TAG, characteristic.getStringValue(0));

        }

        public void setCharacteristicNotification(BluetoothGatt gatt)
        {   if (bluetoothAdapter == null || bluetoothGatt == null) {
                Log.w(DEBUG_TAG, "BluetoothAdapter not initialized");
                return;
            }
            BluetoothGattCharacteristic characteristic = bluetoothGatt.getService(UUID.fromString(SERVICE)).getCharacteristic(UUID.fromString(LED_CHARACTERISTIC));

            Log.d(DEBUG_TAG,characteristic.getUuid().toString());
            for (BluetoothGattDescriptor descriptor:characteristic.getDescriptors())
            {
                Log.d(DEBUG_TAG,"BLE Descriptor "+descriptor.getUuid().toString());
            }

            // Enable local notifications
            gatt.setCharacteristicNotification(characteristic, true);
            // Enabled remote notifications
            BluetoothGattDescriptor desc = characteristic.getDescriptor(CONFIG_DESCRIPTOR);
            desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(desc);
        }

    };





    private static final UUID CONFIG_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");


    private String valueRead="R";
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
//        while(true) {
//            if (isConnected) {
//                // do a read thing
//                BluetoothGattCharacteristic cr = bluetoothGattService.getCharacteristic(UUID.fromString(READ_CHARACTERISTIC));
//                bluetoothGatt.readCharacteristic(cr);
//                Toast.makeText(getApplicationContext(), "CHARACTERISTIC READ", Toast.LENGTH_SHORT).show();
//                break;
//            }
//        }
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

    private static final long SCAN_PERIOD = 10000;
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
