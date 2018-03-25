package com.example.reem.hudmobileapp;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;


import com.openxc.VehicleManager;
import com.openxc.measurements.EngineSpeed;
import com.openxc.measurements.FuelLevel;
import com.openxc.measurements.Measurement;
import com.openxc.measurements.TurnSignalStatus;
import com.openxc.measurements.VehicleSpeed;

/**
 * Created by navjeetdhaliwal on 2018-03-05.
 */

public class VehicleMonitoringService extends Service {
    public VehicleManager VehicleManager;
    private static final String TAG = "VehicleMonitoring";

    private VehicleSpeed.Listener speedListener;
    private double vSpeed = 0.0;

    private EngineSpeed.Listener rpmListener;
    private double rpm = 0.0;

    private TurnSignalStatus.Listener turnSignalListener;
    private String signalPosition = "OFF";

    private FuelLevel.Listener fuelListener;
    private double fuelLevel = 0.0;

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
    public VehicleMonitoringService () {

        /*
         * Define Listeners
         */
        speedListener = new VehicleSpeed.Listener() {
            @Override
            public void receive(Measurement measurement) {
                final VehicleSpeed speed = (VehicleSpeed) measurement;
                vSpeed = speed.getValue().doubleValue();
            }
        };
        rpmListener = new EngineSpeed.Listener() {
            @Override
            public void receive(Measurement measurement) {
                final EngineSpeed eSpeed = (EngineSpeed) measurement;

                rpm = eSpeed.getValue().doubleValue();
            }
        };
        turnSignalListener = new TurnSignalStatus.Listener() {
            @Override
            public void receive(Measurement measurement) {
                final TurnSignalStatus turnSignalStatus = (TurnSignalStatus) measurement;
                signalPosition = turnSignalStatus.toString();
            }
        };
        fuelListener = new FuelLevel.Listener() {
            @Override
            public void receive(Measurement measurement) {
                fuelLevel = ((FuelLevel) measurement).getValue().doubleValue();
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
                VehicleManager.addListener(EngineSpeed.class, rpmListener);
                VehicleManager.addListener(TurnSignalStatus.class, turnSignalListener);
                VehicleManager.addListener(FuelLevel.class, fuelListener);

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

            VehicleManager.removeListener(EngineSpeed.class, rpmListener);
            VehicleManager.removeListener(VehicleSpeed.class, speedListener);
            VehicleManager.removeListener(TurnSignalStatus.class, turnSignalListener);
            VehicleManager.removeListener(FuelLevel.class, fuelListener);
            unbindService(connection);
            VehicleManager = null;
        }
    }






}
