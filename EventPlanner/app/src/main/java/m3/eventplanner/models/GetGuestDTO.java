package m3.eventplanner.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GetGuestDTO implements Parcelable, Serializable {
    @SerializedName("email")
    private String email;

    public GetGuestDTO() {}

    public GetGuestDTO(String email){
        this.email = email;
    }
    public String getGuest(){ return  email; }

    protected GetGuestDTO(Parcel in) {
        email = in.readString();
    }

    public static final Creator<GetGuestDTO> CREATOR = new Creator<GetGuestDTO>() {
        @Override
        public GetGuestDTO createFromParcel(Parcel in) {
            return new GetGuestDTO(in);
        }

        @Override
        public GetGuestDTO[] newArray(int size) {
            return new GetGuestDTO[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
