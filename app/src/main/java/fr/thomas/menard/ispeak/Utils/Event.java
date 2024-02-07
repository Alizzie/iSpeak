package fr.thomas.menard.ispeak.Utils;

import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable {

    long timeStart, timeEnd, duration;

    String categorie, task;
    int id;

    public Event(int id){
        this.id=id;
    }

    public Event(int id, String categorie, String task, long timeStart, long timeEnd, long duration){
        this.categorie = categorie;
        this.task = task;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.duration = duration;
        this.id = id;
    }

    public Event(Parcel in){
        id = in.readInt();
        categorie = in.readString();
        task = in.readString();
        timeStart = in.readLong();
        timeEnd = in.readLong();
        duration = in.readLong();
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
    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(categorie);
        parcel.writeString(task);
        parcel.writeLong(timeStart);
        parcel.writeLong(timeEnd);
        parcel.writeLong(duration);

    }

    public static final Parcelable.Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel parcel) {
            return new Event(parcel);
        }

        @Override
        public Event[] newArray(int i) {
            return new Event[i];
        }
    };
}
