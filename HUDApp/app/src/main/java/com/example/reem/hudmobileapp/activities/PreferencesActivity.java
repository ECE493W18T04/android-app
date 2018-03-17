package com.example.reem.hudmobileapp.activities;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import com.example.reem.hudmobileapp.dialogs.BrightnessDialog;

/**
 * Created by Reem on 2018-03-16.
 */

public class PreferencesActivity extends FragmentActivity
        implements BrightnessDialog.BrightnessDialogListener {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        showBrightnessDialog();
    }
    public void showBrightnessDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new BrightnessDialog();

        dialog.show(getFragmentManager(),"BrightnessDialog");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
}
