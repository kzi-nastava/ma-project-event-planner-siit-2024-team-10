package m3.eventplanner.models;

import java.time.LocalDate;

public class CreateEventDTO {
    private int eventTypeId;
    private int organizerId;
    private String name;
    private String description;
    private int maxParticipants;
    private boolean isOpen;
    private String date;
    private CreateLocationDTO location;

    public CreateEventDTO() {
    }

    public CreateEventDTO(int eventTypeId, int organizerId, String name, String description, int maxParticipants, boolean isOpen, String date, CreateLocationDTO location) {
        this.eventTypeId = eventTypeId;
        this.organizerId = organizerId;
        this.name = name;
        this.description = description;
        this.maxParticipants = maxParticipants;
        this.isOpen = isOpen;
        this.date = date;
        this.location = location;
    }

    public int getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(int eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public int getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(int organizerId) {
        this.organizerId = organizerId;
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

    public CreateLocationDTO getLocation() {
        return location;
    }

    public void setLocation(CreateLocationDTO location) {
        this.location = location;
    }
}
