package m3.eventplanner.models;

public class Product extends Offering {
    public Product(Long id, String title, double rating, String provider, double price) {
        super(id, title, rating, provider, price);
    }
}
