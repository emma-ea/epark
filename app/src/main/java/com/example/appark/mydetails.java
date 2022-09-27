package com.example.appark;


import android.content.Intent;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appark.Model.User3;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class mydetails extends AppCompatActivity implements View.OnClickListener {
	private TextView name, email, contactNumber, carNumber;
	private Button edit;
	private FirebaseAuth mAuth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mydetails);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		name = findViewById(R.id.userProfileTvNameData);
		email = findViewById(R.id.userProfileTvEmailData);
		contactNumber = findViewById(R.id.userProfileTvContactNumberData);
		carNumber = findViewById(R.id.userProfileTvCarNumberData);
		edit = findViewById(R.id.profileBtnEdit);

		mAuth = FirebaseAuth.getInstance();
		FirebaseUser user = mAuth.getCurrentUser();
		final String userId = user.getUid();
		Log.d("mydetails", "userID: " + userId);

		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference myRef = database.getReference().child("Users").child(userId);
		myRef.keepSynced(true);

		myRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				User3 user1 = dataSnapshot.getValue(User3.class);
				Log.d("mydetails", user1.getEmail());

				name.setText(user1.getName());
				email.setText(user1.getEmail());
				contactNumber.setText(user1.getcontact());
				carNumber.setText(user1.getvehicle());
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});

		edit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.profileBtnEdit:
				Intent intent = new Intent(mydetails.this, EditProfileActivity.class);
				intent.putExtra("name", name.getText().toString());
				intent.putExtra("email", email.getText().toString());
				intent.putExtra("contact", contactNumber.getText().toString());
				intent.putExtra("vehicle", carNumber.getText().toString());
				startActivity(intent);
				break;
		}
	}
}
