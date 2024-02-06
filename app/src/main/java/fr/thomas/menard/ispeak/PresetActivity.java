package fr.thomas.menard.ispeak;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;

import fr.thomas.menard.ispeak.databinding.ActivityPresetBinding;

public class PresetActivity extends AppCompatActivity {

    private String idPatient,preset, diagnosis;
    private ActivityPresetBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPresetBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        getInfos();
        listenbtn();
    }

    private void getInfos(){
        Intent intent = getIntent();
        idPatient = intent.getStringExtra("idPatient");
        binding.txtPatientID.setText(idPatient);
        intent.getStringExtra("idCase");
    }

    /**
    private void listenRadioGroup() {
        binding.RadioGroupFirst.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.radioBtn1:
                    preset = "BodyS1";
                    break;
                case R.id.radioBtn2:
                    preset = "BodyS2";
                    break;
                case R.id.radioBtn3:
                    preset = "BodyS3";
                    break;
                case R.id.radioBtn4:
                    preset = "Sustained vowel";
                    break;
                default:
                    break;
            }


        });
    }

     **/

    private void listenbtn(){
        binding.btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MicroConnectionActivity.class);
                startActivity(intent);
            }
        });
    }
}