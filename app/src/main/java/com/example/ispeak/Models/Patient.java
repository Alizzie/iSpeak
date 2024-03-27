package com.example.ispeak.Models;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.example.ispeak.Interfaces.FolderStructureCreator;
import com.example.ispeak.Utils.Utils;

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

    private Patient() {
    }

    public static Patient getInstance() {
        if (instance == null) {
            instance = new Patient();
        }

        return instance;
    }

    public void setPatientData(String patientId, String caseId, String diagnosis, Context context){
        if (instance == null) {
            instance = new Patient();
        }

        this.patientId = patientId;
        this.caseId = caseId;
        this.diagnosis = diagnosis;
        this.date = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        this.assessmentList = null;
        this.applicationContext = context;
        createFolderStructure();
    }

    public void addAssessment(Assessment assessment) {
        if (assessment != null) {
            if (assessmentList == null) {
                assessmentList = new ArrayList<>();
            }
            assessmentList.add(assessment);
        }
    }

    public void addAssessment(Assessment assessment, int index) {
        if (assessment != null) {
            if (assessmentList == null) {
                assessmentList = new ArrayList<>();
            }
            assessmentList.add(index, assessment);
        }
    }

    public int findAssessment(Class<? extends Assessment> assessmentClass){
        if (assessmentList == null) {
            assessmentList = new ArrayList<>();
        }

        for(int i = 0; i < assessmentList.size(); i++) {
            if(assessmentClass.isInstance(assessmentList.get(i))){
                return i;
            }
        }

        return -1;
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
    public List<Assessment> getAssessmentList(){return assessmentList;}

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

//        patientFolderPath = applicationContext.getExternalFilesDir(Environment.DIRECTORY_DCIM) +
//                File.separator + patientId +
//                File.separator + "iSpeak" +
//                File.separator + date +
//                "_" + caseId;

        patientFolderPath = applicationContext.getExternalFilesDir(Environment.DIRECTORY_DCIM) +
                File.separator + patientId +
                File.separator + "iSpeak" +
                File.separator + caseId;

        Utils.createFolder(patientFolderPath);
    }
}
