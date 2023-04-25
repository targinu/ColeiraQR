package com.fatec.coleiraqr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.window.SplashScreen;

import com.google.firebase.ktx.Firebase;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide(); //ESCONDE A BARRA SUPERIOR

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity((new Intent(MainActivity.this, FormLogin.class)));
                finish();
            }
        },2000);

    }
}