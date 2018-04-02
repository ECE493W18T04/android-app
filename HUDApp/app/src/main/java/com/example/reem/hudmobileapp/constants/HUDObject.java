package com.example.reem.hudmobileapp.constants;

import android.graphics.Color;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Reem on 2018-03-20.
 */

public class HUDObject {

    private List<String> priorityQueue;
    private float saturation;
    private float hue;
    private int current;
    private int brightness;
    private boolean auto_brightness;
    private float hsvBrightness;

    public HUDObject()
    {
        this.priorityQueue=new ArrayList<String>();
        addItemsToPriorityQueue();
        this.saturation = 0;
        this.hue = 0;
        this.brightness = 20;
        this.current = 1000;
        this.auto_brightness = true;
        this.hsvBrightness= 1f;
    }

    private void addItemsToPriorityQueue()
    {
        priorityQueue.add(PriorityQueueEnum.NAVIGATION_DISPLAY.getValue());
        priorityQueue.add(PriorityQueueEnum.MUSIC_DISPLAY.getValue());
        priorityQueue.add(PriorityQueueEnum.CALL_DISPLAY.getValue());
        priorityQueue.add(PriorityQueueEnum.SPEED_LEVEL.getValue());
        priorityQueue.add(PriorityQueueEnum.FUEL_LEVEL.getValue());
        priorityQueue.add(PriorityQueueEnum.CLOCK_DISPLAY.getValue());

    }

    public List<String> getPriorityQueue()
    {
        return this.priorityQueue;
    }

    public void setPriorityQueue(List<String> priorityQueue)
    {
        this.priorityQueue=priorityQueue;
    }

    public float getSaturation() {
        return saturation;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
    }

    public float getHue() {
        return hue;
    }

    public void setHue(float hue) {
        this.hue = hue;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public boolean isAuto_brightness() {
        return auto_brightness;
    }

    public void setAuto_brightness(boolean auto_brightness) {
        this.auto_brightness = auto_brightness;
    }

    public float getHsvBrightness() {
        return hsvBrightness;
    }

    public void setHsvBrightness(float hsvBrightness) {
        this.hsvBrightness = hsvBrightness;
    }
}
