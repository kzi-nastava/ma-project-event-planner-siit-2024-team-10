package m3.eventplanner.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GetOrganizerDTO implements Parcelable, Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("email")
    private String email;

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("profilePhoto")
    private String profilePhoto;

    @SerializedName("location")
    private GetLocationDTO location;

    public GetOrganizerDTO() {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(email);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(phoneNumber);
        dest.writeString(profilePhoto);
        dest.writeParcelable(location, flags);
    }

    public static final Creator<GetOrganizerDTO> CREATOR = new Creator<GetOrganizerDTO>() {
        @Override
        public GetOrganizerDTO createFromParcel(Parcel in) {
            return new GetOrganizerDTO(in);
        }

        @Override
        public GetOrganizerDTO[] newArray(int size) {
            return new GetOrganizerDTO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected GetOrganizerDTO(Parcel in) {
        id = in.readInt();
        email = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        phoneNumber = in.readString();
        profilePhoto = in.readString();
        location = in.readParcelable(GetLocationDTO.class.getClassLoader());
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public GetLocationDTO getLocation() {
        return location;
    }

    public void setLocation(GetLocationDTO location) {
        this.location = location;
    }
}
