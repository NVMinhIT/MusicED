package com.example.musiced.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.session.MediaSessionManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.example.musiced.R;
import com.example.musiced.data.model.Song;
import com.example.musiced.screen.listsong.SongActivity;
import com.example.musiced.screen.playmusic.PlayListenInfo;
import com.example.musiced.screen.playmusic.PlayMusicFragment;
import com.example.musiced.service.MusicService;

public class MusicNotification {
    public static final int NOTIFICATION_ID = 101;
    public static final String PLAY_PAUSE_ACTION = "action.PLAYPAUSE";
    public static final String NEXT_ACTION = "action.NEXT";
    public static final String PREV_ACTION = "action.PREV";
    private final String CHANNEL_ID = "action.CHANNEL_ID";
    private final int REQUEST_CODE = 100;
    private NotificationManager mNotificationManager;
    private MusicService mMusicService;
    private NotificationCompat.Builder mNotificationBuilder;
    private MediaSessionCompat mediaSession;
    private MediaSessionManager mediaSessionManager;
    private MediaControllerCompat.TransportControls transportControls;
    private Context context;

    public MusicNotification(@NonNull final MusicService musicService) {
        mMusicService = musicService;
        mNotificationManager = (NotificationManager) mMusicService.getSystemService(Context.NOTIFICATION_SERVICE);
        context = musicService.getBaseContext();
    }

    public final NotificationManager getNotificationManager() {
        return mNotificationManager;
    }

    public final NotificationCompat.Builder getNotificationBuilder() {
        return mNotificationBuilder;
    }

    private PendingIntent playerAction(String action) {

        final Intent pauseIntent = new Intent();
        pauseIntent.setAction(action);

        return PendingIntent.getBroadcast(mMusicService, REQUEST_CODE, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public Notification createNotification() {

        final Song song = mMusicService.getMediaPlayerHolder().getCurrentSong();

        mNotificationBuilder = new NotificationCompat.Builder(mMusicService, CHANNEL_ID);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        final Intent openPlayerIntent = new Intent(mMusicService, SongActivity.class);
        openPlayerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        openPlayerIntent.putExtra("NOTIFICATION", "My notification");
        final PendingIntent contentIntent = PendingIntent.getActivity(mMusicService, REQUEST_CODE,
                openPlayerIntent, 0);
        final String artist = song.getSingerName();
        final String songTitle = song.getSongName();
        initMediaSession(song);
        mNotificationBuilder
                .setShowWhen(false)
                .setSmallIcon(R.drawable.ic_play_small)
                //.setLargeIcon(Utils.songArt(song.path, mMusicService.getBaseContext()))
                //.setColor(context.getResources().getColor(R.color.colorAccent))
                .setContentTitle(songTitle)
                .setContentText(artist)
                .setContentIntent(contentIntent)
                .addAction(notificationAction(PREV_ACTION))
                .addAction(notificationAction(PLAY_PAUSE_ACTION))
                .addAction(notificationAction(NEXT_ACTION))
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        mNotificationBuilder.setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mediaSession.getSessionToken())
                .setShowActionsInCompactView(0, 1, 2));
        return mNotificationBuilder.build();
    }

    @NonNull
    private NotificationCompat.Action notificationAction(final String action) {
        int icon;
        switch (action) {
            default:
            case PREV_ACTION:
                icon = R.drawable.ic_previous_small;
                break;
            case PLAY_PAUSE_ACTION:
                icon = mMusicService.getMediaPlayerHolder().getState() != PlayListenInfo.State.PAUSED
                        ? R.drawable.ic_pause_small : R.drawable.ic_play_small;
                break;
            case NEXT_ACTION:
                icon = R.drawable.ic_next_small;
                break;
        }
        return new NotificationCompat.Action.Builder(icon, action, playerAction(action)).build();
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mNotificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                final NotificationChannel notificationChannel =
                        new NotificationChannel(CHANNEL_ID,
                                mMusicService.getString(R.string.app_name),
                                NotificationManager.IMPORTANCE_LOW);

                notificationChannel.setDescription(
                        mMusicService.getString(R.string.app_name));

                notificationChannel.enableLights(false);
                notificationChannel.enableVibration(false);
                notificationChannel.setShowBadge(false);

                mNotificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    private void initMediaSession(Song song) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaSessionManager = ((MediaSessionManager) context.getSystemService(Context.MEDIA_SESSION_SERVICE));
        }
        mediaSession = new MediaSessionCompat(context, "AudioPlayer");
        transportControls = mediaSession.getController().getTransportControls();
        mediaSession.setActive(true);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        updateMetaData(song);
    }

    private void updateMetaData(Song song) {
        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                //.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, Utils.songArt(song.path, context))
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.getSingerName())
                //.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.albumName)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.getSongName())
                .build());
    }

}
