package m3.eventplanner.models;

import java.util.List;

public class CreateProductDTO {
    private int categoryId;
    private String categoryProposalName;
    private String categoryProposalDescription;
    private int providerID;
    private String name;
    private String description;
    private double price;
    private double discount;
    private List<String> photos;
    private boolean isVisible;
    private boolean isAvailable;

    public CreateProductDTO(int categoryId, String categoryProposalName, String categoryProposalDescription, int providerID, String name, String description, double price, double discount, List<String> photos, boolean isVisible, boolean isAvailable) {
        this.categoryId = categoryId;
        this.categoryProposalName = categoryProposalName;
        this.categoryProposalDescription = categoryProposalDescription;
        this.providerID = providerID;
        this.name = name;
        this.description = description;
        this.price = price;
        this.discount = discount;
        this.photos = photos;
        this.isVisible = isVisible;
        this.isAvailable = isAvailable;
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
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
