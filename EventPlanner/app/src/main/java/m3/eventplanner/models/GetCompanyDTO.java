package m3.eventplanner.models;

import java.util.List;

public class GetCompanyDTO {
    private String email;
    private String name;
    private String phoneNumber;
    private String description;
    private List<String> photos;
    private GetLocationDTO location;
}
