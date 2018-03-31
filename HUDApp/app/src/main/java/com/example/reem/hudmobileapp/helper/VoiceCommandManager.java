package com.example.reem.hudmobileapp.helper;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.ParcelFormatException;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.reem.hudmobileapp.activities.MainActivity;
import com.example.reem.hudmobileapp.ble.CharacteristicWriter;
import com.example.reem.hudmobileapp.constants.HUDObject;
import com.example.reem.hudmobileapp.constants.StateOverrideEnum;
import com.example.reem.hudmobileapp.constants.VoiceCommandsEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/**
 * Created by navjeetdhaliwal on 2018-03-19.
 */

public class VoiceCommandManager implements TextToSpeech.OnInitListener, RecognitionListener {

    private static final int SPEECH_REQUEST_CODE = 0;
    private SpeechRecognizer speech;
    private TextToSpeech tts;
    private boolean ready;
    private Intent intent;
    private Context context;
    private CharacteristicWriter writer;

    public VoiceCommandManager(Context context, CharacteristicWriter writer) {
        this.context = context;
        this.writer = writer;
        speech = SpeechRecognizer.createSpeechRecognizer(context);
        speech.setRecognitionListener(this);

        tts = new TextToSpeech(context, this);

        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                context.getPackageName());

    }
    public void startListener() {
        Log.d("VoiceCommandManager", "Starting listening");
        speech.startListening(intent);
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {

        Log.d("onReady", "service");
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float v) {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int i) {
        Log.d("VoiceCommand onError",Integer.toString(i));
    }

    @Override
    public void onResults(Bundle resultsBundle) {

        ArrayList<String> results = resultsBundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        Log.d("Results", results.get(0));

        //tts.speak(results.get(0),TextToSpeech.QUEUE_FLUSH ,null, "speak" );
        processText(results.get(0));
    }


    public void processText(String message)
    {
        tts.speak(message,TextToSpeech.QUEUE_FLUSH ,null, "speak" );
        HUDObject hudObject = FileManager.loadFromFile(context);
        if (message.toLowerCase().contains(VoiceCommandsEnum.CHANGE_COLOR_RED.getValue()))
        {

            hudObject.setHue(0);
            hudObject.setSaturation(100);
            hudObject.setHsvBrightness(100);
            FileManager.saveToFile(context, hudObject);
            writer.setHUDObject(hudObject);
            writer.writeColor();
            tts.speak("Color has been Set to "+VoiceCommandsEnum.CHANGE_COLOR_RED.getValue(),TextToSpeech.QUEUE_FLUSH ,null, "speak" );

        }
        else if (message.toLowerCase().contains(VoiceCommandsEnum.CHANGE_COLOR_BLUE.getValue()))
        {
            hudObject.setHue(240);
            hudObject.setSaturation(100);
            hudObject.setHsvBrightness(50);
            FileManager.saveToFile(context, hudObject);
            writer.setHUDObject(hudObject);
            writer.writeColor();
            tts.speak("Color has been Set to "+VoiceCommandsEnum.CHANGE_COLOR_BLUE.getValue(),TextToSpeech.QUEUE_FLUSH ,null, "speak" );
        }else if (message.toLowerCase().contains(VoiceCommandsEnum.CHANGE_COLOR_GREEN.getValue()))
        {
            hudObject.setHue(120);
            hudObject.setSaturation(100);
            hudObject.setHsvBrightness(100);
            writer.setHUDObject(hudObject);
            FileManager.saveToFile(context, hudObject);
            writer.writeColor();
            tts.speak("Color has been Set to "+VoiceCommandsEnum.CHANGE_COLOR_GREEN.getValue(),TextToSpeech.QUEUE_FLUSH ,null, "speak" );
        }else if (message.toLowerCase().contains(VoiceCommandsEnum.CHANGE_COLOR_WHITE.getValue()))
        {
            hudObject.setHue(0);
            hudObject.setSaturation(0);
            hudObject.setHsvBrightness(100);
            FileManager.saveToFile(context, hudObject);
            writer.setHUDObject(hudObject);
            writer.writeColor();
            tts.speak("Color has been Set to "+VoiceCommandsEnum.CHANGE_COLOR_WHITE.getValue(),TextToSpeech.QUEUE_FLUSH ,null, "speak" );

        }else if (message.toLowerCase().contains(VoiceCommandsEnum.CHNAGE_COLOR_PURPLE.getValue()))
        {
            hudObject.setHue(270);
            hudObject.setSaturation(100);
            hudObject.setHsvBrightness(100);
            FileManager.saveToFile(context, hudObject);
            writer.setHUDObject(hudObject);
            writer.writeColor();
            tts.speak("Color has been Set to "+VoiceCommandsEnum.CHNAGE_COLOR_PURPLE.getValue(),TextToSpeech.QUEUE_FLUSH ,null, "speak" );

        }else if (message.toLowerCase().contains(VoiceCommandsEnum.CHANGE_BRIGHTNESS.getValue())){
            if (message.toLowerCase().contains("auto")) {
                if (message.toLowerCase().contains("off")){
                    tts.speak("Auto Brightness has been turned Off",TextToSpeech.QUEUE_FLUSH ,null, "speak" );
                    hudObject.setAuto_brightness(false);
                    FileManager.saveToFile(context, hudObject);
                    writer.setHUDObject(hudObject);
                    writer.writeHUDBrightness();
                }else if (message.toLowerCase().contains("on")) {
                    tts.speak("auto brightness has been turned on",TextToSpeech.QUEUE_FLUSH ,null, "speak" );
                    hudObject.setAuto_brightness(true);
                    FileManager.saveToFile(context, hudObject);
                    writer.setHUDObject(hudObject);
                    writer.writeHUDBrightness();
                }else{
                    tts.speak("please repeat that",TextToSpeech.QUEUE_FLUSH ,null, "speak" );

                }
            }
            Log.d("VoiceCommand", message);
            String number = message.replaceAll("[^0-9]", "");
            Log.d("VoiceCommand", number);
            if(number.isEmpty()) {
                tts.speak("No Brightness given, Please select a value between 0 and 100",TextToSpeech.QUEUE_FLUSH ,null, "speak" );

            }
            int num = Integer.parseInt(number);
            if(num > 100 || num < 0) {
                tts.speak("an Invalid brightness, Please select a value between 0 and 100",TextToSpeech.QUEUE_FLUSH ,null, "speak" );
            }else {
                hudObject.setBrightness(num);
                hudObject.setAuto_brightness(false);
                FileManager.saveToFile(context, hudObject);
                writer.setHUDObject(hudObject);
                writer.writeHUDBrightness();
                tts.speak("Brightness has been Set to"+number,TextToSpeech.QUEUE_FLUSH ,null, "speak" );
            }
        }else if (message.toLowerCase().contains(VoiceCommandsEnum.CHANGE_OVERRIDE.getValue()))
        {
            tts.speak("Will start override",TextToSpeech.QUEUE_FLUSH ,null, "speak" );
            if (message.toLowerCase().contains(VoiceCommandsEnum.CLOCK_OVERRIDE.getValue()))
            {
                writer.stateOverride(StateOverrideEnum.CLOCK.getValue());
            }else if (message.toLowerCase().contains(VoiceCommandsEnum.VEHICLE_SPEED_OVERRIDE.getValue()))
            {
                writer.stateOverride(StateOverrideEnum.VEHICLE.getValue());
            }else if (message.toLowerCase().contains(VoiceCommandsEnum.MUSIC_OVERRIDE.getValue()))
            {
                writer.stateOverride(StateOverrideEnum.MUSIC.getValue());
            }else if (message.toLowerCase().contains(VoiceCommandsEnum.FUEL_OVERRIDE.getValue()))
            {
                writer.stateOverride(StateOverrideEnum.FUEL.getValue());
            }else if (message.toLowerCase().contains(VoiceCommandsEnum.NAVIGATION_OVERRIDE.getValue()))
            {
                writer.stateOverride(StateOverrideEnum.NAVIGATION.getValue());
            }else if (message.toLowerCase().contains(VoiceCommandsEnum.PHONE_OVERRIDE.getValue()))
            {
                tts.speak("About to override call",TextToSpeech.QUEUE_FLUSH ,null, "speak" );
                        Log.e("OVERRIDE","ABout to override call");
                writer.stateOverride(StateOverrideEnum.PHONE.getValue());
            }else if (message.toLowerCase().contains(VoiceCommandsEnum.DISABLE_OVERRIDE.getValue()))
            {
                writer.stateOverride(StateOverrideEnum.DISABLE.getValue());
            }else
            {
                tts.speak("Sorry, that is in invalid override request",TextToSpeech.QUEUE_FLUSH ,null, "speak" );
            }
        }
    }

    @Override
    public void onPartialResults(Bundle bundle) {

    }

    @Override
    public void onEvent(int i, Bundle bundle) {}


    @Override
    public void onInit(int i) {
        Log.d("TextToSpeech", "onInit");

        if (i == TextToSpeech.SUCCESS) {
            Log.d("TextToSpeech", "success");

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
                ready = false;
            } else {
                Log.d("TextToSpeech", "tts ready");
                ready = true;
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
            ready = false;
        }
    }
}
