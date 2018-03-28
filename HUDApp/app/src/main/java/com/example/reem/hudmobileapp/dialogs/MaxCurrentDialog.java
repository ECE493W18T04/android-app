package com.example.reem.hudmobileapp.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;

import com.example.reem.hudmobileapp.R;

/**
 * Created by Reem on 2018-03-26.
 */

public class MaxCurrentDialog extends DialogFragment {

    private int maxCurrent;
    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.max_current_layout, null));
        builder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onMaxCurrentDialogPositiveClick(MaxCurrentDialog.this);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onMaxCurrentDialogNegativeClick(MaxCurrentDialog.this);
            }
        });

        maxCurrent = getArguments().getInt("maxLevel");
        return builder.create();
    }
    @Override
    public void onStart()
    {
        super.onStart();
        getDialog().getWindow().setBackgroundDrawableResource(R.color.darkGray);
        EditText maxCurrentBox = (EditText) getDialog().findViewById(R.id.editMax);
        String maxCurrentString = Integer.toString(maxCurrent);
        maxCurrentBox.setText(maxCurrentString);
    }
    public interface MaxCurrentDialogListener {
        public void onMaxCurrentDialogPositiveClick(DialogFragment dialog);
        public void onMaxCurrentDialogNegativeClick(DialogFragment dialog);
    }

    MaxCurrentDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (MaxCurrentDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement BrightnessDialogListener");
        }
    }
}
