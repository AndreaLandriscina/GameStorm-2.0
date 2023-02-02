package com.example.gamestorm.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamestorm.R;

import com.example.gamestorm.ui.ScreenshotFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerScreenshotsViewAdapter extends RecyclerView.Adapter<RecyclerScreenshotsViewAdapter.RecyclerViewHolder> {

    private ArrayList<RecyclerData> dataArrayList;
    private Context mcontext;

    public RecyclerScreenshotsViewAdapter(ArrayList<RecyclerData> recyclerDataArrayList, Context mcontext) {
        this.dataArrayList = recyclerDataArrayList;
        this.mcontext = mcontext;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.screenshot_layout, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        RecyclerData recyclerData = dataArrayList.get(position);
        String newUrl = recyclerData.getImgUrl().replace("thumb", "screenshot_huge");
        Picasso.get().load(newUrl).into(holder.screenshot);
        holder.screenshot.setOnClickListener(v -> {
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            CoordinatorLayout coordinatorLayout = activity.findViewById(R.id.scrollView);
            coordinatorLayout.setVisibility(View.INVISIBLE);

            Fragment fragment = new ScreenshotFragment();
            Bundle bundle = new Bundle();
            ArrayList<String> list = new ArrayList<>();
            for (RecyclerData data : dataArrayList){
                list.add(data.getImgUrl().replace("thumb", "screenshot_huge"));
            }
            bundle.putString("currentImage", newUrl);
            bundle.putInt("position", position);
            bundle.putStringArrayList("imageUrl",list);
            fragment.setArguments(bundle);
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.screenshotContainer, fragment).commit();
        });
    }

    @Override
    public int getItemCount() {
        return dataArrayList.size();
    }

    // View Holder Class to handle Recycler View.
    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private ImageView screenshot;
        private ImageView screenshotBig;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            screenshot = itemView.findViewById(R.id.screenshot);
            screenshot.setVisibility(View.VISIBLE);
            screenshotBig = itemView.findViewById(R.id.screenshotBig);
            screenshotBig.setVisibility(View.GONE);
        }
    }
}
