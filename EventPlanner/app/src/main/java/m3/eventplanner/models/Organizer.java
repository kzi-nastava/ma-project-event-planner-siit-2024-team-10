package m3.eventplanner.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Organizer implements Parcelable {
    private int id;
    private String firstName;
    private String lastName;
    private Location location;
    private String email;
    private String phoneNumber;
    private String profilePhoto;

    // Default Constructor
    public Organizer() {
    }

    // Parameterized Constructor
    public Organizer(int id, String firstName, String lastName, Location location, String email, String phoneNumber, String profilePhoto) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.location = location;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profilePhoto = profilePhoto;
    }

    // Getter and Setter methods
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    // Parcelable Implementation
    protected Organizer(Parcel in) {
        id = in.readInt();
        firstName = in.readString();
        lastName = in.readString();
        location = in.readParcelable(Location.class.getClassLoader());
        email = in.readString();
        phoneNumber = in.readString();
        profilePhoto = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeParcelable(location, flags);
        dest.writeString(email);
        dest.writeString(phoneNumber);
        dest.writeString(profilePhoto);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Organizer> CREATOR = new Creator<Organizer>() {
        @Override
        public Organizer createFromParcel(Parcel in) {
            return new Organizer(in);
        }

        @Override
        public Organizer[] newArray(int size) {
            return new Organizer[size];
        }
    };
}
