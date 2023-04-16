package my.edu.utar.mymedic;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class medicineSQLiteAdapter extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "medicines.db";
    private static final int DATABASE_VERSION = 1;

    public medicineSQLiteAdapter(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE medicines (_id INTEGER PRIMARY KEY AUTOINCREMENT, mname TEXT, quantity DOUBLE, time TEXT);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS medicines");
        onCreate(db);
    }
}
