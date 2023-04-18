package my.edu.utar.mymedic;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ChangePassword extends AppCompatActivity {

    private EditText passwordInputField;
    private EditText confirmPasswordInputField;
    private Button changePasswordButton;
    private String email;
    private SQLiteAdapter user_SQLite;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Handler handler = new Handler();

        passwordInputField = findViewById(R.id.passwordInputField);
        confirmPasswordInputField = findViewById(R.id.confirmPasswordInputField);
        changePasswordButton = findViewById(R.id.changePasswordButton);

        user_SQLite = new SQLiteAdapter(this);
        user_SQLite.openToWrite();
        email = user_SQLite.getEmail();

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = passwordInputField.getText().toString().trim();
                String confirmPassword = confirmPasswordInputField.getText().toString().trim();


                if (TextUtils.isEmpty(password)) {
                    passwordInputField.setError("Please enter a password");
                    return;
                }

                if (password.length() < 8) {
                    passwordInputField.setError("Password must be at least 8 characters long");
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    confirmPasswordInputField.setError("Passwords do not match");
                    return;
                }

                ChangePassword.Thread_changePassword connectThread = new ChangePassword.Thread_changePassword(password, handler);
                connectThread.start();

            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit");
        builder.setMessage("Are you sure you want to back to main menu?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(ChangePassword.this, UserMainMenu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    private class Thread_changePassword extends Thread {
        private String mPassword;
        private Handler mHandler;

        public Thread_changePassword(String password, Handler handler) {
            this.mPassword = password;
            this.mHandler = handler;
        }

        public void run() {
            try {
                URL url = new URL("https://wymykyfxrokwlhokedwg.supabase.co/rest/v1/User?email=eq."+email);
                HttpURLConnection hc = (HttpURLConnection) url.openConnection();

                Log.i("ChangePassword", url.toString());

                hc.setRequestMethod("PATCH");
                hc.setRequestProperty("apikey", getString(R.string.SUPABASE_KEY));
                hc.setRequestProperty("Authorization", "Bearer " + getString(R.string.SUPABASE_KEY));
                hc.setRequestProperty("Content-Type", "application/json");
                hc.setRequestProperty("Prefer", "return=minimal");

                hc.setDoOutput(true);
                OutputStream output = hc.getOutputStream();
                JSONObject info = new JSONObject();
                info.put("password", mPassword);
                output.write(info.toString().getBytes());
                output.flush();

                if (hc.getResponseCode() != 204) {
                    Log.i("ChangePassword", "Response Code: " + hc.getResponseCode());
                } else
                {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(getApplicationContext(), "Change Password Successful.\nPlease Log in again.",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ChangePassword.this, LoginMenu.class);
                            startActivity(intent);
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