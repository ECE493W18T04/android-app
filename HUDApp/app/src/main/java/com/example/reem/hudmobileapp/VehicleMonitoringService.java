package com.example.reem.hudmobileapp;

import android.app.ActivityManager;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;


import com.example.reem.hudmobileapp.activities.MainActivity;
import com.example.reem.hudmobileapp.ble.BLEService;
import com.example.reem.hudmobileapp.ble.CharacteristicWriter;
import com.example.reem.hudmobileapp.helper.FileManager;
import com.openxc.VehicleManager;
import com.openxc.measurements.EngineSpeed;
import com.openxc.measurements.FuelLevel;
import com.openxc.measurements.Measurement;
import com.openxc.measurements.TurnSignalStatus;
import com.openxc.measurements.VehicleSpeed;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

/**
 * Created by navjeetdhaliwal on 2018-03-05.
 */

public class VehicleMonitoringService extends Service {
    public VehicleManager VehicleManager;

    private Intent bluetoothServiceIntent;
    private BLEService bleService;
    private boolean connectedToBLEService = false;

    private static final String TAG = "VehicleMonitoring";

    private VehicleSpeed.Listener speedListener;
    private double vSpeed = 0.0;

    private EngineSpeed.Listener rpmListener;
    private double rpm = 0.0;

    private TurnSignalStatus.Listener turnSignalListener;
    private String signalPosition = "OFF";
    private final CharacteristicWriter writer;
    private FuelLevel.Listener fuelListener;
    private double fuelLevel = 0.0;

    private int counter = 0;

    public ServiceConnection connection;

    // required but does nothing
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /*
     * Getter Functions
     */
    public double getVehicleSpeed() {
        return vSpeed;
    }
    public double getFuelLevel() {
        return fuelLevel;
    }
    public double getRpm() {
        return rpm;
    }
    public String getSignalPosition() {
        return signalPosition;
    }

    //maybe add requested listeners as arguments
    public VehicleMonitoringService (final CharacteristicWriter writer) {
        this.writer = writer;


        /*
         * Define Listeners
         */
        speedListener = new VehicleSpeed.Listener() {
            @Override
            public void receive(Measurement measurement) {
                final VehicleSpeed speed = (VehicleSpeed) measurement;


                if (counter > 100) {
                    vSpeed = speed.getValue().doubleValue();
                    byte[] rawSpeed = new byte[2];

                    rawSpeed[0] = (byte) ((int) Math.round(vSpeed) & 0xFF);
                    rawSpeed[1] = (byte) (((int) Math.round(vSpeed) >> 8) & 0xFF);
                    ByteBuffer.wrap(rawSpeed).order(ByteOrder.LITTLE_ENDIAN);
                    if (!connectedToBLEService) {
                        //do nothing
                    } else if (bleService.isInitialWriteCompleted()){
                        try {
                            writer.writeVehicleSpeed(rawSpeed);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    counter = 0;
                }else {
                    counter++;
                }
            }
        };

//        rpmListener = new EngineSpeed.Listener() {
//            @Override
//            public void receive(Measurement measurement) {
//                final EngineSpeed eSpeed = (EngineSpeed) measurement;
//                rpm = eSpeed.getValue().doubleValue();
//            }
//        };

        turnSignalListener = new TurnSignalStatus.Listener() {
            @Override
            public void receive(Measurement measurement) {
                final TurnSignalStatus turnSignalStatus = (TurnSignalStatus) measurement;
                signalPosition = turnSignalStatus.toString();

                byte[] signal = new byte[4];
                switch (signalPosition){
                    case "off":
                        ByteBuffer.wrap(signal).order(ByteOrder.LITTLE_ENDIAN).putInt(0);
                        break;
                    case "left":
                        ByteBuffer.wrap(signal).order(ByteOrder.LITTLE_ENDIAN).putInt(1);
                        break;

                    case "right":
                        ByteBuffer.wrap(signal).order(ByteOrder.LITTLE_ENDIAN).putInt(2);
                        break;
                    default:
                        ByteBuffer.wrap(signal).order(ByteOrder.LITTLE_ENDIAN).putInt(0);
                        break;
                }

                byte[] content = new byte[1];
                content[0] = signal[0];
                if (!connectedToBLEService) {
                    //do nothing
                } else if (bleService.isInitialWriteCompleted()){
                    writer.writeTurnSignal((content));
                }
                Log.d("Vehicle Monitor",signalPosition);
            }
        };

        fuelListener = new FuelLevel.Listener() {
            @Override
            public void receive(Measurement measurement) {
                fuelLevel = ((FuelLevel) measurement).getValue().doubleValue();
                if (isMyServiceRunning(BLEService.class)){
                    bluetoothServiceIntent = new Intent(getApplicationContext(), BLEService.class);
                    bindService(bluetoothServiceIntent, mConnection, BIND_AUTO_CREATE);
                }

                byte[] rawFuel = new byte[1];
                int currentfuel = (int)Math.round(fuelLevel*100);
                rawFuel[0] = (byte) (currentfuel & 0xFF);
                ByteBuffer.wrap(rawFuel).order(ByteOrder.LITTLE_ENDIAN);
                if (!connectedToBLEService) {

                }else if (bleService.isInitialWriteCompleted()) {
                    writer.writeFuelLevel(rawFuel);
                }
            }
        };


        /*
         * Service Connection
         */
        connection = new ServiceConnection() {

            // Called when the connection with the VehicleManager service is
            // established, i.e. bound.
            public void onServiceConnected(ComponentName className, IBinder service) {
                Log.i(TAG, "Bound to VehicleManager");

                VehicleManager = ((VehicleManager.VehicleBinder) service).getService();

                VehicleManager.addListener(VehicleSpeed.class, speedListener);
                //VehicleManager.addListener(EngineSpeed.class, rpmListener);
                VehicleManager.addListener(TurnSignalStatus.class, turnSignalListener);
                VehicleManager.addListener(FuelLevel.class, fuelListener);

                if (isMyServiceRunning(BLEService.class)){
                    if (!connectedToBLEService)
                        bluetoothServiceIntent = new Intent(getApplicationContext(), BLEService.class);
                    bindService(bluetoothServiceIntent, mConnection, BIND_AUTO_CREATE);
                }


            }

            // Called when the connection with the service disconnects unexpectedly
            public void onServiceDisconnected(ComponentName className) {
                Log.w(TAG, "VehicleManager Service  disconnected unexpectedly");
                VehicleManager = null;

            }
        };
    }

    @Override
    public void onDestroy() {
        if(VehicleManager != null) {

            //VehicleManager.removeListener(EngineSpeed.class, rpmListener);
            VehicleManager.removeListener(VehicleSpeed.class, speedListener);
            VehicleManager.removeListener(TurnSignalStatus.class, turnSignalListener);
            VehicleManager.removeListener(FuelLevel.class, fuelListener);


            VehicleManager = null;
        }
        super.onDestroy();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "On Service Connected");
            BLEService.BLEBinder mBinder = (BLEService.BLEBinder) iBinder;
            bleService = mBinder.getService();
            connectedToBLEService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "ON Service Disconnected");
            connectedToBLEService = false;
        }
    };

}
