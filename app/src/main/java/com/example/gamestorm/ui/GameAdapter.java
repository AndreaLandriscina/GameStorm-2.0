package com.example.gamestorm.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamestorm.Model.GameApiResponse;
import com.example.gamestorm.R;
import com.squareup.picasso.Picasso;
import java.util.List;


public class GameAdapter extends RecyclerView.Adapter<GameAdapter.RecyclerViewHolder> {

    private List<GameApiResponse> games;
    private Context context;

    public GameAdapter(List<GameApiResponse> games, Context context) {
        this.games = games;
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
        GameApiResponse game = games.get(position);
        if(game.getCover() != null)//?
            Picasso.get().load(game.getCover().getUrl().replace("thumb", "cover_big")).into(holder.coverIV);
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

        private ImageView coverIV;
        private CardView gameCV;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            coverIV = itemView.findViewById(R.id.card_game_image_IV);
            gameCV = itemView.findViewById(R.id.game_CV);
        }
    }

    public void setGames(List<GameApiResponse> games) {
        this.games = games;
    }

}
