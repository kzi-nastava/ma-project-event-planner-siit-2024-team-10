package m3.eventplanner.models;

public class BlockStatusDTO {
    boolean blocked;
    public BlockStatusDTO(boolean blocked) {this.blocked = blocked;}

    public boolean getIsBlocked() { return blocked; }
}
