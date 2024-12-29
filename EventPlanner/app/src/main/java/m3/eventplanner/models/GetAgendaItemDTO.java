package m3.eventplanner.models;

import java.time.LocalTime;

public class GetAgendaItemDTO {
    private int id;
    private String name;
    private String description;
    private String location;
    private String startTime;
    private String endTime;

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

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }
}
