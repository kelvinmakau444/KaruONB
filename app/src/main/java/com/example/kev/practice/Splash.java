package com.example.kev.practice;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Locale;


public class Splash extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.splash);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        splash();


    }

    private void showAlertDialog() {
View snack=findViewById(R.id.snackView);

Snackbar snackbar = Snackbar
        .make(snack,"Data Connection Error :( ",Snackbar.LENGTH_INDEFINITE)
        .setActionTextColor(Color.GREEN)
        .setAction("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                splash();
                Toast.makeText(Splash.this,"Refreshing... :)",Toast.LENGTH_SHORT).show();

            }
        });
        snackbar.show();
        }

    private void splash() {
        new Handler().postDelayed(new Runnable() {
            // Using handler with postDelayed called runnable run method
            @Override
            public void run() {
                if (NetworkUtil.hasConnectivity(Splash.this)){
                    SessionManager sessionManager=new SessionManager();
                    String status=sessionManager.getPreferences(Splash.this,"status");
                   // Log.d("status",status);
                    if (status.equals("1")){
                        Intent i=new Intent(Splash.this,MainActivity.class);
                        startActivity(i);
                    }else{
                      //Intent i=new Intent(Splash.this,MainActivity.class);

                      Intent i=new Intent(Splash.this,LoginRegister.class);
                        startActivity(i);
                    }

                }else{
                    showAlertDialog();

                }

            }
        }, 2*1000); // wait for 2 seconds

    }@Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        finish();
        System.exit(0);

    }
}