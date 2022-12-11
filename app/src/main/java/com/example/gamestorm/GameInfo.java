package com.example.gamestorm;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class GameInfo {
    ImageView gameImageView;
    String response;
    JSONArray jsonArray;
    JSONObject jsonObject;

    GameInfo(ImageView gameImageView, String response) throws JSONException {
        this.gameImageView = gameImageView;
        this.response = response;
        jsonArray = new JSONArray(response);
        jsonObject = jsonArray.getJSONObject(0);
    }

    void setGameImage() throws JSONException, IOException {
        URL urlImage = getCoverImage();
        //it's not possible to make the connection in the main thread
        Handler handler = new Handler(Looper.getMainLooper());
        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Bitmap bmp = BitmapFactory.decodeStream(urlImage.openConnection().getInputStream());
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
    URL getCoverImage() throws JSONException, MalformedURLException {
        JSONObject joo = jsonObject.getJSONObject("cover");
        String newURL = joo.getString("url").replace("thumb", "cover_big_2x");
        return new URL("https:" + newURL);
    }

    public String getName() throws JSONException {
        return jsonObject.getString("name");
    }

    public String[] getGenre() throws JSONException {
        JSONArray genres = jsonObject.getJSONArray("genres");
        JSONObject genreObject;
        String[] genresString = new String[genres.length()];
        for (int i = 0; i < genres.length(); i++){
            genreObject = genres.getJSONObject(i);
            genresString[i] = genreObject.getString("name");
        }
        return genresString;
    }

    public String getReleaseDate() throws JSONException {
        JSONArray dates = jsonObject.getJSONArray("release_dates");
        JSONObject dateObject;
        ArrayList<Calendar> dateArrayList = new ArrayList<>();

        for (int i = 0; i < dates.length(); i++){
            dateObject = dates.getJSONObject(i);
            String dateString = dateObject.getString("date");
            String newDateString = dateString + "000";
            Long dateLong = Long.parseLong(newDateString);
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(dateLong);
            dateArrayList.add(date);
        }

        Calendar calendar = Collections.min(dateArrayList);
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
        String[] months = dateFormatSymbols.getMonths();
        String month = months[calendar.get(Calendar.MONTH)];
        String date = calendar.get(Calendar.DAY_OF_MONTH)
                + " " + month
                + " " + calendar.get(Calendar.YEAR);
        return date;
    }

    public String[] getPlatforms() throws JSONException {
        JSONArray platforms = jsonObject.getJSONArray("platforms");
        JSONObject platformObject;
        String[] platformsString = new String[platforms.length()];

        for (int i = 0; i < platforms.length(); i++){
            platformObject = platforms.getJSONObject(i);
            platformsString[i] = platformObject.getString("name");
        }
        return platformsString;
    }

    public String[] getAllURLScreenshots() throws JSONException, MalformedURLException {
        JSONArray screenshots = jsonObject.getJSONArray("screenshots");
        String[] urls = new String[screenshots.length()];
        JSONObject screenshotObject;

        for (int i = 0; i < jsonArray.length(); i++){
            screenshotObject = screenshots.getJSONObject(i);
            String url = screenshotObject.getString("url");
            //String newURL = screenshotsJSONObject.getString("url").replace("thumb", "");
            urls[i] = new URL("https:" + url).toString();
            Log.i("screenshots", urls[i]);
        }
        return urls;
    }

}


