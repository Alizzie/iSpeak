package com.example.ispeak.Views;

import android.annotation.SuppressLint;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import com.example.ispeak.Interfaces.IntentHandler;
import com.example.ispeak.R;

public abstract class BaseApp extends AppCompatActivity implements IntentHandler {

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
}
