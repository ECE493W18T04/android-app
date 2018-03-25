//package com.example.reem.hudmobileapp;
//
//import android.content.Context;
//import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.widget.TextView;
//
//import com.openxc.VehicleManager;
//
//
//public class VehicleActivity extends AppCompatActivity {
//    private VehicleMonitoringService vMonitor;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        vMonitor = new VehicleMonitoringService();
//        final TextView speedView = (TextView) findViewById(R.id.vehicle_speed);
//        final TextView rpmView = (TextView) findViewById(R.id.engine_speed);
//        final TextView turnSignalView = (TextView) findViewById(R.id.turn_signal);
//        final TextView fuelView = (TextView) findViewById(R.id.fuel_level);
//        Thread t = new Thread() {
//            @Override
//            public void run() {
//                try {
//                    while (!isInterrupted()) {
//                        Thread.sleep(10);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                // update TextView here!
//                                speedView.setText("Vehicle speed (Km/h): "
//                                        + vMonitor.getVehicleSpeed());
//                                rpmView.setText("Engine speed (RPM): " + vMonitor.getRpm());
//                                fuelView.setText("Fuel level (%): " + vMonitor.getFuelLevel());
//                                turnSignalView.setText("Turn Signal Status: " + vMonitor.getSignalPosition());
//                            }
//                        });
//                    }
//                } catch (InterruptedException e) {
//                }
//            }
//        };
//
//        t.start();
//
//    }
//    //we want the service to remain bound in the background
///*
//    @Override
//    public void onPause() {
//        super.onPause();
//        // When the activity goes into the background or exits, we want to make
//        // sure to unbind from the service to avoid leaking memory
//        if(mVehicleManager != null) {
//            Log.i(TAG, "Unbinding from Vehicle Manager");
//            // Remember to remove your listeners, in typical Android
//            // fashion.
//            mVehicleManager.removeListener(EngineSpeed.class,
//                    mSpeedListener);
//            unbindService(mConnection);
//            mVehicleManager = null;
//        }
//    }
//
//*/
//
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        // When the activity starts up or returns from the background,
//        // re-connect to the VehicleManager so we can receive updates.
//        if(vMonitor.VehicleManager == null) {
//            Intent intent = new Intent(this, VehicleManager.class);
//            bindService(intent, vMonitor.connection, Context.BIND_AUTO_CREATE);
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        vMonitor.Disconnect();
//    }
//
//
//
//}
