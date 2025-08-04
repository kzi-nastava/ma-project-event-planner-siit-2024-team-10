package m3.eventplanner.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.time.LocalDateTime;

public class Notification implements  Parcelable {
    private int id;
    private String title;
    private boolean isRead;
    private String content;
    private LocalDateTime date;

    public Notification() {
    }

    public Notification(int id, String title, boolean isRead, String content, LocalDateTime date) {
        this.id = id;
        this.title = title;
        this.isRead = isRead;
        this.content = content;
        this.date = date;
    }

    protected Notification(Parcel in){
        id = in.readInt();
        title = in.readString();
        isRead = in.readBoolean();
        content = in.readString();
        String dateString = in.readString();
        date = dateString != null ? LocalDateTime.parse(dateString) : null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Notification{" +
                ", title='" + title + '\'' +
                ", isRead=" + isRead +
                ", content='" + content + '\'' +
                ", date='"+date+
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeBoolean(isRead);
        dest.writeString(content);
        dest.writeString(date != null ? date.toString() : null);
    }
    public static final Parcelable.Creator<Notification> CREATOR = new Parcelable.Creator<Notification>() {
        @Override
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        @Override
        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };
}
