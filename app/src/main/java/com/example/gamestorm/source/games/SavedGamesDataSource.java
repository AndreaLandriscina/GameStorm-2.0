package com.example.gamestorm.source.games;

import static com.example.gamestorm.util.Constants.FIREBASE_PLAYED_GAMES_COLLECTION;
import static com.example.gamestorm.util.Constants.FIREBASE_PLAYING_GAMES_COLLECTION;
import static com.example.gamestorm.util.Constants.FIREBASE_REALTIME_DATABASE;
import static com.example.gamestorm.util.Constants.FIREBASE_USERS_COLLECTION;
import static com.example.gamestorm.util.Constants.FIREBASE_WANTED_GAMES_COLLECTION;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.gamestorm.model.GameApiResponse;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SavedGamesDataSource extends BaseSavedGamesDataSource {
    private static final String TAG = SavedGamesDataSource.class.getSimpleName();

    private final DatabaseReference databaseReference;
    private final String idToken;

    public SavedGamesDataSource(String idToken) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        databaseReference = firebaseDatabase.getReference().getRef();
        this.idToken = idToken;
    }

    @Override
    public void getWantedGames() {
        if (idToken != null){
            databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                    child(FIREBASE_WANTED_GAMES_COLLECTION).get().addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.d(TAG, "Error getting data", task.getException());
                        }
                        else {
                            List<GameApiResponse> gameApiResponses = new ArrayList<>();
                            for (DataSnapshot ds : task.getResult().getChildren()) {
                                GameApiResponse gameApiResponse = ds.getValue(GameApiResponse.class);
                                gameApiResponse.setSynchronized(true);
                                gameApiResponses.add(gameApiResponse);
                            }
                            Log.i("wanted fire", gameApiResponses.toString());
                            gameCallback.onSuccessFromCloudReading(gameApiResponses, "WANTED");
                        }
                    });
        }
    }
    @Override
    public void getPlayingGames() {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(FIREBASE_PLAYING_GAMES_COLLECTION).get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d(TAG, "Error getting data", task.getException());
                    }
                    else {
                        List<GameApiResponse> gameApiResponses = new ArrayList<>();
                        for(DataSnapshot ds : task.getResult().getChildren()) {
                            GameApiResponse gameApiResponse = ds.getValue(GameApiResponse.class);
                            gameApiResponse.setSynchronized(true);
                            gameApiResponses.add(gameApiResponse);
                        }
                        gameCallback.onSuccessFromCloudReading(gameApiResponses, "PLAYING");
                    }
                });
    }
    @Override
    public void getPlayedGames() {
        databaseReference.child(FIREBASE_USERS_COLLECTION)
                .child(idToken)
                .child(FIREBASE_PLAYED_GAMES_COLLECTION).get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d(TAG, "Error getting data", task.getException());
                    }
                    else {
                        List<GameApiResponse> gameApiResponses = new ArrayList<>();
                        for(DataSnapshot ds : task.getResult().getChildren()) {
                            GameApiResponse gameApiResponse = ds.getValue(GameApiResponse.class);
                            gameApiResponse.setSynchronized(true);
                            gameApiResponses.add(gameApiResponse);
                        }
                        gameCallback.onSuccessFromCloudReading(gameApiResponses, "PLAYED");
                    }
                });
    }
    @Override
    public void addWantedGame(GameApiResponse game) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(FIREBASE_WANTED_GAMES_COLLECTION).child(String.valueOf(game.getId())).setValue(game)
                .addOnSuccessListener(aVoid -> {
                    game.setSynchronized(true);
                    Log.i("Firebase", "Aggiunto al DB remoto");
                    gameCallback.onSuccessFromCloudWriting(game, "WANTED");
                })
                .addOnFailureListener(e -> gameCallback.onFailureFromCloud(e));
    }

    @Override
    public void addPlayingGame(GameApiResponse game) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(FIREBASE_PLAYING_GAMES_COLLECTION).child(String.valueOf(game.getId())).setValue(game)
                .addOnSuccessListener(aVoid -> {
                    game.setSynchronized(true);
                    gameCallback.onSuccessFromCloudWriting(game, "PLAYING");
                    Log.i("Firebase", "Aggiunto al DB remoto");
                })
                .addOnFailureListener(e -> gameCallback.onFailureFromCloud(e));
    }
    @Override
    public void synchronizeWantedGame(List<GameApiResponse> notSynchronizedGamesList) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(FIREBASE_WANTED_GAMES_COLLECTION).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<GameApiResponse> gameApiResponseList = new ArrayList<>();
                        for (DataSnapshot ds : task.getResult().getChildren()) {
                            GameApiResponse gameApiResponse = ds.getValue(GameApiResponse.class);
                            gameApiResponse.setSynchronized(true);
                            gameApiResponseList.add(gameApiResponse);
                        }

                        gameApiResponseList.addAll(notSynchronizedGamesList);

                        for (GameApiResponse gameApiResponse : gameApiResponseList) {
                            databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                                    child(FIREBASE_WANTED_GAMES_COLLECTION).
                                    child(String.valueOf(gameApiResponse.getId())).setValue(gameApiResponse).addOnSuccessListener(
                                            unused -> gameApiResponse.setSynchronized(true)
                                    );
                        }
                    }
                });
    }
    @Override
    public void synchronizePlayingGame(List<GameApiResponse> notSynchronizedGamesList) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(FIREBASE_PLAYING_GAMES_COLLECTION).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<GameApiResponse> gameApiResponseList = new ArrayList<>();
                        for (DataSnapshot ds : task.getResult().getChildren()) {
                            GameApiResponse gameApiResponse = ds.getValue(GameApiResponse.class);
                            gameApiResponse.setSynchronized(true);
                            gameApiResponseList.add(gameApiResponse);
                        }

                        gameApiResponseList.addAll(notSynchronizedGamesList);

                        for (GameApiResponse gameApiResponse : gameApiResponseList) {
                            databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                                    child(FIREBASE_PLAYING_GAMES_COLLECTION).
                                    child(String.valueOf(gameApiResponse.getId())).setValue(gameApiResponse).addOnSuccessListener(
                                            unused -> gameApiResponse.setSynchronized(true)
                                    );
                        }
                    }
                });
    }
    @Override
    public void synchronizePlayedGame(List<GameApiResponse> notSynchronizedGamesList) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(FIREBASE_PLAYED_GAMES_COLLECTION).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<GameApiResponse> gameApiResponseList = new ArrayList<>();
                        for (DataSnapshot ds : task.getResult().getChildren()) {
                            GameApiResponse gameApiResponse = ds.getValue(GameApiResponse.class);
                            gameApiResponse.setSynchronized(true);
                            gameApiResponseList.add(gameApiResponse);
                        }

                        gameApiResponseList.addAll(notSynchronizedGamesList);

                        for (GameApiResponse gameApiResponse : gameApiResponseList) {
                            databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                                    child(FIREBASE_PLAYED_GAMES_COLLECTION).
                                    child(String.valueOf(gameApiResponse.getId())).setValue(gameApiResponse).addOnSuccessListener(
                                            unused -> gameApiResponse.setSynchronized(true)
                                    );
                        }
                    }
                });
    }

    @Override
    public void deleteWantedGame(GameApiResponse game) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(FIREBASE_WANTED_GAMES_COLLECTION).child(String.valueOf(game.getId())).
                removeValue().addOnSuccessListener(aVoid -> {
                    game.setSynchronized(true);
                    game.setWanted(false);
                    Log.i("Firebase", "Rimosso dal DB remoto");
                    gameCallback.onSuccessFromCloudWriting(game, "WANTED");
                }).addOnFailureListener(e -> gameCallback.onFailureFromCloud(e));
    }

    @Override
    public void deletePlayingGame(GameApiResponse game) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(FIREBASE_PLAYING_GAMES_COLLECTION).child(String.valueOf(game.getId())).
                removeValue().addOnSuccessListener(aVoid -> {
                    game.setSynchronized(false);
                    game.setPlaying(false);
                    gameCallback.onSuccessFromCloudWriting(game, "PLAYING");
                    Log.i("Firebase", "Rimosso dal DB remoto");
                }).addOnFailureListener(e -> gameCallback.onFailureFromCloud(e));
    }

    @Override
    public void addPlayedGame(GameApiResponse game) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(FIREBASE_PLAYED_GAMES_COLLECTION).child(String.valueOf(game.getId())).setValue(game)
                .addOnSuccessListener(aVoid -> {
                    game.setSynchronized(true);
                    gameCallback.onSuccessFromCloudWriting(game, "PLAYED");
                    Log.i("Firebase", "Aggiunto al DB remoto");
                })
                .addOnFailureListener(e -> gameCallback.onFailureFromCloud(e));
    }

    @Override
    public void deletePlayedGame(GameApiResponse game) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(FIREBASE_PLAYED_GAMES_COLLECTION).child(String.valueOf(game.getId())).
                removeValue().addOnSuccessListener(aVoid -> {
                    game.setSynchronized(false);
                    gameCallback.onSuccessFromCloudWriting(game, "PLAYED");
                    Log.i("Firebase", "Rimosso dal DB remoto");
                }).addOnFailureListener(e -> gameCallback.onFailureFromCloud(e));
    }

    @Override
    public void getForYouGames(long lastUpdate) {

    }
}
