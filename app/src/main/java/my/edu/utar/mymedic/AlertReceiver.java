package my.edu.utar.mymedic;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AlertReceiver extends BroadcastReceiver {



    private int mMedicineId;
    private String mDateTaken;
    private String mTimeTaken;
    private boolean mTakenStatus;
    Calendar c = Calendar.getInstance();
    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
    String date = format1.format(c.getTime());

    private String TAG="DoseChecking";

    @Override
    public void onReceive(Context context, Intent intent) {


        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        String medicineName = intent.getStringExtra("medicineName");
        double dose = intent.getDoubleExtra("dose", 0);
        int mid = intent.getIntExtra("mid", -1);
        int reqCode =intent.getIntExtra("key",0);
        int userid = intent.getIntExtra("userid",-1);
        mMedicineId = mid;

        Intent checkinIntent = new Intent(context, DoseChecking.class);
        checkinIntent.putExtra("reqCode", reqCode);
        checkinIntent.putExtra("dose", dose);
        checkinIntent.putExtra("medicineName", medicineName);
        checkinIntent.putExtra("mid", mid);


        PendingIntent pendingIntent = PendingIntent.getActivity(context, reqCode, checkinIntent, PendingIntent.FLAG_IMMUTABLE);

        Thread_DoseChecking thread_doseChecking = new Thread_DoseChecking(mid,reqCode,userid);
        thread_doseChecking.start();




        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
        nb.setContentIntent(pendingIntent);
        nb.setContentText("Time to eat your medicine \n" + "Medicine:" + medicineName + "| Dose: " + dose);
        notificationHelper.getManager().notify(reqCode, nb.build());


    }
    private class Thread_DoseChecking extends Thread {

        private int mMedicineId;
        private int mReminderId;
        private String mDateTaken;
        private String mTimeTaken;
        private boolean mTakenStatus;
        private int userid;
        Calendar c = Calendar.getInstance();
        int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        String time = String.format("%02d:%02d", hourOfDay, minute);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String date = format1.format(c.getTime());

        private String TAG="DoseChecking";


        public Thread_DoseChecking(int mMedicineId,int reqCode,int userid) {
            this.mMedicineId = mMedicineId;
            this.mReminderId = reqCode;
            this.mDateTaken = date;
            this.mTimeTaken = time;
            this.userid=userid;
            mTakenStatus = false;

        }


        public void run() {
                        try {
                            Log.i(TAG, "helloo woowowow");
                            URL url = new URL("https://bczsansikazvyoywabmo.supabase.co/rest/v1/Report?");
                            HttpURLConnection hc = (HttpURLConnection) url.openConnection();

                            Log.i(TAG, url.toString());


                            hc.setRequestMethod("POST");
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
                            info.put("UserId",userid);
                            output.write(info.toString().getBytes());
                            output.flush();


                            if (hc.getResponseCode() == 201) {
                                Log.i(TAG, "Dose Check in successfully");
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