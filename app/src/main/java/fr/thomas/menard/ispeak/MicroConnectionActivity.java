package fr.thomas.menard.ispeak;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import fr.thomas.menard.ispeak.databinding.ActivityMicroConnectionBinding;

public class MicroConnectionActivity extends AppCompatActivity {

    private ActivityMicroConnectionBinding binding;

    private int taskNumber = 1;
    private String idPatient, diagnosis, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMicroConnectionBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        getInfos();
        listenBtn();
    }

    private void getInfos(){
        Intent intent = getIntent();
        idPatient = intent.getStringExtra("idPatient");
        diagnosis = intent.getStringExtra("diagnosis");
        date = intent.getStringExtra("date");
        //binding.txtPatientID.setText(idPatient);
        intent.getStringExtra("idCase");

    }

    private void connectMicrophone(){

    }

    private void listenBtn(){
        binding.btnRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AssessmentActivity.class);
                intent.putExtra("taskNumber", taskNumber);
                intent.putExtra("patientID", idPatient);
                intent.putExtra("date", date);
                intent.putExtra("diagnosis", diagnosis);
                startActivity(intent);
            }
        });
    }
}