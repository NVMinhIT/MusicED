package com.example.musiced.screen.playmusic;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.FrameLayout;

import com.example.musiced.screen.playmusic.lyric.LyricsFragment;
import com.example.musiced.screen.playmusic.playimage.PlayAnimationFragment;
import com.example.musiced.screen.playmusic.tracklist.ListTrackFragment;

public class PlayMusicAdapter extends FragmentPagerAdapter {
    public static final int TAB_COUNT = 3;

    public PlayMusicAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case PageType.LIST:
                return ListTrackFragment.newInstance();
            case PageType.PLAY:
                return PlayAnimationFragment.newInstance();
            case PageType.LYRICS:
                return LyricsFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }
}
