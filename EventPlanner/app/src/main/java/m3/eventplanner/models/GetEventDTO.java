package m3.eventplanner.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class GetEventDTO implements Parcelable, Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("organizer")
    private GetOrganizerDTO organizer;

    @SerializedName("eventType")
    private GetEventTypeDTO eventType;

    @SerializedName("description")
    private String description;

    @SerializedName("maxParticipants")
    private int maxParticipants;

    @SerializedName("open")
    private boolean isOpen;

    @SerializedName("date")
    private String date; // Store as String for Gson compatibility

    @SerializedName("location")
    private GetLocationDTO location;

    @SerializedName("averageRating")
    private double averageRating;

    @SerializedName("participantsCount")
    private int participantsCount;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE;

    public GetEventDTO() {
    }

    public GetEventDTO(int id, String name, GetOrganizerDTO organizer, GetEventTypeDTO eventType,
                       String description, int maxParticipants, boolean isOpen, String date,
                       GetLocationDTO location, double averageRating) {
        this.id = id;
        this.name = name;
        this.organizer = organizer;
        this.eventType = eventType;
        this.description = description;
        this.maxParticipants = maxParticipants;
        this.isOpen = isOpen;
        this.date = date;
        this.location = location;
        this.averageRating = averageRating;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeParcelable(organizer, flags);
        dest.writeParcelable(eventType, flags);
        dest.writeString(description);
        dest.writeInt(maxParticipants);
        dest.writeByte((byte) (isOpen ? 1 : 0));
        dest.writeString(date);
        dest.writeParcelable(location, flags);
        dest.writeDouble(averageRating);
    }

    public static final Creator<GetEventDTO> CREATOR = new Creator<GetEventDTO>() {
        @Override
        public GetEventDTO createFromParcel(Parcel in) {
            return new GetEventDTO(in);
        }

        @Override
        public GetEventDTO[] newArray(int size) {
            return new GetEventDTO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected GetEventDTO(Parcel in) {
        id = in.readInt();
        name = in.readString();
        organizer = in.readParcelable(GetOrganizerDTO.class.getClassLoader());
        eventType = in.readParcelable(GetEventTypeDTO.class.getClassLoader());
        description = in.readString();
        maxParticipants = in.readInt();
        isOpen = in.readByte() != 0;
        date = in.readString();
        location = in.readParcelable(GetLocationDTO.class.getClassLoader());
        averageRating = in.readDouble();
    }

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

    public GetOrganizerDTO getOrganizer() {
        return organizer;
    }

    public void setOrganizer(GetOrganizerDTO organizer) {
        this.organizer = organizer;
    }

    public GetEventTypeDTO getEventType() {
        return eventType;
    }

    public void setEventType(GetEventTypeDTO eventType) {
        this.eventType = eventType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public LocalDate getParsedDate() {
        return LocalDate.parse(date, DATE_FORMATTER); // Use when it is necessary to work with LocalDate objects
    }

    public GetLocationDTO getLocation() {
        return location;
    }

    public void setLocation(GetLocationDTO location) {
        this.location = location;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getParticipantsCount() {
        return participantsCount;
    }
}
