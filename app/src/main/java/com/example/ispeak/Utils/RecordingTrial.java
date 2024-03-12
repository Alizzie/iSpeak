/*
 * Thomas M., Elisa D.
 */

package com.example.ispeak.Utils;
public class RecordingTrial{

    private int numEvents;
    private int patientTime, totalTime;

    private int numTrial;

    public RecordingTrial(int numEvents, int patientTime, int totalTime, int numTrial) {
        this.numEvents = numEvents;
        this.patientTime = patientTime;
        this.totalTime = totalTime;
        this.numTrial = numTrial;
    }

    public int getNumEvents() {
        return numEvents;
    }

    public int getNumTrial() {
        return numTrial;
    }

    public int getPatientTime() {
        return patientTime;
    }

    public int getTotalTime() {
        return totalTime;
    }
}
