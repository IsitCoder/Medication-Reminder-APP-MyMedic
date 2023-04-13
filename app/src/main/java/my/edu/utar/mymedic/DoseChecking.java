package my.edu.utar.mymedic;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class DoseChecking extends AppCompatActivity {

    private ImageView logoImageView;
    private Button fiveMinButton;
    private Button fifteenMinButton;
    private Button thirtyMinButton;
    private Button takenButton;
    private Button skipButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dose_checking);

        logoImageView = findViewById(R.id.logoImageView);
        fiveMinButton = findViewById(R.id.fivemin_button);
        fifteenMinButton = findViewById(R.id.fifteenmin_button);
        thirtyMinButton = findViewById(R.id.thirtymin_button);
        takenButton = findViewById(R.id.taken_button);
        skipButton = findViewById(R.id.skip_button);

        fiveMinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        fifteenMinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        thirtyMinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        takenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Skip and Exit");
        builder.setMessage("Are you sure you want to skip the medication and exit?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                skipButton.performClick();
                finish();
                System.exit(0);
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }
}