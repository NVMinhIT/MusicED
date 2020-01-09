package com.example.musiced.screen.playmusic.lyric;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.musiced.R;
import com.example.musiced.screen.playmusic.playimage.PlayAnimationFragment;

public class LyricsFragment extends Fragment {
    TextView textViewLyric;

    public static LyricsFragment newInstance() {
        LyricsFragment lyricsFragment = new LyricsFragment();
        return lyricsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lyric, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        textViewLyric = view.findViewById(R.id.txt_lyric);
    }
}
