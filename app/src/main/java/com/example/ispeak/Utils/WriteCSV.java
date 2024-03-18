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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

            CSVWriter writer = new CSVWriter(outputFile, ',',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);

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

            CSVWriter writer = new CSVWriter(outputFile, ',',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);

            for(int i = 0; i < events.size(); i++) {

                List<String[]> datas = events.get(i).onWriteCSV();
                if(i == 0 && assessment.getTaskId() == 0) {
                    writePatientData(writer);
                    writer.writeNext(datas.get(0));
                }

                writer.writeNext(datas.get(1));
            }

            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sumTotalAssessment(List<String> files) {

        try{
            FileWriter outputFile = new FileWriter(files.get(0));
            CSVWriter writer = new CSVWriter(outputFile, ',',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);

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

}
