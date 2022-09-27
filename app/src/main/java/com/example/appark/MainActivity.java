package com.example.appark;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.animation.Animator;

import android.os.CountDownTimer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    private ImageView carIconImageView;
    private TextView carITextView;
    private ProgressBar loadingProgressBar;
    private RelativeLayout rootView, afterAnimationView;
   private Button loginButton,signupButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        rootView = findViewById(R.id.rootView);
        carIconImageView = findViewById(R.id.carIconImageView);
        carITextView = findViewById(R.id.carITextView);
        carITextView.setTextSize(70.0F);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        afterAnimationView = findViewById(R.id.afterAnimationView);


        new CountDownTimer(5000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                rootView.setVisibility(VISIBLE);
                carITextView.setVisibility(VISIBLE);
                carIconImageView.setImageResource(R.drawable.car_png);
                loadingProgressBar.setVisibility(VISIBLE);
                afterAnimationView.setVisibility(GONE);

            }

            @Override
            public void onFinish() {
                afterAnimationView.setVisibility(VISIBLE);
                carIconImageView.setImageResource(R.drawable.car_png);
                loadingProgressBar.setVisibility(GONE);
                carITextView.setVisibility(GONE);
                startAnimation();

            }
        }.start();


        loginButton=  findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);


        signupButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                Intent intent = new Intent(MainActivity.this, Registration.class);
                startActivity(intent);

            }
        });

        loginButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                Intent intent = new Intent(MainActivity.this, User.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        // check if user is logged in, navigate to main screen
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(MainActivity.this, user1.class));
        }
    }

    private void startAnimation() {
        ViewPropertyAnimator viewPropertyAnimator = carIconImageView.animate();
        // viewPropertyAnimator.x(1f);
        viewPropertyAnimator.y(2f);
        viewPropertyAnimator.setDuration(2000);
        viewPropertyAnimator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                afterAnimationView.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
