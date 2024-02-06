package fr.thomas.menard.ispeak.Utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Trial implements Parcelable {

    String categorie, task;
    String side, score, sec, mili;
    String number, number_compens;
    List<String> listcompensations;

    public Trial(String number) {
        this.number = number;
    }

    public Trial(String number, String categorie, String task, String number_compens, List<String> listcompensations, String sec, String mili) {
        this.categorie = categorie;
        this.task = task;
        this.number = number;
        this.number_compens = number_compens;
        this.listcompensations = listcompensations;
        this.sec = sec;
        this.mili = mili;
    }

    public Trial(Parcel in) {
        categorie = in.readString();
        task = in.readString();
        side = in.readString();
        number = in.readString();
        score = in.readString();
        number_compens = in.readString();
        //listcompensations = in.readStringList();
    }

    public Trial(String number, String categorie, String task, String event, Object o, String sec, String milisec) {
        this.number = number;
        this.categorie = categorie;
        this.number_compens = event;
        this.task=task;
        this.sec = sec;
        this.mili = milisec;
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

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getNumber_compens() {
        return number_compens;
    }

    public void setNumber_compens(String number_compens) {
        this.number_compens = number_compens;
    }

    public List<String> getListcompensations() {
        return listcompensations;
    }

    public void setListcompensations(List<String> listcompensations) {
        this.listcompensations = listcompensations;
    }


    public String getSec() {
        return sec;
    }

    public String getMili() {
        return mili;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(number);
        parcel.writeString(categorie);
        parcel.writeString(task);
        parcel.writeString(side);
        parcel.writeString(score);
        parcel.writeString(number_compens);
        parcel.writeList(listcompensations);

    }

    public static final Parcelable.Creator<Trial> CREATOR = new Creator<Trial>() {
        @Override
        public Trial createFromParcel(Parcel parcel) {
            return new Trial(parcel);
        }

        @Override
        public Trial[] newArray(int i) {
            return new Trial[i];
        }
    };
}