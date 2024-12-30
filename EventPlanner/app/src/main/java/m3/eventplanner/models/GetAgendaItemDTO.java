package m3.eventplanner.models;

import java.time.LocalTime;

public class GetAgendaItemDTO {
    private int id;
    private String name;
    private String description;
    private String location;
    private String startTime;
    private String endTime;

    private boolean isDeleted;

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
        return LocalTime.of(Integer.parseInt(startTime.split(":")[0]),Integer.parseInt(startTime.split(":")[1]));
    }

    public LocalTime getEndTime() {
        return LocalTime.of(Integer.parseInt(endTime.split(":")[0]),Integer.parseInt(endTime.split(":")[1]));
    }
}
