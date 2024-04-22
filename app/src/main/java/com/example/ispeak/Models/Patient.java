package com.example.ispeak.Models;

import android.content.Context;
import android.os.Environment;

import com.example.ispeak.Interfaces.IFolderStructureCreator;
import com.example.ispeak.Utils.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Patient implements IFolderStructureCreator {

    private static Patient patient;
    private String patientId, caseId, diagnosis;
    private String date;
    private String patientFolderPath;
    private List<Assessment> assessmentList;
    private Context applicationContext;

    private Patient() {
        assessmentList = new ArrayList<>();
    }

    public static Patient getPatient() {
        if (patient == null) {
            patient = new Patient();
        }

        return patient;
    }

    public void setPatientData(String patientId, String caseId, String diagnosis, Context context){
        if (patient == null) {
            patient = new Patient();
        }

        this.patientId = patientId;
        this.caseId = caseId;
        this.diagnosis = diagnosis;
        this.date = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        this.assessmentList = new ArrayList<>();
        this.applicationContext = context;
        createFolderStructure();
    }

    public void addAssessment(Assessment assessment) {
        if (assessment != null) {
            assessmentList.add(assessment);
        }
    }

    public void addAssessment(Assessment assessment, int index) {
        if (assessment != null) {
            assessmentList.add(index, assessment);
        }
    }

    public int findAssessment(Class<? extends Assessment> assessmentClass){
        for(int i = 0; i < assessmentList.size(); i++) {
            if(assessmentClass.isInstance(assessmentList.get(i))){
                return i;
            }
        }
        return -1;
    }

    @Override
    public void createFolderStructure() {
        patientFolderPath = applicationContext.getExternalFilesDir(Environment.DIRECTORY_DCIM) +
                File.separator + patientId +
                File.separator + "iSpeak" +
                File.separator + caseId;

        Utils.createFolder(patientFolderPath);
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
        return Utils.changeDateFormatFromYMDToDMY(this.date);
    }

    public String getPatientFolderPath() {
        return patientFolderPath;
    }
    public List<Assessment> getAssessmentList(){return assessmentList;}

}
