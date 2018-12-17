package datebase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.chars.chars.Crime;

import static datebase.CrimeDbSchema.*;

public class CrimeBaseHelper extends SQLiteOpenHelper {

    public CrimeBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + CrimeTable.NAME + "(" +
                "_id integer primary key autoincrement " + "," +
                CrimeTable.Cols.UUID + "," +
                CrimeTable.Cols.DATE + "," +
                CrimeTable.Cols.TITLE + "," +
                CrimeTable.Cols.SOLVED + "," +
                CrimeTable.Cols.SUSPECT + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
