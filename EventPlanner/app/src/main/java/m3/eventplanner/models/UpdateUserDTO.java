package m3.eventplanner.models;

public class UpdateUserDTO {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private CreateLocationDTO location;

    public UpdateUserDTO(String firstName, String lastName, String phoneNumber, CreateLocationDTO location) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.location = location;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public CreateLocationDTO getLocation() {
        return location;
    }

    public void setLocation(CreateLocationDTO location) {
        this.location = location;
    }
}
