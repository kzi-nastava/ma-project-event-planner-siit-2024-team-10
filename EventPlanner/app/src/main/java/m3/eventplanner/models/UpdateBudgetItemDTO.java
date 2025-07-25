package m3.eventplanner.models;

public class UpdateBudgetItemDTO {
    private int amount;

    public UpdateBudgetItemDTO(int newAmount) {
        amount = newAmount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
