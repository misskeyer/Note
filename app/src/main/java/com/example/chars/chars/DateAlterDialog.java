package com.example.chars.chars;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;
import java.util.zip.Inflater;

public class DateAlterDialog extends DialogFragment {
    private DatePicker datePicker;

    public static final String DATE = "dateAlterDialog.picker";
    public static final String DATE_RETURNED ="com.example.chars.date.returned";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date,null);
        datePicker = view.findViewById(R.id.dp_date);
        Date date = (Date) getArguments().getSerializable(DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        datePicker.init(year,month,day,null);
        return new AlertDialog.Builder(getActivity()).setTitle("Date picker")
                .setView(view).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int year = datePicker.getYear();
                        int month = datePicker.getMonth();
                        int day = datePicker.getDayOfMonth();
                        Date date = new GregorianCalendar(year,month,day).getTime();
                        sendResult(Activity.RESULT_OK,date);
                    }
                }).create();
    }

    private void sendResult(int resultCode,Date date){
        if (getTargetFragment() == null)
            return;
        Intent i = new Intent();
        i.putExtra(DATE_RETURNED,date);
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,i);
    }

    public static DateAlterDialog newInstance(Date date){
        Bundle bundle = new Bundle();
        bundle.putSerializable(DATE,date);
        DateAlterDialog fragment = new DateAlterDialog();
        fragment.setArguments(bundle);
        return fragment;
    }
}
