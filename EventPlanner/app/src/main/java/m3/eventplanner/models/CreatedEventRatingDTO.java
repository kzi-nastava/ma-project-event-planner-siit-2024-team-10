package m3.eventplanner.models;

public class CreatedEventRatingDTO {
    private double averageRating;

    public CreatedEventRatingDTO(double averageRating) {
        this.averageRating = averageRating;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
}
