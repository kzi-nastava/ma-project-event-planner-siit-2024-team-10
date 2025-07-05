package m3.eventplanner.models;

import java.util.List;

public class CreateGuestListDTO {
    private List<String> guests;

    public CreateGuestListDTO() {}

    public CreateGuestListDTO(List<String> guests) {
        this.guests = guests;
    }

    public List<String> getGuests() {
        return guests;
    }

    public void setGuests(List<String> guests) {
        this.guests = guests;
    }
}
