package my.edu.utar.mymedic;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginMenu extends AppCompatActivity {

    private EditText emailInputField;
    private EditText passwordInputField;
    private Button loginButton;
    private TextView signUpLinkTextView;
    private AlertDialog.Builder builder_alert;
    private AlertDialog Login_alert;
    private Handler handler_alert;
    private Handler handler_success;
    private SQLiteAdapter user_SQLite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_menu);

        Handler handler = new Handler();

        emailInputField = findViewById(R.id.emailInputField);
        passwordInputField = findViewById(R.id.passwordInputField);
        loginButton = findViewById(R.id.loginButton);
        signUpLinkTextView = findViewById(R.id.signUpLinkTextView);
        handler_alert = new Handler(Looper.getMainLooper());
        handler_success = new Handler(Looper.getMainLooper());
        builder_alert = new AlertDialog.Builder(this);
        user_SQLite = new SQLiteAdapter(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInputField.getText().toString().trim();
                String password = passwordInputField.getText().toString().trim();

                Thread_Login connectThread = new Thread_Login(email, password, handler);
                connectThread.start();
            }
        });

        String signUpText = "Don't have an account? Sign Up here.";
        SpannableString signUpSpannable = new SpannableString(signUpText);
        int startIndex = signUpText.indexOf("here");
        int endIndex = startIndex + "here".length();
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(LoginMenu.this, SignUpMenu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        };
        signUpSpannable.setSpan(new ForegroundColorSpan(Color.RED), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        signUpSpannable.setSpan(clickableSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        signUpLinkTextView.setText(signUpSpannable);
        signUpLinkTextView.setMovementMethod(LinkMovementMethod.getInstance());
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
                Intent intent = new Intent(LoginMenu.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    private class Thread_Login extends Thread {
        private String mEmail;
        private String mPassword;
        private Handler mHandler;

        public Thread_Login(String email, String password, Handler handler) {
            this.mEmail = email;
            this.mPassword = password;
            this.mHandler = handler;
        }

        public void run() {
            try {
                URL url = new URL("https://bczsansikazvyoywabmo.supabase.co/rest/v1/User?email=eq."+mEmail);
                HttpURLConnection hc = (HttpURLConnection) url.openConnection();

                Log.i("LoginMenu", url.toString());

                hc.setRequestProperty("apikey", getString(R.string.SUPABASE_KEY1));
                hc.setRequestProperty("Authorization", "Bearer " + getString(R.string.SUPABASE_KEY1));

                if(hc.getResponseCode() == 401)
                {
                    handler_alert.post(new Runnable() {
                        @Override
                        public void run() {
                            builder_alert.setTitle("Error");
                            builder_alert.setMessage("The email or password entered is wrong ! \n Please try again.");
                            builder_alert.setCancelable(false);
                            builder_alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            Login_alert = builder_alert.create();
                            Login_alert.show();
                        }
                    });
                    return;
                }

                InputStream input = hc.getInputStream();
                String result = readStream(input);

                JSONArray InfoArray = new JSONArray(result);
                for (int i = 0; i < InfoArray.length(); i++) {
                    int id = InfoArray.getJSONObject(i).getInt("id");
                    String username_table = InfoArray.getJSONObject(i).get("name").toString();
                    String email_table = InfoArray.getJSONObject(i).get("email").toString();
                    String password_table = InfoArray.getJSONObject(i).get("password").toString();
                    if (mEmail.equals(email_table) && mPassword.equals(password_table)) {
                        handler_success.post(new Runnable() {
                            @Override
                            public void run() {
                                user_SQLite.openToWrite();
                                user_SQLite.deleteAll();
                                user_SQLite.insert(username_table, email_table, password_table);

                                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putInt("Userid", id);
                                editor.apply();


                                Toast.makeText(getApplicationContext(), "Login Successful !\nWelcome " + user_SQLite.welcome() + " !",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginMenu.this, UserMainMenu.class);
                                user_SQLite.close();
                                startActivity(intent);
                            }
                        });
                    } else {
                        handler_alert.post(new Runnable() {
                            @Override
                            public void run() {
                                builder_alert.setTitle("Error");
                                builder_alert.setMessage("The email or password entered is wrong ! \n Please try again.");
                                builder_alert.setCancelable(false);
                                builder_alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                Login_alert = builder_alert.create();
                                Login_alert.show();
                            }
                        });
                        return;
                    }
                }

            } catch (IOException | JSONException e) {
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