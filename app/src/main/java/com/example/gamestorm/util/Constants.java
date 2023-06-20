package com.example.gamestorm.util;

public class Constants {
    public static final int FRESH_TIMEOUT = 60*30*1000;
    public static final String LAST_UPDATE_HOME = "last_update_home";
    public static final String YOUTUBE_URL = "http://www.youtube.com/watch?v=";
    public static final String GAME_API_BASE_URL = "https://api.igdb.com/v4/";
    public static final String TOP_HEADLINES_ENDPOINT = "games";
    public static final String CLIENT_ID = "Client-ID";
    public static final String CLIENT_ID_VALUE = "w70u2nsn2gzl9g0xsbe3uvguva3zos";
    public static final String TOKEN_API_VALUE = "Bearer g5ii3offgar43vpgjg9i0vf73madhk"; //expires after some time
    public static final String DEFAULT_WEB_CLIENT_ID = "667206803957-4vkaelrskd7a6o3gn0698mop60ce9ips.apps.googleusercontent.com";
    public static final String TOKEN_API = "Authorization";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_TYPE_VALUE = "application/json;charset=utf-8";
    public static final String UNEXPECTED_ERROR = "unexpected_error";
    public static final String INVALID_USER_ERROR = "invalidUserError";
    public static final String INVALID_CREDENTIALS_ERROR = "invalidCredentials";
    public static final String USER_COLLISION_ERROR = "userCollisionError";
    public static final String WEAK_PASSWORD_ERROR = "passwordIsWeak";
    public static final String FIREBASE_REALTIME_DATABASE = "https://gamestorm-74204-default-rtdb.europe-west1.firebasedatabase.app/";
    public static final String FIREBASE_USERS_COLLECTION = "users";
    public static final String FIREBASE_WANTED_GAMES_COLLECTION = "wanted_games";
    public static final String FIREBASE_PLAYING_GAMES_COLLECTION = "playing_games";
    public static final String FIREBASE_PLAYED_GAMES_COLLECTION = "played_games";


    public static final String SHARED_PREFERENCES_FILE_NAME = "com.example.gamestorm.preferences";
    public static final String ENCRYPTED_SHARED_PREFERENCES_FILE_NAME = "com.example.gamestorm.encrypted_preferences";
    public static final String ENCRYPTED_DATA_FILE_NAME = "com.example.gamestorm.encrypted_file.txt";
    public static final String USERNAME = "username";
    public static final String EMAIL_ADDRESS = "email_address";
    public static final String PASSWORD = "password";
    public static final String ID_TOKEN = "google_token";

    public static final String SHARED_PREFERENCES_FIRST_LOADING_WANTED = "first_loading";
    public static final String SHARED_PREFERENCES_FIRST_LOADING_PLAYING = "first_loading";
    public static final String SHARED_PREFERENCES_FIRST_LOADING_PLAYED = "first_loading";

}
