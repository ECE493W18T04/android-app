package com.example.reem.hudmobileapp.helper;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.provider.Telephony.Mms.Part.FILENAME;

/**
 * Created by Reem on 2018-03-14.
 */

public class FileManager {



    private static final String MACFILE = "mac.sav";
    public static void saveMACAddress(Context context, String MACAddress)
    {
        try {

            FileOutputStream fos = context.openFileOutput(MACFILE, 0);
            OutputStreamWriter writer = new OutputStreamWriter(fos);
            writer.write(MACAddress);
            writer.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();

        }
    }



    public static String readMACAddress(Context context)
    {
        try
        {
            FileInputStream fis = context.openFileInput(MACFILE);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line;
            if ((line=br.readLine()) != null){
                return line;
            }
            return null;
        }catch (IOException e)
        {
            Log.e("NON-READ","Unable to read mac address from file.");
            throw new RuntimeException();
        }
    }

}
