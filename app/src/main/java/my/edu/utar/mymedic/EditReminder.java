package my.edu.utar.mymedic;

import androidx.annotation.NonNull;
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
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import my.edu.utar.mymedic.model.Reminder;
import my.edu.utar.mymedic.model.reminderMedicineDto;

public class EditReminder extends AppCompatActivity {

    private ImageButton homeButton;
    private Button saveThisReminderButton;
    private ImageButton medicationButton;
    private ImageButton reminderButton;
    private ImageButton reportButton;
    private TextInputEditText reminderName;

    private ReminderSQLiteAdapter remindSQLite;
    private Reminder reminder;
    private int mid;
    private String medicineName;
    private double dose;
    private int key;
    private boolean timeset=false;

    private int NOTIFICATION_PERMISSION_CODE =1;

    private Calendar c1 = Calendar.getInstance();
    private ArrayList<reminderMedicineDto> medicines = new ArrayList<reminderMedicineDto>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_reminder);

        homeButton = findViewById(R.id.home_button);
        saveThisReminderButton = findViewById(R.id.savethisreminder_button);
        medicationButton = findViewById(R.id.medication_button);
        reminderButton = findViewById(R.id.reminder_button);
        reportButton = findViewById(R.id.report_button);



        EditReminder.Thread_GetMedicinesName getMedicinesName = new EditReminder.Thread_GetMedicinesName();
        getMedicinesName.start();

        try {
            getMedicinesName.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Spinner spinner = findViewById(R.id.rmedicine_option);
        ArrayAdapter<reminderMedicineDto> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, medicines);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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






        final TextInputEditText startDateEditText = findViewById(R.id.start_date);
        final ImageButton startCalendarButton = findViewById(R.id.startcalendar_button);

        startCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(EditReminder.this,
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

                DatePickerDialog datePickerDialog = new DatePickerDialog(EditReminder.this,
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

                TimePickerDialog timePickerDialog = new TimePickerDialog(EditReminder.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                timeEditText.setText(String.format("%02d:%02d", hourOfDay, minute));
                                c1.set(Calendar.HOUR_OF_DAY,hourOfDay);
                                c1.set(Calendar.MINUTE,minute);
                                c1.set(Calendar.SECOND,0);
                                timeset = true;
                            }
                        }, hour, minute, DateFormat.is24HourFormat(EditReminder.this));
                timePickerDialog.show();
            }
        });

        saveThisReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat format = new SimpleDateFormat("HH:mm");

                String startDate = startDateEditText.getText().toString();
                String endDate = endDateEditText.getText().toString();
                String time = timeEditText.getText().toString();

                remindSQLite.open();
                remindSQLite.updateReminder(key,mid,medicineName,startDate,endDate,time);
                remindSQLite.close();

                if(timeset == true) {
                    scheduleAlarm(c1, key);
                    Log.d("Alarm", "Setting");
                }else{
                    Toast.makeText(EditReminder.this,"Reminder Updated ",Toast.LENGTH_SHORT).show();
                    Log.d("Reminder", "Reminder Updated without rescheduled alarm");
                    Intent i = new Intent(EditReminder.this,ReminderMenu.class);
                    startActivity(i);
                }
            }
        });

        medicationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditReminder.this, MedicationMenu.class);
                startActivity(intent);
            }
        });

        reminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditReminder.this, ReminderMenu.class);
                startActivity(intent);
            }
        });

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditReminder.this, ReportMenu.class);
                startActivity(intent);
            }
        });


        //fetch info from SQLite to front

        Intent intent = getIntent();
        key = intent.getIntExtra("id",-1);

        if(key<0){
            Intent i = new Intent(getApplicationContext(),MedicationMenu.class);
            Toast.makeText(this,"Failed to connect data",Toast.LENGTH_SHORT).show();
            startActivity(i);
        }else{
            remindSQLite= new ReminderSQLiteAdapter(this);
            remindSQLite.open();
            reminder = remindSQLite.getReminder(key);
            remindSQLite.close();

            String CheckName;
            int index=-1;
            for(int i = 0; i<medicines.size();i++)
            {
                CheckName=medicines.get(i).getMedicineName();
                if(CheckName.equals(reminder.getMedicineName()))
                {
                    index = medicines.indexOf(medicines.get(i));
                }else{
                    continue;
                }
            }


            spinner.setSelection(index);
            timeEditText.setText(reminder.getAlarmTime());
            startDateEditText.setText(reminder.getStartDate());
            endDateEditText.setText(reminder.getEndDate());

        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();



        if (id == R.id.delete)
        {
            AlertDialog.Builder builder_logout = new AlertDialog.Builder(this);
            AlertDialog.Builder delete_success = new AlertDialog.Builder(this);
            builder_logout.setTitle("Delete");
            builder_logout.setMessage("Are you sure to delete this reminder?");
            builder_logout.setCancelable(false);
            builder_logout.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    remindSQLite= new ReminderSQLiteAdapter(EditReminder.this);
                    remindSQLite.open();
                    int deletesuccess=remindSQLite.deleteReminder(key);
                    remindSQLite.close();
                    cancelAlarm(key);
                    if(deletesuccess>0) {
                        delete_success.setTitle("Success");
                        delete_success.setMessage("Delete Successful !");
                        delete_success.setCancelable(false);
                        delete_success.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(EditReminder.this, ReminderMenu.class);
                                startActivity(intent);
                            }
                        });
                        delete_success.show();
                    }else{
                        delete_success.setTitle("Error");
                        delete_success.setMessage("Failed to delete!");
                        delete_success.setCancelable(false);
                        delete_success.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(EditReminder.this, ReminderMenu.class);
                                startActivity(intent);
                            }
                        });
                    }
                }
            });
            builder_logout.setNegativeButton("No", null);
            builder_logout.show();
            return true;
        }

        if (id == R.id.menu)
        {
            Intent intent = new Intent(EditReminder.this, MainActivity.class);
            startActivity(intent);
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void scheduleAlarm(Calendar c, int Alarmid){
        if(ContextCompat.checkSelfPermission(EditReminder.this, Manifest.permission.POST_NOTIFICATIONS)== PackageManager.PERMISSION_GRANTED)
        {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, AlertReceiver.class);
            intent.putExtra("medicineName",String.valueOf(medicineName));
            intent.putExtra("key",Alarmid);
            intent.putExtra("dose",dose);
            intent.putExtra("mid",mid);


            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Alarmid, intent, PendingIntent.FLAG_IMMUTABLE);

//        if(c.before(Calendar.getInstance())){
//            c.add(Calendar.DATE,1);
//        }
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
            Toast.makeText(EditReminder.this,"Reminder Updated ",Toast.LENGTH_SHORT).show();
            Intent i = new Intent(EditReminder.this,ReminderMenu.class);
            startActivity(i);
            Log.d("Reminder", "Reminder Updated");
        }else
        {
            requestNotificationPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void cancelAlarm(int reqcode)
    {
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, reqcode, intent, PendingIntent.FLAG_IMMUTABLE);

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
                            ActivityCompat.requestPermissions(EditReminder.this,new String[]{Manifest.permission.POST_NOTIFICATIONS},NOTIFICATION_PERMISSION_CODE);
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