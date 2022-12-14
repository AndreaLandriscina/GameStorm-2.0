package com.example.gamestorm.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamestorm.R;

import com.example.gamestorm.ui.ScreenshotFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerScreenshotsViewAdapter extends RecyclerView.Adapter<RecyclerScreenshotsViewAdapter.RecyclerViewHolder> {

    private ArrayList<RecyclerData> dataArrayList;
    private Context mcontext;
    private boolean isImageFitToScreen;

    public RecyclerScreenshotsViewAdapter(ArrayList<RecyclerData> recyclerDataArrayList, Context mcontext) {
        this.dataArrayList = recyclerDataArrayList;
        this.mcontext = mcontext;
        this.isImageFitToScreen = false;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.screenshot_layout, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        // Set the data to textview and imageview.

        RecyclerData recyclerData = dataArrayList.get(position);
        String newUrl = recyclerData.getImgUrl().replace("thumb", "screenshot_med");
        Picasso.get().load(newUrl).into(holder.screenshot);
        holder.screenshot.setOnClickListener(v -> {
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            NestedScrollView nestedScrollView = activity.findViewById(R.id.nestedScrollView);
            nestedScrollView.setNestedScrollingEnabled(false);

            Fragment fragment = new ScreenshotFragment();
            Bundle bundle = new Bundle();
            bundle.putString("imageUrl", newUrl);
            fragment.setArguments(bundle);
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        });
    }

    @Override
    public int getItemCount() {
        // this method returns the size of recyclerview
        return dataArrayList.size();
    }

    // View Holder Class to handle Recycler View.
    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private ImageView screenshot;
        //private TextView textView;
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            screenshot = itemView.findViewById(R.id.screenshot);
            //textView = itemView.findViewById(R.id.idTVCourse);
        }

    }
}

