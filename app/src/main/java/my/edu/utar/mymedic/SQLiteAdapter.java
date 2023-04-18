package my.edu.utar.mymedic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteAdapter {

    public static final String MYDATABASE_NAME = "User_DATABASE";
    public static final String MYDATABASE_TABLE = "User_TABLE";
    public static final int MYDATABASE_VERSION = 1;
    public static final String KEY_CONTENT = "username";
    public static final String KEY_CONTENT2 = "email";
    public static final String KEY_CONTENT3 = "password";

    private static final String SCRIPT_CREATE_DATABASE = "create table " + MYDATABASE_TABLE + " ("
            + KEY_CONTENT + " text not null, "
            + KEY_CONTENT2 + " text not null, "
            + KEY_CONTENT3 + " text not null);";

    private static final String SCRIPT_UPDATE_DATABASE = "create table " + MYDATABASE_TABLE + " ("
            + KEY_CONTENT + " text not null, "
            + KEY_CONTENT2 + " text not null, "
            + KEY_CONTENT3 + " text not null);";

    private SQLiteHelper sqLiteHelper;
    private SQLiteDatabase sqLiteDatabase;
    private Context context;
    public SQLiteAdapter(Context c) {
        context = c;
    }

    public SQLiteAdapter openToRead() throws
            android.database.SQLException {
        sqLiteHelper = new SQLiteHelper(context, MYDATABASE_NAME, null, MYDATABASE_VERSION);
        sqLiteDatabase = sqLiteHelper.getReadableDatabase();
        return this;
    }

    public SQLiteAdapter openToWrite() throws
            android.database.SQLException {
        sqLiteHelper = new SQLiteHelper(context, MYDATABASE_NAME, null,MYDATABASE_VERSION);
        sqLiteDatabase = sqLiteHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        sqLiteHelper.close();
    }

    public long insert(String content, String content2, String content3) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_CONTENT, content);
        contentValues.put(KEY_CONTENT2, content2);
        contentValues.put(KEY_CONTENT3, content3);
        return sqLiteDatabase.insert(MYDATABASE_TABLE, null,
                contentValues);
    }

    public int deleteAll() {
        return sqLiteDatabase.delete(MYDATABASE_TABLE, null, null);
    }

    public String queueAll() {
        String[] columns = new String[] { KEY_CONTENT, KEY_CONTENT2, KEY_CONTENT3 };
        Cursor cursor = sqLiteDatabase.query(MYDATABASE_TABLE, columns, null,
                null, null, null, null);
        String result = "";
        int index_CONTENT = cursor.getColumnIndex(KEY_CONTENT);
        int index_CONTENT2 = cursor.getColumnIndex(KEY_CONTENT2);
        int index_CONTENT3 = cursor.getColumnIndex(KEY_CONTENT3);
        for (cursor.moveToFirst(); !(cursor.isAfterLast());
             cursor.moveToNext()) {
            result = result + cursor.getString(index_CONTENT) + "  "
                    + cursor.getString(index_CONTENT2) + "  "
                    + cursor.getString(index_CONTENT3) + "\n";
        }
        return result;
    }

    public boolean check(){
        String count = "SELECT count(*) FROM User_TABLE";
        Cursor cursor = sqLiteDatabase.rawQuery(count, null);
        cursor.moveToFirst();
        int icount = cursor.getInt(0);
        if(icount > 0)
        {
            return true;
        } else return false;
    }

    public String welcome(){
        String[] columns = new String[] { KEY_CONTENT, KEY_CONTENT2, KEY_CONTENT3 };
        Cursor cursor = sqLiteDatabase.query(MYDATABASE_TABLE, columns, null,
                null, null, null, null);
        String result = "";
        int index_CONTENT = cursor.getColumnIndex(KEY_CONTENT);
        for (cursor.moveToFirst(); !(cursor.isAfterLast());
             cursor.moveToNext()) {
            result = result + cursor.getString(index_CONTENT);
        }
        return result;
    }

    public String getEmail() {
        String[] columns = new String[]{KEY_CONTENT2};
        Cursor cursor = sqLiteDatabase.query(MYDATABASE_TABLE, columns,
                null, null, null, null, null);
        String result = "";
        int i = 0;
        int index_CONTENT_1 = cursor.getColumnIndex(KEY_CONTENT2);
        for (cursor.moveToFirst(); !(cursor.isAfterLast());
             cursor.moveToNext()) {
            result = cursor.getString(index_CONTENT_1);
            i++;
        }
        return result;
    }


    public static class SQLiteHelper extends SQLiteOpenHelper {
        public SQLiteHelper(Context context, String name,
                            SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SCRIPT_CREATE_DATABASE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SCRIPT_UPDATE_DATABASE);
        }
    }
}
