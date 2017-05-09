package com.example.kev.practice;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.Telephony;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.UriLoader;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class EventDetail extends AppCompatActivity {
   ImageView pic;
    TextView tag,title,time, description;
    String TITLE,TIME,DESC,TAG,IMGURL;
    FloatingActionButton save,share,fb_btn,sms_btn;
    private SQLiteDatabase db;
    String urlImg;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        createDatabase() ;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        save=(FloatingActionButton) findViewById(R.id.btnSaveToSql);
        fb_btn=(FloatingActionButton) findViewById(R.id.btnShareEvent_fb);

        //twitter_btn=(Button)findViewById(R.id.btnShareEvent_twiter);

        sms_btn=(FloatingActionButton) findViewById(R.id.btnShareEvent_sms);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToDb();
            }
        });
        share=(FloatingActionButton) findViewById(R.id.btnShareEvent);


        tag=(TextView)findViewById(R.id.tag_detail);
        pic=(ImageView)findViewById(R.id.imgDetail);
        title=(TextView)findViewById(R.id.titleDetail);
        time=(TextView)findViewById(R.id.timeDetail);
        description=(TextView)findViewById(R.id.descriptionDetail);

         urlImg=getIntent().getStringExtra("pic");
        //pic.setImageResource(getIntent().getIntExtra("pic",00));

        Glide.with(getApplicationContext()).load(urlImg).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).dontAnimate().placeholder(R.drawable.loader).error(R.drawable.logo).into(new BitmapImageViewTarget(pic){
            @Override
            public void onResourceReady(final Bitmap bmp, GlideAnimation glideAnimation) {
                pic.setImageBitmap(bmp);
                share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        shareEvent(bmp);
                    }
                });
                fb_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openFbIntent(bmp);
                    }
                });
                sms_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openSmsIntent(bmp);
                    }
                });
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
                shareTextOnly();
            }
        });
        tag.setText(getIntent().getStringExtra("tag"));
        title.setText(getIntent().getStringExtra("title"));
        time.setText(getIntent().getStringExtra("time"));
        description.setText(getIntent().getStringExtra("desc"));




    }

    private void shareTextOnly() {
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //shareTextOnlyWithWhatsapp
                TIME = time.getText().toString();
                TITLE = title.getText().toString();
                DESC = description.getText().toString();
                try{
                    Intent share = new Intent();

                    share.setAction(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.setPackage("com.whatsapp");

                    share.putExtra(Intent.EXTRA_TEXT, TITLE + "\n\n" + DESC + "\n\n\n" + "Sent from Karu online-notice app");


                    startActivity(Intent.createChooser(share, "Share to friend(s)"));
                }catch (android.content.ActivityNotFoundException e){

                    Toast.makeText(EventDetail.this,"Something went wrong, make sure you have whatsapp installed",Toast.LENGTH_LONG).show();
                }

            }
        });
        fb_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //shareTextOnlyWithFb
                TIME = time.getText().toString();
                TITLE = title.getText().toString();
                DESC = description.getText().toString();

                try{
                    Intent share = new Intent();

                    share.setAction(Intent.ACTION_SEND);
                    share.setPackage("com.facebook.orca");
                    share.putExtra(Intent.EXTRA_TEXT, TITLE + "\n\n" + DESC + "\n\n\n" + "Sent from Karu online-notice app");
                    share.setType("text/plain");


                    startActivity(Intent.createChooser(share, "Share to friend(s)"));
                }catch (Exception e){
                    Toast.makeText(EventDetail.this,"Something went wrong, make sure you have facebook installed",Toast.LENGTH_LONG).show();
                }

            }
        });

        sms_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // shareTextOnlyWithSms
                TIME = time.getText().toString();
                TITLE = title.getText().toString();
                DESC = description.getText().toString();

                try{
                    Intent share = new Intent() ;
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
                        String default_sms_app= Telephony.Sms.getDefaultSmsPackage(EventDetail.this);
                        //share= new Intent();
                        share.setType("text/plain");
                        share.setAction(Intent.ACTION_SEND);

                        share.putExtra(Intent.EXTRA_TEXT, TITLE + "\n\n" + DESC + "\n\n\n" + "Sent from Karu online-notice app");
                        if(default_sms_app!=null){
                            share.setPackage(default_sms_app);
                        }

                    }else {
                        //share= new Intent();
                        share.setAction(Intent.ACTION_SEND);
                        share.putExtra(Intent.EXTRA_TEXT, TITLE + "\n\n" + DESC + "\n\n\n" + "Sent from Karu online-notice app");
                        share.setType("vnd.android-dir/mms-sms");
                        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    startActivity(Intent.createChooser(share, "Share to friend(s)"));

                }catch (Exception e){
                    Toast.makeText(EventDetail.this,"Something went wrong...",Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void openSmsIntent(Bitmap bmp){
        Uri bmpUri = getLocalBmpUri(bmp);


        TIME = time.getText().toString();
        TITLE = title.getText().toString();
        DESC = description.getText().toString();

        try{
            Intent share = new Intent() ;
if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
    String default_sms_app= Telephony.Sms.getDefaultSmsPackage(EventDetail.this);
    //share= new Intent();
    share.setType("text/plain");
    share.setAction(Intent.ACTION_SEND);

    share.putExtra(Intent.EXTRA_TEXT, TITLE + "\n\n" + DESC + "\n\n\n" + "Sent from Karu online-notice app");
    if(default_sms_app!=null){
        share.setPackage(default_sms_app);
    }

}else {
    //share= new Intent();
    share.setAction(Intent.ACTION_SEND);
    share.putExtra(Intent.EXTRA_TEXT, TITLE + "\n\n" + DESC + "\n\n\n" + "Sent from Karu online-notice app");
}
    startActivity(Intent.createChooser(share, "Share to friend(s)"));

        }catch (Exception e){
            Toast.makeText(EventDetail.this,"Something went wrong...",Toast.LENGTH_LONG).show();
        }
    }



    private void openFbIntent(Bitmap bmp) {
        Uri bmpUri = getLocalBmpUri(bmp);


        TIME = time.getText().toString();
        TITLE = title.getText().toString();
        DESC = description.getText().toString();

        try{

            Intent share = new Intent();

            share.setAction(Intent.ACTION_SEND);
            share.setPackage("com.facebook.katana");
            share.putExtra(Intent.EXTRA_TEXT, TITLE + "\n\n" + DESC + "\n\n\n" + "Sent from Karu online-notice app");
            share.putExtra(Intent.EXTRA_STREAM,bmpUri);
            share.setType("image/*");
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(share, "Share to friend(s)"));
        }catch (Exception e){
            Toast.makeText(EventDetail.this,"Something went wrong, make sure you have facebook installed",Toast.LENGTH_LONG).show();
        }
    }


    private void createDatabase() {
        db=openOrCreateDatabase("EventDB", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS events(tag VARCHAR,imgUrl VARCHAR,date VARCHAR, title TEXT,description VARCHAR UNIQUE); ");
    }

    private void sendToDb() {

        TAG=tag.getText().toString();
        TIME=time.getText().toString();
        TITLE=title.getText().toString();
        DESC=description.getText().toString();
       try {
           String query = "INSERT  INTO events(tag,imgUrl,date,title,description) VALUES('" + TAG + "','" + urlImg + "','" + TIME + "','" + TITLE + "','" + DESC + "');";
           db.execSQL(query);
           Toast.makeText(this,"Successfully added to favourites",Toast.LENGTH_LONG).show();

           save.setEnabled(false);
       }catch (SQLiteException e){
           Toast.makeText(this,"Failed!..Seems its saved already. Try again",Toast.LENGTH_LONG).show();
       }
    }

    private void shareEvent(Bitmap bmp) {
        Uri bmpUri = getLocalBmpUri(bmp);


        TIME = time.getText().toString();
        TITLE = title.getText().toString();
        DESC = description.getText().toString();



try{
        Intent share = new Intent();

        share.setAction(Intent.ACTION_SEND);
   share.setType("image/*");

    //share.setType("text/plain");
        share.setPackage("com.whatsapp");

        share.putExtra(Intent.EXTRA_TEXT, TITLE + "\n\n" + DESC + "\n\n\n" + "Sent from Karu online-notice app");
        share.putExtra(Intent.EXTRA_STREAM,bmpUri);
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(share, "Share to friend(s)"));
    }catch (android.content.ActivityNotFoundException e){

    Toast.makeText(EventDetail.this,"Something went wrong, make sure you have whatsapp installed",Toast.LENGTH_LONG).show();
}
    }

private Uri getLocalBmpUri(Bitmap bmp) {
                Uri bmpUri=null;
            File file=new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),"share_img"+System.currentTimeMillis()+".png");
            FileOutputStream out=null;
            try{
                out=new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG,90,out);
                try{
                    out.close();
                }catch (IOException re){
                    re.printStackTrace();
                }
                bmpUri=Uri.fromFile(file);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
            return bmpUri;
    }
    }



