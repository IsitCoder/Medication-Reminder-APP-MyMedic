package my.edu.utar.mymedic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MedicationMenu extends AppCompatActivity {

    private ImageView homeButton;
    private Button medicationButton;
    private Button reminderButton;
    private Button reportButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_menu);

        homeButton = findViewById(R.id.home_button);
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

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit");
        builder.setMessage("Are you sure you want to exit?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                System.exit(0);
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

}