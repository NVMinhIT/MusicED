package com.example.musiced.utils.navigator;

import android.content.Context;

public class Injection {

    public static Context provideContext() {
        return MusicPlayerApplication.getInstance();
    }
}
