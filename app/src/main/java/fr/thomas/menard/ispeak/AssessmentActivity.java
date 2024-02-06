package fr.thomas.menard.ispeak;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.thomas.menard.ispeak.Adapter.TrialAdapter;
import fr.thomas.menard.ispeak.Utils.AudioVisualizer;
import fr.thomas.menard.ispeak.Utils.Event;
import fr.thomas.menard.ispeak.Utils.RecordingModel;
import fr.thomas.menard.ispeak.Utils.Trial;
import fr.thomas.menard.ispeak.Utils.WriteCSV;
import fr.thomas.menard.ispeak.databinding.ActivityAssessmentBinding;


public class AssessmentActivity extends AppCompatActivity implements Visualizer.OnDataCaptureListener {

    private ActivityAssessmentBinding binding;

    private WriteCSV writeCSV;
    String sec, milisec;
    String clockrunning = "stop";
    private AudioVisualizer audioVisualizer;

    MediaPlayer mediaPlayer;

    String outputRecordingPath, path_to_storage, outputCSVPath, outputFile;
    int taskNumber;
    String task, patientID, date, diagnosis, categorie;

    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);

    private AudioRecord audioRecord;
    private byte[] audioBuffer;
    private boolean isRecording = false;

    private boolean event = false;

    private  boolean isPaused;
    private File audioFile;

    long startTime, endTime;

    private int numberEvent = 0, numberTry = 0;

    private static final int PERMISSION_REQUEST_CODE = 1;

    private TrialAdapter adapter;

    private int itemSelected = 0;


    private MediaRecorder mediaRecorder = new MediaRecorder();

    List<Trial> listPostLabel = new ArrayList<>();
    List<Trial> listEvent = new ArrayList<>();

    RecyclerView.LayoutManager layoutManager;

    RecordingModel model;

    List<RecordingModel> listRecoring = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAssessmentBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        //writeCSV = WriteCSV.getInstance(getApplication());

        /*
        if (checkPermission()) {
            setupVisualizer();
        } else {
            requestPermission();
        }

         */
        init();
        initRecycler();
        initTask();
        isMicroPresent();
        listenBtnClock();
        //listenBtnListenAudio();
        listenBtnValidate();
        listenBtnTherapist();
        listenBtnEvent();
        listenBtnEventDetected();

    }

    private void init() {
        Intent intent = getIntent();
        taskNumber = intent.getIntExtra("taskNumber", 0);
        patientID = intent.getStringExtra("patientID");
        date = intent.getStringExtra("date");
        diagnosis = intent.getStringExtra("diagnosis");
        if(intent.getSerializableExtra("listRecording")!=null){
            listRecoring = (List<RecordingModel>) intent.getSerializableExtra("listRecording");

        }

        path_to_storage = getApplicationContext().getExternalFilesDir(null).getAbsolutePath() + "/"+ date + "/" + patientID;
        outputRecordingPath = path_to_storage + "/recordings";
        outputCSVPath = path_to_storage + "/CSV";

        writeCSV = new WriteCSV();
        audioBuffer = new byte[BUFFER_SIZE];

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE);


        if(isMicroPresent())
            getMicroPhonePermissions();




    }


    private void initRecycler(){
        layoutManager = new LinearLayoutManager(getApplicationContext());
        binding.recyclerTrial.setHasFixedSize(true);
        binding.recyclerTrial.setLayoutManager(layoutManager);
    }


    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_CODE);
    }

    private void initTask(){
        if(taskNumber<5)
            binding.txtTitleIdAssessment.setText("1");
        else
            binding.txtTitleIdAssessment.setText("2");

        if(taskNumber==1){
            binding.txtTitleTask.setText("Spont");
            task = "Spont";
            categorie = "BoDyS - 1";
        } else if (taskNumber == 2) {
            binding.txtTitleTask.setText("Nach");
            task = "Nach";
            categorie = "BoDyS - 1";
        }else if (taskNumber == 3) {
            binding.txtTitleTask.setText("Les");
            task = "Les";
            categorie = "BoDyS - 1";
        }else if (taskNumber == 4) {
            binding.txtTitleTask.setText("Bild");
            task = "Bild";
            categorie = "BoDyS - 1";
        }else if (taskNumber == 5) {
            binding.txtTitleTask.setText("Spont");
            task = "Spont";
            categorie = "BoDyS - 2";
        }else if (taskNumber == 6) {
            binding.txtTitleTask.setText("Nach");
            task = "Nach";
            categorie = "BoDyS - 2";
        }else if (taskNumber == 7) {
            binding.txtTitleTask.setText("Les");
            task = "Les";
            categorie = "BoDyS - 2";
        }else if (taskNumber == 8) {
            binding.txtTitleTask.setText("Bild");
            task = "Bild";
            categorie = "BoDyS - 2";
        }


        outputFile = Environment.getExternalStorageDirectory() +
                File.separator + Environment.DIRECTORY_DCIM +
                File.separator + "iSpeak_recordings" + File.separator + patientID + File.separator + date + File.separator + "/recording_"+task+".3gp";
    }

    @SuppressLint("ClickableViewAccessibility")
    private void listenBtnTherapist(){
        binding.btnEventTherapist.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    binding.btnEventTherapist.setBackgroundResource(R.drawable.bg_button_blue);
                    pauseRecording();
                }else{
                    binding.btnEventTherapist.setBackgroundResource(R.drawable.bg_button);
                    resumeRecording();
                }
                return false;            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void listenBtnEvent() {
        binding.btnEventDetected.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    event = true;
                    binding.testCheckboxAndroidButtonTint.setVisibility(View.VISIBLE);
                    startTime = binding.txtChrono.getTimeElapsed();

                    Log.d("TEST", "start" + startTime);

                } else if(e.getAction() == MotionEvent.ACTION_UP){
                    event = false;
                    binding.testCheckboxAndroidButtonTint.setVisibility(View.INVISIBLE);
                    endTime = binding.txtChrono.getTimeElapsed();
                    Log.d("TEST", "end" + endTime);

                }
                return false;
            }
        });
    }

    private void listenBtnEventDetected(){
        binding.btnEventDetected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numberEvent = numberEvent + 1;
            }
        });
    }

    private boolean isMicroPresent(){
        return this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    private void getMicroPhonePermissions(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[]



                    {Manifest.permission.RECORD_AUDIO}, 200);
        }
    }

    private void listenBtnValidate(){
        binding.btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeCSVtask();
                model = new RecordingModel(taskNumber, outputFile, categorie, task, String.valueOf(numberEvent));
                listRecoring.add(model);
                Log.d("TEST", "recording list" + listRecoring.size());
                listPostLabel.add(listEvent.get(adapter.getCheckPosition()));
                Intent intent = new Intent(getApplicationContext(), AnalysisActivity.class);
                intent.putExtra("taskNumber", taskNumber);
                intent.putExtra("patientID", patientID);
                intent.putExtra("date", date);
                intent.putExtra("task", task);
                intent.putExtra("listPostLabel", (Serializable) listPostLabel);
                intent.putExtra("listRecording", (Serializable) listRecoring);
                intent.putExtra("output_file", outputFile);
                startActivity(intent);
                

            }
        });

    }

    private void writeCSVtask(){
        if(taskNumber==1)
            writeCSV.createWriteCSV(outputCSVPath, patientID, task, "none");
        else
            writeCSV.writeCSV(outputCSVPath, patientID, task, "none");
    }

    private void listenBtnClock(){
        binding.btnClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Trial trial;
                //the clock is running when I press this button I stop the clock and the recording
                // but the clock is not reset
                if(clockrunning.equals("start")) {
                    binding.btnClock.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_circle_button));
                    long delta = SystemClock.elapsedRealtime() - binding.txtChrono.getBase();
                    sec = String.valueOf((delta / 1000) % 60);
                    long remaining = delta % 1000;
                    milisec = String.valueOf((remaining % 1000) / 100);
                    clockrunning = "break";
                    binding.btnClock.setText("Retry task");
                    binding.txtChrono.stop();
                    //binding.btnListenAudio.setVisibility(View.VISIBLE);
                    //stopRecording();
                    stopAudio();
                    binding.btnValidate.setVisibility(View.VISIBLE);
                    binding.linearEvent.setVisibility(View.GONE);
                    numberTry = numberTry + 1;
                    trial = new Trial(String.valueOf(numberTry),categorie, task, String.valueOf(numberEvent), null, sec, milisec);
                    listEvent.add(trial);
                    Event event1 = new Event(1, startTime, endTime, 0);
                    itemSelected = listEvent.size();


                    adapter = new TrialAdapter(listEvent, getApplicationContext());
                    binding.recyclerTrial.setAdapter(adapter);
                    adapter.setSelectedClick(selected -> {
                        itemSelected = selected +1;
                    });

                } // the clock is stopped and reset wainting to start the recording
                else if (clockrunning.equals("stop")){
                    //startRecording();
                    recordAudio();
                    clockrunning = "start";
                    //binding.btnListenAudio.setVisibility(View.INVISIBLE);
                    binding.btnClock.setText("Stop");
                    binding.btnClock.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_clock));
                    long base = SystemClock.elapsedRealtime();
                    binding.txtChrono.setBase(base);
                    binding.txtChrono.start();
                    binding.linearEvent.setVisibility(View.VISIBLE);
                    binding.btnValidate.setVisibility(View.GONE);

                }
                //the clock is stopped and reset
                else{
                    binding.waveForm.clearWaveform();
                    clockrunning = "stop";
                    //binding.btnListenAudio.setVisibility(View.VISIBLE);
                    binding.btnClock.setText("Start");
                    binding.btnClock.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_circle_button));
                    long base = SystemClock.elapsedRealtime();
                    binding.txtChrono.setBase(base);
                    binding.btnValidate.setVisibility(View.GONE);
                    binding.linearEvent.setVisibility(View.VISIBLE);
                    binding.btnEventTherapist.setVisibility(View.VISIBLE);

                }
            }
        });
    }

    /*
    private void listenBtnListenAudio(){
        binding.btnListenAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnListenAudio.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_button_blue));
                playRecording();
            }
        });
    }

     */

    private void recordAudio(){
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(outputFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "e " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        audioRecord.startRecording();

        isRecording = true;

        new Thread(() -> {
            try {
                while (isRecording) {
                    int bytesRead = audioRecord.read(audioBuffer, 0, BUFFER_SIZE);
                    if (bytesRead > 0) {
                        // Update the waveform view with the new audio data
                        runOnUiThread(() -> binding.waveForm.setWaveform(Arrays.copyOf(audioBuffer, bytesRead)));
                    } else {
                        Log.e("AudioRecord", "Error reading audio data");
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();


    }

    private void stopAudio() {
        mediaRecorder.stop();
        mediaRecorder.release();
        isRecording = false;
        mediaRecorder = null;
    }

    private void playRecording() {
        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(outputFile);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startRecording() {
        if (!isRecording) {
            isRecording = true;
            audioRecord.startRecording();
            audioFile = new File(getExternalFilesDir(null), "recorded_audio.wav");

            // Start a background thread to read audio data and update the waveform
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isRecording) {
                        int bytesRead = audioRecord.read(audioBuffer, 0, BUFFER_SIZE);
                        if (bytesRead > 0) {
                            // Update the waveform view with the new audio data
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //binding.waveForm.setWaveform(audioBuffer);
                                    /*
                                    FileOutputStream os = null;
                                    try {
                                        os = new FileOutputStream(audioFile);
                                        os.write(audioBuffer, 0, bytesRead);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } finally {
                                        if (os != null) {
                                            try {
                                                os.close();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                     */

                                }
                            });
                        } else {
                            Log.e("AudioRecord", "Error reading audio data");
                            break;
                        }
                    }
                }
            }).start();
        }
    }

    private void pauseRecording(){
        if(isRecording){
            mediaRecorder.pause();
            isRecording = false;
        }
    }

    private void resumeRecording(){
        if(!isRecording){
            mediaRecorder.resume();
            isRecording = true;
        }

        new Thread(() -> {
            try {
                while (isRecording) {
                    int bytesRead = audioRecord.read(audioBuffer, 0, BUFFER_SIZE);
                    if (bytesRead > 0) {
                        // Update the waveform view with the new audio data
                        runOnUiThread(() -> binding.waveForm.setWaveform(Arrays.copyOf(audioBuffer, bytesRead)));
                    } else {
                        Log.e("AudioRecord", "Error reading audio data");
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void stopRecording() {
        if (isRecording) {
            isRecording = false;
            audioRecord.stop();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release the resources when the activity is destroyed
        if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
            audioRecord.release();
        }

    }

    @Override
    public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int i) {
        //drawWaveform(bytes);
    }

    @Override
    public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int i) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //setupVisualizer();
            } else {
                // Handle permission denied
            }
        }
    }
}