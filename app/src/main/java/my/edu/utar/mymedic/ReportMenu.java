package my.edu.utar.mymedic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.zip.Inflater;

import my.edu.utar.mymedic.model.ItemModel;
import my.edu.utar.mymedic.model.medicineDto;

public class ReportMenu extends AppCompatActivity {

    private ImageButton homeButton;
    private ImageButton medicationButton;
    private ImageButton reminderButton;
    private ImageButton reportButton;
    private ArrayList<ItemModel> itemsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_menu);

        homeButton = findViewById(R.id.home_button);
        medicationButton = findViewById(R.id.medication_button);
        reminderButton = findViewById(R.id.reminder_button);
        reportButton = findViewById(R.id.report_button);
        ListView list = (ListView) findViewById(R.id.report_listview);


        Thread_GetReport getReport = new Thread_GetReport();
        getReport.start();

        try {
            getReport.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        itemsList = sortAndAddSections(itemsList);
        ListAdapter adapter = new ListAdapter(this, itemsList);
        list.setAdapter(adapter);


        medicationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportMenu.this, MedicationMenu.class);
                startActivity(intent);
            }
        });

        reminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportMenu.this, ReminderMenu.class);
                startActivity(intent);
            }
        });

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "You are already on this page.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private ArrayList sortAndAddSections(ArrayList<ItemModel> itemList)
    {

        ArrayList<ItemModel> tempList = new ArrayList<>();
        //First we sort the array
        Collections.sort(itemList,Collections.reverseOrder());

        //Loops thorugh the list and add a section before each sectioncell start
        String header = "";
        for(int i = 0; i < itemList.size(); i++)
        {
            //If it is the start of a new section we create a new listcell and add it to our array
            if(!(header.equals(itemList.get(i).getDate()))) {
                ItemModel sectionCell = new ItemModel(null, null,null,itemList.get(i).getDate(),false);
                sectionCell.setToSectionHeader();
                tempList.add(sectionCell);
                header = itemList.get(i).getDate();
            }
            tempList.add(itemList.get(i));
        }

        return tempList;
    }

    public class ListAdapter extends ArrayAdapter {

        LayoutInflater inflater;
        public ListAdapter(Context context, ArrayList items) {
            super(context, 0, items);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ItemModel cell = (ItemModel) getItem(position);

            //If the cell is a section header we inflate the header layout
            if(cell.isSectionHeader())
            {
                v = inflater.inflate(R.layout.section_header, null);

                v.setClickable(false);

                TextView header = (TextView) v.findViewById(R.id.section_header);
                header.setText(cell.getDate());
            }
            else
            {
                v = inflater.inflate((R.layout.row_item), null);
                TextView tv_item = (TextView) v.findViewById(R.id.tv_item);
                TextView tv_time = (TextView) v.findViewById(R.id.tv_time);
                TextView tv_dose = (TextView) v.findViewById(R.id.tv_dose);
                TextView tv_taken = (TextView) v.findViewById(R.id.tv_taken);

                if(cell.isItemTaken())
                {
                    tv_taken.setText("Taken");
                }else
                {
                    tv_taken.setText("Miss");
                    tv_taken.setTextColor(getResources().getColor(R.color.red));
                }
                //TextView tv_taken = (TextView) v.findViewById(R.id.tv_taken);

                tv_item.setText(cell.getItemName());
                tv_time.setText(cell.getItemTime());
                tv_dose.setText(cell.getItemDose());
               // tv_taken


            }
            return v;
        }
    }


        private class Thread_GetReport extends Thread {

            private String TAG = "GetReport";
            SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

            // Retrieve a boolean value with key "userid"
            int userid = preferences.getInt("Userid",-1);


            public void run() {
                try {
                    URL url = new URL("https://bczsansikazvyoywabmo.supabase.co/rest/v1/Report?UserId=eq."+userid+"&select=DateTaken,TimeTaken,TakenStatus,Medicine(MedicineName,Dose)");
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
                            String name = InfoArray.getJSONObject(i).getJSONObject("Medicine").getString("MedicineName");
                            String time = InfoArray.getJSONObject(i).getString("TimeTaken");
                            String date = InfoArray.getJSONObject(i).getString("DateTaken");
                            String dose = InfoArray.getJSONObject(i).getJSONObject("Medicine").getString("Dose")+" dose";
                            boolean taken = InfoArray.getJSONObject(i).getBoolean("TakenStatus");

                            ItemModel item = new ItemModel(time,name,dose,date,taken);
                            itemsList.add(item);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
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