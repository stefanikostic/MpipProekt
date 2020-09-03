package com.example.mpip.freeride.fragments;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.appcompat.*;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {

    @NonNull
    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity().getApplicationContext(), (TimePickerDialog.OnTimeSetListener) getActivity().getApplicationContext(), hour, minute,
                DateFormat.is24HourFormat(getActivity().getApplicationContext()));
    }
}
