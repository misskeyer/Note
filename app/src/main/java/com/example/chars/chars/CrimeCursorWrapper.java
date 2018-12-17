package com.example.chars.chars;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

import datebase.CrimeDbSchema;

import static datebase.CrimeDbSchema.*;

public class CrimeCursorWrapper extends CursorWrapper {
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime(){
        String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        String suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));

        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setDate(new Date(date));
        crime.setTitle(title);
        crime.setSuspect(suspect);
        crime.setChecked(isSolved != 0);

        return crime;
    }
}
