package com.example.gamestorm;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText game_name = view.findViewById(R.id.game_name);
        Button search = view.findViewById(R.id.search);
        RecyclerView gamesRV = view.findViewById(R.id.gamesRV);
        ArrayList<GameData> games = new ArrayList<>();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                games.clear();
                String user_input = game_name.getText().toString();

                String response = callAPI("fields id,name, cover.url; where name ~ \"" + user_input +"\";",
                                            "https://api.igdb.com/v4/games");

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for(int i = 0; i < jsonArray.length();i++){
                        GameInfo g = new GameInfo(view.findViewById(R.id.card_game_image), "[" + jsonArray.getJSONObject(i).toString() + "]");
                        games.add(new GameData(Integer.parseInt(jsonArray.getJSONObject(i).getString("id")), g.getName(), g.getCoverImage()));
                    }
                    // added data from arraylist to adapter class.
                    GameAdapter adapter=new GameAdapter(games,getContext());

                    // setting grid layout manager to implement grid view.
                    // in this method '2' represents number of columns to be displayed in grid view.
                    GridLayoutManager layoutManager=new GridLayoutManager(getContext(),3);

                    // at last set adapter to recycler view.
                    gamesRV.setLayoutManager(layoutManager);
                    gamesRV.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String callAPI(String query, String url) {
        API api = new API(getContext(), url);
        String response = "";
        try {
            api.callAPI(query);
            response = api.getResponse();
        } catch (JSONException e) {
            e.printStackTrace();
            response = "Json exception";
        }
        return response;
    }

}