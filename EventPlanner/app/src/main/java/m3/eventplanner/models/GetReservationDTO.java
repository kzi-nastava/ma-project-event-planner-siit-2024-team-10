package m3.eventplanner.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.time.LocalTime;

public class GetReservationDTO implements Parcelable {
    private int id;
    private String startTime;
    private String endTime;
    private String status;
    private GetEventDTO event;
    private GetServiceDTO service;

    public int getId() {
        return id;
    }

    public LocalTime getStartTime() {
        return LocalTime.of(Integer.parseInt(startTime.split(":" )[0]), Integer.parseInt(startTime.split(":" )[1]));
    }

    public LocalTime getEndTime() {
        return LocalTime.of(Integer.parseInt(endTime.split(":" )[0]), Integer.parseInt(endTime.split(":" )[1]));
    }

    public String getStatus() {
        return status;
    }

    public GetEventDTO getEvent() {
        return event;
    }

    public GetServiceDTO getService() {
        return service;
    }

    // Parcelable implementation
    protected GetReservationDTO(Parcel in) {
        id = in.readInt();
        startTime = in.readString();
        endTime = in.readString();
        status = in.readString();
        event = in.readParcelable(GetEventDTO.class.getClassLoader());
        service = in.readParcelable(GetServiceDTO.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeString(status);
        dest.writeParcelable(event, flags);
        dest.writeParcelable(service, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GetReservationDTO> CREATOR = new Creator<GetReservationDTO>() {
        @Override
        public GetReservationDTO createFromParcel(Parcel in) {
            return new GetReservationDTO(in);
        }

        @Override
        public GetReservationDTO[] newArray(int size) {
            return new GetReservationDTO[size];
        }
    };
}