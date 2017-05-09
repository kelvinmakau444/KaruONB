package com.example.kev.practice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    TextInputEditText regno,pass;
    String reg_no,password;
    Button loginBtn;

    TextInputLayout regTIL,passTIL;
    View snack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-8887969906112799~3916523862");

        AdView adView = (AdView) this.findViewById(R.id.adMob);
        //request TEST ads to avoid being disabled for clicking your own ads
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)// This is for emulators
                //test mode on DEVICE (this example code must be replaced with your device uniquq ID)
                // .addTestDevice("2EAB96D84FE62876379A9C030AA6A0AC") // Nexus 5
                .build();
        adView.loadAd(adRequest);


        regno=(TextInputEditText) findViewById(R.id.regNoET);
        pass=(TextInputEditText) findViewById(R.id.passET);
        pass.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);


        regTIL=(TextInputLayout)findViewById(R.id.regTIL);
        passTIL=(TextInputLayout)findViewById(R.id.passTIL);

        snack=findViewById(R.id.view);


        loginBtn=(Button)findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Authenticate();
            }
        });









    }

    private void Authenticate() {
        reg_no = regno.getText().toString();
        password = pass.getText().toString().trim();

        if (reg_no.isEmpty() | password.isEmpty()) {
Snackbar.make(snack,"Please fill all fields",Snackbar.LENGTH_LONG).show();
            }else if(reg_no.length()<4|password.length()<5){
            Snackbar.make(snack,"Too short Reg. No. or Password",Snackbar.LENGTH_LONG).show();

        } else {

            FetchData();


            }

        }


    private void FetchData() {
        final ProgressDialog pdiag=new ProgressDialog(Login.this);
       // pdiag.setTitle("Contacting Karu Servers");
        pdiag.setMessage("Signing in. Please Wait...");
        pdiag.setCancelable(false);
        pdiag.show();
final String url="http://10.0.2.2/karu/notices/login.php";
     //   final String url="http://kelvo.capnix.com/login.php";

        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pdiag.dismiss();
                if (response.contains("Welcome ")) {
                    SessionManager sessionManager=new SessionManager();
                    sessionManager.setPreferences(Login.this, "status", "1");

                    Toast.makeText(Login.this,"Login Success",Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(Login.this,MainActivity.class);
                    intent.putExtra("REGNO",response);
                    startActivity(intent);
                } else {
                    Snackbar.make(snack, "Sorry Login "+ "Failed" +" "+"Check your credentials", Snackbar.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pdiag.dismiss();

                Snackbar.make(snack, "An error occurred..Try again", Snackbar.LENGTH_LONG).show();
              //  Toast.makeText(Login.this,volleyError.toString(),Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> map=new HashMap<String, String>();
                map.put("reg_no",reg_no);
                map.put("password",password);
                return  map;
            }
        };
        RequestQueue req= Volley.newRequestQueue(this);
        req.add(stringRequest);


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
       Intent intent = new Intent(this,LoginRegister.class);
        startActivity(intent);


    }

    public void ChangePassword(View view) {
        startActivity(new Intent(Login.this,RemindPassword.class));
    }
}
