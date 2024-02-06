package fr.thomas.menard.ispeak;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import fr.thomas.menard.ispeak.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    String idPatient, caseId, diagnosis, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        listenBtnConfirm();
        listenRadioGroup();
    }

    private void listenBtnConfirm(){
        binding.APatientBtnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.APatientTilCaseId.setError("");
                binding.APatientTilId.setError("");

                idPatient = binding.APatientTxtIdPatient.getText().toString().trim();
                date =new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

                /*
                if((binding.APatientTxtIdPatient.getText().toString().length() != 7))
                    binding.APatientTilId.setError("Patient ID not correct");
                else if((binding.APatientTxtIdCase.getText().toString().length() != 7))
                    binding.APatientTilCaseId.setError("Case ID not correct");
                else {
                    idPatient = binding.APatientTxtIdPatient.getText().toString().trim();
                    caseId = binding.APatientTxtIdCase.getText().toString().trim();
                    date =new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

                 */

                    Intent intent = new Intent(getApplicationContext(), MicroConnectionActivity.class);
                    intent.putExtra("idPatient", idPatient);
                    intent.putExtra("date", date);
                    intent.putExtra("idCase", caseId);
                    intent.putExtra("diagnosis", diagnosis);
                    createFolder();
                    startActivity(intent);
                    finish();

                }

            //}
        });
    }

    private void createFolder() {

        String directoryPath = Environment.getExternalStorageDirectory() +
                File.separator + Environment.DIRECTORY_DCIM +
                File.separator + "iSpeak_recordings" + File.separator + idPatient + File.separator + date;

// Create the File object
        File directory = new File(directoryPath);

// Check if the directory already exists or create it
        if (!directory.exists()) {
            // Create the directory and its parent directories if they don't exist
            if (directory.mkdirs()) {
                System.out.println("Directories created successfully: " + directory.getAbsolutePath());
            } else {
                System.err.println("Failed to create directories!");
            }
        } else {
            System.out.println("Directory already exists: " + directory.getAbsolutePath());
        }


    }


    private void listenRadioGroup() {
        binding.RadioGroupDiagnosis.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.radioBtnStroke) {
                diagnosis = "Stroke";
            } else if (i == R.id.radioBtnParkinsom) {
                diagnosis = "Parkinson";
            } else if (i == R.id.radioBtnOther) {
                diagnosis = "Other";
            }


        });
    }
}