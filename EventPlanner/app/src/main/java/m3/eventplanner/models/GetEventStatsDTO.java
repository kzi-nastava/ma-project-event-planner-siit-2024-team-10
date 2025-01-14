package m3.eventplanner.models;

public class GetEventStatsDTO {
    private int id;
    private int oneStarCount;
    private int twoStarCount;
    private int threeStarCount;
    private int fourStarCount;
    private int fiveStarCount;
    private int participantsCount;
    private double averageRating;
    private String eventName;

    public int getId() {
        return id;
    }

    public int getOneStarCount() {
        return oneStarCount;
    }

    public int getTwoStarCount() {
        return twoStarCount;
    }

    public int getThreeStarCount() {
        return threeStarCount;
    }

    public int getFourStarCount() {
        return fourStarCount;
    }

    public int getFiveStarCount() {
        return fiveStarCount;
    }

    public int getParticipantsCount() {
        return participantsCount;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public String getEventName() {
        return eventName;
    }
}
