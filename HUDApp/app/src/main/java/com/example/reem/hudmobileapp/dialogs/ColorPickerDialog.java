package com.example.reem.hudmobileapp.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.RelativeLayout;

import com.example.reem.hudmobileapp.R;
import com.example.reem.hudmobileapp.views.ColorPicker;

/**
 * Created by Reem on 2018-03-14.
 *
 * This class is the color picker for the user.
 * Meets requirements: REQ-A-4.5.3.4
 */

public class ColorPickerDialog extends AlertDialog{

    // this is similar to the code on github
    private ColorPicker colorPicker;
    private final OnColorSelectedListener onColorSelectedListener;
    public ColorPickerDialog(Context context, int initialColor, OnColorSelectedListener onColorSelectedListener) {
        super(context);
        this.onColorSelectedListener = onColorSelectedListener;
        RelativeLayout relativeLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        colorPicker = new ColorPicker(context);
        colorPicker.setColor(initialColor);

        relativeLayout.addView(colorPicker, layoutParams);
        setButton(BUTTON_POSITIVE, context.getString(android.R.string.ok), onClickListener);
        setButton(BUTTON_NEGATIVE, context.getString(android.R.string.cancel), onClickListener);
        relativeLayout.setBackgroundColor(getContext().getResources().getColor(R.color.darkGray));
        setView(relativeLayout);
    }

    private OnClickListener onClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case BUTTON_POSITIVE:
                    int selectedColor = colorPicker.getColor();
                    onColorSelectedListener.onColorSelected(selectedColor);
                    break;
                case BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
            }
        }
    };




    public interface OnColorSelectedListener {
        public void onColorSelected(int color);
    }

}
