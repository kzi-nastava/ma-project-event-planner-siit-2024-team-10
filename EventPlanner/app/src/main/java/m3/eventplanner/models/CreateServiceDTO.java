package m3.eventplanner.models;

import java.util.List;

public class CreateServiceDTO {
    private int categoryId;
    private String categoryProposalName;
    private String categoryProposalDescription;
    private int providerID;
    private String name;
    private String description;
    private String specification;
    private double price;
    private double discount;
    private List<String> photos;
    private boolean visible;
    private boolean available;
    private int maxDuration;
    private int minDuration;
    private int cancellationPeriod;
    private int reservationPeriod;
    private boolean autoConfirm;

    public CreateServiceDTO(int categoryId, String categoryProposalName, String categoryProposalDescription, int providerID, String name, String description, double price, double discount, List<String> photos, boolean isVisible, boolean isAvailable, int maxDuration, int minDuration, int cancellationPeriod, int reservationPeriod, boolean autoConfirm) {
        this.categoryId = categoryId;
        this.categoryProposalName = categoryProposalName;
        this.categoryProposalDescription = categoryProposalDescription;
        this.providerID = providerID;
        this.name = name;
        this.description = description;
        this.price = price;
        this.discount = discount;
        this.photos = photos;
        this.visible = isVisible;
        this.available = isAvailable;
        this.maxDuration = maxDuration;
        this.minDuration = minDuration;
        this.cancellationPeriod = cancellationPeriod;
        this.reservationPeriod = reservationPeriod;
        this.autoConfirm = autoConfirm;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryProposalName() {
        return categoryProposalName;
    }

    public void setCategoryProposalName(String categoryProposalName) {
        this.categoryProposalName = categoryProposalName;
    }

    public String getCategoryProposalDescription() {
        return categoryProposalDescription;
    }

    public void setCategoryProposalDescription(String categoryProposalDescription) {
        this.categoryProposalDescription = categoryProposalDescription;
    }

    public int getProviderID() {
        return providerID;
    }

    public void setProviderID(int providerID) {
        this.providerID = providerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

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
}
