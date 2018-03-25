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
    private int saturation;
    private int hue;
    private int current;
    private int brightness;
    private boolean auto_brightness;

    public HUDObject()
    {
        this.priorityQueue=new ArrayList<String>();
        addItemsToPriorityQueue();
        this.saturation = 0;
        this.hue = 0;
        this.brightness = 20;
        this.current = 3;
        this.auto_brightness = true;
    }

    private void addItemsToPriorityQueue()
    {
        priorityQueue.add("Fuel Level");
        priorityQueue.add("Speed Level");
        priorityQueue.add("Music Display");
        priorityQueue.add("Call Display");
        priorityQueue.add("Navigational Display");
        priorityQueue.add("Clock Display");
    }

    public List<String> getPriorityQueue()
    {
        return this.priorityQueue;
    }

    public void setPriorityQueue(List<String> priorityQueue)
    {
        this.priorityQueue=priorityQueue;
    }

    public int getSaturation() {
        return saturation;
    }

    public void setSaturation(int saturation) {
        this.saturation = saturation;
    }

    public int getHue() {
        return hue;
    }

    public void setHue(int hue) {
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
}
