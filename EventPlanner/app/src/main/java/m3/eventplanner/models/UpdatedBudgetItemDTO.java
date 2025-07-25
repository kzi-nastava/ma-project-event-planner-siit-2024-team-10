package m3.eventplanner.models;

public class UpdatedBudgetItemDTO {
    private int id;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
