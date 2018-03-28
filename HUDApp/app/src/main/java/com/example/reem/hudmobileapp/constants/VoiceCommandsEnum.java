package com.example.reem.hudmobileapp.constants;

/**
 * Created by Reem on 2018-03-28.
 */

public enum VoiceCommandsEnum {

    CHANGE_COLOR_RED("red"),
    CHANGE_COLOR_GREEN("green"),
    CHANGE_COLOR_BLUE("blue"),
    CHANGE_COLOR_WHITE("white"),
    CHNAGE_COLOR_PURPLE("purple"),
    CHANGE_BRIGHTNESS("brightness"),
    CHANGE_OVERRIDE("override"),
    CLOCK_OVERRIDE("clock"),
    VEHICLE_SPEED_OVERRIDE("speed"),
    MUSIC_OVERRIDE("music"),
    FUEL_OVERRIDE("fuel"),
    NAVIGATION_OVERRIDE("navigation"),
    PHONE_OVERRIDE("call"),
    DISABLE_OVERRIDE("disable");



    private String value;

    VoiceCommandsEnum(String value)
    {
        this.value= value;
    }

    public String getValue()
    {
        return value;
    }

}
