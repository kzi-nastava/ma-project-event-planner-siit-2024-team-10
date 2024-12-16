package m3.eventplanner.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class GetCompanyDTO implements Parcelable, Serializable {
    @SerializedName("email")
    private String email;
    @SerializedName("name")
    private String name;
    @SerializedName("phoneNumber")
    private String phoneNumber;
    @SerializedName("description")
    private String description;
    @SerializedName("photo_url")
    private List<String> photos;
    @SerializedName("location")
    private GetLocationDTO location;

    public GetCompanyDTO() {
    }

    public GetCompanyDTO(String email, String name, String phoneNumber, String description, List<String> photos, GetLocationDTO location) {
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.photos = photos;
        this.location = location;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(email);
        dest.writeString(name);
        dest.writeString(phoneNumber);
        dest.writeString(description);
        dest.writeList(photos);
        dest.writeParcelable(location, flags);
    }
    public static final Creator<GetCompanyDTO> CREATOR = new Creator<GetCompanyDTO>() {
        @Override
        public GetCompanyDTO createFromParcel(Parcel in) {
            return new GetCompanyDTO(in);
        }

        @Override
        public GetCompanyDTO[] newArray(int size) {
            return new GetCompanyDTO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected GetCompanyDTO(Parcel in){
        email = in.readString();
        name = in.readString();
        description = in.readString();
        phoneNumber = in.readString();
        photos = in.createStringArrayList();
        location = in.readParcelable(GetLocationDTO.class.getClassLoader());
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

    public GetLocationDTO getLocation() {
        return location;
    }

    public void setLocation(GetLocationDTO location) {
        this.location = location;
    }
}
