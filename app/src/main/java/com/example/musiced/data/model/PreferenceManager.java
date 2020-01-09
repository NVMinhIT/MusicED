package com.example.musiced.data.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PreferenceManager {

    private static SharedPreferences preferences(Context context) {
        return context.getSharedPreferences("MUSIC", Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor edit(Context context) {
        return preferences(context).edit();
    }

    public static List<Song> getListSong(Context context) {
        String songs = preferences(context).getString("LIST_SONG", null);
        Type listType = new TypeToken<ArrayList<Song>>() {
        }.getType();
        return new Gson().fromJson(songs, listType);
    }

    public static void putListSongs(Context context, List<Song> songs) {
        edit(context).putString("LIST_SONG", new Gson().toJson(songs)).commit();
    }
    public static String getImageUrl(Context context) {
        return preferences(context).getString("GET_IMAGE_URL", null);
    }

    public static void setImageUrl(Context context, String url) {
        edit(context).putString("SET_IMAGE_URL", url).commit();
    }

    public static int getLastPosition(Context context) {
        return preferences(context).getInt("GET_LAST_POSITION", 0);
    }
}
