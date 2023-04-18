package my.edu.utar.mymedic;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DoseChecking extends AppCompatActivity {


    private Button takenButton;
    private Button skipButton;
    private TextView DoseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dose_checking);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);;

        takenButton = findViewById(R.id.taken_button);
        skipButton = findViewById(R.id.skip_button);
        DoseName =findViewById(R.id.DoseName);

        Intent intent = getIntent();
        String medicineName = intent.getStringExtra("medicineName");
        double dose = intent.getDoubleExtra("dose",0);
        int reqCode = intent.getIntExtra("reqCode",-1);
        int mid = intent.getIntExtra("mid",-1);

        notificationManager.cancel(reqCode);
        DoseName.setText(medicineName+" | "+String.valueOf(dose)+" dose");



        takenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Thread_DoseUpdate thread_doseChecking = new Thread_DoseUpdate(mid,reqCode);
                thread_doseChecking.start();
                finish();

            }
        });

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

    private class Thread_DoseUpdate extends Thread {

        private int mMedicineId;
        private int mReminderId;
        private String mDateTaken;
        private String mTimeTaken;
        private boolean mTakenStatus;
        Calendar c = Calendar.getInstance();
        int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        String time = String.format("%02d:%02d", hourOfDay, minute);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String date = format1.format(c.getTime());

        private String TAG="DoseUpdating";

        public Thread_DoseUpdate(int mMedicineId,int reqCode) {
            this.mMedicineId = mMedicineId;
            this.mReminderId = reqCode;
            this.mDateTaken = date;
            this.mTimeTaken = time;
            mTakenStatus = true;
        }


        public void run() {
            try {
                Log.i(TAG, "helloo woowowow");
                URL url = new URL("https://bczsansikazvyoywabmo.supabase.co/rest/v1/Report?DateTaken=eq."+date+"&ReminderId=eq."+mReminderId);
                HttpURLConnection hc = (HttpURLConnection) url.openConnection();

                Log.i(TAG, url.toString());


                hc.setRequestMethod("PATCH");
                hc.setRequestProperty("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJjenNhbnNpa2F6dnlveXdhYm1vIiwicm9sZSI6ImFub24iLCJpYXQiOjE2ODA1OTg3NTYsImV4cCI6MTk5NjE3NDc1Nn0.SJTZDlwnRYtHPumBfhEHAu21bX7D8wll6xLVP-0xYqE");
                hc.setRequestProperty("Authorization", "Bearer " + "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJjenNhbnNpa2F6dnlveXdhYm1vIiwicm9sZSI6ImFub24iLCJpYXQiOjE2ODA1OTg3NTYsImV4cCI6MTk5NjE3NDc1Nn0.SJTZDlwnRYtHPumBfhEHAu21bX7D8wll6xLVP-0xYqE");
                hc.setRequestProperty("Content-Type", "application/json");
                hc.setRequestProperty("Prefer", "return=minimal");
                hc.setDoOutput(true);
                OutputStream output = hc.getOutputStream();
                JSONObject info = new JSONObject();
                info.put("MedicineId", mMedicineId);
                info.put("DateTaken", mDateTaken);
                info.put("TimeTaken", mTimeTaken);
                info.put("TakenStatus", mTakenStatus);
                info.put("ReminderId", mReminderId);
                output.write(info.toString().getBytes());
                output.flush();


                if (hc.getResponseCode() == 204) {
                    Log.i(TAG, "Dose Check in successfully");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Good job medicine taken", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.i(TAG, "Response Code: " + hc.getResponseCode());

                }
                output.close();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }

}