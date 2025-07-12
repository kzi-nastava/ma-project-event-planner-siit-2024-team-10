package m3.eventplanner.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GetNotificationDTO implements Parcelable, Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("date")
    private String date;

    @SerializedName("read")
    private boolean read;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public GetNotificationDTO() {
    }

    protected GetNotificationDTO(Parcel in) {
        id = in.readInt();
        title = in.readString();
        content = in.readString();
        date = in.readString();
        read = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(date);
        dest.writeByte((byte) (read ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GetNotificationDTO> CREATOR = new Creator<GetNotificationDTO>() {
        @Override
        public GetNotificationDTO createFromParcel(Parcel in) {
            return new GetNotificationDTO(in);
        }

        @Override
        public GetNotificationDTO[] newArray(int size) {
            return new GetNotificationDTO[size];
        }
    };

    // Getters and Setters
    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public boolean isRead() { return read; }

    public void setRead(boolean read) { this.read = read; }

    @Override
    public String toString() {
        return "Notification: " + title + " - " + content + " (" + date + ")";
    }
}
