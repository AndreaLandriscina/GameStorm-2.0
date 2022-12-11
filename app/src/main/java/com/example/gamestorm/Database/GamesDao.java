package com.example.gamestorm.Database;

import androidx.room.Dao;


/**
 * Data Access Object (DAO) that provides methods that can be used to query,
 * update, insert, and delete data in the database.
 * https://developer.android.com/training/data-storage/room/accessing-data
 */
@Dao
public interface GamesDao {
    /*
    @Query("SELECT * FROM games ORDER BY published_at DESC")
    List<Game> getAll();

    @Query("SELECT * FROM games WHERE id = :id")
    Game getNews(long id);

    @Query("SELECT * FROM news WHERE is_favorite = 1 ORDER BY published_at DESC")
    List<Game> getFavoriteNews();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertNewsList(List<Game> newsList);

    @Insert
    void insertAll(Game... games);

    @Delete
    void delete(Game game);

    @Query("DELETE FROM games")
    void deleteAll();

    //@Query("DELETE FROM games WHERE is_favorite = 0")
    //void deleteNotFavoriteNews();

    @Delete
    void deleteAllWithoutQuery(Game... games);

    @Update
    void updateSingleFavoriteNews(Game games);

    @Update
    void updateListFavoriteNews(List<Game> games);

     */
}
