package m3.eventplanner.models;

import java.util.List;

public class CreateCategoryDTO {
    private String name;
    private String description;

    public CreateCategoryDTO() {
    }

    public CreateCategoryDTO(String name, String description, List<Integer> recommendedCategoryIds) {
        this.name = name;
        this.description = description;
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

}
