package m3.eventplanner.models;

import java.util.List;

public class UpdateCategoryDTO {
    private String name;
    private String description;
    private List<Integer> recommendedCategoryIds;

    public UpdateCategoryDTO() {
    }

    public UpdateCategoryDTO(String name, String description) {
        this.description = description;
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
