package com.example.reem.hudmobileapp.notifications;

/**
 * Created by Reem on 2018-03-09.
 *
 */

public abstract class NotificationManager {

    // would probably take in a ble service
    public NotificationManager()
    {

    }
    public abstract byte[] getContent();


}
