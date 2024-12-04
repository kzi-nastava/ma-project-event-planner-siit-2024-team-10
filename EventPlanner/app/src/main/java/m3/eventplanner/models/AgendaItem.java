package m3.eventplanner.models;

import android.os.Parcel;
import android.os.Parcelable;

public class AgendaItem implements Parcelable {
    private int id;
    private String name;
    private String description;
    private String startTime;
    private String endTime;
    private String location;

    // Default Constructor
    public AgendaItem() {
    }

    // Parameterized Constructor
    public AgendaItem(int id, String name, String description, String startTime, String endTime, String location) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
    }

    // Getter and Setter methods
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    // Parcelable Implementation
    protected AgendaItem(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        location = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeString(location);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AgendaItem> CREATOR = new Creator<AgendaItem>() {
        @Override
        public AgendaItem createFromParcel(Parcel in) {
            return new AgendaItem(in);
        }

        @Override
        public AgendaItem[] newArray(int size) {
            return new AgendaItem[size];
        }
    };
}
