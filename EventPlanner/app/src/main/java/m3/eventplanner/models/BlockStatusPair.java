package m3.eventplanner.models;

public class BlockStatusPair {
    private final boolean isBlockingThem;
    private final boolean isBlockedByThem;

    public BlockStatusPair(boolean isBlockingThem, boolean isBlockedByThem) {
        this.isBlockingThem = isBlockingThem;
        this.isBlockedByThem = isBlockedByThem;
    }

    public boolean isBlockingThem() {
        return isBlockingThem;
    }

    public boolean isBlockedByThem() {
        return isBlockedByThem;
    }
}

