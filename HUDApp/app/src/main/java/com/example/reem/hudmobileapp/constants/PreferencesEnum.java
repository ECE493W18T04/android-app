package com.example.reem.hudmobileapp.constants;

/**
 * Created by Reem on 2018-03-21.
 */

public enum PreferencesEnum {

    PRIORITY_QUEUE (0),
    BRIGHTNESS_CONTROL (1),
    COLOR_CONTROL(2),
    MAX_CURRENT (3);


    private int value;

    PreferencesEnum(int value)
    {
        this.value= value;
    }

    public int getValue()
    {
        return value;
    }



    }
