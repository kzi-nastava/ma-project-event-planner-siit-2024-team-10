package m3.eventplanner.models;

public class ChangeCategoryDTO {
    private int newCategoryId;

    public ChangeCategoryDTO(int newCategoryId) {
        this.newCategoryId = newCategoryId;
    }

    public int getNewCategoryId() {
        return newCategoryId;
    }

    public void setNewCategoryId(int newCategoryId) {
        this.newCategoryId = newCategoryId;
    }
}
