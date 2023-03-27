package com.example.gamestorm.source.user;

import static com.example.gamestorm.util.Constants.FIREBASE_IMAGES_COLLECTION;
import static com.example.gamestorm.util.Constants.FIREBASE_PLAYED_GAMES_COLLECTION;
import static com.example.gamestorm.util.Constants.FIREBASE_PLAYING_GAMES_COLLECTION;
import static com.example.gamestorm.util.Constants.FIREBASE_REALTIME_DATABASE;
import static com.example.gamestorm.util.Constants.FIREBASE_USERS_COLLECTION;
import static com.example.gamestorm.util.Constants.FIREBASE_WANTED_GAMES_COLLECTION;
import static com.example.gamestorm.util.Constants.PHOTOPROFILE;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.gamestorm.model.GameApiResponse;
import com.example.gamestorm.model.User;
import com.example.gamestorm.util.Constants;
import com.example.gamestorm.util.SharedPreferencesUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class UserDataRemoteDataSource extends BaseUserDataRemoteDataSource{
    private static final String TAG = UserDataRemoteDataSource.class.getSimpleName();

    private final DatabaseReference databaseReference;
    private SharedPreferencesUtil sharedPreferencesUtil;
    public UserDataRemoteDataSource(SharedPreferencesUtil sharedPreferencesUtil) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        databaseReference = firebaseDatabase.getReference().getRef();
        this.sharedPreferencesUtil = sharedPreferencesUtil;
    }

    @Override
    public void saveUserData(User user) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d(TAG, "User already present in Firebase Realtime Database");
                    HashMap<String,?> hashMap = (HashMap<String, String>) snapshot.getValue();
                    String name = (String) hashMap.get("name");
                    user.setName(name);
                    sharedPreferencesUtil.writeStringData(Constants.SHARED_PREFERENCES_FILE_NAME, Constants.USERNAME, name);
                    userResponseCallback.onSuccessFromRemoteDatabase(user);
                } else {
                    Log.d(TAG, "User not present in Firebase Realtime Database");
                    databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getId()).setValue(user)
                            .addOnSuccessListener(aVoid -> userResponseCallback.onSuccessFromRemoteDatabase(user))
                            .addOnFailureListener(e -> userResponseCallback.onFailureFromRemoteDatabase(e.getLocalizedMessage()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userResponseCallback.onFailureFromRemoteDatabase(error.getMessage());
            }
        });
    }

    @Override
    public void getUserWantedGames(String idToken) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(FIREBASE_WANTED_GAMES_COLLECTION).get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d(TAG, "Error getting data", task.getException());
                        userResponseCallback.onFailureFromRemoteDatabase(task.getException().getLocalizedMessage());
                    }
                    else {
                        Log.d(TAG, "Successful read: " + task.getResult().getValue());

                        List<GameApiResponse> gameApiResponses = new ArrayList<>();
                        for(DataSnapshot ds : task.getResult().getChildren()) {
                            GameApiResponse news = ds.getValue(GameApiResponse.class);
                            gameApiResponses.add(news);
                        }
                        userResponseCallback.onSuccessFromRemoteDatabase(gameApiResponses, "WANTED");
                    }
                });
    }

    @Override
    public void getUserPlayingGames(String idToken) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(FIREBASE_PLAYING_GAMES_COLLECTION).get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d(TAG, "Error getting data", task.getException());
                        userResponseCallback.onFailureFromRemoteDatabase(task.getException().getLocalizedMessage());
                    }
                    else {
                        Log.d(TAG, "Successful read: " + task.getResult().getValue());

                        List<GameApiResponse> gameApiResponses = new ArrayList<>();
                        for(DataSnapshot ds : task.getResult().getChildren()) {
                            GameApiResponse news = ds.getValue(GameApiResponse.class);
                            gameApiResponses.add(news);
                        }
                        userResponseCallback.onSuccessFromRemoteDatabase(gameApiResponses, "PLAYING");
                    }
                });
    }

    @Override
    public void getUserPlayedGames(String idToken) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(FIREBASE_PLAYED_GAMES_COLLECTION).get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d(TAG, "Error getting data", task.getException());
                        userResponseCallback.onFailureFromRemoteDatabase(task.getException().getLocalizedMessage());
                    }
                    else {
                        Log.d(TAG, "Successful read: " + task.getResult().getValue());

                        List<GameApiResponse> gameApiResponses = new ArrayList<>();
                        for(DataSnapshot ds : task.getResult().getChildren()) {
                            GameApiResponse news = ds.getValue(GameApiResponse.class);
                            gameApiResponses.add(news);
                        }
                        userResponseCallback.onSuccessFromRemoteDatabase(gameApiResponses, "PLAYED");
                    }
                });
    }

    @Override
    public void saveUserPreferences(String favoriteCountry, Set<String> favoriteTopics, String idToken) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child("game").setValue(favoriteCountry);

        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child("SHARED_PREFERENCES_TOPICS_OF_INTEREST").setValue(new ArrayList<>(favoriteTopics));

    }
}
