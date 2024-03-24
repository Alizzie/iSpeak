package com.example.ispeak.Utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.ispeak.Models.Assessment;
import com.example.ispeak.Models.BoDyS;
import com.example.ispeak.Models.Event;
import com.example.ispeak.Models.Patient;
import com.example.ispeak.Models.Recording;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WriteCSV extends ViewModel {

    String name;
    public static WriteCSV getInstance(@NonNull ViewModelStoreOwner owner) {
        return new ViewModelProvider(owner, (ViewModelProvider.Factory) new ViewModelProvider.NewInstanceFactory()).get(WriteCSV.class);
    }

    public void storeDataCSVNote(Assessment assessment, String filepath) {
        try {

            FileWriter outputFile;
            if(assessment.getTaskId() == 0) {
                File file = new File(filepath);
                outputFile = new FileWriter(file);
            } else {
                outputFile = new FileWriter(filepath, true);
            }

            CSVWriter writer = getCSVWriter(outputFile);

            List<String[]> results = assessment.onWriteCSV();

            if(assessment.getTaskId() == 0){
                writePatientData(writer);
                writer.writeNext(results.get(0));
            }

            writer.writeNext(results.get(1));


            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateDataCSVNote(Assessment assessment, String filepath){
        List<String[]> lines;
        int lineNumber = assessment.getTaskId() + getPatientDataLines();
        try {
            CSVReader reader = new CSVReader(new FileReader(filepath));
            lines = reader.readAll();
            reader.close();

            List<String[]> results = assessment.onWriteCSV();
            if (lineNumber >= 0 && lineNumber < lines.size()) {
                lines.set(lineNumber, results.get(1));
            }

            CSVWriter writer = new CSVWriter(new FileWriter(filepath));
            writer.writeAll(lines);
            writer.close();

        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }
    }

    public void storeEventDataCSVNote(Assessment assessment, String filepath){
        Recording recording = assessment.getRecordings()[assessment.getTaskId()];
        ArrayList<Event> events = recording.getEvents();

        try {
            FileWriter outputFile;
            if(assessment.getTaskId() == 0) {
                File file = new File(filepath);
                outputFile = new FileWriter(file);
            } else {
                outputFile = new FileWriter(filepath, true);
            }

            CSVWriter writer = getCSVWriter(outputFile);

            if(checkIfFileIsEmpty(new FileReader(filepath)) && events.size() != 0){
                List<String[]> data = events.get(0).onWriteCSV();
                writePatientData(writer);
                writer.writeNext(data.get(0));
            }

            for(int i = 0; i < events.size(); i++) {

                List<String[]> data = events.get(i).onWriteCSV();
                writer.writeNext(data.get(1));
            }

            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateEventDataCSVNote(Assessment assessment, String filepath){
        List<String[]> lines;
        List<String[]> newLines = new ArrayList<>();
        Recording recording = assessment.getRecordings()[assessment.getTaskId()];
        ArrayList<Event> events = recording.getEvents();

        try {
            CSVReader reader = new CSVReader(new FileReader(filepath));
            lines = reader.readAll();
            reader.close();

            List<String[]> eventsData = new ArrayList<>();
            for(int i = 0; i < events.size(); i++) {
                eventsData.add(events.get(i).onWriteCSV().get(1));
            }

            boolean addedOnce = false;
            for (int i = 0; i < lines.size(); i++) {

                String[] line = lines.get(i);
                if(i < getPatientDataLines()) {
                    newLines.add(line);
                    continue;
                }

                int taskId = Integer.parseInt(line[0]);
                int searchedTaskId = assessment.getTaskId();

                if(taskId > searchedTaskId && !addedOnce) {
                    newLines.addAll(eventsData);
                    addedOnce = true;
                }

                if (taskId != searchedTaskId){
                    newLines.add(line);
                }

            }


            CSVWriter writer = new CSVWriter(new FileWriter(filepath));
            writer.writeAll(newLines);
            writer.close();

        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }
    }

    public void sumTotalAssessment(List<String> files) {

        try{
            FileWriter outputFile = new FileWriter(files.get(0));
            CSVWriter writer = getCSVWriter(outputFile);

            writePatientData(writer);

            for(int i = 1; i < files.size(); i++) {
                readAndAppendCSV(files.get(i), writer);
            }

            writer.close();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void readAndWriteAllCSV(String file, CSVWriter writer) {
        try {
            FileReader inputFile = new FileReader(file);
            CSVReader reader = new CSVReader(inputFile);

            List<String[]> lines = reader.readAll();
            lines.add(new String[] {});
            writer.writeAll(lines);

            reader.close();

        } catch (CsvException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void readAndAppendCSV(String file, CSVWriter writer) {
        try{
            FileReader inputFile = new FileReader(file);
            CSVReader reader = new CSVReader(inputFile);

            for(int i = 0; i < 3; i++) {
                reader.readNext();
            }

            List<String[]> lines = reader.readAll();
            lines.add(new String[] {});
            writer.writeAll(lines);

            reader.close();

        } catch (CsvException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writePatientData(CSVWriter writer){
        List<String[]> patientData = new ArrayList<String[]>();
        patientData.add(new String[] {"Patient_Id", "Case_Id", "Diagnosis", "Time"});
        patientData.add(new String[] {Patient.getInstance().getPatientId(), Patient.getInstance().getCaseId(), Patient.getInstance().getDiagnosis(), Patient.getInstance().getFormattedDate()});
        patientData.add(new String[] {});
        writer.writeAll(patientData);
    }

    private int getPatientDataLines(){
        return 4;
    }

    private CSVWriter getCSVWriter(FileWriter outputFile) {
        return new CSVWriter(outputFile, ',',
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);
    }

    private boolean checkIfFileIsEmpty(FileReader file){
        CSVReader reader = new CSVReader(file);
        List<String[]> lines = null;
        try {
            lines = reader.readAll();
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }

        return lines.isEmpty();
    }

}
