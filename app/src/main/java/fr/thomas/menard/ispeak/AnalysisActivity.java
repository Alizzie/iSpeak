package fr.thomas.menard.ispeak;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.thomas.menard.ispeak.Utils.Event;
import fr.thomas.menard.ispeak.Utils.EventAdapter;
import fr.thomas.menard.ispeak.Utils.RecordingModel;
import fr.thomas.menard.ispeak.Utils.Trial;
import fr.thomas.menard.ispeak.databinding.ActivityAnalysisBinding;

public class AnalysisActivity extends AppCompatActivity {

    private int taskNumber;
    private String task, outputFile, date, patientID;
    private ActivityAnalysisBinding binding;

    MediaPlayer mediaPlayer;

    private EventAdapter eventAdapter;

    private RecyclerView.LayoutManager layoutManager;

    int eventselected =0;



    private boolean isPressedATM1, isPressedATM2, isPressedATM3, isPressedSTL1,isPressedSTL2,
            isPressedSTL3,isPressedSTL4;
    private boolean isPressedSTS1, isPressedSTS2, isPressedSTS3, isPressedSTS4, isPressedSTS5;
    private boolean isPressedAbd, isPressedAdd, isPressedElev, isPressedExtR, isPressedIntR;
    private boolean isPressedInc, isPressedRot, isPressedFlexTrunk;
    private boolean isPressedIncHead, isPressedRotHead, isPressedFlexHead;



    List<Trial> listTrial = new ArrayList<>();
    List<String>postLabel = new ArrayList<>();

    List<Event> listEvent = new ArrayList<>();

    List<RecordingModel> listRecording = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnalysisBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        init();
        listenBtnLabel();
        initRecycler();
        listenBtnValidate();
        listenbtnAudio();
        displayWaves();

    }

    private void init() {
        Intent intent = getIntent();
        taskNumber = intent.getIntExtra("taskNumber", 0);
        task = intent.getStringExtra("task");
        outputFile = intent.getStringExtra("output_file");
        date = intent.getStringExtra("date");
        patientID = intent.getStringExtra("patientID");
        listTrial = (List<Trial>) intent.getSerializableExtra("listPostLabel");
        listRecording = (List<RecordingModel>) intent.getSerializableExtra("listRecording");
        listEvent = (List<Event>) intent.getSerializableExtra("listEvent");

        Log.d("TEST", "list trial"+listTrial.get(0).getNumber_compens());
        Log.d("TEST", "list recording"+listRecording.size());
        Log.d("TEST", "list event"+listEvent.size());



        binding.txtTask.setText(task);
        if (taskNumber < 5)
            binding.txtBodyNumer.setText("1");
        else
            binding.txtBodyNumer.setText("2");

        binding.customCardView.setVisibility(View.VISIBLE);

    }

    private void initRecycler(){
        layoutManager = new LinearLayoutManager(getApplicationContext());
        binding.recyclerTaskEvent.setHasFixedSize(true);
        binding.recyclerTaskEvent.setLayoutManager(layoutManager);
        eventAdapter = new EventAdapter(listEvent, getApplicationContext(), postLabel);
        binding.recyclerTaskEvent.setAdapter(eventAdapter);
        binding.recyclerTaskEvent.setVisibility(View.VISIBLE);
        // the post labelling layout appear if it's invisible
        eventAdapter.setOnClickListener(visible -> {
            if(visible)
                binding.layoutPostLabel.setVisibility(View.VISIBLE);
            else {
                binding.layoutPostLabel.setVisibility(View.GONE);
                postLabel.clear();
                resetBoolean();
                modifyBackground();
            }
        });

        eventAdapter.setOnTitleListener((number, categorie, task) -> {
            binding.txtCategoriePostLabel.setText(categorie);
            binding.txtTaskPostLabel.setText(task);
            eventselected = number;
            Log.d("TEST", ""+number);
        });




    }

    private void modifyBackground(){
        if(!isPressedATM1)
            binding.btnATM1.setBackgroundResource(R.drawable.bg_button);
        if(!isPressedATM2)
            binding.btnATM2.setBackgroundResource(R.drawable.bg_button);
        if(!isPressedATM3)
            binding.btnATM3.setBackgroundResource(R.drawable.bg_button);

        if(!isPressedSTL1)
            binding.btnSTL1.setBackgroundResource(R.drawable.bg_button);
        if(!isPressedSTL2)
            binding.btnSTL2.setBackgroundResource(R.drawable.bg_button);
        if(!isPressedSTL3)
            binding.btnSTL3.setBackgroundResource(R.drawable.bg_button);
        if(!isPressedSTL4)
            binding.btnSTL4.setBackgroundResource(R.drawable.bg_button);


        if(!isPressedSTS1)
            binding.btnSTS1.setBackgroundResource(R.drawable.bg_button);
        if(!isPressedSTS2)
            binding.btnSTS2.setBackgroundResource(R.drawable.bg_button);
        if(!isPressedSTS3)
            binding.btnSTS3.setBackgroundResource(R.drawable.bg_button);
        if(!isPressedSTS4)
            binding.btnSTS4.setBackgroundResource(R.drawable.bg_button);
        if(!isPressedSTS5)
            binding.btnSTS5.setBackgroundResource(R.drawable.bg_button);



    }

    private void resetBoolean(){
        isPressedATM1 = false;
        isPressedATM2 = false;
        isPressedATM3 = false;
        isPressedSTL1 = false;
        isPressedSTL2 = false;
        isPressedSTL3 = false;
        isPressedSTL4 = false;
        isPressedSTS1 = false;
        isPressedSTS2 = false;
        isPressedSTS3 = false;
        isPressedSTS4 = false;
        isPressedSTS5 = false;
    }

    private void listenBtnValidate() {
        binding.btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(taskNumber==2){
                    Intent intent = new Intent(getApplicationContext(), FinishActivity.class);
                    intent.putExtra("patientID", patientID);
                    intent.putExtra("date", date);
                    intent.putExtra("listRecording", (Serializable) listRecording);
                    startActivity(intent);
                    finish();
                }else{
                    taskNumber += 1;
                    Intent intent = new Intent(getApplicationContext(), AssessmentActivity.class);
                    intent.putExtra("taskNumber", taskNumber);
                    intent.putExtra("patientID", patientID);
                    intent.putExtra("date", date);
                    intent.putExtra("listRecording", (Serializable) listRecording);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    private void listenbtnAudio() {
        binding.btnListenAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer = new MediaPlayer();

                try {
                    mediaPlayer.setDataSource(outputFile);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    displayWaves();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void listenBtnLabel(){
        binding.btnATM1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isPressedATM1){
                    postLabel.add("Erhöhte Einatmungshäufigk");
                    isPressedATM1 = true;
                    binding.btnATM1.setBackgroundResource(R.drawable.bg_button_postlabel_pressed);
                }else{
                    isPressedATM1 = false;
                    binding.btnATM1.setBackgroundResource(R.drawable.bg_button);
                    postLabel.remove("Erhöhte Einatmungshäufigk");
                }
                eventAdapter.notifyItemChanged(eventselected - 1);
            }
        });
        binding.btnATM2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isPressedATM2){
                    postLabel.add("übermäBiges Unterschreiten");
                    isPressedATM2 = true;
                    binding.btnATM2.setBackgroundResource(R.drawable.bg_button_postlabel_pressed);
                }else{
                    isPressedATM2 = false;
                    binding.btnATM2.setBackgroundResource(R.drawable.bg_button);
                    postLabel.remove("übermäBiges Unterschreiten");
                }
                eventAdapter.notifyItemChanged(eventselected - 1);
            }
        });
        binding.btnATM3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isPressedATM3){
                    postLabel.add("Hör-/sichtbar");
                    isPressedATM3 = true;
                    binding.btnATM3.setBackgroundResource(R.drawable.bg_button_postlabel_pressed);
                }else{
                    isPressedATM3 = false;
                    binding.btnATM3.setBackgroundResource(R.drawable.bg_button);
                    postLabel.remove("Hör-/sichtbar");
                }
                eventAdapter.notifyItemChanged(eventselected - 1);
            }
        });



        binding.btnSTL1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isPressedSTL1){
                    postLabel.add("Zu hoch");
                    isPressedSTL1 = true;
                    binding.btnSTL1.setBackgroundResource(R.drawable.bg_button_postlabel_pressed);
                }else{
                    isPressedSTL1 = false;
                    binding.btnSTL1.setBackgroundResource(R.drawable.bg_button);
                    postLabel.remove("Zu hoch");
                }
                eventAdapter.notifyItemChanged(eventselected - 1);
            }
        });
        binding.btnSTL2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isPressedSTL2){
                    postLabel.add("Zu tief");
                    isPressedSTL2 = true;
                    binding.btnSTL2.setBackgroundResource(R.drawable.bg_button_postlabel_pressed);
                }else{
                    isPressedSTL2 = false;
                    binding.btnSTL2.setBackgroundResource(R.drawable.bg_button);
                    postLabel.remove("Zu tief");
                }
                eventAdapter.notifyItemChanged(eventselected - 1);
            }
        });
        binding.btnSTL3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isPressedSTL3){
                    postLabel.add("Zu laut");
                    isPressedSTL3 = true;
                    binding.btnSTL3.setBackgroundResource(R.drawable.bg_button_postlabel_pressed);
                }else{
                    isPressedSTL3 = false;
                    binding.btnSTL3.setBackgroundResource(R.drawable.bg_button);
                    postLabel.remove("Zu laut");
                }
                eventAdapter.notifyItemChanged(eventselected - 1);
            }
        });
        binding.btnSTL4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isPressedSTL4){
                    postLabel.add("Zu leise");
                    isPressedSTL4 = true;
                    binding.btnSTL4.setBackgroundResource(R.drawable.bg_button_postlabel_pressed);
                }else{
                    isPressedSTL4 = false;
                    binding.btnSTL4.setBackgroundResource(R.drawable.bg_button);
                    postLabel.remove("Zu leise");
                }
                eventAdapter.notifyItemChanged(eventselected - 1);
            }
        });


        binding.btnSTS1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isPressedSTS1){
                    postLabel.add("Wechselnde stimmqualität");
                    isPressedSTS1 = true;
                    binding.btnSTS1.setBackgroundResource(R.drawable.bg_button_postlabel_pressed);
                }else{
                    isPressedSTS1 = false;
                    binding.btnSTS1.setBackgroundResource(R.drawable.bg_button);
                    postLabel.remove("Wechselnde stimmqualität");
                }
                eventAdapter.notifyItemChanged(eventselected - 1);
            }
        });
        binding.btnSTS2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isPressedSTS2){
                    postLabel.add("Lautstärke");
                    isPressedSTS2 = true;
                    binding.btnSTS2.setBackgroundResource(R.drawable.bg_button_postlabel_pressed);
                }else{
                    isPressedSTS2 = false;
                    binding.btnSTS2.setBackgroundResource(R.drawable.bg_button);
                    postLabel.remove("Lautstärke");
                }
                eventAdapter.notifyItemChanged(eventselected - 1);
            }
        });
        binding.btnSTS3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isPressedSTS3){
                    postLabel.add("Stimmzittern");
                    isPressedSTS3 = true;
                    binding.btnSTS3.setBackgroundResource(R.drawable.bg_button_postlabel_pressed);
                }else{
                    isPressedSTS3 = false;
                    binding.btnSTS3.setBackgroundResource(R.drawable.bg_button);
                    postLabel.remove("Stimmzittern");
                }
                eventAdapter.notifyItemChanged(eventselected - 1);


            }

        });
        binding.btnSTS4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isPressedSTS4){
                    postLabel.add("Entstimmungen");
                    isPressedSTS4 = true;
                    binding.btnSTS4.setBackgroundResource(R.drawable.bg_button_postlabel_pressed);
                }else{
                    isPressedSTS4 = false;
                    binding.btnSTS4.setBackgroundResource(R.drawable.bg_button);
                    postLabel.remove("Entstimmungen");
                }
                eventAdapter.notifyItemChanged(eventselected - 1);
            }
        });
        binding.btnSTS5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isPressedSTS5){
                    postLabel.add("Unwillkürliche");
                    isPressedSTS5 = true;
                    binding.btnSTS5.setBackgroundResource(R.drawable.bg_button_postlabel_pressed);
                }else{
                    isPressedSTS5 = false;
                    binding.btnSTS5.setBackgroundResource(R.drawable.bg_button);
                    postLabel.remove("Unwillkürliche");
                }
                eventAdapter.notifyItemChanged(eventselected - 1);
            }
        });





    }

    private void displayWaves(){
        new Thread(() -> {
            try {
                FileInputStream fileInputStream = new FileInputStream(outputFile);
                byte[] audioBuffer = new byte[4000];
                int bytesRead;

                while ((bytesRead = fileInputStream.read(audioBuffer)) != -1) {
                    // Update the waveform view with the new audio data
                    int finalBytesRead = bytesRead;
                    runOnUiThread(() -> binding.waveFormAnalysis.setWaveform(Arrays.copyOf(audioBuffer, finalBytesRead)));
                }

                fileInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

}