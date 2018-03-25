package com.example.reem.hudmobileapp.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import com.example.reem.hudmobileapp.constants.CharacteristicUUIDs;
import com.example.reem.hudmobileapp.constants.HUDObject;
import com.example.reem.hudmobileapp.constants.PriorityQueueEnum;
import com.example.reem.hudmobileapp.helper.FileManager;

import java.nio.ByteBuffer;
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

    public void initialConnectWrite()
    {

//        writeMaxCurrent();
//        writeHUDBrightness();
        writeColor();
//        writePriorityQueue();
    }

    public void writeMaxCurrent()
    {
        Integer maxCurrent = hudObject.getCurrent();
//        String binaryString = Integer.toBinaryString(maxCurrent);
//        binaryString = binaryString.substring(binaryString.length() - 8);

        List<BluetoothGattCharacteristic> characteristics=gattService.getCharacteristics();
        for (BluetoothGattCharacteristic c: characteristics)
        {
            Log.e("CHARACTERISTIC",c.getUuid().toString());
        }
        BluetoothGattCharacteristic maxCurrentCharacteristic = gattService.getCharacteristic(UUID.fromString(CharacteristicUUIDs.MAX_CURRENT_CHARACTERISTIC_UUID));
        byte[] bytes = new byte[2];
        ByteBuffer.wrap(bytes).putInt(maxCurrent);
        maxCurrentCharacteristic.setValue(bytes);
        gatt.writeCharacteristic(maxCurrentCharacteristic);

    }

    public void writeHUDBrightness()
    {
        Integer brightness = hudObject.getBrightness();
        BluetoothGattCharacteristic brightnessChar = gattService.getCharacteristic(UUID.fromString(CharacteristicUUIDs.BRIGHTNESS_CHARACTERISTIC_UUID));
        byte[] bytes = new byte[1];
        ByteBuffer.wrap(bytes).putInt(brightness);
        brightnessChar.setValue(bytes);
        gatt.writeCharacteristic(brightnessChar);

    }



    public void writeColor(){

        int saturation = hudObject.getSaturation();
        int hue = hudObject.getHue();

        hue = hue << 7;
        hue|=saturation&0x7F;

        byte[] bytes = new byte[2];
        ByteBuffer.wrap(bytes).putShort((short) hue);
        List<BluetoothGattCharacteristic> characteristics=gattService.getCharacteristics();
        for (BluetoothGattCharacteristic c: characteristics)
        {
            Log.e("CHARACTERISTIC",c.getUuid().toString());
        }
        BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(UUID.fromString(CharacteristicUUIDs.COLOR_CHARACTERISTIC_UUID));
        characteristic.setValue(bytes);
        gatt.writeCharacteristic(characteristic);


    }

//    public void writePriorityQueue()
//    {
//        List<String> priorityQueue = hudObject.getPriorityQueue();
//        for (int i =0;i<priorityQueue.size();i++)
//        {
//            BluetoothGattCharacteristic characteristic = null;
//            if (priorityQueue.get(i).equals(PriorityQueueEnum.CALL_DISPLAY))
//            {
//                characteristic = gattService.getCharacteristic(UUID.fromString(CharacteristicUUIDs.CALL_PRIORITY_CHARACTERISTIC_UUID));
//            }else if (priorityQueue.get(i).equals(PriorityQueueEnum.CLOCK_DISPLAY))
//            {
//                characteristic = gattService.getCharacteristic(UUID.fromString(CharacteristicUUIDs.CLOCK_PRIORITY_CHARACTERISTIC_UUID));
//            }else if (priorityQueue.get(i).equals(PriorityQueueEnum.FUEL_LEVEL))
//            {
//                characteristic = gattService.getCharacteristic(UUID.fromString(CharacteristicUUIDs.FUEL_PRIORITY_CHARACTERISTIC_UUID));
//            }else if (priorityQueue.get(i).equals(PriorityQueueEnum.MUSIC_DISPLAY))
//            {
//                characteristic = gattService.getCharacteristic(UUID.fromString(CharacteristicUUIDs.MUSIC_PRIORITY_CHARACTERISTIC_UUID));
//            }else if (priorityQueue.get(i).equals(PriorityQueueEnum.SPEED_LEVEL))
//            {
//                characteristic = gattService.getCharacteristic(UUID.fromString(CharacteristicUUIDs.SPEED_PRIORITY_CHARACTERISTIC_UUID));
//            }
//            else if (priorityQueue.get(i).equals(PriorityQueueEnum.NAVIGATION_DISPLAY))
//            {
//                characteristic = gattService.getCharacteristic(UUID.fromString(CharacteristicUUIDs.MAPS_PRIORITY_CHARACTERISTIC_UUID));
//            }
//            byte[] bytes = new byte[8];
//            ByteBuffer.wrap(bytes).putInt(i);
//            characteristic.setValue(bytes);
//            gatt.writeCharacteristic(characteristic);
//        }
//
//    }

}
