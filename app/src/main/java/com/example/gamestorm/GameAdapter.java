package com.example.gamestorm;

import android.content.Context;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class GameAdapter extends RecyclerView.Adapter<GameAdapter.RecyclerViewHolder> {

    private ArrayList<GameData> games;
    private Context context;

    public GameAdapter(ArrayList<GameData> recyclerDataArrayList, Context context) {
        this.games = recyclerDataArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_card_item, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameAdapter.RecyclerViewHolder holder, int position) {
        // Set the data to textview and imageview.
        GameData game = games.get(position);
        holder.nameTV.setText(game.getName());
        Picasso.get().load(game.getImage().toString()).into(holder.coverIV);
        holder.gameCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //link
                Bundle bundle = new Bundle();
                bundle.putInt("GAME_ID", game.getId());
                Navigation.findNavController(v).navigate(R.id.action_searchFragment_to_gameActivity,bundle);
            }
        });
    }


    @Override
    public int getItemCount() {
        // this method returns the size of recyclerview
        return games.size();
    }

    // View Holder Class to handle Recycler View.
    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTV;
        private ImageView coverIV;
        private CardView gameCV;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.card_game_name);
            coverIV = itemView.findViewById(R.id.card_game_image);
            gameCV = itemView.findViewById(R.id.game_cv);
        }
    }
}
