package fr.thomas.menard.ispeak.Utils;

public class Event {

    long timeStart, timeEnd, duration;
    int id;

    public Event(int id, long timeStart, long timeEnd, long duration){
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.duration = duration;
        this.id = id;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public long getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
