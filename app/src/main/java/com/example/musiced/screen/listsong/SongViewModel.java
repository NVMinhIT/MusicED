package com.example.musiced.screen.listsong;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.musiced.data.model.Song;

import java.util.List;

public class SongViewModel extends ViewModel {
    private MutableLiveData<List<Song>> songMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<List<Song>> getSongMutableLiveData() {
        return songMutableLiveData;
    }


}
