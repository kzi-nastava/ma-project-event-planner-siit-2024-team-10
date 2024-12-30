package m3.eventplanner.models;

import android.os.Parcel;
import android.os.Parcelable;
import java.time.LocalTime;

public class GetAgendaItemDTO implements Parcelable {
    private int id;
    private String name;
    private String description;
    private String location;
    private String startTime;
    private String endTime;
    private boolean isDeleted;

    // Existing getters
    public boolean isDeleted() {
        return isDeleted;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public LocalTime getStartTime() {
        return LocalTime.of(Integer.parseInt(startTime.split(":")[0]), Integer.parseInt(startTime.split(":")[1]));
    }

    public LocalTime getEndTime() {
        return LocalTime.of(Integer.parseInt(endTime.split(":")[0]), Integer.parseInt(endTime.split(":")[1]));
    }

    // Parcelable implementation
    protected GetAgendaItemDTO(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        location = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        isDeleted = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(location);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeByte((byte) (isDeleted ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GetAgendaItemDTO> CREATOR = new Creator<GetAgendaItemDTO>() {
        @Override
        public GetAgendaItemDTO createFromParcel(Parcel in) {
            return new GetAgendaItemDTO(in);
        }

        @Override
        public GetAgendaItemDTO[] newArray(int size) {
            return new GetAgendaItemDTO[size];
        }
    };
}