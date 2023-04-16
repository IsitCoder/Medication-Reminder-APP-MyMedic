package my.edu.utar.mymedic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

import my.edu.utar.mymedic.model.medicineDto;

public class EditMedicine extends AppCompatActivity {

    private ImageButton homeButton;
    private Button saveThisMedicineButton;
    private ImageButton medicationButton;
    private ImageButton reminderButton;
    private ImageButton reportButton;
    private ImageButton tabletButton;
    private ImageButton dropButton;
    private ImageButton injectionButton;
    private TextView medicineTypeTV;
    private TextInputEditText medicineName;
    private TextInputEditText initialVolume;
    private TextInputEditText dose;
    private int mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_medicine);

        Handler handler = new Handler();

        homeButton = findViewById(R.id.home_button);
        saveThisMedicineButton = findViewById(R.id.savethismedicine_button);
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
        TextView dosetypeTv = findViewById(R.id.dosetypeTv);


        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditMedicine.this, UserMainMenu.class);
                startActivity(intent);
            }
        });

        tabletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mType = 1;
                medicineTypeTV.setText("Select Medicine Type: Tablet");
                dosetypeTv.setText("Dose (Tablet pills)");
            }
        });

        dropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mType = 2;
                medicineTypeTV.setText("Select Medicine Type: Drop");
                dosetypeTv.setText("Dose (Drop ml)");
            }
        });

        injectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mType = 3;
                medicineTypeTV.setText("Select Medicine Type: Injection");
                dosetypeTv.setText("Dose (Injection ml)");
            }
        });


        //write info back to edit text

        Intent intent = getIntent();
        int key = intent.getIntExtra("id",-1);

        if(key<0){
            Intent i = new Intent(getApplicationContext(),MedicationMenu.class);
            Toast.makeText(this,"Failed to connect data",Toast.LENGTH_SHORT).show();
            startActivity(i);
        }else{
            Thread_GetMedicine getMedicineinfo = new Thread_GetMedicine(key,handler);
            getMedicineinfo.start();
        }






        saveThisMedicineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mName = medicineName.getText().toString().trim();

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

                if (mVolume<=0||mDose<=0) {
                    Toast.makeText(getApplicationContext(),"Initial Volume and Dose Volume cannot be 0 value",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mVolume<mDose) {
                    Toast.makeText(getApplicationContext(),"Initial Volume stock must be larger than Dose Volume",Toast.LENGTH_SHORT).show();
                    return;
                }



                Thread_UpdateMedicine connectThread = new Thread_UpdateMedicine(key, mName, mType, mVolume, mDose, handler);
                connectThread.start();



            }
        });

        medicationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditMedicine.this, MedicationMenu.class);
                startActivity(intent);
            }
        });

        reminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditMedicine.this, ReminderMenu.class);
                startActivity(intent);
            }
        });

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditMedicine.this, ReportMenu.class);
                startActivity(intent);
            }
        });
    }




    private class Thread_UpdateMedicine extends Thread {
        private String mName;
        private int mdType;
        private double mQuantity;
        private double mDose;
        private Handler mHandler;
        private String TAG="Thread_UpdateMedicine";
        private int id;

        public Thread_UpdateMedicine(int id,String mName, int mdType,double mQuantity, double mDose, Handler handler) {
            this.id = id;
            this.mName = mName;
            this.mdType = mdType;
            this.mQuantity = mQuantity;
            this.mDose = mDose;
            this.mHandler = handler;
        }

        public void run() {
            try {
                URL url = new URL("https://bczsansikazvyoywabmo.supabase.co/rest/v1/Medicine?id=eq."+id);
                HttpURLConnection hc = (HttpURLConnection) url.openConnection();

                Log.i(TAG, url.toString());


                hc.setRequestMethod("PATCH");
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
                info.put("DosageTypeId", mdType);
                output.write(info.toString().getBytes());
                output.flush();


                if (hc.getResponseCode() == 204) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Medicine Updated Successful",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EditMedicine.this, MedicationMenu.class);
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


    private class Thread_GetMedicine extends Thread {
        private String TAG = "GetMedicine";
        private int id;
        private String result;
        private Handler mHandler;

        public Thread_GetMedicine(int id,Handler handler){
            this.id=id;
            this.mHandler = handler;
        }

        public void run() {
            try {
                URL url = new URL("https://bczsansikazvyoywabmo.supabase.co/rest/v1/Medicine?id=eq."+id+"&select=id,MedicineName,Volume,Dose,DosageTypeId");
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
                    int id = InfoArray.getJSONObject(0).getInt("id");
                    String mName = InfoArray.getJSONObject(0).getString("MedicineName");
                    double mVolume = InfoArray.getJSONObject(0).getDouble("Volume");
                    double mDose = InfoArray.getJSONObject(0).getDouble("Dose");
                    int mDosetype = InfoArray.getJSONObject(0).getInt("DosageTypeId");
                    medicineName = findViewById(R.id.medicine_name);
                    initialVolume = findViewById(R.id.initial_volume);
                    dose = findViewById(R.id.dose);




                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            medicineName.setText(mName);
                            initialVolume.setText(String.format("%.2f",mVolume));
                            dose.setText(String.format("%.2f",mDose));

                            switch (mDosetype){

                                case 1:
                                    tabletButton.callOnClick();
                                    break;
                                case 2:
                                    dropButton.callOnClick();
                                    break;

                                case 3:
                                    injectionButton.callOnClick();
                                    break;
                                default:
                                    break;
                            }

                            Toast.makeText(getApplicationContext(), "Open edit medicine page", Toast.LENGTH_SHORT).show();
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


            }catch (IOException| JSONException e) {
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