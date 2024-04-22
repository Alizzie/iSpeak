package com.example.ispeak.Views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import com.example.ispeak.Interfaces.IActivityCreator;
import com.example.ispeak.Interfaces.IIntentHandler;
import com.example.ispeak.Models.Patient;
import com.example.ispeak.R;

import java.util.Objects;

public abstract class BaseApp extends AppCompatActivity implements IIntentHandler, IActivityCreator {
    protected Patient patientInfo;

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menu instanceof MenuBuilder) {
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
        }

        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();
        if (itemId == R.id.action_patient_profile) {
            return true;
        } else if (itemId == R.id.action_logout) {
            navigateToNextActivity(this, MainActivity.class);
            return true;
        } else if(itemId == R.id.action_return_home) {
            navigateToNextActivity(this, MenuActivity.class);
        }
        return super.onOptionsItemSelected(item);
    }

    protected void enableNavBackArrow(){
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public void onBackPressed() {

        if(this instanceof MainActivity || this instanceof MenuActivity){
            return;
        }

        navigateToNextActivity(this, MenuActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        patientInfo = Patient.getPatient();
        setBinding();
        init();
        listenBtn();
    }
}
