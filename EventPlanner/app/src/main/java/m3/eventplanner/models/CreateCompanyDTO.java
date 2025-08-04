package m3.eventplanner.models;

import java.util.List;

public class CreateCompanyDTO {
    private String email;
    private String name;
    private String phoneNumber;
    private String description;
    private List<String> photos;
    private CreateLocationDTO location;

    public CreateCompanyDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public CreateLocationDTO getLocation() {
        return location;
    }

    public void setLocation(CreateLocationDTO location) {
        this.location = location;
    }
}
