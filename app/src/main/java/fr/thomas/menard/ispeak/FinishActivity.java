package fr.thomas.menard.ispeak;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.thomas.menard.ispeak.Adapter.RecordingAdapter;
import fr.thomas.menard.ispeak.Utils.RecordingModel;
import fr.thomas.menard.ispeak.databinding.ActivityFinishBinding;

public class FinishActivity extends AppCompatActivity {

    private ActivityFinishBinding binding;

    private List<RecordingModel> recordingList;
    private RecordingAdapter recordingAdapter;

    private String patientID, date;

    private MediaPlayer mediaPlayer;

    List<RecordingModel> listRecording = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFinishBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        init();
    }

    private void init(){

        Intent intent = getIntent();
        patientID = intent.getStringExtra("patientID");
        date = intent.getStringExtra("date");
        listRecording = (List<RecordingModel>) intent.getSerializableExtra("listRecording");

        //recordingList = getRecordingList(); // Implement this method

        // Initialize RecyclerView and set its adapter

        binding.recyclerRecording.setLayoutManager(new LinearLayoutManager(this));
        recordingAdapter = new RecordingAdapter(listRecording);
        binding.recyclerRecording.setAdapter(recordingAdapter);


    }

    /*

    private List<RecordingModel> getRecordingList() {
        List<RecordingModel> recordingList = new ArrayList<>();


        String baseDirectory = Environment.getExternalStorageDirectory() +
                File.separator + Environment.DIRECTORY_DCIM +
                File.separator + "iSpeak_recordings";


        File directory = new File(baseDirectory +
                File.separator + patientID +
                File.separator + date);


        // Check if the directory exists and is a directory
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    Log.d("TEST", "path file" + file);
                    // Assuming the file is an audio recording, you can adjust this check based on your file types
                    if (file.isFile() && file.getName().endsWith(".3gp")) {
                        String filePath = file.getAbsolutePath();
                        RecordingModel recording = new RecordingModel(filePath,null, null);
                        recordingList.add(recording);
                    }
                }
            }
        }

        return recordingList;


    }

     */
}