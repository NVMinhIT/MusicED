package com.example.musiced.screen.playmusic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musiced.R;
import com.example.musiced.data.model.PreferenceManager;
import com.example.musiced.data.model.Song;
import com.example.musiced.notification.MusicNotification;
import com.example.musiced.screen.listsong.SongActivity;
import com.example.musiced.screen.playmusic.playimage.PlayAnimationFragment;
import com.example.musiced.screen.playmusic.tracklist.ListTrackFragment;
import com.example.musiced.screen.playvideo.VideoFragment;
import com.example.musiced.service.MusicService;
import com.example.musiced.utils.navigator.Equazil;
import com.example.musiced.utils.navigator.Injection;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class PlayMusicFragment extends Fragment implements ViewPager.OnPageChangeListener {
    public static final String TAG = "PlayMusicFragment";
    public TextView tvNameSong, tvNameSinger;
    TextView tvStart, tvLast;
    ImageButton imShuffle, imPre, imPlay, imNext, imRepeat;
    SeekBar seekbar;
    //ImageView imageViewDisc;
    List<Song> aList;
    Animation animation;
    private ImageButton imageButton;
    private boolean mShuffle = false;
    private boolean mRepeat = false;
    MusicService musicService;
    private Boolean mIsBound;
    private IPlayerMedia iPlayerMedia;
    SongActivity songActivity;
    private MusicNotification musicNotification;
    private boolean mUserIsSeeking = false;
    private PlaybackListener mPlaybackListener;
    private Handler mHandler = new Handler();
    private Song selectedSong;
    private Song song;
    private LinearLayout mSliderDots;
    private int mDotsCount;
    private PlayMusicAdapter playMusicAdapter;
    private ImageView[] mDotsView;
    private ViewPager mViewPager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aList = new ArrayList<>();
        aList = songActivity.getmSongList();

        if (getArguments() != null) {
            song = (Song) getArguments().getSerializable("OBJECT");
        }
        doBindService();
    }

    public static PlayMusicFragment newInstance() {
        return new PlayMusicFragment();
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            musicService = ((MusicService.LocalBinder) iBinder).getInstance();
            iPlayerMedia = musicService.getMediaPlayerHolder();
            musicNotification = musicService.getMusicNotificationManager();

            if (mPlaybackListener == null) {
                mPlaybackListener = new PlaybackListener();
                iPlayerMedia.setPlaybackInfoListener(mPlaybackListener);
            }
            if (iPlayerMedia != null && iPlayerMedia.isPlaying()) {

                restorePlayerStatus();
            }
            checkReadStoragePermissions();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicService = null;
        }
    };

    private void checkReadStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    private void restorePlayerStatus() {
        seekbar.setEnabled(iPlayerMedia.isMediaPlayer());

        if (iPlayerMedia != null && iPlayerMedia.isMediaPlayer()) {

            iPlayerMedia.onResumeActivity();
            updatePlayingInfo(true, false);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setUpSliderDot() {
        mDotsCount = playMusicAdapter.getCount();
        mDotsView = new ImageView[mDotsCount];
        for (int i = 0; i < mDotsCount; i++) {
            mDotsView[i] = new ImageView(songActivity);
            mDotsView[i].setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()),
                    R.drawable.inactive_dots));
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            mSliderDots.addView(mDotsView[i], params);
        }
        mDotsView[PageType.PLAY].setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()),
                R.drawable.active_dots));
    }

    private void updatePlayingInfo(boolean restore, boolean startPlay) {

        if (startPlay) {
            iPlayerMedia.getMediaPlayer().start();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    musicService.startForeground(MusicNotification.NOTIFICATION_ID,
                            musicNotification.createNotification());
                }
            }, 250);
        }
        selectedSong = iPlayerMedia.getCurrentSong();
        tvNameSong.setText(selectedSong.getSongName());
        tvNameSinger.setText(selectedSong.getSingerName());
        final int duration = selectedSong.getDurationSong();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dinhdang = new SimpleDateFormat("mm:ss");
        tvLast.setText(String.valueOf(dinhdang.format(duration)));
        seekbar.setMax(duration);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dinhdang9 = new SimpleDateFormat("mm:ss");
                tvStart.setText(String.valueOf(dinhdang9.format(iPlayerMedia.getPlayerPosition()))); // getCurrentPosition: vi tri hien tai
                // update seek bar
                seekbar.setProgress(iPlayerMedia.getPlayerPosition());
                mHandler.postDelayed(this, 100);
            }
        }, 100);
        if (restore) {
            updatePlayingStatus();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (musicService.isRestoredFromPause()) {
                        musicService.stopForeground(false);
                        musicService.getMusicNotificationManager().getNotificationManager()
                                .notify(MusicNotification.NOTIFICATION_ID,
                                        musicService.getMusicNotificationManager().getNotificationBuilder().build());
                        musicService.setRestoredFromPause(false);
                    }
                }
            }, 250);
        }
    }

    private void updatePlayingStatus() {
        final int drawable = iPlayerMedia.getState() != PlayListenInfo.State.PAUSED ?
                R.drawable.ic_pause : R.drawable.ic_play;
        imPlay.post(new Runnable() {
            @Override
            public void run() {
                imPlay.setImageResource(drawable);
            }
        });
    }

    public void checkEvent() {
        imRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRepeat(mRepeat);
            }
        });
        imPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resumeOrPause();
            }
        });

        imNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skipNext();
            }
        });
        imPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skipPrev();
            }
        });
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onPageSelected(int position) {
        if (position != PageType.LIST) {
            Fragment fragment = getChildFragmentManager().findFragmentByTag("android:switcher:" + R.id.view_paper_music + ":" + mViewPager.getCurrentItem());
            if (fragment != null && PageType.LIST == mViewPager.getCurrentItem()) {
                PreferenceManager.setImageUrl(getActivity(), PreferenceManager.getImageUrl(songActivity));
                ListTrackFragment listTrackFragment = (ListTrackFragment) fragment;
                LinearLayoutManager linearLayoutManager = listTrackFragment.getLayoutManager();
                linearLayoutManager.scrollToPosition(PreferenceManager.getLastPosition(songActivity));
            }

        }
        if (position != PageType.PLAY) {

        } else {
            Fragment fragment = getChildFragmentManager().findFragmentByTag("android:switcher:" + R.id.view_paper_music + ":" + mViewPager.getCurrentItem());
            if (fragment != null && PageType.PLAY == mViewPager.getCurrentItem()) {
                PreferenceManager.setImageUrl(getActivity(), PreferenceManager.getImageUrl(songActivity));
                PlayAnimationFragment playAnimationFragment = (PlayAnimationFragment) fragment;
                playAnimationFragment.setImage();
                if (!musicService.isRestoredFromPause()) {
                    playAnimationFragment.cancelAnimation();
                } else {
                    playAnimationFragment.startAnimation();
                }

            }
        }
        for (int i = 0; i < mDotsCount; i++) {
            mDotsView[i].setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()),
                    R.drawable.inactive_dots));
        }
        mDotsView[position].setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()),
                R.drawable.active_dots));
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }


    class PlaybackListener extends PlayListenInfo {

        @Override
        public void onPositionChanged(int position) {
            if (!mUserIsSeeking) {
                seekbar.setProgress(position);
            }
        }

        @Override
        public void onStateChanged(@State int state) {

            updatePlayingStatus();
            if (iPlayerMedia.getState() != State.RESUMED && iPlayerMedia.getState() != State.PAUSED) {
                updatePlayingInfo(false, true);
            }
        }

        @Override
        public void onPlaybackCompleted() {

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SongActivity) {
            songActivity = (SongActivity) context;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play_music, container, false);
        initView(view);
        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_text);
        tvNameSong.startAnimation(animation);
        //imageViewDisc.startAnimation(animation);
        checkEvent();
        initializeSeekBar();
        setUpSliderDot();
        return view;

    }

    private void initView(View view) {
        playMusicAdapter = new PlayMusicAdapter(getChildFragmentManager());
        mSliderDots = view.findViewById(R.id.slider_dots);
        tvStart = view.findViewById(R.id.text_view_start);
        tvLast = view.findViewById(R.id.text_view_end);
        seekbar = view.findViewById(R.id.seekbar_song);
        //imageViewDisc = view.findViewById(R.id.imageViewDisc);
        imShuffle = view.findViewById(R.id.image_shuffle);
        imPre = view.findViewById(R.id.image_back);
        imPlay = view.findViewById(R.id.img_play);
        mViewPager = view.findViewById(R.id.view_paper_music);
        imNext = view.findViewById(R.id.image_next);
        imRepeat = view.findViewById(R.id.image_repeat);
        tvNameSong = view.findViewById(R.id.text_track_title);
        tvNameSinger = view.findViewById(R.id.text_view_artist);
        imageButton = view.findViewById(R.id.image_button_down);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFragmentManager() != null) {
                    getFragmentManager().popBackStack();

                }
            }
        });
        mViewPager.setAdapter(playMusicAdapter);
        mViewPager.setCurrentItem(PageType.PLAY);
        mViewPager.addOnPageChangeListener(this);
    }

    private void doBindService() {
        final Intent startNotStickyIntent = new Intent(getContext(), MusicService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("SONG", song);
        startNotStickyIntent.putExtras(bundle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getContext()).startService(startNotStickyIntent);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getContext()).bindService(new Intent(getContext(),
                    MusicService.class), mConnection, Context.BIND_AUTO_CREATE);
        }
        mIsBound = true;


    }

    private void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Objects.requireNonNull(getActivity()).unbindService(mConnection);
            }
            mIsBound = false;
        }
    }


    // Event Shuffle
    private void setShuffle(boolean s) {
        if (!s) {
            imShuffle.setBackgroundResource(R.drawable.ic_action_shuffle);
            mShuffle = true;

        } else {
            imShuffle.setBackgroundResource(R.drawable.shuffle);
            mShuffle = false;
        }
    }

    // Event Repeat
    private void setRepeat(boolean s) {
        if (!s) {
            imRepeat.setBackgroundResource(R.drawable.ic_action_repeat);
            mRepeat = true;
            iPlayerMedia.setCurrentSong(song, aList);
            iPlayerMedia.initMediaPlayer();
        } else {
            imRepeat.setBackgroundResource(R.drawable.repeat);
            mRepeat = false;
        }
    }


    public void skipPrev() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (checkIsPlayer()) {
                iPlayerMedia.instantReset();
            }
        }
    }

    public void resumeOrPause() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (checkIsPlayer()) {
                iPlayerMedia.resumeOrPause();
            }
        }
    }

    public void skipNext() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (checkIsPlayer()) {
                iPlayerMedia.skip(true);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean checkIsPlayer() {

        boolean isPlayer = iPlayerMedia.isMediaPlayer();
        if (!isPlayer) {
            Equazil.notifyNoSessionId(Objects.requireNonNull(getContext()));
        }
        return isPlayer;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        doUnbindService();
        if (iPlayerMedia != null && iPlayerMedia.isMediaPlayer()) {
            iPlayerMedia.onPauseActivity();
        }
    }

    private void initializeSeekBar() {
        seekbar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int userSelectedPosition = 0;

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        mUserIsSeeking = true;
                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                        if (fromUser) {
                            userSelectedPosition = progress;

                        }

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                        if (mUserIsSeeking) {

                        }
                        mUserIsSeeking = false;
                        iPlayerMedia.seekTo(userSelectedPosition);
                    }
                });
    }

}
