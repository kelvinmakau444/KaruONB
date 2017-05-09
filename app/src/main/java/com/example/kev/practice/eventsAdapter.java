package com.example.kev.practice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class eventsAdapter extends RecyclerView.Adapter<eventsAdapter.myViewHolder> {
    private  List<event> eventList;
    private Context context;
    private LayoutInflater inflater;




    public eventsAdapter(Context context,List<event> eventList){
        this.context=context;
        inflater= LayoutInflater.from(context);
        this.eventList=eventList;


    }


    public class myViewHolder extends RecyclerView.ViewHolder{
        public ImageView pic;
        public TextView tag,title,time, description,showMore;


        public myViewHolder(View itemView) {
            super(itemView);

            tag=(TextView)itemView.findViewById(R.id.tag);
            pic=(ImageView)itemView.findViewById(R.id.imageView);
            title=(TextView)itemView.findViewById(R.id.tvTitle);
            description=(TextView)itemView.findViewById(R.id.tvDescription);
            time=(TextView)itemView.findViewById(R.id.tvTime);
            showMore=(TextView)itemView.findViewById(R.id.tvShowMore);
            showMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    event eve;
                    eve=eventList.get(position);

                    String imgUrl=eve.images;

                    Intent intent=new Intent(context,EventDetail.class);


                    intent.putExtra("tag",eve.tag);
                    intent.putExtra("pic",imgUrl);
                    intent.putExtra("title",eve.title);
                    intent.putExtra("desc",eve.description);
                    intent.putExtra("time","Posted on: "+eve.date);

                    context.startActivity(intent);

                }
            });
        }



    }




    @Override
    public eventsAdapter.myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.event,parent,false);

        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(eventsAdapter.myViewHolder holder, int position) {



        event newEvent= eventList.get(position);
        holder.tag.setText("Under: "+newEvent.tag);
        holder.title.setText(newEvent.title);
        holder.time.setText("Date Posted: "+newEvent.date);
        holder.description.setText(newEvent.description);

        Glide.with(context).load(newEvent.images).diskCacheStrategy(DiskCacheStrategy.ALL).dontAnimate().placeholder(R.drawable.loader).error(R.drawable.logo).override(200,100).into(holder.pic);

    }

    @Override
    public int getItemCount() {

        return eventList.size();
    }

}
