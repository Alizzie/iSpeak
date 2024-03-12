package com.example.ispeak.Models;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.example.ispeak.Interfaces.FolderStructureCreator;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Patient implements FolderStructureCreator {

    private static Patient instance;
    private String patientId, caseId, diagnosis;
    private String date;
    private String patientFolderPath;
    private List<Assessment> assessmentList;
    private Context applicationContext;

    private Patient(String patientId, String caseId, String diagnosis, Context context) {
        this.patientId = patientId;
        this.caseId = caseId;
        this.diagnosis = diagnosis;
        this.date = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        this.applicationContext = context;
        createFolderStructure();
    }

    public static Patient getInstance(String patientId, String caseId, String diagnosis, Context context) {
        if (instance == null) {
            instance = new Patient(patientId, caseId, diagnosis, context);
        }
        return instance;
    }

    public static Patient getInstance() {
        if (instance == null) {
            instance = new Patient("", "", "", null);
        }
        return instance;
    }

    public void addAssessment(Assessment assessment) {
        if (assessment != null) {
            if (assessmentList == null) {
                assessmentList = new ArrayList<>();
            }
            assessmentList.add(assessment);
        }
    }

    public String getPatientId(){
        return this.patientId;
    }

    public String getCaseId(){
        return this.caseId;
    }

    public String getDiagnosis(){
        return this.diagnosis;
    }

    public String getFormattedDate(){
        return changeDateFormat(this.date);
    }

    public String getPatientFolderPath() {
        return patientFolderPath;
    }

    private String changeDateFormat(String date)  {
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

    @Override
    public void createFolderStructure() {

        patientFolderPath = applicationContext.getExternalFilesDir(Environment.DIRECTORY_DCIM) +
                File.separator + "iSpeak_recordings" +
                File.separator + patientId +
                File.separator + caseId +
                "_" + date;
        File directory = new File(patientFolderPath);

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
}
