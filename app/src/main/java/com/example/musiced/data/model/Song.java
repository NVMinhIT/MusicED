package com.example.musiced.data.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Song implements Serializable {
    private int SongId;
    private String SongName;
    private String SingerName;
    private String pathSong;
    private int DurationSong;

    public Song() {

    }

    public Song(int songId, String songName, String singerName, String pathSong, int durationSong) {
        SongId = songId;
        SongName = songName;
        SingerName = singerName;
        this.pathSong = pathSong;
        DurationSong = durationSong;
    }

    protected Song(Parcel in) {
        SongId = in.readInt();
        SongName = in.readString();
        SingerName = in.readString();
        pathSong = in.readString();
        DurationSong = in.readInt();
    }


    public int getSongId() {
        return SongId;
    }

    public void setSongId(int songId) {
        SongId = songId;
    }

    public String getSongName() {
        return SongName;
    }

    public void setSongName(String songName) {
        SongName = songName;
    }

    public String getSingerName() {
        return SingerName;
    }

    public void setSingerName(String singerName) {
        SingerName = singerName;
    }

    public String getPathSong() {
        return pathSong;
    }

    public void setPathSong(String pathSong) {
        this.pathSong = pathSong;
    }

    public int getDurationSong() {
        return DurationSong;
    }

    public void setDurationSong(int durationSong) {
        DurationSong = durationSong;
    }


}
