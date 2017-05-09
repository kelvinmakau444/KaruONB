package com.example.kev.practice;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Arrays;


public class Feedback extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText email;
    Spinner feedBackType;
    EditText feedbackmsg;
    Button sendFeed;

    String EMAIL,FEEDBACK,MSG;
    CheckBox response;
    String emailResp;
    View snack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        snack=findViewById(R.id.feedback_snack_view);
        email=(EditText)findViewById(R.id.email);
        feedbackmsg=(EditText)findViewById(R.id.feedBack);

        feedBackType=(Spinner)findViewById(R.id.spinner);
       feedBackType.setOnItemSelectedListener(this);
        response=(CheckBox)findViewById(R.id.checkBox);


        ArrayAdapter aa=  ArrayAdapter.createFromResource(this,R.array.feedbackType,R.layout.support_simple_spinner_dropdown_item);
        aa.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        feedBackType.setAdapter(aa);

        sendFeed=(Button)findViewById(R.id.btnSendFeedback);
        sendFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
            }
        });
    }

    protected void sendMail() {
        String[] devInfo= {Build.MANUFACTURER,Build.MODEL, "API"+String.valueOf(Build.VERSION.SDK_INT)};
        EMAIL=email.getText().toString();
        FEEDBACK=feedBackType.getSelectedItem().toString();
        MSG=feedbackmsg.getText().toString();
        //boolean responseCB=response.isChecked();
        if(response.isChecked()){
             emailResp=" \nUser wants an email response.";
        }else{
            emailResp=" ";
        }
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "makau.kelvin@s.karu.ac.ke", null));

            emailIntent.putExtra(Intent.EXTRA_SUBJECT, FEEDBACK);
            emailIntent.putExtra(Intent.EXTRA_TEXT, MSG + " " + emailResp+"\n"+ "Sent from "+Arrays.toString(devInfo));
            if(MSG.isEmpty()){
                Snackbar.make(snack,"Feedback can not be empty..try again",Snackbar.LENGTH_LONG).show();
            }else {
                startActivityForResult(emailIntent, 100);
            }
        }  catch (android.content.ActivityNotFoundException ex) {
        Toast.makeText(Feedback.this, "Sorry..There is no email client installed.", Toast.LENGTH_SHORT).show();
    }
/*
        try {
            startActivityForResult(Intent.createChooser(emailIntent, "Send email..."),100);
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(Feedback.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }*/
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Feedback sent..thankyou", Toast.LENGTH_LONG).show();

            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu karu_menu) {
        return super.onCreateOptionsMenu(karu_menu);
    }
}