package com.example.musiced.screen.playmusic;

import android.support.annotation.IntDef;
import android.support.v4.media.session.PlaybackStateCompat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class PlayListenInfo {

    public void onStateChanged(final @State int state) {
    }
    public void onPositionChanged(final int position) {
    }
    public void onPlaybackCompleted() {
    }

    @IntDef({State.INVALID, State.PLAYING, State.PAUSED, State.COMPLETED, State.RESUMED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {

        int INVALID = -1;
        int PLAYING = 0;
        int PAUSED = 1;
        int COMPLETED = 2;
        int RESUMED = 3;
    }
}
