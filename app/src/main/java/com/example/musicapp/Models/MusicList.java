package com.example.musicapp.Models;

import android.net.Uri;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MusicList {

    private String title, artist, duration;
    private boolean isPlaying;
    private Uri musicFile;

    public MusicList(String title, String artist, String duration, boolean  isPlaying, Uri musicFile) {
        this.isPlaying = isPlaying;
        this.duration = duration;
        this.artist = artist;
        this.title = title;
        this.musicFile = musicFile;
    }

    public String getTitle() {
        return title;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public String getDuration() {
        return duration;
    }

    public String getArtist() {
        return artist;
    }

    public Uri getMusicFile() {
        return musicFile;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMusicFile(Uri musicFile) {
        this.musicFile = musicFile;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getDurationMillis() {
        try {
            String[] parts = duration.split(":");
            if (parts.length == 2) {
                return TimeUnit.MINUTES.toMillis(Long.parseLong(parts[0])) + TimeUnit.SECONDS.toMillis(Long.parseLong(parts[1]));
            } else {
                return 0;
            }
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
