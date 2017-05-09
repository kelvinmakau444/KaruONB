package com.example.kev.practice;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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


public class RemindPassword extends AppCompatActivity {
   private static final  String POST_URL ="http://10.0.2.2/karu/notices/change_password.php" ;
    //private static final  String POST_URL ="http://kelvo.capnix.com/change_password.php" ;
    EditText remindRegNo,remindPass,confamRemindPass;
    String RREGNO,RPASS,RRPASS;
    Button changePass;
    View snackbr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remind_password);

        remindRegNo=(EditText)findViewById(R.id.remindRegNo);
        remindPass=(EditText)findViewById(R.id.remindPass);
        confamRemindPass=(EditText)findViewById(R.id.confamRemindPass);
        snackbr=findViewById(R.id.snack_view_remind);

        changePass=(Button)findViewById(R.id.btnChangePass);
        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validatePassword();
            }
        });

    }

    private void validatePassword() {
        RREGNO = remindRegNo.getText().toString().trim();
        RPASS = remindPass.getText().toString().trim();
        RRPASS = confamRemindPass.getText().toString().trim();

        if (!(RPASS.equals(RRPASS))) {
            Snackbar.make(snackbr, "Password Mismatch", Snackbar.LENGTH_LONG).show();
        }  else if (RREGNO.isEmpty()|RPASS.isEmpty()|RRPASS.isEmpty()) {

                Snackbar.make(snackbr, "Please fill all fields", Snackbar.LENGTH_LONG).show();

            } else{
            ChangePassword();
        }


    }

    private void ChangePassword() {
       // Snackbar.make(snackbr, "Great..We can continue", Snackbar.LENGTH_LONG).show();
        RREGNO = remindRegNo.getText().toString().trim();
        RPASS = remindPass.getText().toString().trim();
        RRPASS = confamRemindPass.getText().toString().trim();
        final ProgressDialog pdiag=new ProgressDialog(RemindPassword.this);
        // pdiag.setTitle("Contacting Karu Servers");
        pdiag.setMessage("Please wait...");
        pdiag.setCancelable(false);
        pdiag.show();
        StringRequest request=new StringRequest(Request.Method.POST, POST_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                pdiag.dismiss();
                if(response.contains("Password Successfully Changed")){
                    Toast.makeText(RemindPassword.this,response.toString(),Toast.LENGTH_LONG).show();
                    startActivity(new Intent(RemindPassword.this,Login.class));
                }else {
                    Snackbar.make(snackbr, response.toString(), Snackbar.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pdiag.dismiss();
                Snackbar.make(snackbr, volleyError.toString(), Snackbar.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String,String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("remind_reg_no", RREGNO);
                params.put("remind_password", RPASS);



                return params;
            }
        };

        RequestQueue requestQueue=  Volley.newRequestQueue(this);
        requestQueue.add(request);
    }


}


