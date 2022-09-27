package com.example.appark;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.appark.Model.User3;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ParkingActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView email, carPlate, amount, dateTime;
    private Spinner carCompany, carColor, paymentMode, spotNumber, lotNumber;
    private EditText parkingEtNoHours;
    private Button submit;
    private FirebaseAuth mAuth;

    private String strEmail = "", strCarPlate = "",
            strAmount = "", strDateTime = "", strCarCompany = "",
            strCarColor = "", strPaymentMode = "", strSpotNumber = "",
            strLotNumber = "", strParkingNoOfHours = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        email = findViewById(R.id.parkingTxtEmailData);

        carPlate = findViewById(R.id.parkingTxtCarNoData);
        amount = findViewById(R.id.parkingTxtAmountData);
        dateTime = findViewById(R.id.parkingTxtDateData);

        carCompany = findViewById(R.id.parkingSpinCarCompany);
        paymentMode = findViewById(R.id.parkingSpinPaymentMode);
        spotNumber = findViewById(R.id.parkingSpinSpot);
        lotNumber = findViewById(R.id.parkingSpinLot);
        submit = findViewById(R.id.parkingBtnSubmit);
        submit.setOnClickListener(this);


        parkingEtNoHours = findViewById(R.id.parkingEtNoHours);

        DateFormat dateFormatDate = new SimpleDateFormat("dd-MM-yyyy", Locale.CANADA);
        DateFormat dateFormatTime = new SimpleDateFormat("HH:mm:ss", Locale.CANADA);
        Calendar cal = Calendar.getInstance();
        final String date = dateFormatDate.format(cal.getTime());
        final String time = dateFormatTime.format(cal.getTime());

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        final String userId = user.getUid();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("Users").child(userId);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User3 user1 = dataSnapshot.getValue(User3.class);
                assert user1 != null;
                Log.d("mydetails", user1.getEmail());

                email.setText(user1.getEmail());
                carPlate.setText(user1.getvehicle());
                dateTime.setText(date + " " + time);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        database.getReference().child("Location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                final List<String> areas = new ArrayList<String>();

                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    String areaName = areaSnapshot.child("locname").getValue(String.class);
                    areas.add(areaName);
                }

                Spinner areaSpinner = (Spinner) findViewById(R.id.parkingSpinLot);
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(ParkingActivity.this, R.layout.slotavail, areas);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                areaSpinner.setAdapter(areasAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !s.toString().equalsIgnoreCase("")){
                    int value = Integer.valueOf(s.toString());
                    String  amountValue = "GHs" + value * 10 ;
                    amount.setText(String.valueOf(amountValue));
                }
            }
        };

        parkingEtNoHours.addTextChangedListener(textWatcher);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.parkingBtnSubmit:
                //Save data to database
                extractData();
                if(checkData()){
                    //save to db
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userId = user.getUid();

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference();
                    HashMap<String, String> map = new HashMap<>();
                    map.put("carCompany", strCarCompany);
                    map.put("carNo", strCarPlate);
                    map.put("dateTime", strDateTime);
                    map.put("email", strEmail);
                    map.put("lotNo", strLotNumber);
                    map.put("noOfHours", strParkingNoOfHours);
                    map.put("paymentAmount", strAmount);
                    map.put("paymentMethod", strPaymentMode);
                    map.put("spotNo", strSpotNumber);
                    myRef.child("parkingReceipt").child(userId).push().setValue(map);
                    Toast.makeText(ParkingActivity.this, "Parking reciept successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ParkingActivity.this, ParkingReceiptActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Incomplete form", Toast.LENGTH_SHORT).show();
                    break;
                }
                break;
            default:

                break;
        }
    }

    boolean checkData(){
        boolean isDataTrue = true;

        if (parkingEtNoHours.getText().toString().equals("")){
            isDataTrue = false;
        }

        return isDataTrue;
    }

    void extractData(){
        strEmail = email.getText().toString();
        strCarPlate = carPlate.getText().toString();
        strCarCompany = carCompany.getSelectedItem().toString();
        strParkingNoOfHours = parkingEtNoHours.getText().toString();
        strAmount = amount.getText().toString();
        strDateTime = dateTime.getText().toString();
        strPaymentMode = paymentMode.getSelectedItem().toString();
        strLotNumber = lotNumber.getSelectedItem().toString();
        strSpotNumber = spotNumber.getSelectedItem().toString();
    }
}
