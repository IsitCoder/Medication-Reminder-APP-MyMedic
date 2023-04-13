package my.edu.utar.mymedic;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class TakeDose extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_dose);

        TextView test = new TextView(this);
        test.setText("Hello Take the medic");
        test.setTextSize(20);
        setContentView(test);

    }
}