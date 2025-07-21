package m3.eventplanner.models;

import java.util.List;

public class GetBudgetItemDTO {
    private int id;
    private double amount;
    private List<GetServiceDTO> services;
    private List<GetProductDTO> products;
    private GetOfferingCategoryDTO category;
    private boolean isDeleted;

    public GetBudgetItemDTO(List<GetServiceDTO> services, List<GetProductDTO> products, GetOfferingCategoryDTO category, boolean isDeleted) {
        this.services = services;
        this.products = products;
        this.category = category;
        this.isDeleted = isDeleted;
    }

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
}

