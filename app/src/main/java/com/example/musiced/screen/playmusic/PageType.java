package com.example.musiced.screen.playmusic;

import android.support.annotation.IntDef;

import static com.example.musiced.screen.playmusic.PageType.LIST;
import static com.example.musiced.screen.playmusic.PageType.LYRICS;
import static com.example.musiced.screen.playmusic.PageType.PLAY;

@IntDef({LIST, PLAY, LYRICS})
    public @interface PageType {
    int LIST = 0;
    int PLAY = 1;
    int LYRICS = 2;
}
