package m3.eventplanner.models;

public class UpdatedCompanyDTO {
    int id;
    private String phoneNumber;
    private String description;
    private Location location;

    public UpdatedCompanyDTO(int id, String phoneNumber, String description, Location location) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}

