package com.example.personale.noteexam.controller.utilities;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.CalendarView;

import com.rarepebble.colorpicker.ColorPickerView;

/**
 * Created by personale on 13/03/2017.
 */

public class Utilities {
    public static AlertDialog.Builder createCalendar(Context context, CalendarView calendarView){
        AlertDialog.Builder calendarBuilder = new AlertDialog.Builder(context);
        calendarBuilder.setView(calendarView);
        calendarBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Nothing
            }
        });

        return calendarBuilder;
    }

    public static AlertDialog.Builder createPicker(Context context, ColorPickerView colorPickerView) {
        AlertDialog.Builder colorPicker = new AlertDialog.Builder(context);
        colorPicker.setView(colorPickerView);
        return colorPicker;
    }
}
