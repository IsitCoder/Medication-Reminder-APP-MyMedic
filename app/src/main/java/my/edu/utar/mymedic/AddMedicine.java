package my.edu.utar.mymedic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddMedicine extends AppCompatActivity {

    private ImageButton homeButton;
    private Button addThisMedicineButton;
    private ImageButton medicationButton;
    private ImageButton reminderButton;
    private ImageButton reportButton;
    private ImageButton selectedButton;
    private ImageButton tabletButton;
    private ImageButton dropButton;
    private ImageButton injectionButton;
    private TextView medicineTypeTV;
    private TextInputEditText medicineName;
    private TextInputEditText initialVolume;
    private TextInputEditText dose;

    private Integer medicineType=0;

    private String mName;
    private int mType;
    private double mQuantity;
    private double mDose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        Handler handler = new Handler();

        homeButton = findViewById(R.id.home_button);
        addThisMedicineButton = findViewById(R.id.addthismedicine_button);
        medicationButton = findViewById(R.id.medication_button);
        reminderButton = findViewById(R.id.reminder_button);
        reportButton = findViewById(R.id.report_button);
        tabletButton = findViewById(R.id.tablet_button);
        dropButton = findViewById(R.id.drop_button);
        injectionButton = findViewById(R.id.injection_button);
        medicineTypeTV = findViewById(R.id.medicinetype_tv);
        medicineName = findViewById(R.id.medicine_name);
        initialVolume = findViewById(R.id.initial_volume);
        dose = findViewById(R.id.dose);
        TextView dosetypeTv = findViewById(R.id.dosetype);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddMedicine.this, UserMainMenu.class);
                startActivity(intent);
            }
        });

        tabletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medicineType = 1;
                medicineTypeTV.setText("Select Medicine Type: Tablet");
                dosetypeTv.setText("Dose (Tablet pills)");
            }
        });

        dropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medicineType = 2;
                medicineTypeTV.setText("Select Medicine Type: Drop");
                dosetypeTv.setText("Dose (Drop ml)");
            }
        });

        injectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medicineType = 3;
                medicineTypeTV.setText("Select Medicine Type: Injection");
                dosetypeTv.setText("Dose (Injection ml)");
            }
        });

        addThisMedicineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mName = medicineName.getText().toString().trim();
                Integer mType =medicineType;
                double mVolume ;
                double mDose ;
                String checkInitialVolume =initialVolume.getText().toString().trim();
                String checkDose = dose.getText().toString().trim();

                if(checkInitialVolume.isEmpty()||checkDose.isEmpty()) {
                    Toast.makeText(getApplicationContext(),"Please fill all the info! Cannot leave it blank",Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    mVolume =Double.parseDouble(initialVolume.getText().toString().trim()) ;
                    mDose = Double.parseDouble(dose.getText().toString().trim());
                }


                if (TextUtils.isEmpty(mName)) {
                    Toast.makeText(getApplicationContext(),"Please fill all the info! Cannot leave it blank",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mType<=0) {
                    Toast.makeText(getApplicationContext(),"Please select one of the medicine type",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mVolume<1||mDose<1) {
                    Toast.makeText(getApplicationContext(),"Initial Volume and Dose Volume cannot be 0 value",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mVolume<mDose) {
                    Toast.makeText(getApplicationContext(),"Initial Volume stock must be larger than Dose Volume",Toast.LENGTH_SHORT).show();
                    return;
                }

                AddMedicine.Thread_AddMedicine connectThread = new AddMedicine.Thread_AddMedicine(mName, mType, mVolume, mDose, handler);
                connectThread.start();

            }
        });

        medicationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddMedicine.this, MedicationMenu.class);
                startActivity(intent);
            }
        });

        reminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddMedicine.this, ReminderMenu.class);
                startActivity(intent);
            }
        });

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddMedicine.this, ReportMenu.class);
                startActivity(intent);
            }
        });
    }


    private class Thread_AddMedicine extends Thread {
        private String mName;
        private int mType;
        private double mQuantity;
        private double mDose;
        private Handler mHandler;
        private String TAG="AddMedicine";

        public Thread_AddMedicine(String mName, int mType,double mQuantity, double mDose, Handler handler) {
            this.mName = mName;
            this.mType = mType;
            this.mQuantity = mQuantity;
            this.mDose = mDose;
            this.mHandler = handler;
        }

        public void run() {
            try {
                URL url = new URL("https://bczsansikazvyoywabmo.supabase.co/rest/v1/Medicine");
                HttpURLConnection hc = (HttpURLConnection) url.openConnection();

                Log.i("AddMedicine", url.toString());


                hc.setRequestMethod("POST");
                hc.setRequestProperty("apikey", getString(R.string.SUPABASE_KEY1));
                hc.setRequestProperty("Authorization", "Bearer " + getString(R.string.SUPABASE_KEY1));
                hc.setRequestProperty("Content-Type", "application/json");
                hc.setRequestProperty("Prefer", "return=minimal");
                hc.setDoOutput(true);
                OutputStream output = hc.getOutputStream();
                JSONObject info = new JSONObject();
                info.put("MedicineName", mName);
                info.put("Volume", mQuantity);
                info.put("Dose", mDose);
                info.put("DosageTypeId", mType);
                output.write(info.toString().getBytes());
                output.flush();


                if (hc.getResponseCode() == 201) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Medicine Add Successful",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddMedicine.this, MedicationMenu.class);
                            startActivity(intent);
                        }
                    });
                } else {
                    Log.i(TAG, "Response Code: " + hc.getResponseCode());
                    int reponseCode = hc.getResponseCode();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Error code: "+reponseCode, Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                output.close();
            }catch (IOException | JSONException e) {
                e.printStackTrace();
            }

        }
    }

}