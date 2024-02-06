package fr.thomas.menard.ispeak.Adapter;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

import fr.thomas.menard.ispeak.R;
import fr.thomas.menard.ispeak.Utils.RecordingModel;

public class RecordingAdapter extends RecyclerView.Adapter<RecordingAdapter.ViewHolder> {

    private List<RecordingModel> recordingList;
    private MediaPlayer mediaPlayer;
    private int playingPosition = -1;

    public RecordingAdapter(List<RecordingModel> recordingList) {
        this.recordingList = recordingList;
        mediaPlayer = new MediaPlayer();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recording, parent, false);
        return new ViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecordingModel recording = recordingList.get(position);
        holder.bind(recording, position == playingPosition);
    }

    @Override
    public int getItemCount() {
        return recordingList.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtPath, txtCategorie, txtTask, txtEvent;
        private ImageButton btnPlay;
        private List<RecordingModel> recordingList;
        private MediaPlayer mediaPlayer;
        private RecordingAdapter adapter;


        public ViewHolder(@NonNull View itemView, RecordingAdapter adapter) {
            super(itemView);
            txtPath = itemView.findViewById(R.id.txt_path);
            txtCategorie = itemView.findViewById(R.id.txtCategorieRecording);
            txtTask = itemView.findViewById(R.id.txtTaskRecording);
            txtEvent = itemView.findViewById(R.id.txtEventRecording);
            btnPlay = itemView.findViewById(R.id.imPlay);
            this.adapter = adapter;

            btnPlay.setOnClickListener(v -> {
                // Handle play button click
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    // Get the RecordingModel at this position
                    RecordingModel recording = adapter.recordingList.get(position);
                    adapter.playAudio(recording.getFilePath(), position);
                }
            });
        }

        public void bind(RecordingModel recording, boolean isPlaying) {
            txtPath.setText(recording.getFilePath());
            txtCategorie.setText(recording.getCategorie());
            txtTask.setText(recording.getTask());
            txtEvent.setText(recording.getNumber_event());

            // Change the button icon based on the playback state
            if (isPlaying) {
                btnPlay.setImageResource(R.drawable.pase);
            } else {
                btnPlay.setImageResource(R.drawable.play_24);
            }
        }
    }
    private void playAudio(String filePath, int position) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            playingPosition = -1;
            Log.d("TEST", "release" );
            releaseMediaPlayer();

            notifyDataSetChanged();
        } else {
            try {
                mediaPlayer.stop();
                mediaPlayer.reset();
                Log.d("TEST", "" + filePath + mediaPlayer.getAudioSessionId() + mediaPlayer.isPlaying());
                mediaPlayer.setDataSource(filePath);
                mediaPlayer.prepare();
                mediaPlayer.start();
                playingPosition = position;
            } catch (IOException e) {
                e.printStackTrace();
            }
            notifyDataSetChanged();
        }
    }

    // Release the MediaPlayer when the activity is destroyed
    public void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


}

