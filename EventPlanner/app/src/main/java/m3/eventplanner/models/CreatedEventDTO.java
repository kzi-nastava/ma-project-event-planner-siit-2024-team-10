package m3.eventplanner.models;

import java.time.LocalDate;

public class CreatedEventDTO {
    int id;
    private int eventTypeId;
    private int organizerId;
    private String name;
    private String description;
    private int maxParticipants;
    private boolean isOpen;
    private String date;
    private boolean isDeleted;
    private GetLocationDTO location;

    public int getId() {
        return id;
    }

    public int getEventTypeId() {
        return eventTypeId;
    }

    public int getOrganizerId() {
        return organizerId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public String getDate() {
        return date;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public GetLocationDTO getLocation() {
        return location;
    }
}
