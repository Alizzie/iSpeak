package fr.thomas.menard.ispeak.Utils;

import android.os.Parcel;
import android.os.Parcelable;

public class RecordingModel implements Parcelable {

    private String filePath, categorie, task, number_event;
    private int id;
    // You can add more fields as needed, such as recording duration, date, etc.



    public RecordingModel(int id, String filePath, String categorie, String task, String number_event) {
        this.id = id;
        this.filePath = filePath;
        this.categorie = categorie;
        this.task = task;
        this.number_event = number_event;
    }

    public RecordingModel(Parcel in){
        id = in.readInt();
        filePath = in.readString();
        categorie = in.readString();
        task = in.readString();
        number_event = in.readString();
    }

    public String getFilePath() {
        return filePath;
    }
    public String getCategorie() {
        return categorie;
    }

    public String getTask() {
        return task;
    }
    public String getNumber_event(){
        return number_event;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(filePath);
        parcel.writeString(categorie);
        parcel.writeString(task);
        parcel.writeString(number_event);
    }

    public static final Parcelable.Creator<RecordingModel> CREATOR = new Creator<RecordingModel>() {
        @Override
        public RecordingModel createFromParcel(Parcel parcel) {
            return new RecordingModel(parcel);
        }

        @Override
        public RecordingModel[] newArray(int i) {
            return new RecordingModel[i];
        }
    };
}
