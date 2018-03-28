package com.example.reem.hudmobileapp.constants;

/**
 * Created by Reem on 2018-03-28.
 */

public enum StateOverrideEnum {


    CLOCK(0),
    VEHICLE(1),
    MUSIC(2),
    FUEL(3),
    NAVIGATION(4),
    PHONE(5),
    DISABLE(-1);

    private int value;

    StateOverrideEnum(int value)
    {
        this.value= value;
    }

    public int getValue()
    {
        return value;
    }

}
