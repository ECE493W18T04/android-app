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
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.reem.hudmobileapp.activities.MainActivity;
import com.example.reem.hudmobileapp.helper.FileManager;

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
    public static String DISCONNECT_CHARACTERISTIC_UUID = "0000a004-0000-1000-8000-00805f9b34fb";
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


    // when the ble service is initialized.
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
            String macAddress=FileManager.readMACAddress(BLEService.this);
//            String macAddress=null;
                if (!devices.contains(device)) {
                    Log.d(DEBUG_TAG, "Discovered " + device.getName() + " : " + device.getAddress());
                    // store the address in a file or something
                    if (macAddress==null)
                    {
                        if ("WNH".equals(device.getName())) {

                            Log.i("BLUETOOTH DEVICE FOUND", "wnh found via name");
                            bluetoothDevice = device;
                            bluetoothDevice.createBond();
                            connect(device.getAddress());
                            Log.d(DEBUG_TAG, "Found device name with address: " + device.getAddress());

                            stopScan();
                        } else {
                            devices.add(device);
                        }
//
                    }
                    else
                    {
                        Log.i("MAC ADDRESS", "The mac adress looking for is"+macAddress);
                        if (macAddress.equals(device.getAddress())){
                            Log.i("BLUETOOTH DEVICE FOUND ", "wnh found via mac address");
                            bluetoothDevice = device;
                            bluetoothDevice.createBond();
                            connect(device.getAddress());
                            Log.d(DEBUG_TAG, "Found device name with address: " + device.getAddress());
                            stopScan();
                        } else {
                            devices.add(device);
                        }
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
                bluetoothGatt.close();
//                bluetoothDevice = null;
//                gatt.disconnect();
//                gatt.close();

                Log.e(DEBUG_TAG,bluetoothManager.getConnectedDevices(BluetoothProfile.GATT).toString());
//                bluetoothAdapter.disable();
//                bluetoothManager=null;
                //scanForDevices();
            }
        }

        private String valueRead="R";

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
//                    BluetoothGattCharacteristic writeCr = bluetoothGattService.getCharacteristic(UUID.fromString(DISCONNECT_CHARACTERISTIC_UUID));
//                    writeCr.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
//                    writeCr.setValue(valueRead.getBytes());
//                    Log.d(DEBUG_TAG,writeCr.toString());
//                    Log.d(DEBUG_TAG,"ABout to write characteristic");
//                    bluetoothGatt.writeCharacteristic(writeCr);
                }
            } else {
                Log.w(DEBUG_TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d(DEBUG_TAG,"I should be writing now");
            inFlight = false;
            if (status != GATT_SUCCESS) {
                Log.e(DEBUG_TAG, "Failed to write characteristic ");
                Log.e(DEBUG_TAG, gatt.toString());
            } else {
                Log.d(DEBUG_TAG, "SUCCESS");
            }
            characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
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
        mHandler = new Handler();
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

        FileManager.saveMACAddress(this,bluetoothDeviceMACAdress);
        return true;

    }

    private static final long SCAN_PERIOD = 10000;
    private void scanForDevices()
    {
        Toast.makeText(getApplicationContext(), "About to scan for devices", Toast.LENGTH_SHORT).show();
        if (blueToothScanThread != null)
        {
            Log.e(DEBUG_TAG, "Scan is currently running, error");
            return;
        }
        boolean enable = true;
        if (enable)
        {
            isScanActive = true;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isScanActive = false;
                    bluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            isScanActive = true;
            bluetoothAdapter.startLeScan(mLeScanCallback);
        }
        else
        {
            isScanActive = false;
            bluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    private void stopScan()
    {
        isScanActive = false;
        mHandler=null;
//        blueToothScanThread = null;
    }
    @Override
    public void onDestroy() {

        stopScan();
        if (bluetoothGatt != null)
        {
            String value = "0";
            if (bluetoothGattService !=null)
            {
                BluetoothGattCharacteristic writeCr = bluetoothGattService.getCharacteristic(UUID.fromString(DISCONNECT_CHARACTERISTIC_UUID));
                writeCr.setValue(value.getBytes());
                writeCr.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                Log.d(DEBUG_TAG, writeCr.getValue().toString());
                Log.d(DEBUG_TAG,writeCr.toString());
                bluetoothGatt.writeCharacteristic(writeCr);
            }
           bluetoothGatt.disconnect();

        }
        Log.e("DISCONNECT", "Disconnecting from bluetoothGatt and stopping service");
        super.onDestroy();
    }



    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(), "Figuring out where the problem is", Toast.LENGTH_SHORT).show();
        return mBinder;
    }
}
