package m3.eventplanner.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Offering implements Parcelable {
    private Long id;
    private String title;
    private double rating;
    private String provider;

    private double price;

    public Offering(Long id, String title, double rating, String provider, double price) {
        this.id = id;
        this.title = title;
        this.rating = rating;
        this.provider = provider;
        this.price = price;
    }

    public Offering() {
    }
    protected Offering(Parcel in) {
        id = in.readLong();
        title = in.readString();
        rating = in.readDouble();
        provider = in.readString();
        price = in.readDouble();
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
        return provider;
    }

    public void setOrganizer(String provider) {
        this.provider = provider;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Offering{" +
                "title='" + title + '\'' +
                ", rating='" + rating + '\'' +
                ", provider='" + provider + '\'' +
                ", price='" + price + '\'' +
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
        dest.writeString(provider);
        dest.writeDouble(price);
    }
    public static final Creator<Offering> CREATOR = new Creator<Offering>() {
        @Override
        public Offering createFromParcel(Parcel in) {
            return new Offering(in);
        }

        @Override
        public Offering[] newArray(int size) {
            return new Offering[size];
        }
    };
}
