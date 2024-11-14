package m3.eventplanner.models;

public class Product extends Offering {
    public Product(Long id, String title, String description, String provider, double price) {
        super(id, title, description, provider, price);
    }
}
