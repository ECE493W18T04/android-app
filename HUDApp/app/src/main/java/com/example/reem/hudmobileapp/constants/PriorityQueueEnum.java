package com.example.reem.hudmobileapp.constants;

/**
 * Created by Reem on 2018-03-21.
 */

public enum PriorityQueueEnum {

    CALL_DISPLAY ("Call Display"),
    MUSIC_DISPLAY ("Music Display"),
    NAVIGATION_DISPLAY ("Navigation Notifications"),
    CLOCK_DISPLAY ("Clock"),
    FUEL_LEVEL ("Fuel Level"),
    SPEED_LEVEL ("Speed Level");


    private String value;

    PriorityQueueEnum(String value)
    {
        this.value= value;
    }

    public String getValue()
    {
        return value;
    }


}
