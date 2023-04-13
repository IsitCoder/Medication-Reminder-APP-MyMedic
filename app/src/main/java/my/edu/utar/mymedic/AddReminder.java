package my.edu.utar.mymedic;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class AddReminder extends AppCompatActivity {

    private ImageButton homeButton;
    private Button addThisReminderButton;
    private ImageButton medicationButton;
    private ImageButton reminderButton;
    private ImageButton reportButton;
    private int NOTIFICATION_PERMISSION_CODE =1;
    private int totalalarm = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        homeButton = findViewById(R.id.home_button);
        addThisReminderButton = findViewById(R.id.addthisreminder_button);
        medicationButton = findViewById(R.id.medication_button);
        reminderButton = findViewById(R.id.reminder_button);
        reportButton = findViewById(R.id.report_button);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddReminder.this, UserMainMenu.class);
                startActivity(intent);
            }
        });

        final Spinner optionSpinner = findViewById(R.id.medicine_option);
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


                                Calendar c1 = Calendar.getInstance();
                                c1.set(Calendar.HOUR_OF_DAY,hourOfDay);
                                c1.set(Calendar.MINUTE,minute);
                                c1.set(Calendar.SECOND,0);
                                scheduleAlarm(c1);
                                Log.d("Alarm","Setting");

                            }
                        }, hour, minute, DateFormat.is24HourFormat(AddReminder.this));
                timePickerDialog.show();
            }
        });

        addThisReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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


    private void scheduleAlarm(Calendar c){
        if(ContextCompat.checkSelfPermission(AddReminder.this, Manifest.permission.POST_NOTIFICATIONS)== PackageManager.PERMISSION_GRANTED)
        {


            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, AlertReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_IMMUTABLE);

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
                    .setMessage("Notificaiton Permission is needed to notify user the reminder")
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

}