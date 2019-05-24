package com.example.rotemy213.itsadate;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private static final String TAG = "RecyclerViewAdapter";
    private ArrayList<String> mImagesNames;
    private ArrayList<String> mImages;
    private ArrayList<String> mDates;
    private ArrayList<String> mHours;
    private Context mContext;

    public RecyclerViewAdapter(ArrayList<String> imageNames, Context context, ArrayList<String> images, ArrayList<String> dates, ArrayList<String> hours)
    {
        mImagesNames = imageNames;
        mContext = context;
        mImages = images;
        mDates = dates;
        mHours = hours;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()) .inflate(R.layout.layout_listitem, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Log.d(TAG, "OnBindViewHolder: called.");
        viewHolder.imageName.setText(mImagesNames.get(i));
        viewHolder.imageName.setTypeface(viewHolder.imageName.getTypeface(), Typeface.BOLD);

        viewHolder.date.setText(mDates.get(i));
        viewHolder.date.setTextSize(8);

        viewHolder.hour.setText(mHours.get(i));
        viewHolder.hour.setTextSize(8);
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView image;
        TextView imageName;
        TextView date;
        TextView hour;
        RelativeLayout parentLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            imageName = itemView.findViewById(R.id.first_item);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            date = itemView.findViewById(R.id.date);
            hour = itemView.findViewById(R.id.hour);

        }
    }
}
