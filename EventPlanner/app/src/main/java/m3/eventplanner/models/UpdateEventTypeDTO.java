package m3.eventplanner.models;

import java.util.List;

public class UpdateEventTypeDTO {
    private String description;
    private List<Integer> recommendedCategoryIds;

    public UpdateEventTypeDTO() {
    }

    public UpdateEventTypeDTO(String description, List<Integer> recommendedCategoryIds) {
        this.description = description;
        this.recommendedCategoryIds = recommendedCategoryIds;
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
