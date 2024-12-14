package com.example.musicapp.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.Activities.MainActivity;
import com.example.musicapp.Models.MusicList;
import com.example.musicapp.R;
import com.example.musicapp.SongChangeListener;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {

    private List<MusicList> list;
    private final Context context;
    private int playingPosition = -1;
    private final SongChangeListener songChangeListener;

    public MusicAdapter(List<MusicList> list, Context context) {
        this.list = list;
        this.context = context;
        this.songChangeListener = ((SongChangeListener) context);
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public MusicAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.music_adapter_layout, null));
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MusicList musicList = list.get(position);

        // Xử lý trường hợp null hoặc rỗng cho title và artist
        String title = musicList.getTitle() != null ? musicList.getTitle().trim() : "<unknown>";
        String artist = musicList.getArtist() != null ? musicList.getArtist().trim() : "<unknown>";

        String[] titleParts = processTitle(title);
        String finalTitle = titleParts[1];
        String finalArtist = titleParts[0];

        holder.title.setText(finalTitle);
        holder.artist.setText(finalArtist);

        // cập nhật background chỉ khi bài hát được chọn
        if (musicList.isPlaying()) {
            playingPosition = position;
            holder.rootLayout.setBackgroundResource(R.drawable.round_back_blue_10);
        } else {
            holder.rootLayout.setBackgroundResource(R.drawable.round_back_10);
        }

        long durationMillis = musicList.getDurationMillis();
        holder.musicDuration.setText(formatMillis(durationMillis));

        holder.rootLayout.setOnClickListener(v -> {
            runOnUiThread(() -> {
                if (playingPosition != position) {
                    if (playingPosition != -1 && playingPosition < list.size()) { // Check if playingPosition is valid
                        list.get(playingPosition).setPlaying(false);
                        notifyItemChanged(playingPosition); // Update previous item
                    }
                    playingPosition = position;
                    list.get(playingPosition).setPlaying(true);
                    songChangeListener.onChange(playingPosition);
                    notifyItemChanged(playingPosition); // Update current item
                    notifyDataSetChanged();
                }
            });
        });
    }

    private String formatMillis(long millis) {
        if (millis <= 0) return "--:--";
        return String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) % 60);
    }

    private String[] processTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            Log.w("MUSIC_ADAPTER", "Title is null or empty!");
            return new String[]{"<unknown>", "<unknown>"};
        }

        String[] parts = title.split(" - ", 2);
        if (parts.length == 2) {
            String artist = parts[0].trim();
            String songTitle = parts[1].trim();
            Log.d("MUSIC_ADAPTER", "Title processed: Artist='" + artist + "', Title='" + songTitle + "'");
            return new String[]{artist, songTitle};
        } else {
            Log.w("MUSIC_ADAPTER", "Invalid title format! Using full title as title, artist = <unknown>: Title=" + title);
            return new String[]{"<unknown>", title.trim()};
        }
    }


    public void updateList(List<MusicList> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final RelativeLayout rootLayout;
        private final TextView title;
        private final TextView artist;
        private final TextView musicDuration;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            rootLayout = itemView.findViewById(R.id.rootLayout);
            title = itemView.findViewById(R.id.musicTitle);
            artist = itemView.findViewById(R.id.musicArtist);
            musicDuration = itemView.findViewById(R.id.musicDuration);
        }
    }

    private void runOnUiThread(Runnable action) {
        if (context instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) context; // Explicit cast
            mainActivity.runOnUiThread(action);
        }
    }
}