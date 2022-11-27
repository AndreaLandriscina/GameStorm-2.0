package com.example.gamestorm;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class GameImage {
    ImageView gameImageView;

    GameImage(ImageView gameImageView){
        this.gameImageView = gameImageView;
    }

     void setGameImage(String response) throws JSONException, IOException {
        ArrayList<URL> urls = getAllUrlImages(response);
        //it's not possible to make the connection in the main thread
         Handler handler = new Handler(Looper.getMainLooper());
        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Bitmap bmp = BitmapFactory.decodeStream(urls.get(0).openConnection().getInputStream());
                    //it's not possible to modify the view outside the main thread
                    handler.post(() -> gameImageView.setImageBitmap(bmp));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    @NonNull
      ArrayList<URL> getAllUrlImages(String response) throws JSONException, MalformedURLException {
        JSONArray jsonArray = new JSONArray(response);

        ArrayList<URL> urls = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++){

            JSONObject jsonObject = jsonArray.getJSONObject(i);
            JSONArray screenshots = jsonObject.getJSONArray("screenshots");

            for (int j = 0; j < screenshots.length(); j++){
                JSONObject screenshotsJSONObject = screenshots.getJSONObject(j);
                String newURL = screenshotsJSONObject.getString("url").replace("thumb", "720p");
                urls.add(new URL("https:" + newURL));
            }
        }
        return urls;
    }
}


