package m3.eventplanner.models;

public class ChangeCategoryDTO {
    private int categoryId;

    public ChangeCategoryDTO(int newCategoryId) {
        this.categoryId = newCategoryId;
    }

    public int getNewCategoryId() {
        return categoryId;
    }

    public void setNewCategoryId(int newCategoryId) {
        this.categoryId = newCategoryId;
    }
}
