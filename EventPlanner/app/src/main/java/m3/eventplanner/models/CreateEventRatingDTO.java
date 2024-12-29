package m3.eventplanner.models;

public class CreateEventRatingDTO {
    private int rating;

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public CreateEventRatingDTO(int rating) {
        this.rating = rating;
    }
}
