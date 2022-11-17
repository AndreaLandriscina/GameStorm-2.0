package com.example.gamestorm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {
    static MyUrlRequestCallback myUrlRequestCallback;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        API api = new API(this);
        query = "fields name; limit 2;";
        try {
            api.callAPI(query);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}