package com.example.chars.chars;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import datebase.CrimeBaseHelper;
import datebase.CrimeDbSchema;

import static datebase.CrimeDbSchema.*;

public class CrimeLab {
    private static CrimeLab sCrimeLab = null;
    private Context mContext;
    private SQLiteDatabase mDatebase;
    private static final String DATEBASE_NAME = "crimebase.db";
    private static final int VERSION = 1;

    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatebase = new CrimeBaseHelper(mContext,DATEBASE_NAME,null,VERSION).
                getWritableDatabase();
    }

    private ContentValues getContentValues(Crime crime){
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.TITLE,crime.getTitle());
        values.put(CrimeTable.Cols.DATE,crime.getDate().toString());
        values.put(CrimeTable.Cols.SOLVED,crime.getChecked());
        values.put(CrimeTable.Cols.UUID,crime.getId().toString());
        values.put(CrimeTable.Cols.SUSPECT,crime.getSuspect());

        return values;
    }

    public File getPhotoFile(Crime crime){
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (externalFilesDir == null)
            return null;
        return new File(externalFilesDir,crime.getPhotoFilename());
    }

    public void addCrime(Crime crime){
        ContentValues values = getContentValues(crime);
        mDatebase.insert(CrimeTable.NAME,null,values);
    }

    public void updateCrime(Crime crime){
        UUID id = crime.getId();
        ContentValues values = getContentValues(crime);
        mDatebase.update(CrimeTable.NAME,values,CrimeTable.Cols.UUID + " = ?",
                new String[]{id.toString()});
    }

    public CrimeCursorWrapper queryCrimes(String WhereClause,String[] WhereArgs){
        Cursor cursor = mDatebase.query(CrimeTable.NAME,null,WhereClause,WhereArgs,
                null,null,null);
        return new CrimeCursorWrapper(cursor);
    }

    public static CrimeLab getInstance(Context context) {
        if (sCrimeLab == null) {
            synchronized (CrimeLab.class) {
                if (sCrimeLab == null) {
                    sCrimeLab = new CrimeLab(context);
                }
            }
        }
        return sCrimeLab;
    }

    public List<Crime> getCrimeList() {
        List<Crime> crimeList = new ArrayList<>();
        CrimeCursorWrapper cursorWrapper = queryCrimes(null,null);
        try {
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()){
                crimeList.add(cursorWrapper.getCrime());
                cursorWrapper.moveToNext();
            }
        }finally {
            cursorWrapper.close();
        }
        return crimeList;
    }

    public void deleteCrime(Crime crime){
        UUID uuid = crime.getId();
        mDatebase.delete(CrimeTable.NAME,CrimeTable.Cols.UUID + " = ?",
                new String[]{uuid.toString()});
    }

    public Crime getCrime(UUID id){
        CrimeCursorWrapper cursorWrapper = queryCrimes(CrimeTable.Cols.UUID + " = ?",
                new String[]{id.toString()});

        try {
            if (cursorWrapper.getCount() == 0)
                return null;
            cursorWrapper.moveToFirst();
            return cursorWrapper.getCrime();
        }finally {
            cursorWrapper.close();
        }
    }


}
