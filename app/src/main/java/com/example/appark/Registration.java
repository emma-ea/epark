package com.example.appark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity  {


    private static final String TAG = "SignUpActivity";
    public FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener  firebaseAuthlistener;


    EditText edtFname, edtEmail, edtPwd, edtCno, edtVeh;
    Button btnReg, btncan;

    private ProgressBar signUpProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.registration);

        mAuth = FirebaseAuth.getInstance();

        signUpProgress = findViewById(R.id.signUp_progress_spinner);

        signUpProgress.setVisibility(View.GONE);

        edtFname = (EditText) findViewById(R.id.edtFname);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtCno = (EditText) findViewById(R.id.edtCno);
        edtPwd = (EditText) findViewById(R.id.edtPwd);
        edtVeh = (EditText) findViewById(R.id.edtVeh);
        btnReg = (Button) findViewById(R.id.btnReg);
        btncan = (Button) findViewById(R.id.btncan);

        btncan.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(Registration.this, MainActivity.class);
                startActivity(intent);
            }
        });


        btnReg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final String name = edtFname.getText().toString();
                final String email = edtEmail.getText().toString();
                final String contact = edtCno.getText().toString();
                final String vehicle = edtVeh.getText().toString();
                final String password = edtPwd.getText().toString();


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                    if (Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches()) {

                        if (edtCno.length() == 10) {
                            if (edtVeh.length() > 1) {
                                if (edtPwd.length() > 5) {

                                    signUpProgress.setVisibility(View.VISIBLE);
                                    mAuth.createUserWithEmailAndPassword(edtEmail.getText().toString(), edtPwd.getText().toString())
                                            .addOnCompleteListener(Registration.this, new   OnCompleteListener<AuthResult>() {

                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {

                                            if (task.isSuccessful()) {
                                                // Sign in success, update UI with the signed-in user's information
                                                Log.d(TAG, "createUserWithEmail:success");

                                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
                                                DatabaseReference currentuserDB= mDatabase.child(mAuth.getCurrentUser().getUid());
                                                currentuserDB.child("name").setValue(name);
                                                currentuserDB.child("email").setValue(email);
                                                currentuserDB.child("contact").setValue(contact);
                                                currentuserDB.child("vehicle").setValue(vehicle);
                                                currentuserDB.child("password").setValue(password);

                                                signUpProgress.setVisibility(View.GONE);

                                                FirebaseUser user = mAuth.getCurrentUser();
                                                if (user != null) {
                                                    Intent signupActivity = new Intent(Registration.this, User.class);
                                                    setResult(RESULT_OK, null);
                                                    startActivity(signupActivity);
                                                    Registration.this.finish();
                                                    signUpProgress.setVisibility(View.GONE);

                                                } else {
                                                    // If sign in fails, display a message to the user.
                                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                                                    Toast.makeText(Registration.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                                    Toast.makeText(Registration.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                    signUpProgress.setVisibility(View.GONE);
                                                }
                                            }
                                        }


                                    });


                                } else {
                                    edtPwd.setError("password is too short");
                                }
                            } else {
                                edtVeh.setError("number is too short");
                            }
                        } else {
                            edtCno.setError("Wrong Mobile Number");
                        }

                    } else {
                        edtEmail.setError("Not Valid Email ID");
                    }
                }


            }

        });

    }
}








