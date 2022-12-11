package com.example.gamestorm.Database;

import android.content.Context;

import androidx.room.Database;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class GamesRoomDatabase  {

    public abstract GamesDao gamesDao();

    private static volatile GamesRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors();
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static GamesRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (GamesRoomDatabase.class) {
                if (INSTANCE == null) {
                    //INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                           // GamesRoomDatabase.class, "NEWS_DATABASE_NAME").build();
                }
            }
        }
        return INSTANCE;
    }
}
