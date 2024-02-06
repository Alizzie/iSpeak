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



    private boolean isPressedMultiPlural, isPressedPluriDigi, isPressedLatPinch, isPressedHandPalmar,
            isPressedDigitoPalmar,isPressedRanking,isPressedHandUlnar,isPressedInterdigital;
    private boolean isPressedFlexWrist, isPressedTremor, isPressedPron, isPressedSup;
    private boolean isPressedAbd, isPressedAdd, isPressedElev, isPressedExtR, isPressedIntR;
    private boolean isPressedInc, isPressedRot, isPressedFlexTrunk;
    private boolean isPressedIncHead, isPressedRotHead, isPressedFlexHead;



    List<Trial> listTrial = new ArrayList<>();
    List<String>postLabel = new ArrayList<>();

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

        Log.d("TEST", "list trial"+listTrial.get(0).getNumber_compens());
        Log.d("TEST", "list recording"+listRecording.size());


        binding.txtTask.setText(task);
        if (taskNumber < 5)
            binding.txtBodyNumer.setText("1");
        else
            binding.txtBodyNumer.setText("2");

        binding.customCardView.setVisibility(View.VISIBLE);

        Toast.makeText(this, "id task" + taskNumber, Toast.LENGTH_SHORT).show();

    }

    private void initRecycler(){
        layoutManager = new LinearLayoutManager(getApplicationContext());
        binding.recyclerTaskEvent.setHasFixedSize(true);
        binding.recyclerTaskEvent.setLayoutManager(layoutManager);
        eventAdapter = new EventAdapter(listTrial, getApplicationContext(), postLabel);
        binding.recyclerTaskEvent.setAdapter(eventAdapter);
        binding.recyclerTaskEvent.setVisibility(View.VISIBLE);
        // the post labelling layout appear if it's invisible
        eventAdapter.setOnClickListener(visible -> {
            if(visible)
                binding.layoutPostLabel.setVisibility(View.VISIBLE);
            else
                binding.layoutPostLabel.setVisibility(View.GONE);
        });

        eventAdapter.setOnTitleListener((number, categorie, task, side) -> {
            binding.txtidTrial.setText(number);
            binding.txtCategoriePostLabel.setText(categorie);
            binding.txtTaskPostLabel.setText(task);
            binding.txtSidePostLabel.setText(side);
        });




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
        Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
        /*3
        binding.btnMultiplural.setOnClickListener(view -> {
            if(!isPressedMultiPlural) {
                postLabel.add("Multiplural");
                isPressedMultiPlural = true;
                binding.btnMultiplural.setBackgroundResource(R.drawable.bg_button_postlabel_pressed);
            }
            else{
                isPressedMultiPlural = false;
                binding.btnMultiplural.setBackgroundResource(R.drawable.bg_button);
                postLabel.remove("Multiplural");
            }
            eventAdapter.notifyDataSetChanged();
        });
        binding.btnPluriDigital.setOnClickListener(view -> {
            if(!isPressedPluriDigi) {
                postLabel.add("PluriDigital");
                isPressedPluriDigi = true;
                binding.btnPluriDigital.setBackgroundResource(R.drawable.bg_button_postlabel_pressed);
            }
            else{
                isPressedPluriDigi = false;
                binding.btnPluriDigital.setBackgroundResource(R.drawable.bg_button);
                postLabel.remove("PluriDigital");
            }
            eventAdapter.notifyDataSetChanged();

        });
        binding.btnLateralPinch.setOnClickListener(view -> {
            if(!isPressedLatPinch) {
                postLabel.add("LateralPinch");
                isPressedLatPinch = true;
                binding.btnLateralPinch.setBackgroundResource(R.drawable.bg_button_postlabel_pressed);
            }
            else{
                isPressedLatPinch = false;
                binding.btnLateralPinch.setBackgroundResource(R.drawable.bg_button);
                postLabel.remove("LateralPinch");

            }
            eventAdapter.notifyDataSetChanged();

        });
        binding.btnPalmar.setOnClickListener(view -> {
            if(!isPressedHandPalmar) {
                postLabel.add("Palmar");
                isPressedHandPalmar = true;
                binding.btnPalmar.setBackgroundResource(R.drawable.bg_button_postlabel_pressed);
            }
            else{
                isPressedHandPalmar = false;
                binding.btnPalmar.setBackgroundResource(R.drawable.bg_button);
                postLabel.remove("Palmar");
            }
            eventAdapter.notifyDataSetChanged();

        });
        binding.btnDigitopalmar.setOnClickListener(view -> {
            if(!isPressedDigitoPalmar) {
                postLabel.add("DigitoPalmar");
                isPressedDigitoPalmar = true;
                binding.btnDigitopalmar.setBackgroundResource(R.drawable.bg_button_postlabel_pressed);
            }
            else{
                isPressedDigitoPalmar = false;
                binding.btnDigitopalmar.setBackgroundResource(R.drawable.bg_button);
                postLabel.remove("DigitoPalmar");
            }
            eventAdapter.notifyDataSetChanged();

        });
        binding.btnRanking.setOnClickListener(view -> {
            if(!isPressedRanking) {
                postLabel.add("Ranking");
                isPressedRanking = true;
                binding.btnRanking.setBackgroundResource(R.drawable.bg_button_postlabel_pressed);
            }
            else{
                isPressedRanking = false;
                binding.btnRanking.setBackgroundResource(R.drawable.bg_button);
                postLabel.remove("Ranking");

            }
            eventAdapter.notifyDataSetChanged();

        });
        binding.btnUlnar.setOnClickListener(view -> {
            if(!isPressedHandUlnar) {
                postLabel.add("Ulnar");
                isPressedHandUlnar = true;
                binding.btnUlnar.setBackgroundResource(R.drawable.bg_button_postlabel_pressed);
            }
            else{
                isPressedHandUlnar = false;
                binding.btnUlnar.setBackgroundResource(R.drawable.bg_button);
                postLabel.remove("Ulnar");

            }
            eventAdapter.notifyDataSetChanged();

        });
        binding.btnInterdigital.setOnClickListener(view -> {
            if(!isPressedInterdigital) {
                postLabel.add("Interdigital");
                isPressedInterdigital = true;
                binding.btnInterdigital.setBackgroundResource(R.drawable.bg_button_postlabel_pressed);
            }
            else{
                isPressedInterdigital = false;
                binding.btnInterdigital.setBackgroundResource(R.drawable.bg_button);
                postLabel.remove("Interdigital");

            }
            eventAdapter.notifyDataSetChanged();

        });

        binding.btnFlex.setOnClickListener(view -> {
            if(!isPressedFlexWrist) {
                postLabel.add("WristFlex");
                isPressedFlexWrist = true;
                binding.btnFlex.setBackgroundResource(R.drawable.bg_button_postlabel_pressed);
            }
            else{
                isPressedFlexWrist = false;
                binding.btnFlex.setBackgroundResource(R.drawable.bg_button);
                postLabel.remove("WristFlex");

            }
            eventAdapter.notifyDataSetChanged();

        });
        binding.btnTremor.setOnClickListener(view -> {
            if(!isPressedTremor) {
                postLabel.add("Tremor");
                isPressedTremor = true;
                binding.btnTremor.setBackgroundResource(R.drawable.bg_button_postlabel_pressed);
            }
            else{
                isPressedTremor = false;
                binding.btnTremor.setBackgroundResource(R.drawable.bg_button);
                postLabel.remove("Tremor");
            }
            eventAdapter.notifyDataSetChanged();

        });
        binding.btnPron.setOnClickListener(view -> {
            if(!isPressedPron) {
                isPressedPron = true;
                binding.btnPron.setBackgroundResource(R.drawable.bg_button_postlabel_pressed);
                postLabel.add("Pron");

            }
            else{
                isPressedPron = false;
                binding.btnPron.setBackgroundResource(R.drawable.bg_button);
                postLabel.remove("Pron");

            }
            eventAdapter.notifyDataSetChanged();

        });
        binding.btnSup.setOnClickListener(view -> {
            if(!isPressedSup) {
                isPressedSup = true;
                binding.btnSup.setBackgroundResource(R.drawable.bg_button_postlabel_pressed);
                postLabel.add("Sup");
            }
            else{
                isPressedSup = false;
                binding.btnSup.setBackgroundResource(R.drawable.bg_button);
                postLabel.remove("Sup");
            }
            eventAdapter.notifyDataSetChanged();

        });

        binding.btnAbd.setOnClickListener(view -> {
            if(!isPressedAbd) {
                isPressedAbd = true;
                binding.btnAbd.setBackgroundResource(R.drawable.bg_button_postlabel_pressed);
                postLabel.add("Abd");
            }
            else{
                isPressedAbd = false;
                binding.btnAbd.setBackgroundResource(R.drawable.bg_button);
                postLabel.remove("Abd");
            }
            eventAdapter.notifyDataSetChanged();

        });
        binding.btnAdd.setOnClickListener(view -> {
            if(!isPressedAdd) {
                isPressedAdd = true;
                binding.btnAdd.setBackgroundResource(R.drawable.bg_button_postlabel_pressed);
                postLabel.add("Add");
            }
            else{
                isPressedAdd = false;
                binding.btnAdd.setBackgroundResource(R.drawable.bg_button);
                postLabel.remove("Add");
            }
            eventAdapter.notifyDataSetChanged();

        });
        binding.btnElev.setOnClickListener(view -> {
            if(!isPressedElev) {
                isPressedElev = true;
                binding.btnElev.setBackgroundResource(R.drawable.bg_button_postlabel_pressed);
                postLabel.add("Elev");
            }
            else{
                isPressedElev = false;
                binding.btnElev.setBackgroundResource(R.drawable.bg_button);
                postLabel.remove("Elev");
            }
            eventAdapter.notifyDataSetChanged();

        });
        binding.btnExtR.setOnClickListener(view -> {
            if(!isPressedExtR) {
                isPressedExtR = true;
                binding.btnExtR.setBackgroundResource(R.drawable.bg_button_postlabel_pressed);
                postLabel.add("ExtR");
            }
            else{
                isPressedExtR = false;
                binding.btnExtR.setBackgroundResource(R.drawable.bg_button);
                postLabel.remove("ExtR");
            }
            eventAdapter.notifyDataSetChanged();

        });
        binding.btnIntR.setOnClickListener(view -> {
            if(!isPressedIntR) {
                isPressedIntR = true;
                binding.btnIntR.setBackgroundResource(R.drawable.bg_button_postlabel_pressed);
                postLabel.add("IntR");
            }
            else{
                isPressedIntR = false;
                binding.btnIntR.setBackgroundResource(R.drawable.bg_button);
                postLabel.remove("IntR");

            }
            eventAdapter.notifyDataSetChanged();

        });

        binding.btnInc.setOnClickListener(view -> {
            if(!isPressedInc) {
                isPressedInc = true;
                binding.btnInc.setBackgroundResource(R.drawable.bg_button_postlabel_pressed);
                postLabel.add("Inc");
            }
            else{
                isPressedInc = false;
                binding.btnInc.setBackgroundResource(R.drawable.bg_button);
                postLabel.remove("Inc");
            }
            eventAdapter.notifyDataSetChanged();

        });
        binding.btnRot.setOnClickListener(view -> {
            if(!isPressedRot) {
                isPressedRot = true;
                binding.btnRot.setBackgroundResource(R.drawable.bg_button_postlabel_pressed);
                postLabel.add("Rot");
            }
            else{
                isPressedRot = false;
                binding.btnRot.setBackgroundResource(R.drawable.bg_button);
                postLabel.remove("Rot");
            }
            eventAdapter.notifyDataSetChanged();

        });
        binding.btnFlexTrunk.setOnClickListener(view -> {
            if(!isPressedFlexTrunk) {
                isPressedFlexTrunk = true;
                binding.btnFlexTrunk.setBackgroundResource(R.drawable.bg_button_postlabel_pressed);
                postLabel.add("FlexTrunk");
            }
            else{
                isPressedFlexTrunk = false;
                binding.btnFlexTrunk.setBackgroundResource(R.drawable.bg_button);
                postLabel.remove("FlexTrunk");
            }
            eventAdapter.notifyDataSetChanged();

        });

        binding.btnIncHead.setOnClickListener(view -> {
            if(!isPressedIncHead) {
                isPressedIncHead = true;
                binding.btnIncHead.setBackgroundResource(R.drawable.bg_button_postlabel_pressed);
                postLabel.add("IncHead");
            }
            else{
                isPressedIncHead = false;
                binding.btnIncHead.setBackgroundResource(R.drawable.bg_button);
                postLabel.remove("IncHead");
            }
            eventAdapter.notifyDataSetChanged();

        });
        binding.btnRotHead.setOnClickListener(view -> {
            if(!isPressedRotHead) {
                isPressedRotHead = true;
                binding.btnRotHead.setBackgroundResource(R.drawable.bg_button_postlabel_pressed);
                postLabel.add("RotHead");
            }
            else{
                isPressedRotHead = false;
                binding.btnRotHead.setBackgroundResource(R.drawable.bg_button);
                postLabel.remove("RotHead");
            }
            eventAdapter.notifyDataSetChanged();

        });
        binding.btnFlexHead.setOnClickListener(view -> {
            if(!isPressedFlexHead) {
                isPressedFlexHead = true;
                binding.btnFlexHead.setBackgroundResource(R.drawable.bg_button_postlabel_pressed);
                postLabel.add("FlexHead");
            }
            else{
                isPressedFlexHead = false;
                binding.btnFlexHead.setBackgroundResource(R.drawable.bg_button);
                postLabel.remove("FlexHead");
            }
            eventAdapter.notifyDataSetChanged();
            


        });

         */


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