package com.example.gamestorm;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.json.JSONException;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    static MyUrlRequestCallback myUrlRequestCallback;

    public HomeFragment() {

    }

    private void callAPI() {
        API api = new API(getContext());
        String query = "fields name; limit 2;";
        try {
            api.callAPI(query);
            String response = api.getResponse();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        callAPI();
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //this button is used to open the game page
        Button button = requireView().findViewById(R.id.button);
        button.setOnClickListener(view1 -> {
            Intent myIntent = new Intent(getContext(), GameActivity.class);
            //when we open the game page we have to pass the game's id
            int idGame = 0;
            myIntent.putExtra("idGame", idGame);
            requireActivity().startActivity(myIntent);
        });
    }
}