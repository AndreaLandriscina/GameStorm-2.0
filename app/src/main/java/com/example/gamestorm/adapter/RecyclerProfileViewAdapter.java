package com.example.gamestorm.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamestorm.R;
import com.example.gamestorm.ui.gameDetails.GameActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerProfileViewAdapter extends RecyclerView.Adapter<RecyclerProfileViewAdapter.RecyclerViewHolder> {

    private final ArrayList<RecyclerData> dataArrayList;
    private final Context mcontext;
    public RecyclerProfileViewAdapter(ArrayList<RecyclerData> recyclerDataArrayList, Context mcontext) {
        this.dataArrayList = recyclerDataArrayList;
        this.mcontext = mcontext;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        // Set the data to textview and imageview.
        RecyclerData recyclerData = dataArrayList.get(position);
        String newUrl = recyclerData.getImgUrl().replace("thumb", "cover_big");
        Picasso.get().load(newUrl).into(holder.cover);
        holder.cover.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("idGame", recyclerData.getId());
            Intent i = new Intent(mcontext, GameActivity.class);
            i.putExtras(bundle);
            mcontext.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        // this method returns the size of recyclerview
        return dataArrayList.size();
    }

    // View Holder Class to handle Recycler View.
    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private final ImageView cover;
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.cover);
        }

    }
}