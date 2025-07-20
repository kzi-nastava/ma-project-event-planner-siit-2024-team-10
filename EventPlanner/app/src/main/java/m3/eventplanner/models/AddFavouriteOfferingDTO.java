package m3.eventplanner.models;

public class AddFavouriteOfferingDTO {
    private int offeringId;
    public AddFavouriteOfferingDTO(int offeringId) {
        this.offeringId = offeringId;
    }
    public void setOfferingId(int offeringId){this.offeringId=offeringId;}
}
