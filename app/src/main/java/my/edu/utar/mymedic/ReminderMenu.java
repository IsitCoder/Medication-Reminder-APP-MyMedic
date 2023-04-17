package my.edu.utar.mymedic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import my.edu.utar.mymedic.model.Reminder;

public class ReminderMenu extends AppCompatActivity {

    private ImageButton homeButton;
    private ImageButton addReminderButton;
    private ImageButton medicationButton;
    private ImageButton reminderButton;
    private ImageButton reportButton;
    private ReminderSQLiteAdapter remindSQLite;
    private ArrayList<Reminder> reminders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_menu);

        homeButton = findViewById(R.id.home_button);
        addReminderButton = findViewById(R.id.addreminder_button);
        medicationButton = findViewById(R.id.medication_button);
        reminderButton = findViewById(R.id.reminder_button);
        reportButton = findViewById(R.id.report_button);


        remindSQLite= new ReminderSQLiteAdapter(this);
        remindSQLite.open();
        reminders = remindSQLite.getAllReminders();
        remindSQLite.close();

        RecyclerView recyclerView = findViewById(R.id.addreminder_recycleView);
        Reminder_RecycleViewAdapter adapter = new Reminder_RecycleViewAdapter(ReminderMenu.this,reminders);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ReminderMenu.this));
        Toast.makeText(getApplicationContext(), "Load successfully", Toast.LENGTH_SHORT).show();


        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReminderMenu.this, UserMainMenu.class);
                startActivity(intent);
            }
        });

        addReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReminderMenu.this, AddReminder.class);
                startActivity(intent);
            }
        });

        medicationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReminderMenu.this, MedicationMenu.class);
                startActivity(intent);
            }
        });

        reminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "You are already on this page.", Toast.LENGTH_SHORT).show();
            }
        });

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReminderMenu.this, ReportMenu.class);
                startActivity(intent);
            }
        });

    }

}