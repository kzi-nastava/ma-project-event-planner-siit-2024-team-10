package m3.eventplanner.models;

import java.util.List;

public class UpdatedEventTypeDTO {
    private int id;
    private String name;
    private String description;
    private List<GetOfferingCategoryDTO> recommendedCategories;

    public UpdatedEventTypeDTO() {
    }

    public UpdatedEventTypeDTO(int id, String name, String description, List<GetOfferingCategoryDTO> recommendedCategories) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.recommendedCategories = recommendedCategories;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public List<GetOfferingCategoryDTO> getRecommendedCategories() {
        return recommendedCategories;
    }

    public void setRecommendedCategories(List<GetOfferingCategoryDTO> recommendedCategories) {
        this.recommendedCategories = recommendedCategories;
    }
}
