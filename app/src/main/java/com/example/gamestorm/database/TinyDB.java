package com.example.gamestorm.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.gamestorm.model.GameApiResponse;
import com.google.gson.Gson;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;



public class TinyDB {

    private Context context;
    private SharedPreferences preferences;

    public TinyDB(Context appContext) {
        preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        context = appContext;
    }

    // METODI GET

    public ArrayList<String> getListString(String key) {
        return new ArrayList<String>(Arrays.asList(TextUtils.split(preferences.getString(key, ""), "‚‗‚")));
    }


   public List<GameApiResponse> getListObject(String key, Class<?> mClass){
   	Gson gson = new Gson();

  	ArrayList<String> objStrings = getListString(key);
    	List<GameApiResponse> objects =  new ArrayList<>();

   	for(String jObjString : objStrings){
        GameApiResponse value  = (GameApiResponse) gson.fromJson(jObjString,  mClass);
    		objects.add(value);
    	}
    	return objects;
   }

   //METODI PUT

    public void putListString(String key, ArrayList<String> stringList) {
        checkForNullKey(key);
        String[] myStringList = stringList.toArray(new String[stringList.size()]);
        preferences.edit().putString(key, TextUtils.join("‚‗‚", myStringList)).apply();
    }

   public void putListObject(String key, List<GameApiResponse> objArray){
   	checkForNullKey(key);
   	Gson gson = new Gson();
   	ArrayList<String> objStrings = new ArrayList<String>();
   	for(Object obj : objArray){
   		objStrings.add(gson.toJson(obj));
  	}
   	putListString(key, objStrings);
    }

    private void checkForNullKey(String key){
        if (key == null){
            throw new NullPointerException();
        }
    }
}