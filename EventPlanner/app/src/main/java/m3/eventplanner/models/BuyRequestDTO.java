package m3.eventplanner.models;

public class BuyRequestDTO {
    private boolean pending;

    public BuyRequestDTO(boolean pending) {
        this.pending = pending;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }
}
