package com.example.kev.practice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Kev on 12/26/2016.
 */
public class Register extends AppCompatActivity {
    private static final String POST_URL ="http://10.0.2.2/karu/notices/register.php" ;
   // private static final String POST_URL ="http://kelvo.capnix.com/register.php" ;

    EditText fName,lName,regNo,pass;
    String FNAME,LNAME,REGNO,PASS;
    Button submit;
    View snack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        fName=(EditText)findViewById(R.id.firstNameET);
        lName=(EditText)findViewById(R.id.lastNameET);
        regNo=(EditText)findViewById(R.id.regNoET);
        pass=(EditText)findViewById(R.id.passwordET);
        submit=(Button)findViewById(R.id.btnRegister);
        snack=findViewById(R.id.snack_view);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validate();

            }
        });






    }

    private void Validate() {
        FNAME= fName.getText().toString();
        LNAME= lName.getText().toString();
        REGNO=regNo.getText().toString();
        PASS=pass.getText().toString();
        if (FNAME.isEmpty()|LNAME.isEmpty()|REGNO.isEmpty()|PASS.isEmpty()) {
            Snackbar.make(snack, "Please fill all fields", Snackbar.LENGTH_LONG).show();
        }else if(REGNO.length()<4|PASS.length()<5) {
            Snackbar.make(snack, "Too short Reg. No. or Password", Snackbar.LENGTH_LONG).show();
        }
            else {

            registerUser();
        }

        }


    private void registerUser() {
       // Toast.makeText(Register.this,"So far so good",Toast.LENGTH_LONG).show();
        FNAME= fName.getText().toString();
        LNAME= lName.getText().toString();
        REGNO=regNo.getText().toString();
        PASS=pass.getText().toString();
        final ProgressDialog pdiag=new ProgressDialog(Register.this);
        // pdiag.setTitle("Contacting Karu Servers");
        pdiag.setMessage("Registering...");
        pdiag.setCancelable(false);
        pdiag.show();
        StringRequest request=new StringRequest(Request.Method.POST, POST_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(Confirmation.this, response, Toast.LENGTH_LONG).show();
                pdiag.dismiss();
                if(response.contains("You Have Successfully Registered")){
                    Toast.makeText(Register.this,response.toString(),Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Register.this,Login.class));
                }else if(response.contains("An error occurred")){
                    Snackbar.make(snack, "An error occurred...Maybe Reg No. is already registered", Snackbar.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pdiag.dismiss();
                Snackbar.make(snack, volleyError.toString(), Snackbar.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String,String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("first_name", FNAME);
                params.put("last_name", LNAME);
                params.put("reg_no", REGNO);
                params.put("password", PASS);



                return params;
            }
        };

        RequestQueue requestQueue=  Volley.newRequestQueue(this);
        requestQueue.add(request);
    }



}
