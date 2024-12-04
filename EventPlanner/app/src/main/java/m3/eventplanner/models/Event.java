package m3.eventplanner.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.util.List;


import android.os.Parcel;
import android.os.Parcelable;
import java.time.LocalDate;
import java.util.List;

public class Event implements Parcelable {
    private int id;
    private EventType eventType;
    private String name;
    private double rating;
    private Organizer organizer;
    private Location location;
    private LocalDate date;
    private String description;
    private int maxParticipants;
    private boolean isOpen;
    private List<AgendaItem> agenda;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Organizer getOrganizer() {
        return organizer;
    }

    public void setOrganizer(Organizer organizer) {
        this.organizer = organizer;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    public List<AgendaItem> getAgenda() {
        return agenda;
    }

    public void setAgenda(List<AgendaItem> agenda) {
        this.agenda = agenda;
    }

    // Constructor
    public Event(int id, EventType eventType, String name, double rating, Organizer organizer,
                 Location location, LocalDate date, String description,
                 int maxParticipants, boolean isOpen, List<AgendaItem> agenda) {
        this.id = id;
        this.eventType = eventType;
        this.name = name;
        this.rating = rating;
        this.organizer = organizer;
        this.location = location;
        this.date = date;
        this.description = description;
        this.maxParticipants = maxParticipants;
        this.isOpen = isOpen;
        this.agenda = agenda;
    }

    // Parcelable implementation
    protected Event(Parcel in) {
        id = in.readInt();
        eventType = in.readParcelable(EventType.class.getClassLoader());
        name = in.readString();
        rating = in.readDouble();
        organizer = in.readParcelable(Organizer.class.getClassLoader());
        location = in.readParcelable(Location.class.getClassLoader());
        date = LocalDate.parse(in.readString());
        description = in.readString();
        maxParticipants = in.readInt();
        isOpen = in.readByte() != 0;
        agenda = in.createTypedArrayList(AgendaItem.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeParcelable(eventType, flags);
        dest.writeString(name);
        dest.writeDouble(rating);
        dest.writeParcelable(organizer, flags);
        dest.writeParcelable(location, flags);
        dest.writeString(date.toString());
        dest.writeString(description);
        dest.writeInt(maxParticipants);
        dest.writeByte((byte) (isOpen ? 1 : 0));
        dest.writeTypedList(agenda);
    }

    @Override
    public int describeContents() {
        return 0;
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
}

