package m3.eventplanner.models;

import android.os.Parcel;
import android.os.Parcelable;

public class GetServiceDTO extends GetOfferingDTO {
    private int maxDuration;
    private int minDuration;
    private int cancellationPeriod;
    private int reservationPeriod;
    private boolean autoConfirm;
    private boolean deleted;

    public GetServiceDTO() {}

    protected GetServiceDTO(Parcel in) {
        super(in);
        maxDuration = in.readInt();
        minDuration = in.readInt();
        cancellationPeriod = in.readInt();
        reservationPeriod = in.readInt();
        autoConfirm = in.readByte() != 0;
        deleted = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(maxDuration);
        dest.writeInt(minDuration);
        dest.writeInt(cancellationPeriod);
        dest.writeInt(reservationPeriod);
        dest.writeByte((byte) (autoConfirm ? 1 : 0));
        dest.writeByte((byte) (deleted ? 1 : 0));
    }

    public static final Creator<GetServiceDTO> CREATOR = new Creator<GetServiceDTO>() {
        @Override
        public GetServiceDTO createFromParcel(Parcel in) {
            return new GetServiceDTO(in);
        }

        @Override
        public GetServiceDTO[] newArray(int size) {
            return new GetServiceDTO[size];
        }
    };

    // Getters and Setters

    public int getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(int maxDuration) {
        this.maxDuration = maxDuration;
    }

    public int getMinDuration() {
        return minDuration;
    }

    public void setMinDuration(int minDuration) {
        this.minDuration = minDuration;
    }

    public int getCancellationPeriod() {
        return cancellationPeriod;
    }

    public void setCancellationPeriod(int cancellationPeriod) {
        this.cancellationPeriod = cancellationPeriod;
    }

    public int getReservationPeriod() {
        return reservationPeriod;
    }

    public void setReservationPeriod(int reservationPeriod) {
        this.reservationPeriod = reservationPeriod;
    }

    public boolean isAutoConfirm() {
        return autoConfirm;
    }

    public void setAutoConfirm(boolean autoConfirm) {
        this.autoConfirm = autoConfirm;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
