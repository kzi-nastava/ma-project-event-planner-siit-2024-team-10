package m3.eventplanner.models;

public class GetOfferingDTO {
    private int id;
    private GetOfferingCategoryDTO category;
    private GetProviderDTO provider;
    private String name;
    private String description;
    private String specification;
    private GetLocationDTO location;
    private double price;
    private double discount;
    private double averageRating;
    private boolean isProduct;
}
