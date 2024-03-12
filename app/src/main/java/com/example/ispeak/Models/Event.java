package com.example.ispeak.Models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.ispeak.Utils.Utils;

import java.util.ArrayList;

public class Event implements Parcelable {

    private long timeStart, timeEnd, duration;
    private int eventId, taskId;
    private ArrayList<String> eventLabels = new ArrayList<>();

    public Event(int eventId, int taskId, long timeStart, long timeEnd, long duration, String firstLabel){
        this.eventId = eventId;
        this.taskId = taskId;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.duration = duration;
        eventLabels.add(firstLabel);
    }

    public Event(int eventId, int taskId, long timeStart){
        this(eventId, taskId, timeStart, timeStart, 0, "");
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
        calculateDuration();
        documentEvent();
    }

    private void calculateDuration() {
        this.duration = timeEnd - timeStart;
    }

    private void documentEvent(){
        Log.d("PLAYBACKK", "Starttime " + timeStart + ", Endtime " + timeEnd + ", duration " + duration);
    }

    public long getTimeStart() {
        return timeStart;
    }
    public String getFormattedTimeStart(){return Utils.formatTime(timeStart);}
    public String getFormattedTimeEnd(){return Utils.formatTime(timeEnd);}

    public ArrayList<String> getEventLabelsList(){
        return eventLabels;
    }

    public String getEventLabels(){
        return eventLabels.toString();
    }

    public void addEventLabel(String label){
        eventLabels.add(label);
    }

    public void removeEventLabel(String label){
        eventLabels.remove(label);
    }

    protected Event(Parcel in) {
        timeStart = in.readLong();
        timeEnd = in.readLong();
        duration = in.readLong();
        eventId = in.readInt();
        taskId = in.readInt();
        eventLabels = in.createStringArrayList();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(timeStart);
        parcel.writeLong(timeEnd);
        parcel.writeLong(duration);
        parcel.writeInt(eventId);
        parcel.writeInt(taskId);
        parcel.writeStringList(eventLabels);
    }
}
