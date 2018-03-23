package com.example.reem.hudmobileapp.helper;

import android.content.Context;
import android.util.Log;

import com.example.reem.hudmobileapp.constants.HUDObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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


    // Code taken from Lonely Twitter from github: https://github.com/watts1/lonelyTwitter Sept 22, 2016
    public static HUDObject loadFromFile(Context context) {
        try {
            Log.e("ABOUTTOOPENFILE","ABout to open hud.sav");
            FileInputStream fis = context.openFileInput("hud.sav");
            Log.e("FOUNDHUD","Was about to file HUD");
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));
            Log.e("CREATEDBUFFERREADER","Buffer reader created");
            Gson gson = new Gson();
            HUDObject hudObject = gson.fromJson(in, HUDObject.class);

            //Code taken from stackOverFlow http://stackoverflow.com/questions/12384064/gson-convert-from-json-to-a-typed-arraylistt Sept 22,2016
            return hudObject;


        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        }

    }


    // Code taken from Lonely Twitter from github: https://github.com/watts1/lonelyTwitter Sept 22, 2016
    public static void saveToFile(Context context,HUDObject hudObject)
    {
        try {

            FileOutputStream fos = context.openFileOutput("hud.sav", 0);
            OutputStreamWriter writer = new OutputStreamWriter(fos);
            Gson gson = new Gson();
            gson.toJson(hudObject, writer);
            writer.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();

        }
    }



    private static final String MACFILE = "mac.sav";
    public static void saveMACAddress(Context context, ArrayList<String> macAddress)
    {
        try {

            FileOutputStream fos = context.openFileOutput(MACFILE, 0);
            OutputStreamWriter writer = new OutputStreamWriter(fos);
            Gson gson = new Gson();
            gson.toJson(macAddress, writer);
            writer.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();

        }
    }





    public static ArrayList<String> readMACAddress(Context context)
    {
        try
        {
            FileInputStream fis = context.openFileInput(MACFILE);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            ArrayList<String> lines= new ArrayList<String>();
            String line;
            while ((line=br.readLine()) != null){
                lines.add(line);
            }
            if (lines.size()==0)
                return null;
            return lines;
        }catch (IOException e)
        {
            Log.e("NON-READ","Unable to read mac address from file.");
            throw new RuntimeException();
        }
    }

}
