package com.example.kev.practice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

/**
 * Created by Kev on 12/26/2016.
 */

public class LoginRegister extends AppCompatActivity {
    Button toLogin, toRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_or_register);



        toLogin=(Button)findViewById(R.id.btnToLogin);
        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(LoginRegister.this,Login.class));
            }
        });
        toRegister=(Button)findViewById(R.id.btnToRegister);
        toRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(LoginRegister.this,Register.class));

            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
       Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        finish();
        System.exit(0);

    }
}