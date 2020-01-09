package com.example.musiced.screen.playmusic;

import android.media.MediaPlayer;
import android.support.annotation.NonNull;

import com.example.musiced.data.model.Song;

import java.util.List;

public interface IPlayerMedia {
    void initMediaPlayer();

    void release();

    boolean isMediaPlayer();

    boolean isPlaying();

    void resumeOrPause();

    void reset();

    boolean isReset();

    void instantReset();

    void skip(final boolean isNext);

    void seekTo(final int position);

    void setPlaybackInfoListener(final PlayListenInfo playListenInfo);

    Song getCurrentSong();

    @PlayListenInfo.State
    int getState();

    int getPlayerPosition();

    void registerNotificationActionsReceiver(final boolean isRegister);


    void setCurrentSong(@NonNull final Song song, @NonNull final List<Song> songs);

    MediaPlayer getMediaPlayer();

    void onPauseActivity();

    void onResumeActivity();
}
