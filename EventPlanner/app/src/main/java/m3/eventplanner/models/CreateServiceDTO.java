package m3.eventplanner.models;

import java.util.List;

public class CreateServiceDTO {
    private int categoryId;
    private String categoryProposalName;
    private String categoryProposalDescription;
    private int provider;
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

    public CreateServiceDTO(int categoryId, String categoryProposalName, String categoryProposalDescription, int providerID, String name, String description, String specification, double price, double discount, List<String> photos, boolean isVisible, boolean isAvailable, int maxDuration, int minDuration, int cancellationPeriod, int reservationPeriod, boolean autoConfirm) {
        this.categoryId = categoryId;
        this.categoryProposalName = categoryProposalName;
        this.categoryProposalDescription = categoryProposalDescription;
        this.provider = providerID;
        this.name = name;
        this.description = description;
        this.specification = specification;
        this.price = price;
        this.discount = discount;
        this.photos = photos;
        this.isVisible = isVisible;
        this.isAvailable = isAvailable;
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

    public int getProvider() {
        return provider;
    }

    public void setProvider(int providerID) {
        this.provider = providerID;
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
        return isVisible;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
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

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }
}
