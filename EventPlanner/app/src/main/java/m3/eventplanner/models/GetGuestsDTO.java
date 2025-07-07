package m3.eventplanner.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class GetGuestsDTO implements Parcelable, Serializable {
    @SerializedName("guests")
    private List<String> guests;

    public GetGuestsDTO() {}

    public GetGuestsDTO(List<String> guests){
        this.guests = guests;
    }
    public List<String> getGuests(){ return  guests; }

    protected GetGuestsDTO(Parcel in) {
        guests = in.createStringArrayList();
    }

    public static final Creator<GetGuestsDTO> CREATOR = new Creator<GetGuestsDTO>() {
        @Override
        public GetGuestsDTO createFromParcel(Parcel in) {
            return new GetGuestsDTO(in);
        }

        @Override
        public GetGuestsDTO[] newArray(int size) {
            return new GetGuestsDTO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeStringList(guests);
    }
}
