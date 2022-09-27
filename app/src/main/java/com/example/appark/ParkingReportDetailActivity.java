package com.example.appark;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.appark.Model.Receipt;
import com.example.appark.R;

public class ParkingReportDetailActivity extends AppCompatActivity {

    private TextView txtViewEmail, txtViewCarPlate, txtViewCompany, txtViewHours, txtViewDateTime, txtViewLot, txtViewSpot, txtViewPayment, txtViewAmount;
    Receipt receipt = new Receipt();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_report_detail);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        txtViewEmail = findViewById(R.id.txtVEmail);
        txtViewCarPlate = findViewById(R.id.textViewCarPlate);
        txtViewCompany = findViewById(R.id.textViewCompany);
        txtViewHours = findViewById(R.id.textViewHours);
        txtViewDateTime = findViewById(R.id.textViewDate);
        txtViewLot = findViewById(R.id.textViewLot);
        txtViewSpot = findViewById(R.id.textViewSpot);
        txtViewPayment = findViewById(R.id.textViewPaymentMode);
        txtViewAmount = findViewById(R.id.textViewAmount);

        Intent intent = getIntent();
        receipt = (Receipt) intent.getSerializableExtra("receipt");

        txtViewEmail.setText(receipt.getEmail());
        txtViewCarPlate.setText(receipt.getCarNo());
        txtViewCompany.setText(receipt.getCarCompany());
        txtViewHours.setText(receipt.getNoOfHours());
        txtViewDateTime.setText(receipt.getDateTime());
        txtViewLot.setText(receipt.getLotNo());
        txtViewSpot.setText(receipt.getSpotNo());
        txtViewPayment.setText(receipt.getPaymentMethod());
        txtViewAmount.setText(receipt.getPaymentAmount());
    }
}
