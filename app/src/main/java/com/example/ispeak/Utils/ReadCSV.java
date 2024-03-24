package com.example.ispeak.Utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.ispeak.Models.Assessment;
import com.example.ispeak.Models.Event;
import com.example.ispeak.Models.Recording;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class ReadCSV extends ViewModel {

    String name;
    public static WriteCSV getInstance(@NonNull ViewModelStoreOwner owner) {
        return new ViewModelProvider(owner, (ViewModelProvider.Factory) new ViewModelProvider.NewInstanceFactory()).get(WriteCSV.class);
    }

    public ArrayList<String[]> readAssessmentDataCSVNote(String filepath){
        ArrayList<String[]> lines = new ArrayList<>();
        try{
            CSVReader reader = new CSVReader(new FileReader(filepath));
            skipPatientData(reader);

            String[] line;
            while((line = reader.readNext()) != null) {
                lines.add(line);
            }

            reader.close();
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }

        return lines;
    }

    public ArrayList<Event> readEventDataCSVNote(Recording recording, String filepath, int recordingTaskId) {
        ArrayList<Event> events = new ArrayList<>();

        try{
            CSVReader reader = new CSVReader(new FileReader(filepath));
            skipPatientData(reader);
            reader.skip(1);

            String[] line;
            int eventNumber = 0;
            while((line = reader.readNext()) != null) {

                int taskId = Integer.parseInt(line[0]);
                if(taskId != recordingTaskId) {
                    continue;
                }

                Event event = recording.onReadCSV(line, eventNumber, taskId);
                events.add(event);
                eventNumber++;
            }

            reader.close();
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }

        return events;
    }

    private void skipPatientData(CSVReader reader) throws IOException {
        reader.skip(3);
    }
}
