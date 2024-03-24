package com.example.ispeak.Utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

public class Utils {

    public static String formatTime(long time){
        DecimalFormat df = new DecimalFormat("00");

        int hours = (int) (time / (3600 * 1000));
        int remaining = (int) (time % (3600 * 1000));

        int minutes = (int) (remaining / (60 * 1000));
        remaining = (int) (remaining % (60 * 1000));

        int seconds = (int) (remaining / 1000);
        remaining = (int) (remaining % (1000));

        int milliseconds = (int) (remaining/ 100);

        String text = "";

        if (hours > 0) {
            text += df.format(hours) + ":";
        }

        text += df.format(minutes) + ":";
        text += df.format(seconds) + ":";
        text += Integer.toString(milliseconds);

        return text;
    }

    public static long parseTimeToMilliseconds(String formattedTime) {
        String[] parts = formattedTime.split(":");

        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid time format: " + formattedTime);
        }

        int minutes = Integer.parseInt(parts[0]);
        int seconds = Integer.parseInt(parts[1]);
        int milliseconds = Integer.parseInt(parts[2]);

        long totalTimeInMillis =
                (long) minutes * 60 * 1000 +
                seconds * 1000L +
                milliseconds * 100;

        return totalTimeInMillis;
    }

    public static void createFolder(String path) {

        File directory = new File(path);

        if (!directory.exists()) {
            if (directory.mkdirs()) {
                Log.d("DIRECTORY", "SUCCESS: " + directory.getAbsolutePath());
            } else {
                Log.e("DIRECTORY", "FAIL");
            }
        } else {
            Log.d("DIRECTORY", "ALREADY EXISTS: " + directory.getAbsolutePath());
        }
    }

    public static File[] getFilesFromInternalStorageFolder(String folderName) {
        File directory = new File(folderName);

        if(!directory.exists()){
            return null;
        }

        return directory.listFiles();
    }

    public static void setMediaPlayer(String path, MediaPlayer mediaPlayer) {
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteFromDir(File directory){
        if(directory.isDirectory()) {
            File[] files = directory.listFiles();

            if(files != null) {
                for(File file : files) {
                    file.delete();
                }
            }
        }
    }
}
