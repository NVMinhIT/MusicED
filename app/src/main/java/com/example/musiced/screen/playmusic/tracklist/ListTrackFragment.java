package com.example.musiced.screen.playmusic.tracklist;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.musiced.R;
import com.example.musiced.screen.playmusic.lyric.LyricsFragment;

public class ListTrackFragment extends Fragment {
    TextView textViewListTrack;
    private LinearLayoutManager mLayoutManager;

    public static ListTrackFragment newInstance() {
        ListTrackFragment listTrackFragment = new ListTrackFragment();
        return listTrackFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_track, container, false);
        mLayoutManager = new LinearLayoutManager(getActivity());
        init(view);
        return view;
    }

    public LinearLayoutManager getLayoutManager() {
        return mLayoutManager;
    }

    private void init(View view) {
        textViewListTrack = view.findViewById(R.id.txt_list_track);

    }
}
