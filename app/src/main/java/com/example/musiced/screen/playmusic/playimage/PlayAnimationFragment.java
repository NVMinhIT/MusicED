package com.example.musiced.screen.playmusic.playimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.musiced.R;
import com.example.musiced.data.model.PreferenceManager;
import com.example.musiced.screen.listsong.SongActivity;

import java.util.Objects;

public class PlayAnimationFragment extends Fragment {
    private SongActivity songActivity;
    private ImageView imageView;
    private Animation mAnimation;

    public static PlayAnimationFragment newInstance() {
        PlayAnimationFragment playAnimationFragment = new PlayAnimationFragment();
        return playAnimationFragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frament_play_animation, container, false);
        init(view);
        mAnimation = AnimationUtils.loadAnimation(imageView.getContext(), R.anim.disc);
        imageView.startAnimation(mAnimation);
        setImage();
        return view;
    }

    private void init(View view) {
        imageView = view.findViewById(R.id.imageViewDisc);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setImage() {
//        Glide.with(Objects.requireNonNull(getContext()))
//                //.load(PreferenceManager.getImageUrl(getActivity()))
//                .asBitmap()
//                .placeholder(R.drawable.compacc_disc)
//                .error(R.mipmap.ic_launcher_round)
//                .listener(new RequestListener<Bitmap>() {
//                    @Override
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
//                        imageView.setImageBitmap(resource);
//                        return false;
//                    }
//                })
//                .preload();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SongActivity) {
            songActivity = (SongActivity) context;
        }
    }

    public void startAnimation() {
        imageView.startAnimation(mAnimation);
    }


    public void cancelAnimation() {
        imageView.clearAnimation();
    }
}
