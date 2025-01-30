package m3.eventplanner.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class GetServiceDTO implements Parcelable {
    private int id;
    private GetOfferingCategoryDTO category;
    private boolean pending;
    private GetProviderDTO provider;
    private String name;
    private String description;
    private String specification;
    private double price;
    private double discount;
    private List<String> photos;
    private boolean isVisible;
    private boolean isAvailable;
    private int maxDuration;
    private int minDuration;
    private int cancellationPeriod;
    private int reservationPeriod;
    private boolean autoConfirm;
    private boolean deleted;

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isAutoConfirm() {
        return autoConfirm;
    }

    public void setAutoConfirm(boolean autoConfirm) {
        this.autoConfirm = autoConfirm;
    }

    public int getReservationPeriod() {
        return reservationPeriod;
    }

    public void setReservationPeriod(int reservationPeriod) {
        this.reservationPeriod = reservationPeriod;
    }

    public int getCancellationPeriod() {
        return cancellationPeriod;
    }

    public void setCancellationPeriod(int cancellationPeriod) {
        this.cancellationPeriod = cancellationPeriod;
    }

    public int getMinDuration() {
        return minDuration;
    }

    public void setMinDuration(int minDuration) {
        this.minDuration = minDuration;
    }

    public int getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(int maxDuration) {
        this.maxDuration = maxDuration;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GetProviderDTO getProvider() {
        return provider;
    }

    public void setProvider(GetProviderDTO provider) {
        this.provider = provider;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }

    public GetOfferingCategoryDTO getCategory() {
        return category;
    }

    public void setCategory(GetOfferingCategoryDTO category) {
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public GetServiceDTO() {
    }

    protected GetServiceDTO(Parcel in) {
        id = in.readInt();
        category = in.readParcelable(GetOfferingCategoryDTO.class.getClassLoader());
        pending = in.readByte() != 0;
        provider = in.readParcelable(GetProviderDTO.class.getClassLoader());
        name = in.readString();
        description = in.readString();
        specification = in.readString();
        price = in.readDouble();
        discount = in.readDouble();
        photos = in.createStringArrayList();
        isVisible = in.readByte() != 0;
        isAvailable = in.readByte() != 0;
        maxDuration = in.readInt();
        minDuration = in.readInt();
        cancellationPeriod = in.readInt();
        reservationPeriod = in.readInt();
        autoConfirm = in.readByte() != 0;
        deleted = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeParcelable(category, flags);
        dest.writeByte((byte) (pending ? 1 : 0));
        dest.writeParcelable(provider, flags);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(specification);
        dest.writeDouble(price);
        dest.writeDouble(discount);
        dest.writeStringList(photos);
        dest.writeByte((byte) (isVisible ? 1 : 0));
        dest.writeByte((byte) (isAvailable ? 1 : 0));
        dest.writeInt(maxDuration);
        dest.writeInt(minDuration);
        dest.writeInt(cancellationPeriod);
        dest.writeInt(reservationPeriod);
        dest.writeByte((byte) (autoConfirm ? 1 : 0));
        dest.writeByte((byte) (deleted ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
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
}
