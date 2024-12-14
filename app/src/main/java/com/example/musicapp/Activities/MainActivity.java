package com.example.musicapp.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.Adapters.MusicAdapter;
import com.example.musicapp.Models.MusicList;
import com.example.musicapp.R;
import com.example.musicapp.SongChangeListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements SongChangeListener {
    ImageView btn_playPause, btn_next, btn_prev;
    LinearLayout btn_search, btn_menu;
    RecyclerView musicRecycleView;
    CardView playPause_Card;
    private Timer timer;

    private final List<MusicList> musicLists = new ArrayList<>();

    private MediaPlayer mediaPlayer;

    private TextView endTime, startTime;

    private boolean isPlaying = false;

    private SeekBar playerSeekBar;

    private int currentSongListPosition = 0;

    private MusicAdapter musicAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorateView = getWindow().getDecorView();
        setContentView(R.layout.activity_main);
        AnhXa();

        musicRecycleView = findViewById(R.id.musicRecyleView);
        musicRecycleView.setLayoutManager(new LinearLayoutManager(this));

        int options = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorateView.setSystemUiVisibility(options);

        mediaPlayer = new MediaPlayer();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)   {
            getMusicFiles();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 11);
            } else {
                getMusicFiles();
            }
        }


        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int nextSongListPosition = currentSongListPosition+1;

                if (nextSongListPosition>=musicLists.size())    {
                    nextSongListPosition = 0;
                }
                musicLists.get(currentSongListPosition).setPlaying(false);
                musicLists.get(nextSongListPosition).setPlaying(true);

                musicAdapter.updateList(musicLists);

                musicRecycleView.scrollToPosition(nextSongListPosition);

                onChange(nextSongListPosition);
            }
        });

        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int prevSongListPosition = currentSongListPosition-1;

                if (prevSongListPosition<0)    {
                    prevSongListPosition = musicLists.size()-1; //last song
                }
                musicLists.get(currentSongListPosition).setPlaying(false);
                musicLists.get(prevSongListPosition).setPlaying(true);

                musicAdapter.updateList(musicLists);

                musicRecycleView.scrollToPosition(prevSongListPosition);

                onChange(prevSongListPosition);
            }
        });

        playPause_Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying)  {
                    isPlaying = false;

                    mediaPlayer.pause();
                    btn_playPause.setImageResource(R.drawable.ic_play);
                } else {
                    isPlaying = true;

                    mediaPlayer.start();
                    btn_playPause.setImageResource(R.drawable.ic_pause);
                }
            }
        });

        playerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)   {
                    if (isPlaying)  {
                        mediaPlayer.seekTo(progress);
                    } else {
                        mediaPlayer.seekTo(0);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }//-END onCreate

    private void AnhXa()    {
        btn_search = findViewById(R.id.btn_Search);
        btn_menu = findViewById(R.id.btn_Menu);

        musicRecycleView = findViewById(R.id.musicRecyleView);
        playPause_Card = findViewById(R.id.playPause_Card);

        btn_playPause = findViewById(R.id.btn_playPause);
        btn_next = findViewById(R.id.btn_Next);
        btn_prev = findViewById(R.id.btn_Prev);

        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);

        playerSeekBar = findViewById(R.id.seekBarPlayer);
    }

    private void setupRecyclerView() {
        musicRecycleView.setHasFixedSize(true);
        musicRecycleView.setLayoutManager(new LinearLayoutManager(this));
        musicRecycleView.setAdapter(musicAdapter);
    }

//    @SuppressLint("Range")
//    private void getMusicFiles()    {
//
//        ContentResolver contentResolver = getContentResolver();
//
//        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//
//        Cursor cursor = contentResolver.query(uri, null,
//                MediaStore.Audio.Media.DATA+" LIKE?", new String[]{"%.mp3"}, null);
//        if (cursor == null) {
//            Toast.makeText(this, "Some thing went wrong!!", Toast.LENGTH_SHORT).show();
//        } else if(!cursor.moveToNext()) {
//            Toast.makeText(this, "No Music found.", Toast.LENGTH_LONG).show();
//        } else {
//            while (cursor.moveToNext()) {
//                final String getMusicFileName =
//                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
//
//                final String getArtistName =
//                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
//
//                long cusorId =
//                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
//
//                Uri musicFileUri =
//                        ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,cusorId);
//
//                String getDuration = "00:00";
//                Log.d("MUSIC_DATA", "Title: " + getTitle() + ", Artist: " + getArtistName + ", Duration: " + getDuration + ", URI: " + uri);
//                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q)  {
//                    getDuration =
//                            cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));
//                }
//
//                final MusicList musicList = new MusicList(getMusicFileName, getArtistName, getDuration, false, musicFileUri);
//                musicLists.add(musicList);
//            }
//
//            musicAdapter = new MusicAdapter(musicLists, this);
////            musicRecycleView.setAdapter(musicAdapter);
//            setupRecyclerView();
//        }
//        cursor.close();
//    }

    @SuppressLint("Range")
    private void getMusicFiles() {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        // Sử dụng projection để chỉ lấy các cột cần thiết
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"; // Chỉ lấy file nhạc
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC"; // Sắp xếp theo tên

        Cursor cursor = null;
        try {
            cursor = contentResolver.query(uri, projection, selection, null, sortOrder);
        } catch (SecurityException e) {
            Log.e("PERMISSION_ERROR", "Permission denied: " + e.getMessage());
            Toast.makeText(this, "Ứng dụng không có quyền truy cập!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cursor == null) {
            Toast.makeText(this, "Đã xảy ra lỗi!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "Không tìm thấy nhạc!", Toast.LENGTH_LONG).show();
            cursor.close();
            return;
        }

        List<MusicList> musicFiles = new ArrayList<>();

        try {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));

                // Xử lý trường hợp artist rỗng hoặc null
                artist = (artist != null && !artist.trim().isEmpty()) ? artist : "<unknown>";


                Uri musicFileUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);


                long durationMillis = 0;
                try {
                    durationMillis = Long.parseLong(duration);
                } catch (NumberFormatException e) {
                    Log.w("DURATION_ERROR", "Invalid duration format: " + duration);
                }

                //Định dạng duration hiển thị MM:SS
                String formattedDuration = "";
                if (durationMillis > 0) {
                    formattedDuration = String.format(Locale.getDefault(), "%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(durationMillis),
                            TimeUnit.MILLISECONDS.toSeconds(durationMillis) % 60);
                }

                Log.d("MUSIC_DATA", "Title: " + title + ", Artist: " + artist + ", Duration: " + formattedDuration + ", URI: " + musicFileUri);

                MusicList musicList = new MusicList(title, artist, formattedDuration, false, musicFileUri);
                musicFiles.add(musicList);
            }
        } catch (Exception e) {
            Log.e("GET_MUSIC_FILES_ERROR", "Lỗi khi đọc dữ liệu nhạc: " + e.getMessage());
            Toast.makeText(this, "Lỗi khi đọc dữ liệu nhạc!", Toast.LENGTH_SHORT).show();
            return;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        runOnUiThread(() -> {
            musicLists.clear();
            musicLists.addAll(musicFiles);
            musicAdapter = new MusicAdapter(musicLists, MainActivity.this);
            setupRecyclerView();
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus)   {
            View decorateView = getWindow().getDecorView();

            int options = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            decorateView.setSystemUiVisibility(options);
        }
    }

    @Override
    public void onChange(int position) {

        currentSongListPosition = position;
        if (mediaPlayer.isPlaying())    {
            mediaPlayer.pause();
            mediaPlayer.reset();
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mediaPlayer.setDataSource(MainActivity.this, musicLists.get(position).getMusicFile());
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Unable to Play play track", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                final int getTotalDuration = mp.getDuration();

                String genrenateDuration = String.format(Locale.getDefault(), "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(getTotalDuration),
                        TimeUnit.MILLISECONDS.toSeconds(getTotalDuration),
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getTotalDuration)));

                endTime.setText(genrenateDuration);

                isPlaying = true;

                mp.start();

                playerSeekBar.setMax(getTotalDuration);

                btn_playPause.setImageResource(R.drawable.ic_pause);
            }
        });

        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        final int getCurrentDuration = mediaPlayer.getCurrentPosition();

                        String genrenateDuration = String.format(Locale.getDefault(), "%02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes(getCurrentDuration),
                                TimeUnit.MILLISECONDS.toSeconds(getCurrentDuration),
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrentDuration)));

                        playerSeekBar.setProgress(getCurrentDuration);

                        startTime.setText(genrenateDuration);
                    }
                });
            }
        }, 1000, 1000);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.reset();

                timer.purge();
                timer.cancel();
                isPlaying = false;

                btn_playPause.setImageResource(R.drawable.ic_play);

                playerSeekBar.setProgress(0);
            }
        });
    }
}