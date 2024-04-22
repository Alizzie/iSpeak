package com.example.ispeak.Utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static String changeDateFormatFromYMDToDMY(String date){
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        Date newDate = null;
        try {
            newDate = inputFormat.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return outputFormat.format(newDate);
    }

    public static String formatAudioTimeToStringPresentation(long time){
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
            directory.mkdirs();
        }
    }

    public static File[] getFilesFromInternalStorageFolder(String folderName) {
        File directory = new File(folderName);

        if(!directory.exists()){
            return null;
        }

        return directory.listFiles();
    }

    public static void deleteFilesFromDir(File directory){
        if(directory.isDirectory()) {
            File[] files = directory.listFiles();

            if(files != null) {
                for(File file : files) {
                    file.delete();
                }
            }
        }
    }

    public static boolean containsNumber(String input) {
        Pattern pattern = Pattern.compile(".*\\d.*");
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }
}
