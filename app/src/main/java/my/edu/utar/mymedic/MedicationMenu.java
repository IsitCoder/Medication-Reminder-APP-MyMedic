package my.edu.utar.mymedic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import my.edu.utar.mymedic.model.medicineDto;

public class MedicationMenu extends AppCompatActivity {

    public ArrayList<medicineDto> medicineList = new ArrayList<medicineDto>();

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




        Thread_GetMedicines getMedicines = new Thread_GetMedicines();
        getMedicines.start();
    }


    private class Thread_GetMedicines extends Thread {
        private String TAG = "GetMedicine";

        public void run() {
            try {
                URL url = new URL("https://bczsansikazvyoywabmo.supabase.co/rest/v1/Medicine?select=id,MedicineName,Volume,Dose,DosageType(DosageName)");
                HttpURLConnection hc = (HttpURLConnection) url.openConnection();

                Log.i(TAG, url.toString());

                hc.setRequestProperty("apikey", getString(R.string.SUPABASE_KEY1));
                hc.setRequestProperty("Authorization", "Bearer " + getString(R.string.SUPABASE_KEY1));




                if(hc.getResponseCode()==200) {

                    InputStream input = hc.getInputStream();
                    String result = readStream(input);
                    input.close();
                    Log.i(TAG,"HTTP GET request successful");
                    Log.i(TAG,"Output"+result);


                    JSONArray InfoArray = new JSONArray(result);

                    int s = InfoArray.length();
                    for (int i = 0; i < InfoArray.length(); i++) {
                        String medicineName = InfoArray.getJSONObject(i).get("MedicineName").toString();
                        double volume = InfoArray.getJSONObject(i).getDouble("Volume");
                        double dose = InfoArray.getJSONObject(i).getDouble("Dose");
                        int id = InfoArray.getJSONObject(i).getInt("id");
                        medicineDto m = new medicineDto(id,medicineName,dose,volume);
                        medicineList.add(m);
                    }


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RecyclerView recyclerView = findViewById(R.id.addmedicine_recycleView);
                            Medicine_RecycleViewAdapter adapter = new Medicine_RecycleViewAdapter(MedicationMenu.this,medicineList);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(MedicationMenu.this));
                            Toast.makeText(getApplicationContext(), "Load successfully", Toast.LENGTH_SHORT).show();
                        }
                    });

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