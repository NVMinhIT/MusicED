package com.example.musiced.screen.playvideo;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.musiced.R;
import com.example.musiced.screen.personalmusic.MusicPersonFragment;

import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class VideoFragment extends Fragment {
    public static final String TAG = "VideoFragment";
    VideoView videoView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play_video, container, false);
        init(view);
        return view;
    }

    public static VideoFragment newInstance() {
        return new VideoFragment();
    }

    private void init(View view) {
        videoView = view.findViewById(R.id.video_view);
        String videoPath = "android.resource://" + Objects.requireNonNull(getActivity()).getPackageName() + "/" + R.raw.phule;
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);
        videoView.start();
        MediaController mediaController = new MediaController(getContext());
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);
    }
}
