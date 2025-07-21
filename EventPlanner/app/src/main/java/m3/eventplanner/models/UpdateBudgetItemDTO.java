package m3.eventplanner.models;

public class UpdateBudgetItemDTO {
    private int amount;
    private int offeringId;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getOfferingId() {
        return offeringId;
    }

    public void setOfferingId(int offeringId) {
        this.offeringId = offeringId;
    }
}
