package com.example.kev.practice;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ProgressBar;

import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.support.v7.widget.Toolbar;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;


public class MainActivity extends AppCompatActivity   {
    private RecyclerView recyclerView;
    private eventsAdapter eventsAdapter;
    TextView wentWrong;
    private SwipeRefreshLayout swipeRefreshLayout;
    SearchView searchview;
    SharedPreferences prefs=null;
    List<event> eventList;
    LinearLayoutManager myLinearLayout;
    InterstitialAd mInterstitialAd;
 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        //banner add
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-8887969906112799~3916523862");

        AdView adView_one = (AdView) this.findViewById(R.id.adMob_one);
        //request TEST ads to avoid being disabled for clicking your own ads
        AdRequest adRequest = new AdRequest.Builder()
               // .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)// This is for emulators
                //test mode on DEVICE (this example code must be replaced with your device uniquq ID)
                // .addTestDevice("2EAB96D84FE62876379A9C030AA6A0AC") // Nexus 5
                .build();
        adView_one.loadAd(adRequest);
        //intestial add
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-8887969906112799/3690487060");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                SessionManager newSess=new SessionManager();
                newSess.setPreferences(MainActivity.this, "status", "0");
                Intent i = new Intent(MainActivity.this, Login.class);
                // Closing all the Activities
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                // Add new Flag to start new Activity
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                // Staring Login Activity

                    startActivity(i);
            }
        });
        requestNewInterstitial();

        try{

            Snackbar snack= Snackbar.make(findViewById(R.id.reg_snack_view),getIntent().getStringExtra("REGNO"),Snackbar.LENGTH_LONG);

            snack.show();

    }catch (Exception e){
            e.printStackTrace();
        }





        wentWrong=(TextView)findViewById(R.id.wentWrong);
        wentWrong.setVisibility(View.GONE);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }

        });

  searchview= (SearchView) findViewById(R.id.my_searchView);
        searchview.setQueryHint("Search Event titles or category");
        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
               //

                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                query=query.toLowerCase();
                final   List<event> filtered=new ArrayList<>();
                for(int i=0; i<eventList.size();i++){
                    final event eveee=eventList.get(i);
                    if(eveee.title.toLowerCase().contains(query)||eveee.tag.toLowerCase().contains(query)){
                        filtered.add(eventList.get(i));
                    }
                }
                myLinearLayout=new LinearLayoutManager(MainActivity.this);
                recyclerView.setLayoutManager(myLinearLayout);

                eventsAdapter=new eventsAdapter(MainActivity.this,filtered);
                recyclerView.setAdapter(eventsAdapter);
                eventsAdapter.notifyDataSetChanged();
                return true;
            }
        });
        recyclerView=(RecyclerView) findViewById(R.id.recyclerview);
        registerForContextMenu(recyclerView);
        RecyclerView.ItemAnimator animator=new DefaultItemAnimator();
        animator.setAddDuration(1000);
        animator.setRemoveDuration(1000);
        recyclerView.setItemAnimator(animator);

        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipeContainer);

        prefs=MainActivity.this.getSharedPreferences("KARU",0);

        AsyncHttpTask asyncHttpTask=new AsyncHttpTask();
        asyncHttpTask.execute();



    }

    private void requestNewInterstitial() {
            AdRequest adRequest = new AdRequest.Builder()
                    //.addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                    .build();

            mInterstitialAd.loadAd(adRequest);

    }


    private void refresh() {
        try {
            onItemsLoadComplete();

        }catch (Exception e){
            Toast.makeText(MainActivity.this,"Failed to refresh..",Toast.LENGTH_LONG).show();

        }
    }

    private void onItemsLoadComplete() {
        final AsyncHttpTask asyncHttpTask=new AsyncHttpTask();

        asyncHttpTask.execute();
        swipeRefreshLayout.setRefreshing(false);




    }



    public class AsyncHttpTask extends AsyncTask<String,String,String>{
        ProgressDialog progressDialog=new ProgressDialog(MainActivity.this);
        HttpURLConnection conn;
        URL url=null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setTitle("Please Wait");
            progressDialog.setMessage("Notices Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
           try{
                   url = new URL("http://10.0.2.2/karu/notices/read_notice.php");
             //  url = new URL("http://kelvo.capnix.com/read_notice.php");

           } catch (MalformedURLException e) {
               e.printStackTrace();
               return e.toString();

           }
            try{
                conn= (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoOutput(true);

            } catch (IOException e1) {
                e1.printStackTrace();
                return e1.toString();
            }
            try{
                int response_code=conn.getResponseCode();
                if(response_code==HttpURLConnection.HTTP_OK){
                    InputStream inputStream=conn.getInputStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));

                    StringBuilder result= new StringBuilder();
                    String line;
                    while((line=reader.readLine())!=null){
                        result.append(line);
                    }
                    return (result.toString());
                }else{
                    return ("unsuccessful");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            }finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
        progressDialog.dismiss();
            eventList=new ArrayList<>();
            try{
            JSONArray jsonArray=new JSONArray(result) ;
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject json_data= jsonArray.getJSONObject(i);
                    event newEvent = new event();
                    newEvent.tag=json_data.optString("tag");
                    newEvent.images=json_data.optString("pic");
                    newEvent.title=json_data.getString("title");
                    newEvent.description=json_data.optString("description");
                    newEvent.date=json_data.optString("created at");

                    eventList.add(newEvent);
                }
                eventsAdapter=new eventsAdapter(MainActivity.this,eventList);
                myLinearLayout=new LinearLayoutManager(MainActivity.this);
                recyclerView.setLayoutManager(myLinearLayout);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(eventsAdapter);


                wentWrong.setVisibility(View.GONE);



            } catch (JSONException e) {
               //Toast.makeText(MainActivity.this,e.toString(),Toast.LENGTH_LONG).show();
                Toast.makeText(MainActivity.this,"Failed to load notices..Check your connectivity",Toast.LENGTH_LONG).show();
                wentWrong.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu karu_menu) {
    getMenuInflater().inflate(R.menu.karu_menu,karu_menu);
        return true;
    }
    @Override
    public void onBackPressed() {
      //  super.onBackPressed();
        AlertDialog.Builder sure=new AlertDialog.Builder(MainActivity.this);
        sure.setCancelable(false);
        sure.setMessage("Are you sure you want to exit?");
        sure.setNegativeButton("Yap", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);


                startActivity(intent);


            }
        });
        sure.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        sure.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch (id) {
            case R.id.saved:
                startActivity(new Intent(MainActivity.this,History.class));
                break;
            case R.id.about:
                showAbout();
                break;
            case R.id.share:
                Intent shareIntent=new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Hello comrade, please install karu notice board app to get up to date news and notices... Regards");
                startActivity(Intent.createChooser(shareIntent, "Share your thoughts"));
                break;
            case R.id.call:
                Intent call= new Intent(Intent.ACTION_DIAL, Uri.parse("tel:+254710526766"));
                startActivity(call);
                break;
            case R.id.sendFeedBack:
                startActivity(new Intent(this,Feedback.class));
                break;

            case R.id.LogOut:
                SessionManager newSess=new SessionManager();
                newSess.setPreferences(MainActivity.this, "status", "0");
                Intent i = new Intent(MainActivity.this, Login.class);
                // Closing all the Activities
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                // Add new Flag to start new Activity
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                // Staring Login Activity
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    startActivity(i);
                }
                break;
            default:
                Toast.makeText(this,"An error Occurred",Toast.LENGTH_LONG).show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void showAbout() {
        Dialog d= new Dialog(MainActivity.this);
        Window window=d.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        d.setTitle("About Android Online Notice Board");
        d.setContentView(R.layout.about);
        d.setCancelable(true);
        d.show();
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void showNotification(){
        Uri soundUri= Uri.parse("android.resource://com.example.kev.practice/"+R.raw.karu_notify);
        NotificationCompat.Builder mBuilder= new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setSound(soundUri);
        mBuilder.setContentTitle("New Notice");
        mBuilder.setContentText("Hi, Newer items has been pinned on the notice board. Click to view");

        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            stackBuilder = TaskStackBuilder.create(this);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            stackBuilder.addParentStack(MainActivity.class);
        }

// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);


        getApplicationContext();
        NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(100,mBuilder.build());
    }


}
