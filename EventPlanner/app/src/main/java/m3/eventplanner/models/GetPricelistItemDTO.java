package m3.eventplanner.models;

public class GetPricelistItemDTO {
    private int id;
    private int offeringId;
    private String name;
    private double price;
    private double discount;
    private double priceWithDiscount;

    public GetPricelistItemDTO(double priceWithDiscount) {
        this.priceWithDiscount = priceWithDiscount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOfferingId() {
        return offeringId;
    }

    public void setOfferingId(int offeringId) {
        this.offeringId = offeringId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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