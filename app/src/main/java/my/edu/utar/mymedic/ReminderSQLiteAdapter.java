package my.edu.utar.mymedic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Date;
import java.util.ArrayList;

import my.edu.utar.mymedic.model.Reminder;

public class ReminderSQLiteAdapter {

    private static final String DATABASE_NAME = "reminders.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "reminder";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_MEDICINE_ID = "medicine_id";
    private static final String COLUMN_MEDICINE_NAME = "medicine_name";
    private static final String COLUMN_START_DATE = "start_date";
    private static final String COLUMN_END_DATE = "end_date";
    private static final String COLUMN_ALARM_TIME = "alarm_time";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_MEDICINE_ID + " INTEGER, "
            + COLUMN_MEDICINE_NAME + " TEXT, "
            + COLUMN_START_DATE + " TEXT, "
            + COLUMN_END_DATE + " TEXT, "
            + COLUMN_ALARM_TIME + " TEXT"
            + ");";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    private SQLiteDatabase db;
    private SQLiteOpenHelper dbHelper;

    public ReminderSQLiteAdapter(Context context) {
        dbHelper = new SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL(CREATE_TABLE);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL(DROP_TABLE);
                onCreate(db);
            }
        };
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        db.close();
    }

    public long insertReminder(int medicineId, String medicineName, String startDate, String endDate, String alarmTime) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_MEDICINE_ID, medicineId);
        values.put(COLUMN_MEDICINE_NAME, medicineName);
        values.put(COLUMN_START_DATE, startDate);
        values.put(COLUMN_END_DATE, endDate);
        values.put(COLUMN_ALARM_TIME, alarmTime);
        return db.insert(TABLE_NAME, null, values);
    }

    public ArrayList<Reminder> getAllReminders() {
        ArrayList<Reminder> reminders = new ArrayList<>();
        String[] columns = {COLUMN_ID, COLUMN_MEDICINE_ID, COLUMN_MEDICINE_NAME, COLUMN_START_DATE, COLUMN_END_DATE, COLUMN_ALARM_TIME};
        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            int medicineId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MEDICINE_ID));
            String medicineName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEDICINE_NAME));
            String startDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_DATE));
            String endDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_DATE));
            String alarmTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALARM_TIME));
            Reminder reminder = new Reminder(id, medicineId, medicineName, startDate, endDate, alarmTime);
            reminders.add(reminder);
        }
        cursor.close();
        return reminders;
    }

    public Reminder getReminder(int key) {
        Reminder reminder = null;
        String[] columns = {COLUMN_ID, COLUMN_MEDICINE_ID, COLUMN_MEDICINE_NAME, COLUMN_START_DATE, COLUMN_END_DATE, COLUMN_ALARM_TIME};
        Cursor cursor = db.query(TABLE_NAME, columns, COLUMN_ID+"=?", new String[]{String.valueOf(key)}, null, null, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            int medicineId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MEDICINE_ID));
            String medicineName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEDICINE_NAME));
            String startDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_DATE));
            String endDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_DATE));
            String alarmTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALARM_TIME));
            reminder = new Reminder(id, medicineId, medicineName, startDate, endDate, alarmTime);

        }
        cursor.close();
        return reminder;
    }

    public Reminder getReminderbymid(int key,String time) {
        Reminder reminder = null;
        String[] columns = {COLUMN_ID, COLUMN_MEDICINE_ID, COLUMN_MEDICINE_NAME, COLUMN_START_DATE, COLUMN_END_DATE, COLUMN_ALARM_TIME};
        Cursor cursor = db.query(TABLE_NAME, columns, COLUMN_MEDICINE_ID+"=? && "+COLUMN_ALARM_TIME+"=?", new String[]{String.valueOf(key),time}, null, null, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            int medicineId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MEDICINE_ID));
            String medicineName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEDICINE_NAME));
            String startDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_DATE));
            String endDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_DATE));
            String alarmTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALARM_TIME));
            reminder = new Reminder(id, medicineId, medicineName, startDate, endDate, alarmTime);

        }
        cursor.close();
        return reminder;
    }

    public void updateReminder(int id, int medicineId, String medicineName, String startDate, String endDate, String alarmTime) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_MEDICINE_ID, medicineId);
        values.put(COLUMN_MEDICINE_NAME, medicineName);
        values.put(COLUMN_START_DATE, startDate);
        values.put(COLUMN_END_DATE, endDate);
        values.put(COLUMN_ALARM_TIME, alarmTime);
        String[] whereArgs = {String.valueOf(id)};
        db.update(TABLE_NAME, values,COLUMN_ID+"=?", whereArgs);
    }

}
