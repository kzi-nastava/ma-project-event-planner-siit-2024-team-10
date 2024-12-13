package m3.eventplanner.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Event implements Parcelable {
    private Long id;
    private String title;
    private double rating;
    private String organizer;

    public Event(Long id, String title, double rating, String organizer) {
        this.id = id;
        this.title = title;
        this.rating = rating;
        this.organizer = organizer;
    }

    public Event() {
    }
    protected Event(Parcel in) {
        id = in.readLong();
        title = in.readString();
        rating = in.readDouble();
        organizer = in.readString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    @Override
    public String toString() {
        return "Event{" +
                "title='" + title + '\'' +
                ", rating='" + rating + '\'' +
                ", organizer='" + organizer + '\'' +
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
        dest.writeDouble(rating);
        dest.writeString(organizer);
    }
    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}
