package m3.eventplanner.models;

public class AddFavouriteEventDTO {
    private int eventId;

    public AddFavouriteEventDTO(int eventId) {
        this.eventId = eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }
}
