package com.example.reem.hudmobileapp.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.example.reem.hudmobileapp.R;
import com.example.reem.hudmobileapp.activities.MainActivity;
import com.example.reem.hudmobileapp.constants.HUDObject;
import com.example.reem.hudmobileapp.helper.FileManager;

/**
 * Created by Reem on 2018-03-16.
 *
 * This the brightness dialog class for the user. Meets requirements:
 * REQ-A-4.5.3.2
 */

public class BrightnessDialog extends DialogFragment {

    private Boolean autoBrightness;
    private int brightnessControl;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.brightness_control, null));

        autoBrightness = getArguments().getBoolean("autobrighness");
        brightnessControl = getArguments().getInt("brightnessLevel");


        builder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onDialogPositiveClick(BrightnessDialog.this);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onDialogNegativeClick(BrightnessDialog.this);
            }
        });

        return builder.create();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        getDialog().getWindow().setBackgroundDrawableResource(R.color.darkGray);
        CheckBox autoBrightnessBox = (CheckBox) getDialog().findViewById(R.id.autoBrightness);
        SeekBar brightnessControlBar = (SeekBar) getDialog().findViewById(R.id.brightnessSeekBar);
        autoBrightnessBox.setChecked(autoBrightness);
        brightnessControlBar.setProgress(brightnessControl);
    }
    public interface BrightnessDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    BrightnessDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (BrightnessDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ");
        }
    }
}
