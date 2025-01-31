package m3.eventplanner.models;

public class GetUserDTO {
    private int accountId;
    private int userId;
    private String email;
    private String role;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String profilePhoto;
    private GetLocationDTO location;
    private GetCompanyDTO company;

    public int getAccountId() {
        return accountId;
    }

    public int getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public GetLocationDTO getLocation() {
        return location;
    }

    public GetCompanyDTO getCompany() {
        return company;
    }
}
