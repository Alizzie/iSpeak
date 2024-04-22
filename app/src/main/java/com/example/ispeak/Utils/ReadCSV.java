package com.example.ispeak.Utils;

import androidx.lifecycle.ViewModel;

import com.example.ispeak.Models.Event;
import com.example.ispeak.Models.Recording;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ReadCSV extends ViewModel {

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

                Event event = recording.onReadEventsCSV(line, eventNumber, taskId);
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
