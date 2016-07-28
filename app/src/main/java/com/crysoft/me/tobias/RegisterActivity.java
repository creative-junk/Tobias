package com.crysoft.me.tobias;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crysoft.me.tobias.helpers.Utils;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.TimeZone;

public class RegisterActivity extends AppCompatActivity {
    private EditText etEmailAddress;
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etPassword;
    private Button btnRegister;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etEmailAddress = (EditText) findViewById(R.id.tvEmailAddress);
        etFirstName = (EditText) findViewById(R.id.tvFirstName);
        etLastName = (EditText) findViewById(R.id.tvLastName);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRegisterClicked();
            }
        });

    }

    private void onRegisterClicked() {
        boolean isValid = true;

        if (etEmailAddress.getText().toString().trim().length() == 0) {
            etEmailAddress.setError("Please Enter Your Email Address");
            isValid = false;
        }
        if (etFirstName.getText().toString().trim().length() == 0) {
            etFirstName.setError("Please Enter Your First Name");
            isValid = false;
        }
        if (etLastName.getText().toString().trim().length() == 0) {
            etLastName.setError("Please Enter Your Last Name");
            isValid = false;
        }
        if (etPassword.getText().toString().trim().length() == 0) {
            etPassword.setError("Please Enter a Password");
            isValid = false;
        }
        //If the number is not valid, no need to continue
        if (!isValid) {
            return;
        }
        //No use continuing if there is no internet connection
        if (!Utils.isOnline(this)) {
            Utils.showToast("No Internet Connection", this);
            Log.i("NetInfo: ", "No connection Should Exit");
            return;
        } else {
            Log.i("NetInfo: ", "There is connection Should Continue");
            //is all good, proceed
            progressDialog = ProgressDialog.show(this, "Registering...", "Please Wait");

            register(etEmailAddress.getText().toString().trim(), etFirstName.getText().toString().trim(), etLastName.getText().toString().trim(), etPassword.getText().toString().trim());
        }
    }

    private void register(String email, String firstName, String lastName, final String password) {
        String timezone = TimeZone.getDefault().getID();
        String verificationCode = generateVerificationCode().toString();

        final String finalEmail = email;
        final String finalPassword = password;

        ParseUser user = new ParseUser();
        user.setUsername(email);
        user.setPassword(password);
        user.setEmail(email);
        user.put("first_name", firstName);
        user.put("last_name", lastName);
        user.put("timezone", timezone);
        user.put("device_type", "0");
        user.put("verification_code", verificationCode);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("EmailAddress", email);
        editor.putString("username", email);
        editor.apply();

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (e == null) {
                    login(finalEmail, finalPassword);
                } else {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void login(String email, String password) {
        if (ParseUser.getCurrentUser() == null) {
            ParseUser.logInInBackground(email, password, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (e == null) {
                        moveToNext();
                    } else {
                        moveToSignIn();

                    }
                }
            });
        } else {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            moveToNext();
        }

    }

    private void moveToNext() {
        Intent i = new Intent(this, HomeActivity.class);
        this.startActivity(i);
    }

    private void moveToSignIn() {
        Intent i = new Intent(this, SignIn.class);
        this.startActivity(i);
    }

    private Integer generateVerificationCode() {
        int random = (int) (Math.random() * 99999 + 1);
        return random;
    }

}
