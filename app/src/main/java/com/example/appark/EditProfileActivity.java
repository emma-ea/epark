package com.example.appark;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText name, email, contact, carPlate;


    private Button save;

    String strName = "", strEmail = "", strContact = "", strCarPlate = "";

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        name = findViewById(R.id.userProfileEditEtNameData);
        email = findViewById(R.id.userProfileEditEtEmailData);
        contact = findViewById(R.id.userProfileEditEtContactNumberData);
        carPlate = findViewById(R.id.userProfileEditEtCarNumberData);
        save = findViewById(R.id.profileEditBtnSave);

        mAuth = FirebaseAuth.getInstance();

        save.setOnClickListener(this);

        Intent intent = getIntent();
        strName = intent.getStringExtra("name");
        strEmail = intent.getStringExtra("email");
        strContact = intent.getStringExtra("contact");
        strCarPlate = intent.getStringExtra("vehicle");


        name.setText(strName);
        email.setText(strEmail);
        contact.setText(strContact);
        carPlate.setText(strCarPlate);



    }

    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }

        return 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.profileEditBtnSave:
                //Save to firebase
                FirebaseUser user = mAuth.getCurrentUser();
                String userId = user.getUid();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference().child("Users").child(userId);

                myRef.child("vehicle").setValue(carPlate.getText().toString());
                myRef.child("name").setValue(name.getText().toString());
                myRef.child("contact").setValue(contact.getText().toString());

                Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }
}
