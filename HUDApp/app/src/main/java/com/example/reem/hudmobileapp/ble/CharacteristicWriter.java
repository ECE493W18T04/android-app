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
import java.util.List;
import java.util.UUID;

/**
 * Created by Reem on 2018-03-21.
 */

public class CharacteristicWriter {

    private BluetoothGattService gattService;
    private BluetoothGatt gatt;
    private HUDObject hudObject;
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

//        writeMaxCurrent();
//        Thread.sleep(100);
////        writeHUDBrightness();
//        writeCurrentTime();
//        Thread.sleep(100);
//        writeColor();
//        writePriorityQueue();
        writeHUDBrightness();
    }

    public void writeMaxCurrent()
    {
        int maxCurrent = hudObject.getCurrent();
//        String binaryString = Integer.toBinaryString(maxCurrent);
//        binaryString = binaryString.substring(binaryString.length() - 8);

        List<BluetoothGattCharacteristic> characteristics=gattService.getCharacteristics();
        for (BluetoothGattCharacteristic c: characteristics)
        {
            Log.e("CHARACTERISTIC",c.getUuid().toString());
        }
        BluetoothGattCharacteristic maxCurrentCharacteristic = gattService.getCharacteristic(UUID.fromString(CharacteristicUUIDs.MAX_CURRENT_CHARACTERISTIC_UUID));
        byte[] bytes = new byte[2];
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).putShort((short) maxCurrent);
        maxCurrentCharacteristic.setValue(bytes);
        gatt.writeCharacteristic(maxCurrentCharacteristic);

    }

    public void writeCurrentTime()
    {
        long unixTime = System.currentTimeMillis() / 1000L;
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
        Thread.sleep(100);
        BluetoothGattCharacteristic call_music = gattService.getCharacteristic(UUID.fromString(CharacteristicUUIDs.CALL_MUSIC_PRIORITY_CHARACTERISTIC_UUID));
        callPriority = callPriority << 4;
        callPriority|=musicPriority & 0x0F;
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).put((byte)callPriority);
        call_music.setValue(bytes);
        gatt.writeCharacteristic(call_music);
        Thread.sleep(100);

        BluetoothGattCharacteristic speed_fuel = gattService.getCharacteristic(UUID.fromString(CharacteristicUUIDs.SPEED_FUEL_PRIORITY_CHARACTERISTIC_UUID));
        speedPriority =speedPriority << 4;
        speedPriority|=fuelPriority&0x0F;
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).put((byte)speedPriority);
        gatt.writeCharacteristic(speed_fuel);
    }



    public void writeCallInfo(byte[] content){
        BluetoothGattCharacteristic BGC = gattService.getCharacteristic(UUID.fromString(CharacteristicUUIDs.CALL_NAME_CHARACTERISTIC_UUID));
        BGC.setValue(content);
        gatt.writeCharacteristic(BGC);
    }
    public void writeMusicInfo(byte[] content){
        BluetoothGattCharacteristic BGC = gattService.getCharacteristic(UUID.fromString(CharacteristicUUIDs.MUSIC_SONG_CHARACTERISTIC_UUID));
        BGC.setValue(content);
        gatt.writeCharacteristic(BGC);
    }

    public void writeNavigationInfo( byte[] content) {
        if (content.length > 5) {
            byte[] directionAndDistanceUnit = new byte[1];
            directionAndDistanceUnit[0] = content[0];
            byte[] distance = new byte[4];
            distance[0] = content[1];
            distance[1] = content[2];
            distance[2] = content[3];
            distance[3] = content[4];
            byte[] streetName = new byte[content.length - 5];
            for (int i=5; i<content.length; i++){
                streetName[i-5] = content[i];
            }

            BluetoothGattCharacteristic BGC = gattService.getCharacteristic(UUID.fromString(CharacteristicUUIDs.MAPS_DISTANCE_CHARACTERISTIC_UUID));
            BGC.setValue(distance);
            gatt.writeCharacteristic(BGC);
            try {
                Thread.sleep(100);

            } catch (Exception e){
                e.printStackTrace();
            }
            BGC = gattService.getCharacteristic(UUID.fromString(CharacteristicUUIDs.MAPS_DIRECTION_AND_UNITS_CHARACTERISTIC_UUID));
            BGC.setValue(directionAndDistanceUnit);
            gatt.writeCharacteristic(BGC);
            try {
                Thread.sleep(100);

            } catch (Exception e){
                e.printStackTrace();
            }
            BGC = gattService.getCharacteristic(UUID.fromString(CharacteristicUUIDs.MAPS_STREET_CHARACTERISTIC_UUID));
            BGC.setValue(streetName);
            gatt.writeCharacteristic(BGC);
        }
    }

}
