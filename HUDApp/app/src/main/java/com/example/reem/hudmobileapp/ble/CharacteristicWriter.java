package com.example.reem.hudmobileapp.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import com.example.reem.hudmobileapp.constants.CharacteristicUUIDs;
import com.example.reem.hudmobileapp.constants.HUDObject;
import com.example.reem.hudmobileapp.constants.PriorityQueueEnum;
import com.example.reem.hudmobileapp.helper.FileManager;
import com.google.common.escape.ArrayBasedUnicodeEscaper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import java.util.TimeZone;
import java.util.UUID;

/**
 * Created by Reem on 2018-03-21.
 *
 * This class will be reponsible for relaying information for the HUD.
 * The requirements satisfied are:
 * REQ-A-4.2.3.2;
 * REQ-A-4.3.3.2;
 * REQ-B-4.6.3.2

 */


public class CharacteristicWriter {

    private BluetoothGattService gattService;
    private BluetoothGatt gatt;
    private HUDObject hudObject;
    private static byte[] lastDistance;
    private static byte[] lastStreetname;
    private static byte lastDirection;
    public CharacteristicWriter(BluetoothGattService gattService, HUDObject hudObject, BluetoothGatt gatt)
    {
        this.gattService=gattService;
        this.hudObject=hudObject;
        this.gatt=gatt;
    }
    public CharacteristicWriter(BluetoothGattService gattService, BluetoothGatt gatt){
        this.gattService = gattService;
        this.gatt = gatt;
    }


    public void setHUDObject(HUDObject hud){
        this.hudObject = hud;
    }


    public void initialConnectWrite() throws InterruptedException {

        Thread.sleep(1000);
        writePriorityQueue();

        Thread.sleep(200);
        writeHUDBrightness();
        Thread.sleep(200);
        writeCurrentTime();
        Thread.sleep(200);
        writeColor();
        Thread.sleep(200);
        writeMaxCurrent();
        Thread.sleep(200);
//        writeHUDBrightness();
    }

    public void writeMaxCurrent()
    {
        int maxCurrent = hudObject.getCurrent();
//        String binaryString = Integer.toBinaryString(maxCurrent);
//        binaryString = binaryString.substring(binaryString.length() - 8);

        List<BluetoothGattCharacteristic> characteristics=gattService.getCharacteristics();
        BluetoothGattCharacteristic maxCurrentCharacteristic = gattService.getCharacteristic(UUID.fromString(CharacteristicUUIDs.MAX_CURRENT_CHARACTERISTIC_UUID));
        byte[] bytes = new byte[2];
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).putShort((short) maxCurrent);
        maxCurrentCharacteristic.setValue(bytes);
        gatt.writeCharacteristic(maxCurrentCharacteristic);

    }

    public void stateOverride(int value)
    {
        BluetoothGattCharacteristic stateOverride = gattService.getCharacteristic(UUID.fromString(CharacteristicUUIDs.STATE_OVERRIDE_CHARACTERISTIC_UUID));
        byte[] bytes = new byte[1];
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).put((byte) value);
        stateOverride.setValue(bytes);
        gatt.writeCharacteristic(stateOverride);
    }

    public void writeCurrentTime()
    {

        //long unixTime = System.currentTimeMillis() / 1000L;
        TimeZone zone = TimeZone.getTimeZone("America/Edmonton");
        System.out.println("IS IT A STRNIG:"+ Calendar.getInstance().getTimeZone());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(zone);
        long unixTime=(calendar.getTimeInMillis() + TimeZone.getDefault().getOffset(calendar.getTimeInMillis())) / 1000L;

        Log.i("UNIXTIME",Long.toString(unixTime));
        BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(UUID.fromString(CharacteristicUUIDs.CURRENT_TIME_CHARACTERISTIC_UUID));
        byte[] bytes = new byte[4];
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).putInt((int) unixTime);
        characteristic.setValue(bytes);
        gatt.writeCharacteristic(characteristic);

    }

    public void writeHUDBrightness()
    {
        int brightness = hudObject.getBrightness();
        boolean autoBrightness = hudObject.isAuto_brightness();
        int autoBright =0;
        if (autoBrightness)
            autoBright=1;
        BluetoothGattCharacteristic brightnessChar = gattService.getCharacteristic(UUID.fromString(CharacteristicUUIDs.AUTO_BRIGHT_CHARACTERISTIC_UUID));
        byte[] bytes = new byte[1];

        autoBright=autoBright<<7;
        autoBright|=brightness&0x7F;
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).put((byte)autoBright);
        brightnessChar.setValue(bytes);
        gatt.writeCharacteristic(brightnessChar);

    }



    public void writeColor(){

        int saturation = Math.round(hudObject.getSaturation());
        int hue = Math.round(hudObject.getHue());

        hue = hue << 7;
        hue|=saturation&0x7F;

        byte[] bytes = new byte[2];
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).putShort((short) hue);
        List<BluetoothGattCharacteristic> characteristics=gattService.getCharacteristics();
        for (BluetoothGattCharacteristic c: characteristics)
        {
            Log.e("CHARACTERISTIC",c.getUuid().toString());
        }
        BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(UUID.fromString(CharacteristicUUIDs.COLOR_CHARACTERISTIC_UUID));
        characteristic.setValue(bytes);
        gatt.writeCharacteristic(characteristic);


    }

    public void writePriorityQueue() throws InterruptedException {
        ArrayList<String> priorityQueue = (ArrayList<String>) hudObject.getPriorityQueue();
        int callPriority=-1;
        int navigationPriority=-1;
        int speedPriority=-1;
        int musicPriority=-1;
        int fuelPriority=-1;
        int clockPriority=-1;
        for (int i =0;i<priorityQueue.size();i++)
        {
            if (priorityQueue.get(i).equals(PriorityQueueEnum.CALL_DISPLAY.getValue()))
            {
                callPriority=i;
            }else if (priorityQueue.get(i).equals(PriorityQueueEnum.NAVIGATION_DISPLAY.getValue()))
            {
                navigationPriority = i;
            }else if (priorityQueue.get(i).equals(PriorityQueueEnum.SPEED_LEVEL.getValue()))
            {
                speedPriority = i;
            }else if (priorityQueue.get(i).equals(PriorityQueueEnum.MUSIC_DISPLAY.getValue()))
            {
                musicPriority = i;
            }else if (priorityQueue.get(i).equals(PriorityQueueEnum.FUEL_LEVEL.getValue()))
            {
                fuelPriority = i;
            }else if (priorityQueue.get(i).equals(PriorityQueueEnum.CLOCK_DISPLAY.getValue()))
            {
                clockPriority = i;
            }
        }
        BluetoothGattCharacteristic clocks_map = gattService.getCharacteristic(UUID.fromString(CharacteristicUUIDs.CLOCK_MAPS_PRIORITY_CHARACTERISTIC_UUID));

        clockPriority = clockPriority << 4;
        clockPriority|=navigationPriority & 0x0F;
        byte[] bytes = new byte[1];

        Log.d("INTEGERTOBINARY",Integer.toBinaryString(clockPriority));
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).put((byte) clockPriority);
        clocks_map.setValue(bytes);
        gatt.writeCharacteristic(clocks_map);
        Thread.sleep(200);
        BluetoothGattCharacteristic call_music = gattService.getCharacteristic(UUID.fromString(CharacteristicUUIDs.CALL_MUSIC_PRIORITY_CHARACTERISTIC_UUID));
        callPriority = callPriority << 4;
        callPriority|=musicPriority & 0x0F;
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).put((byte)callPriority);
        call_music.setValue(bytes);
        gatt.writeCharacteristic(call_music);
        Thread.sleep(200);

        BluetoothGattCharacteristic speed_fuel = gattService.getCharacteristic(UUID.fromString(CharacteristicUUIDs.SPEED_FUEL_PRIORITY_CHARACTERISTIC_UUID));
        speedPriority =speedPriority << 4;
        speedPriority|=fuelPriority & 0x0F;
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).put((byte)speedPriority);
        speed_fuel.setValue(bytes);
        Log.e("WRITEPRIORITIES","ABOUT TO WRITE PRIORITIES");
        gatt.writeCharacteristic(speed_fuel);
    }



    public void writeCallInfo(byte[] content){
        BluetoothGattCharacteristic BGC = gattService.getCharacteristic(UUID.fromString(CharacteristicUUIDs.CALL_NAME_CHARACTERISTIC_UUID));
        BGC.setValue(content);
        gatt.writeCharacteristic(BGC);
    }
    public void writeMusicInfo(byte[] content){
        Log.d("Writing","Music Info");

        BluetoothGattCharacteristic BGC = gattService.getCharacteristic(UUID.fromString(CharacteristicUUIDs.MUSIC_SONG_CHARACTERISTIC_UUID));
        BGC.setValue(content);
        gatt.writeCharacteristic(BGC);
    }
    public void writeFuelLevel(byte[] content){
        Log.d("Writing","Fuel Level");
        BluetoothGattCharacteristic BGC = gattService.getCharacteristic(UUID.fromString(CharacteristicUUIDs.FUEL_VALUE_CHARACTERISTIC_UUID));
        BGC.setValue(content);
        gatt.writeCharacteristic(BGC);
    }

    public void writeVehicleSpeed(byte[] content) throws InterruptedException {
        BluetoothGattCharacteristic speedUnits = gattService.getCharacteristic(UUID.fromString(CharacteristicUUIDs.SPEED_UNITS_AND_SIGNAL_CHARACTERISTIC_UUID));
        byte[] bytes = new byte[1];
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).put((byte) 0);
        speedUnits.setValue(bytes);
        gatt.writeCharacteristic(speedUnits);
        Thread.sleep(100);
        Log.d("Writing","Vehicle Speed");
        BluetoothGattCharacteristic BGC = gattService.getCharacteristic(UUID.fromString(CharacteristicUUIDs.SPEED_VALUE_CHARACTERISTIC_UUID));
        BGC.setValue(content);
        gatt.writeCharacteristic(BGC);

    }
    public void writeTurnSignal(byte[] content){
        BluetoothGattCharacteristic turnSignal = gattService.getCharacteristic(UUID.fromString(CharacteristicUUIDs.SPEED_UNITS_AND_SIGNAL_CHARACTERISTIC_UUID));
        turnSignal.setValue(content);
        gatt.writeCharacteristic(turnSignal);

    }
    public void writeNavigationEnded(byte[] content) {
        BluetoothGattCharacteristic BGC = gattService.getCharacteristic(UUID.fromString(CharacteristicUUIDs.MAPS_STREET_CHARACTERISTIC_UUID));
        BGC.setValue(content);
        gatt.writeCharacteristic(BGC);
    }
    public void writeNavigationInfo( byte[] content) {
        if (content.length > 5) {
            BluetoothGattCharacteristic BGC;

            byte[] directionAndDistanceUnit = new byte[1];
            directionAndDistanceUnit[0] = content[0];
            if (lastDirection != directionAndDistanceUnit[0]) {
                lastDirection = directionAndDistanceUnit[0];
                BGC = gattService.getCharacteristic(UUID.fromString(CharacteristicUUIDs.MAPS_DIRECTION_AND_UNITS_CHARACTERISTIC_UUID));
                BGC.setValue(directionAndDistanceUnit);
                gatt.writeCharacteristic(BGC);
            }
            try {
                Thread.sleep(100);

            } catch (Exception e){
                e.printStackTrace();
            }

            byte[] streetName = new byte[content.length - 5];
            for (int i=5; i<content.length; i++){
                streetName[i-5] = content[i];
            }
            if (lastStreetname != streetName) {
                BGC = gattService.getCharacteristic(UUID.fromString(CharacteristicUUIDs.MAPS_STREET_CHARACTERISTIC_UUID));
                BGC.setValue(streetName);
                gatt.writeCharacteristic(BGC);
                lastStreetname = streetName;
            }


            try {
                Thread.sleep(100);

            } catch (Exception e){
                e.printStackTrace();
            }

            byte[] distance = new byte[4];
            distance[0] = content[1];
            distance[1] = content[2];
            distance[2] = content[3];
            distance[3] = content[4];

            if (lastDistance != distance) {
                BGC = gattService.getCharacteristic(UUID.fromString(CharacteristicUUIDs.MAPS_DISTANCE_CHARACTERISTIC_UUID));
                BGC.setValue(distance);
                gatt.writeCharacteristic(BGC);
                lastDistance = distance;
            }




        }
    }

}
