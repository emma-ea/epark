package com.example.appark;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import androidx.annotation.NonNull;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class User extends AppCompatActivity {

    private static final String TAG = "User";
    public FirebaseAuth mAuth;
    public Button Login;
    private EditText edtEmail, edtPwd;
    public TextView Sign_up;

    private ProgressBar loginProgressSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user);


        Login = findViewById(R.id.cirLoginButton);
        edtEmail = findViewById(R.id.emailEditText);
        edtPwd = findViewById(R.id.passwordEditText);
        Sign_up = findViewById(R.id.signup);

        loginProgressSpinner = findViewById(R.id.login_progress_spinner);
        loginProgressSpinner.setVisibility(View.GONE);

        Sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(User.this, Registration.class);
                User.this.startActivity(registerIntent);
            }
        });


        mAuth = FirebaseAuth.getInstance();

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edtEmail.getText().toString().contentEquals("")) {


                    Toast.makeText(getApplicationContext(), "please enter your email", Toast.LENGTH_SHORT).show();


                } else if (edtPwd.getText().toString().contentEquals("")) {

                    Toast.makeText(getApplicationContext(), "please enter your password", Toast.LENGTH_SHORT).show();

                } else {

                    loginProgressSpinner.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(edtEmail.getText().toString(), edtPwd.getText().toString())
                            .addOnCompleteListener(User.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithEmail:success");

                                        FirebaseUser user = mAuth.getCurrentUser();
                                        loginProgressSpinner.setVisibility(View.GONE);


                                        if (user != null) {

                                            Intent HomeActivity = new Intent(User.this, user1.class);
                                            setResult(RESULT_OK, null);
                                            startActivity(HomeActivity);
                                            finisher();
                                            loginProgressSpinner.setVisibility(View.GONE);
                                        }

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
//                                        Toast.makeText(User.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(User.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        loginProgressSpinner.setVisibility(View.GONE);

                                    }

                                }


                            });


                }
            }
        });

    }

    private void finisher() {
        finish();
    }
}