package m3.eventplanner.models;

import java.util.List;

public class GetBudgetItemDTO {
    private int id;
    private double amount;
    private List<GetServiceDTO> services;
    private List<GetProductDTO> products;
    private GetOfferingCategoryDTO category;
    private boolean isDeleted;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public List<GetServiceDTO> getServices() {
        return services;
    }

    public void setServices(List<GetServiceDTO> services) {
        this.services = services;
    }

    public List<GetProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<GetProductDTO> products) {
        this.products = products;
    }

    public GetOfferingCategoryDTO getCategory() {
        return category;
    }

    public void setCategory(GetOfferingCategoryDTO category) {
        this.category = category;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}

