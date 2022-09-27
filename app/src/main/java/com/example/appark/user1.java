package com.example.appark;

import android.content.Intent;

import android.os.Build;
import android.os.Bundle;

import com.example.appark.Model.User3;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.MenuItem;
import android.view.View;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.TextView;
import android.widget.Toast;

import android.view.Menu;

public class user1 extends AppCompatActivity   implements NavigationView.OnNavigationItemSelectedListener {


    private FirebaseAuth mAuth;
    private TextView drawerUsername, drawerAccount;
    private View headerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user2);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                Intent mapIntent = new Intent(user1.this, MapsActs.class);
                startActivity(mapIntent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        drawerUsername = headerView.findViewById(R.id.drawer_name);
        drawerAccount = headerView.findViewById(R.id.drawer_email);

        navigationView.setNavigationItemSelectedListener(this);


        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        final String userId = user.getUid();

        drawerUsername.setText(user.getDisplayName().isEmpty() ? "" : user.getDisplayName());
        drawerAccount.setText(user.getEmail().isEmpty() ? "" : user.getEmail());

        DatabaseReference myRef = database.getReference().child("Users").child(userId);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User3 user1 = dataSnapshot.getValue(User3.class);
                drawerUsername.setText(user1.getName());
                drawerAccount.setText(user1.getEmail());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // Intent intent = new Intent(user1.this, MainActivity.class);
            // startActivity(intent);
            super.onBackPressed();
        }
    }






    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {

            Intent userProfileIntent = new Intent(user1.this, mydetails.class);
            startActivity(userProfileIntent);

        } else if (id == R.id.nav_report) {

            Intent paymentReportIntent = new Intent(user1.this, ParkingReportActivity.class);
            startActivity(paymentReportIntent);

        } else if (id == R.id.nav_parking_manual) {
            Intent manualIntent = new Intent(user1.this, ManualActivity.class);
            startActivity(manualIntent);


        } else if (id == R.id.nav_support_contact) {
            Intent supportIntent = new Intent(user1.this, SupportActivity.class);
            startActivity(supportIntent);
        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Intent intent = new Intent(user1.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
