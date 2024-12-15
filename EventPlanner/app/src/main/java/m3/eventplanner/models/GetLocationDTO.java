package m3.eventplanner.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GetLocationDTO implements Parcelable, Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("city")
    private String city;

    @SerializedName("country")
    private String country;

    @SerializedName("street")
    private String street;

    @SerializedName("houseNumber")
    private String houseNumber;

    public GetLocationDTO() {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(city);
        dest.writeString(country);
        dest.writeString(street);
        dest.writeString(houseNumber);
    }

    public static final Creator<GetLocationDTO> CREATOR = new Creator<GetLocationDTO>() {
        @Override
        public GetLocationDTO createFromParcel(Parcel in) {
            return new GetLocationDTO(in);
        }

        @Override
        public GetLocationDTO[] newArray(int size) {
            return new GetLocationDTO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected GetLocationDTO(Parcel in) {
        id = in.readInt();
        city = in.readString();
        country = in.readString();
        street = in.readString();
        houseNumber = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }
}
