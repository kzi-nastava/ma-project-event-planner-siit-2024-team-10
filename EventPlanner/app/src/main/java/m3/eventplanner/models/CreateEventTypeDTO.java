package m3.eventplanner.models;

import java.util.List;

public class CreateEventTypeDTO {
    private String name;
    private String description;
    private List<Integer> recommendedCategoryIds;

    public CreateEventTypeDTO() {
    }

    public CreateEventTypeDTO(String name, String description, List<Integer> recommendedCategoryIds) {
        this.name = name;
        this.description = description;
        this.recommendedCategoryIds = recommendedCategoryIds;
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

    public List<Integer> getRecommendedCategoryIds() {
        return recommendedCategoryIds;
    }

    public void setRecommendedCategoryIds(List<Integer> recommendedCategoryIds) {
        this.recommendedCategoryIds = recommendedCategoryIds;
    }
}
