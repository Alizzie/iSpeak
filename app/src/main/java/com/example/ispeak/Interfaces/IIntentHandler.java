package com.example.ispeak.Interfaces;

import android.app.Activity;
import android.content.Intent;

public interface IIntentHandler {

    default void navigateToNextActivity(Activity currentActivity, Class nextClass) {
        Intent intent = new Intent(currentActivity, nextClass);
        prepareIntent(intent);
        currentActivity.startActivity(intent);
        currentActivity.finish();
    }

    default void retrieveIntent(Activity currentActivity) {
        Intent intent = currentActivity.getIntent();
        processReceivedIntent(intent);
    }

    void prepareIntent(Intent intent);

    void processReceivedIntent(Intent intent);
}
