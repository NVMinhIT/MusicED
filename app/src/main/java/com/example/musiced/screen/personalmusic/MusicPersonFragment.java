package com.example.musiced.screen.personalmusic;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.musiced.R;
import com.example.musiced.screen.listsong.SongActivity;
import com.example.musiced.service.MusicService;
import com.example.musiced.utils.navigator.Navigator;

import java.util.Objects;

public class MusicPersonFragment extends Fragment {
    public static final String TAG = "MusicPersonFragment";
    TextView tvSong;
    Navigator navigator;


    public static MusicPersonFragment newInstance() {
        return new MusicPersonFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_music_person, container, false);
        initView(v);

        return v;
    }

    private void initView(View v) {
        tvSong = v.findViewById(R.id.txt_song);
        navigator = new Navigator(this);
        tvSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigator.startActivity(SongActivity.getInstance(getContext()));

            }
        });
    }
}

