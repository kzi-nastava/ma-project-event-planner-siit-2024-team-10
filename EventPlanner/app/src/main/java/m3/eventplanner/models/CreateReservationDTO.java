package m3.eventplanner.models;


public class CreateReservationDTO {
    private String startTime;
    private String endTime;
    private int event;
    private int service;

    public CreateReservationDTO() {
    }

    public CreateReservationDTO(String startTime, String endTime, int event, int service) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.event = event;
        this.service = service;
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

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    public int getService() {
        return service;
    }

    public void setService(int service) {
        this.service = service;
    }
}
