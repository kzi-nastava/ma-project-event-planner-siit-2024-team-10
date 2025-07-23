package m3.eventplanner.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GetProviderDTO implements Parcelable, Serializable {
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
    @SerializedName("company")
    private GetCompanyDTO company;
    @SerializedName("accountId")
    private int accountId;

    public GetProviderDTO() {
    }

    public GetProviderDTO(int id, String email, String firstName, String lastName, String phoneNumber, String profilePhoto, GetLocationDTO location, GetCompanyDTO company) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.profilePhoto = profilePhoto;
        this.location = location;
        this.company = company;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeInt(id);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(email);
        dest.writeString(phoneNumber);
        dest.writeString(profilePhoto);
        dest.writeParcelable(company, flags);
        dest.writeParcelable(location, flags);
    }
    public static final Parcelable.Creator<GetProviderDTO> CREATOR = new Parcelable.Creator<GetProviderDTO>() {
        @Override
        public GetProviderDTO createFromParcel(Parcel in) {
            return new GetProviderDTO(in);
        }

        @Override
        public GetProviderDTO[] newArray(int size) {
            return new GetProviderDTO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected GetProviderDTO(Parcel in){
        id = in.readInt();
        email = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        phoneNumber = in.readString();
        profilePhoto = in.readString();
        company = in.readParcelable(GetCompanyDTO.class.getClassLoader());
        location = in.readParcelable(GetLocationDTO.class.getClassLoader());
    }

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

    public GetCompanyDTO getCompany() {
        return company;
    }

    public void setCompany(GetCompanyDTO company) {
        this.company = company;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }
}
