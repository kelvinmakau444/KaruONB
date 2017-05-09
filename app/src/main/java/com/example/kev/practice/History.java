package com.example.kev.practice;

import android.app.Dialog;
import com.example.kev.practice.eventsAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class History extends AppCompatActivity {
    private RecyclerView recyclerView;
    private eventsAdapter eventsAdapter;
    private SQLiteDatabase db;
    TextView nothing;
    Cursor cursor;
    View snack;
    MenuItem delete_all;
    eventsAdapter.myViewHolder holder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerviewHistory);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        nothing=(TextView)findViewById(R.id.nothing);
        snack=findViewById(R.id.view);
        delete_all=(MenuItem)findViewById(R.id.delete_All);


        List<event> HistEventList = new ArrayList<>();
        db = openOrCreateDatabase("EventDB", Context.MODE_PRIVATE, null);
        String query = "SELECT *  FROM events";
try {
    cursor = db.rawQuery(query, null);

}catch (Exception e){
   // e.printStackTrace();

    Toast.makeText(this, "An error occured", Toast.LENGTH_LONG).show();

}

        try {
            if (cursor.moveToFirst()) {
                do {
                    event newEvent = new event();
                    newEvent.tag=cursor.getString(cursor.getColumnIndex("tag"));
                    newEvent.images=cursor.getString(cursor.getColumnIndex("imgUrl"));
                    newEvent.title = cursor.getString(cursor.getColumnIndex("title"));
                    newEvent.description = cursor.getString(cursor.getColumnIndex("description"));
                    newEvent.date = cursor.getString(cursor.getColumnIndex("date"));

                    HistEventList.add(newEvent);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
           Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }


        eventsAdapter=new eventsAdapter(History.this,HistEventList);


        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(eventsAdapter);
        if(eventsAdapter.getItemCount()==0){

            nothing.setVisibility(View.VISIBLE);
            //delete_all.setVisible(false);
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu karu_menu) {
        if(eventsAdapter.getItemCount()>0){
            getMenuInflater().inflate(R.menu.offline_menu,karu_menu);

        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch (id) {
            case R.id.delete_All:
                final AlertDialog.Builder diag=new AlertDialog.Builder(History.this);
                diag.setCancelable(false);
                diag.setIcon(android.R.drawable.ic_dialog_alert);
                diag.setTitle("Delete All");
                diag.setMessage("Do you want to delete all saved notices?");
                diag.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteAll();


                    }
                });
                diag.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //
                    }
                });
                diag.show();

             break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAll() {
        db.delete("events",null,null);
        Snackbar.make(snack,"Successfully deleted...",Snackbar.LENGTH_LONG).show();
        finish();

    }
}
