package com.example.musiced.screen.main;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.example.musiced.R;
import com.example.musiced.screen.personalmusic.MusicPersonFragment;
import com.example.musiced.screen.playvideo.VideoFragment;
import com.example.musiced.utils.navigator.BottomNavigationViewHelper;
import com.example.musiced.utils.navigator.Navigator;

public class MainActivity extends AppCompatActivity {

    public Navigator navigator;
    public BottomNavigationView bottomNavigationView;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initview();
        bottomNavigationView = findViewById(R.id.bottom_nav);
        setupBottomNav(bottomNavigationView.getSelectedItemId());
        //BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                setupBottomNav(item.getItemId());
                return true;
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void hideNavigationBar() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }

    private void initview() {
        navigator = new Navigator(this);
    }
    private void setupBottomNav(int itemId) {
        switch (itemId) {
            case R.id.action_person:
                navigator.addFragment(R.id.content_main, MusicPersonFragment.newInstance(),
                        false, Navigator.NavigateAnim.FADED, MusicPersonFragment.TAG);
                break;

            case R.id.action_video:
                navigator.addFragment(R.id.content_main, VideoFragment.newInstance(),
                        false,Navigator.NavigateAnim.FADED,VideoFragment.TAG);
            default:
        }
    }
}