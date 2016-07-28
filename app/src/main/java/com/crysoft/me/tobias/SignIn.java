package com.crysoft.me.tobias;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.crysoft.me.tobias.helpers.Utils;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class SignIn extends AppCompatActivity {
    private EditText etEmail;
    private EditText etPassword;
    private Button btnSignin;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etEmail = (EditText) findViewById(R.id.etSigninEmail);
        etPassword = (EditText) findViewById(R.id.etSigninPassword);
        btnSignin =(Button) findViewById(R.id.btnSignIn);

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginClicked();
                login(etEmail.getText().toString().trim(),etPassword.getText().toString().trim());
            }
        });
    }
    private void loginClicked(){
        boolean isValid = true;

        if (etEmail.getText().toString().trim().length() == 0) {
            etEmail.setError("Please Enter Your Email Address");
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
            progressDialog = ProgressDialog.show(this, "Logging you in...", "Please Wait");

            login(etEmail.getText().toString().trim(), etPassword.getText().toString().trim());
        }
    }

    private void login(String email,String password){
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
                        Toast.makeText(getApplicationContext(), "Login Failed. Please try again", Toast.LENGTH_LONG).show();

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



}
