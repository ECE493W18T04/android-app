package com.example.reem.hudmobileapp.helper;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

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

    public VoiceCommandManager(Context context) {
        this.context = context;

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
        Log.d("TextToSpeech", "tts created");
        //if (!ready){
            Log.d("TextToSpeech", "tts speak");

            tts.speak(results.get(0),TextToSpeech.QUEUE_FLUSH ,null, "speak" );
        //}
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
