package m3.eventplanner.models;

public class UpdatePricelistItemDTO {

    private double price;
    private double discount;

    public UpdatePricelistItemDTO() {
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
}