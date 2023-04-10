package my.edu.utar.mymedic;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignUpMenu extends AppCompatActivity {

    private EditText nameInputField;
    private EditText emailInputField;
    private EditText passwordInputField;
    private EditText confirmPasswordInputField;
    private Button signUpButton;
    private TextView loginLinkTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_menu);

        Handler handler = new Handler();

        nameInputField = findViewById(R.id.nameInputField);
        emailInputField = findViewById(R.id.emailInputField);
        passwordInputField = findViewById(R.id.passwordInputField);
        confirmPasswordInputField = findViewById(R.id.confirmPasswordInputField);
        signUpButton = findViewById(R.id.signUpButton);
        loginLinkTextView = findViewById(R.id.loginLinkTextView);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameInputField.getText().toString().trim();
                String email = emailInputField.getText().toString().trim();
                String password = passwordInputField.getText().toString().trim();
                String confirmPassword = confirmPasswordInputField.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    nameInputField.setError("Please enter your name");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    emailInputField.setError("Please enter your email address");
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailInputField.setError("Please enter a valid email address");
                    return;
                }

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

                Thread_SignUp connectThread = new Thread_SignUp(name, email, password, handler);
                connectThread.start();

            }
        });

        String loginText = "Already have an account? Login here.";
        SpannableString loginSpannable = new SpannableString(loginText);
        int startIndex = loginText.indexOf("here");
        int endIndex = startIndex + "here".length();
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(SignUpMenu.this, LoginMenu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        };
        loginSpannable.setSpan(new ForegroundColorSpan(Color.RED), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        loginSpannable.setSpan(clickableSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        loginLinkTextView.setText(loginSpannable);
        loginLinkTextView.setMovementMethod(LinkMovementMethod.getInstance());
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
                Intent intent = new Intent(SignUpMenu.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    private class Thread_SignUp extends Thread {
        private String mName;
        private String mEmail;
        private String mPassword;
        private Handler mHandler;

        public Thread_SignUp(String name, String email, String password, Handler handler) {
            this.mName = name;
            this.mEmail = email;
            this.mPassword = password;
            this.mHandler = handler;
        }

        public void run() {
            try {
                URL url = new URL("https://wymykyfxrokwlhokedwg.supabase.co/rest/v1/User");
                HttpURLConnection hc = (HttpURLConnection) url.openConnection();

                Log.i("SignUpMenu", url.toString());

                hc.setRequestMethod("POST");
                hc.setRequestProperty("apikey", getString(R.string.SUPABASE_KEY));
                hc.setRequestProperty("Authorization", "Bearer " + getString(R.string.SUPABASE_KEY));
                hc.setRequestProperty("Content-Type", "application/json");
                hc.setRequestProperty("Prefer", "return=minimal");

                JSONObject info = new JSONObject();
                info.put("name", mName);
                info.put("email", mEmail);
                info.put("password", mPassword);
                hc.setDoOutput(true);
                OutputStream output = hc.getOutputStream();
                output.write(info.toString().getBytes());
                output.flush();

                InputStream input = hc.getInputStream();
                String result = readStream(input);

                Log.i("SignUpMenu", "Output: " + result);

                if (hc.getResponseCode() == 201) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Success",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUpMenu.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });
                } else {
                    Log.i("SignUpMenu", "Response Code: " + hc.getResponseCode());
                }
                input.close();
                output.close();
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