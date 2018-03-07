package com.example.reem.hudmobileapp;

import android.app.Service;
import android.bluetooth.BluetoothClass;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.content.ServiceConnection;


import com.openxc.VehicleManager;
import com.openxc.measurements.EngineSpeed;
import com.openxc.measurements.Measurement;
import com.openxc.measurements.VehicleSpeed;

/**
 * Created by navjeetdhaliwal on 2018-03-05.
 */

public class VehicleMonitoringService extends Service {
    public VehicleManager VehicleManager;
    private static final String TAG = "VehicleMonitor";

    private VehicleSpeed.Listener speedListener;
    private double vSpeed = 0.0;

    public ServiceConnection connection;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public double getvehicleSpeed() {
        return vSpeed;
    }

    //maybe add requested listeners as arguments
    public VehicleMonitoringService () {

        //define listeners
        speedListener = new VehicleSpeed.Listener() {
            @Override
            public void receive(Measurement measurement) {
                final VehicleSpeed speed = (VehicleSpeed) measurement;

                vSpeed = speed.getValue().doubleValue();
            }
        };

        /* This is an OpenXC measurement listener object - the type is recognized
        * by the VehicleManager as something that can receive measurement updates.
        * Later in the file, we'll ask the VehicleManager to call the receive()
        * function here whenever a new EngineSpeed value arrives.
        */
        connection = new ServiceConnection() {
            // Called when the connection with the VehicleManager service is
            // established, i.e. bound.
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                Log.i(TAG, "Bound to VehicleManager");
                // When the VehicleManager starts up, we store a reference to it
                // here in "VehicleManager" so we can call functions on it
                // elsewhere in our code.
                VehicleManager = ((VehicleManager.VehicleBinder) service)
                        .getService();

                // We want to receive updates whenever the EngineSpeed changes. We
                // have an EngineSpeed.Listener (see above, mSpeedListener) and here
                // we request that the VehicleManager call its receive() method
                // whenever the EngineSpeed changes
                VehicleManager.addListener(VehicleSpeed.class, speedListener);

            }

            // Called when the connection with the service disconnects unexpectedly
            public void onServiceDisconnected(ComponentName className) {
                Log.w(TAG, "VehicleManager Service  disconnected unexpectedly");
                VehicleManager = null;
            }
        };
    }
    public void Disconnect() {
        if(VehicleManager != null) {
            Log.i(TAG, "Unbinding from Vehicle Manager");
            // Remember to remove your listeners, in typical Android
            // fashion.
            VehicleManager.removeListener(EngineSpeed.class,
                    speedListener);
            unbindService(connection);
            VehicleManager = null;
        }
    }






}
