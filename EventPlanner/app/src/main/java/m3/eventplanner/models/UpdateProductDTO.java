package m3.eventplanner.models;

public class UpdateProductDTO {
    private String name;
    private String description;
    private double price;
    private double discount;
    private boolean isVisible;
    private boolean isAvailable;

    public UpdateProductDTO(String name, String description, double price, double discount, boolean visible, boolean available) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.discount = discount;
        this.isVisible = visible;
        this.isAvailable = available;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public double getDiscount() {
        return discount;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public boolean isAvailable() {
        return isAvailable;
    }
}
