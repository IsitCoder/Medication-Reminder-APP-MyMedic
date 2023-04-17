package my.edu.utar.mymedic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import my.edu.utar.mymedic.ReminderSQLiteAdapter;
import my.edu.utar.mymedic.model.Reminder;
import my.edu.utar.mymedic.model.reminderMedicineDto;

public class AddReminder extends AppCompatActivity {

    private ImageButton homeButton;
    private Button addThisReminderButton;
    private ImageButton medicationButton;
    private ImageButton reminderButton;
    private ImageButton reportButton;

    private ReminderSQLiteAdapter remindSQLite;
    private int mid;
    private String medicineName;
    private double dose;

    private int NOTIFICATION_PERMISSION_CODE =1;

    private Calendar c1=Calendar.getInstance();
    private ArrayList<reminderMedicineDto> medicines = new ArrayList<reminderMedicineDto>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        addThisReminderButton = findViewById(R.id.addthisreminder_button);
        medicationButton = findViewById(R.id.medication_button);
        reminderButton = findViewById(R.id.reminder_button);
        reportButton = findViewById(R.id.report_button);


        remindSQLite= new ReminderSQLiteAdapter(this);



        Thread_GetMedicinesName getMedicinesName = new Thread_GetMedicinesName();
        getMedicinesName.start();

        try {
            getMedicinesName.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Spinner spinner = findViewById(R.id.medicine_option);
        ArrayAdapter<reminderMedicineDto> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, medicines);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String selectedItem = parent.getItemAtPosition(position).toString();
//                adapter.notifyDataSetChanged();
                reminderMedicineDto medicine = (reminderMedicineDto) parent.getItemAtPosition(position);
                mid=medicine.getId();
                medicineName=medicine.getMedicineName();
                dose=medicine.getDose();
                Toast.makeText(getApplicationContext(), "Selected item: " + medicine.toString(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle nothing selected here
            }
        });


        //here is for medicine name that saved in (add medicine) page

        final TextInputEditText startDateEditText = findViewById(R.id.start_date);
        final ImageButton startCalendarButton = findViewById(R.id.startcalendar_button);

        startCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(AddReminder.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                startDateEditText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

        final TextInputEditText endDateEditText = findViewById(R.id.end_date);
        final ImageButton endCalendarButton = findViewById(R.id.endcalendar_button);

        endCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddReminder.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                endDateEditText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

        final TextInputEditText timeEditText = findViewById(R.id.reminder_time);
        final ImageButton timeButton = findViewById(R.id.clock_button);

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(AddReminder.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                timeEditText.setText(String.format("%02d:%02d", hourOfDay, minute));
                                c1.set(Calendar.HOUR_OF_DAY,hourOfDay);
                                c1.set(Calendar.MINUTE,minute);
                                c1.set(Calendar.SECOND,0);
                            }
                        }, hour, minute, DateFormat.is24HourFormat(AddReminder.this) );
                timePickerDialog.show();
            }
        });

        addThisReminderButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                String startDate = startDateEditText.getText().toString();
                String endDate = endDateEditText.getText().toString();
                String time = timeEditText.getText().toString();


                remindSQLite.open();
                remindSQLite.insertReminder(mid,medicineName,startDate,endDate,time);
                Reminder reminder = remindSQLite.getReminderbymid(mid);
                remindSQLite.close();

                scheduleAlarm(c1,reminder.getId());
                Log.d("Alarm","Setting");
                Intent i = new Intent(AddReminder.this,ReminderMenu.class);
                startActivity(i);
            }
        });

        medicationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddReminder.this, MedicationMenu.class);
                startActivity(intent);
            }
        });

        reminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddReminder.this, ReminderMenu.class);
                startActivity(intent);
            }
        });

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddReminder.this, ReportMenu.class);
                startActivity(intent);
            }
        });
    }


    private void scheduleAlarm(Calendar c, int Alarmid){
        if(ContextCompat.checkSelfPermission(AddReminder.this, Manifest.permission.POST_NOTIFICATIONS)== PackageManager.PERMISSION_GRANTED)
        {


            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, AlertReceiver.class);
            intent.putExtra("key",String.valueOf(Alarmid));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Alarmid, intent, PendingIntent.FLAG_IMMUTABLE);

//        if(c.before(Calendar.getInstance())){
//            c.add(Calendar.DATE,1);
//        }
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
            Toast.makeText(AddReminder.this,"Reminder Add ",Toast.LENGTH_SHORT).show();
            Log.d("Reminder", "Reminder Add");
        }else
        {
            requestNotificationPermissions();
        }
    }

    private void cancelAlarm()
    {
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

    }


    private void requestNotificationPermissions(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.POST_NOTIFICATIONS))
        {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage("Notification Permission is needed to notify user the reminder")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(AddReminder.this,new String[]{Manifest.permission.POST_NOTIFICATIONS},NOTIFICATION_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.POST_NOTIFICATIONS},NOTIFICATION_PERMISSION_CODE);
        }
    }

    private class Thread_GetMedicinesName extends Thread {
        private String TAG = "Thread_GetMedicinesName";
        private int id;
        private String result;
        private Handler mHandler;

        public void run() {
            try {
                URL url = new URL("https://bczsansikazvyoywabmo.supabase.co/rest/v1/Medicine?select=id,MedicineName,Dose");
                HttpURLConnection hc = (HttpURLConnection) url.openConnection();

                Log.i(TAG, url.toString());

                hc.setRequestProperty("apikey", getString(R.string.SUPABASE_KEY1));
                hc.setRequestProperty("Authorization", "Bearer " + getString(R.string.SUPABASE_KEY1));

                if(hc.getResponseCode()==200) {

                    InputStream input = hc.getInputStream();
                    result = readStream(input);
                    input.close();
                    Log.i(TAG,"HTTP GET request successful");
                    Log.i(TAG,"Output"+result);

                    JSONArray InfoArray = new JSONArray(result);
                    for (int i = 0; i < InfoArray.length(); i++) {
                        int id = InfoArray.getJSONObject(i).getInt("id");
                        String mName = InfoArray.getJSONObject(i).getString("MedicineName");
                        double mDose = InfoArray.getJSONObject(i).getDouble("Dose");

                        reminderMedicineDto medicine = new reminderMedicineDto(id, mName, mDose);
                        medicines.add(medicine);
                    }

                } else {
                    Log.i(TAG, "Response Code: " + hc.getResponseCode());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            }catch (IOException | JSONException e) {
                e.printStackTrace();
            }


        }
    }


    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new
                    ByteArrayOutputStream();
            int i = is.read();
            while (i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }

}