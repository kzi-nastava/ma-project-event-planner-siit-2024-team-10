package m3.eventplanner.models;

public class UpdateCompanyDTO {
    private String phoneNumber;
    private String description;
    private CreateLocationDTO location;

    public UpdateCompanyDTO(String phoneNumber, String description, CreateLocationDTO location) {
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.location = location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CreateLocationDTO getLocation() {
        return location;
    }

    public void setLocation(CreateLocationDTO location) {
        this.location = location;
    }
}
