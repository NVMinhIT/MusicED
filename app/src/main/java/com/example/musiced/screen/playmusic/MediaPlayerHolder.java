package com.example.musiced.screen.playmusic;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.NonNull;

import com.example.musiced.data.model.Song;
import com.example.musiced.notification.MusicNotification;
import com.example.musiced.service.MusicService;


import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MediaPlayerHolder implements IPlayerMedia,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {
    private static final float VOLUME_DUCK = 0.2f;

    private static final float VOLUME_NORMAL = 1.0f;

    private static final int AUDIO_NO_FOCUS_NO_DUCK = 0;

    private static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;

    private static final int AUDIO_FOCUSED = 2;
    private Context mContext;
    private MusicService mMusicService;
    private AudioManager mAudioManager;
    private MediaPlayer mMediaPlayer;
    private PlayListenInfo mPlayListenInfo;
    private ScheduledExecutorService mExecutor;
    private Runnable mSeekBarPositionUpdateTask;
    private Song mSelectedSong;
    private List<Song> mSongs;
    private boolean sReplaySong = false;
    private @PlayListenInfo.State
    int mState;
    private NotificationReceiver mNotificationActionsReceiver;
    private MusicNotification mMusicNotificationManager;
    private int mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
    private boolean mPlayOnFocusGain;

    private final AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {

                @Override
                public void onAudioFocusChange(int focusChange) {

                    switch (focusChange) {
                        case AudioManager.AUDIOFOCUS_GAIN:
                            mCurrentAudioFocusState = AUDIO_FOCUSED;
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:

                            mCurrentAudioFocusState = AUDIO_NO_FOCUS_CAN_DUCK;
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:

                            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
                            mPlayOnFocusGain = isMediaPlayer() && mState == PlayListenInfo.State.PLAYING
                                    || mState == PlayListenInfo.State.RESUMED;
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS:

                            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
                            break;
                    }

                    if (mMediaPlayer != null) {

                        configurePlayerState();
                    }

                }
            };

    private void configurePlayerState() {

        if (mCurrentAudioFocusState == AUDIO_NO_FOCUS_NO_DUCK) {

            pauseMediaPlayer();
        } else {

            if (mCurrentAudioFocusState == AUDIO_NO_FOCUS_CAN_DUCK) {

                mMediaPlayer.setVolume(VOLUME_DUCK, VOLUME_DUCK);
            } else {
                mMediaPlayer.setVolume(VOLUME_NORMAL, VOLUME_NORMAL);
            }
            if (mPlayOnFocusGain) {
                resumeMediaPlayer();
                mPlayOnFocusGain = false;
            }
        }
    }

    public MediaPlayerHolder(@NonNull final MusicService musicService) {
        mMusicService = musicService;
        mContext = mMusicService.getApplicationContext();
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    private void registerActionsReceiver() {
        mNotificationActionsReceiver = new NotificationReceiver();
        final IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(MusicNotification.PREV_ACTION);
        intentFilter.addAction(MusicNotification.PLAY_PAUSE_ACTION);
        intentFilter.addAction(MusicNotification.NEXT_ACTION);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        intentFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

        mMusicService.registerReceiver(mNotificationActionsReceiver, intentFilter);
    }

    private void unregisterActionsReceiver() {
        if (mMusicService != null && mNotificationActionsReceiver != null) {
            try {
                mMusicService.unregisterReceiver(mNotificationActionsReceiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (mPlayListenInfo != null) {
            mPlayListenInfo.onStateChanged(PlayListenInfo.State.COMPLETED);
            mPlayListenInfo.onPlaybackCompleted();
        }

        if (sReplaySong) {
            if (isMediaPlayer()) {
                resetSong();
            }
            sReplaySong = false;
        } else {
            skip(true);
        }
    }

    private void resetSong() {
        mMediaPlayer.seekTo(0);
        mMediaPlayer.start();
        setStatus(PlayListenInfo.State.PLAYING);
    }

    private void setStatus(final @PlayListenInfo.State int state) {

        mState = state;
        if (mPlayListenInfo != null) {
            mPlayListenInfo.onStateChanged(state);
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {

        //mMediaPlayer.start();
        startUpdatingCallbackWithPosition();
        setStatus(PlayListenInfo.State.PLAYING);
    }

    //private Uri uri ;
    @Override
    public void initMediaPlayer() {

        try {
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
            }
            mMediaPlayer.reset();
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setWakeMode(mContext, PowerManager.PARTIAL_WAKE_LOCK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mMediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build());
            }
            mMusicNotificationManager = mMusicService.getMusicNotificationManager();
            tryToGetAudioFocus();
            mMediaPlayer.setDataSource(mSelectedSong.getPathSong());
            mMediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void tryToGetAudioFocus() {

        final int result = mAudioManager.requestAudioFocus(
                mOnAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mCurrentAudioFocusState = AUDIO_FOCUSED;
        } else {
            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
        }
    }

    @Override
    public void release() {
        if (isMediaPlayer()) {
            mMediaPlayer.release();
            mMediaPlayer = null;
            giveUpAudioFocus();
            unregisterActionsReceiver();
        }
    }

    private void giveUpAudioFocus() {
        if (mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener)
                == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
        }
    }

    @Override
    public boolean isMediaPlayer() {
        return mMediaPlayer != null;
    }

    @Override
    public boolean isPlaying() {
        return isMediaPlayer() && mMediaPlayer.isPlaying();
    }

    @Override
    public void resumeOrPause() {
        if (isPlaying()) {
            pauseMediaPlayer();
        } else {
            resumeMediaPlayer();
        }
    }

    private void pauseMediaPlayer() {
        setStatus(PlayListenInfo.State.PAUSED);
        mMediaPlayer.pause();
        mMusicService.stopForeground(false);
        mMusicNotificationManager.getNotificationManager().notify(MusicNotification.NOTIFICATION_ID, mMusicNotificationManager.createNotification());
    }

    private void resumeMediaPlayer() {
        if (!isPlaying()) {
            mMediaPlayer.start();
            setStatus(PlayListenInfo.State.RESUMED);
            mMusicService.startForeground(MusicNotification.NOTIFICATION_ID, mMusicNotificationManager.createNotification());
        }
    }

    @Override
    public void reset() {
        sReplaySong = !sReplaySong;
    }

    @Override
    public boolean isReset() {
        return sReplaySong;
    }

    @Override
    public void instantReset() {
        if (isMediaPlayer()) {
            if (mMediaPlayer.getCurrentPosition() < 5000) {
                skip(false);
            } else {
                resetSong();
            }
        }
    }

    @Override
    public void skip(boolean isNext) {
        getSkipSong(isNext);
    }

    private void getSkipSong(final boolean isNext) {
        final int currentIndex = mSongs.indexOf(mSelectedSong);

        int index;

        try {
            index = isNext ? currentIndex + 1 : currentIndex - 1;
            mSelectedSong = mSongs.get(index);
        } catch (IndexOutOfBoundsException e) {
            mSelectedSong = currentIndex != 0 ? mSongs.get(0) : mSongs.get(mSongs.size() - 1);
            e.printStackTrace();
        }
        initMediaPlayer();
    }

    @Override
    public void seekTo(int position) {
        if (isMediaPlayer()) {
            mMediaPlayer.seekTo(position);
        }
    }

    @Override
    public void setPlaybackInfoListener(PlayListenInfo listener) {
        mPlayListenInfo = listener;
    }

    @Override
    public Song getCurrentSong() {
        return mSelectedSong;
    }

    @Override
    public final @PlayListenInfo.State
    int getState() {
        return mState;
    }

    @Override
    public int getPlayerPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public void registerNotificationActionsReceiver(boolean isReceiver) {
        if (isReceiver) {
            registerActionsReceiver();
        } else {
            unregisterActionsReceiver();
        }
    }

    @Override
    public void setCurrentSong(@NonNull Song song, @NonNull List<Song> songs) {
        mSelectedSong = song;
        mSongs = songs;
    }

    @Override
    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    @Override
    public void onPauseActivity() {
        stopUpdatingCallbackWithPosition();
    }

    private void stopUpdatingCallbackWithPosition() {
        if (mExecutor != null) {
            mExecutor.shutdownNow();
            mExecutor = null;
            mSeekBarPositionUpdateTask = null;
        }
    }

    @Override
    public void onResumeActivity() {
        startUpdatingCallbackWithPosition();
    }

    private void startUpdatingCallbackWithPosition() {
        if (mExecutor == null) {
            mExecutor = Executors.newSingleThreadScheduledExecutor();
        }
        if (mSeekBarPositionUpdateTask == null) {
            mSeekBarPositionUpdateTask = new Runnable() {
                @Override
                public void run() {
                    updateProgressCallbackTask();
                }
            };
        }

        mExecutor.scheduleAtFixedRate(
                mSeekBarPositionUpdateTask,
                0,
                1000,
                TimeUnit.MILLISECONDS
        );
    }

    private void updateProgressCallbackTask() {
        if (isMediaPlayer() && mMediaPlayer.isPlaying()) {
            int currentPosition = mMediaPlayer.getCurrentPosition();
            if (mPlayListenInfo != null) {
                mPlayListenInfo.onPositionChanged(currentPosition);
            }
        }
    }

    private class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            final String action = intent.getAction();

            if (action != null) {

                switch (action) {
                    case MusicNotification.PREV_ACTION:
                        instantReset();
                        break;
                    case MusicNotification.PLAY_PAUSE_ACTION:
                        resumeOrPause();
                        break;
                    case MusicNotification.NEXT_ACTION:
                        skip(true);
                        break;

                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                        if (mSelectedSong != null) {
                            pauseMediaPlayer();
                        }
                        break;
                    case BluetoothDevice.ACTION_ACL_CONNECTED:
                        if (mSelectedSong != null && !isPlaying()) {
                            resumeMediaPlayer();
                        }
                        break;
                    case Intent.ACTION_HEADSET_PLUG:
                        if (mSelectedSong != null) {
                            switch (intent.getIntExtra("state", -1)) {
                                //0 means disconnected
                                case 0:
                                    pauseMediaPlayer();
                                    break;
                                //1 means connected
                                case 1:
                                    if (!isPlaying()) {
                                        resumeMediaPlayer();
                                    }
                                    break;
                            }
                        }
                        break;
                    case AudioManager.ACTION_AUDIO_BECOMING_NOISY:
                        if (isPlaying()) {
                            pauseMediaPlayer();
                        }
                        break;
                }
            }
        }
    }
}
