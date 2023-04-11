package my.edu.utar.mymedic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class MedicationMenu extends AppCompatActivity {

    private ImageButton homeButton;
    private ImageButton addMedicineButton;
    private ImageButton medicationButton;
    private ImageButton reminderButton;
    private ImageButton reportButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_menu);

        homeButton = findViewById(R.id.home_button);
        addMedicineButton = findViewById(R.id.addmedicine_button);
        medicationButton = findViewById(R.id.medication_button);
        reminderButton = findViewById(R.id.reminder_button);
        reportButton = findViewById(R.id.report_button);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MedicationMenu.this, UserMainMenu.class);
                startActivity(intent);
            }
        });

        addMedicineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MedicationMenu.this, AddMedicine.class);
                startActivity(intent);
            }
        });

        medicationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "You are already on this page.", Toast.LENGTH_SHORT).show();
            }
        });

        reminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MedicationMenu.this, ReminderMenu.class);
                startActivity(intent);
            }
        });

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MedicationMenu.this, ReportMenu.class);
                startActivity(intent);
            }
        });

    }

}