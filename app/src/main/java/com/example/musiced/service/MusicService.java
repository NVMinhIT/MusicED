package com.example.musiced.service;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.example.musiced.data.model.PreferenceManager;
import com.example.musiced.data.model.Song;
import com.example.musiced.screen.playmusic.MediaPlayerHolder;
import com.example.musiced.notification.MusicNotification;

import java.util.List;


public class MusicService extends Service {

    private final IBinder mIBinder = new LocalBinder();

    private MediaPlayerHolder mMediaPlayerHolder;

    private MusicNotification musicNotification;

    private boolean sRestoredFromPause = false;

    public final boolean isRestoredFromPause() {
        return sRestoredFromPause;
    }

    public void setRestoredFromPause(boolean restore) {
        sRestoredFromPause = restore;
    }

    public final MediaPlayerHolder getMediaPlayerHolder() {
        return mMediaPlayerHolder;
    }

    public MusicNotification getMusicNotificationManager() {
        return musicNotification;
    }

    private List<Song> listSong;
    Song song;

    @Override
    public void onCreate() {
        super.onCreate();
        listSong = PreferenceManager.getListSong(this);
        if (mMediaPlayerHolder == null) {
            mMediaPlayerHolder = new MediaPlayerHolder(this);
            musicNotification = new MusicNotification(this);
            mMediaPlayerHolder.registerNotificationActionsReceiver(true);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            song = (Song) bundle.getSerializable("SONG");
            if (song != null) {
                mMediaPlayerHolder.setCurrentSong(song, listSong);
                Log.d("minh","" +song);
                mMediaPlayerHolder.initMediaPlayer();
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mMediaPlayerHolder.registerNotificationActionsReceiver(false);
        musicNotification = null;
        mMediaPlayerHolder.release();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {

        return mIBinder;
    }

    public class LocalBinder extends Binder {
        public MusicService getInstance() {
            return MusicService.this;
        }
    }

}
