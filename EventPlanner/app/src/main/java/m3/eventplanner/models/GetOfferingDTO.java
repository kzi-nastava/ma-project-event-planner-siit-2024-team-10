package m3.eventplanner.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GetOfferingDTO implements Parcelable, Serializable {
    @SerializedName("id")
    private int id;
    @SerializedName("category")
    private GetOfferingCategoryDTO category;
    @SerializedName("provider")
    private GetProviderDTO provider;
    @SerializedName("name")
    private String name;
    @SerializedName("description")
    private String description;
    @SerializedName("specification")
    private String specification;
    @SerializedName("location")
    private GetLocationDTO location;
    @SerializedName("price")
    private double price;
    @SerializedName("discount")
    private double discount;
    @SerializedName("averageRating")
    private double averageRating;
    @SerializedName("isProduct")
    private boolean isProduct;
    @SerializedName("photos")
    private List<String> photos;
    public GetOfferingDTO() {
    }

    public GetOfferingDTO(int id, GetOfferingCategoryDTO category, GetProviderDTO provider, String name, String description, String specification, GetLocationDTO location, double price, double discount, double averageRating, boolean isProduct,List<String> photos) {
        this.id = id;
        this.category = category;
        this.provider = provider;
        this.name = name;
        this.description = description;
        this.specification = specification;
        this.location = location;
        this.price = price;
        this.discount = discount;
        this.averageRating = averageRating;
        this.isProduct = isProduct;
        this.photos=photos;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeInt(id);
        dest.writeParcelable(category,flags);
        dest.writeParcelable(provider, flags);
        dest.writeString(description);
        dest.writeString(name);
        dest.writeString(specification);
        dest.writeDouble(price);
        dest.writeDouble(discount);
        dest.writeDouble(averageRating);
        dest.writeParcelable(location, flags);
        dest.writeBoolean(isProduct);
        dest.writeStringList(photos);
    }
    public static final Creator<GetOfferingDTO> CREATOR = new Creator<GetOfferingDTO>() {
        @Override
        public GetOfferingDTO createFromParcel(Parcel in) {
            return new GetOfferingDTO(in);
        }

        @Override
        public GetOfferingDTO[] newArray(int size) {
            return new GetOfferingDTO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected GetOfferingDTO(Parcel in) {
        id = in.readInt();
        category = in.readParcelable(GetOfferingCategoryDTO.class.getClassLoader());
        provider = in.readParcelable(GetProviderDTO.class.getClassLoader());
        name = in.readString();
        description = in.readString();
        specification = in.readString();
        location = in.readParcelable(GetLocationDTO.class.getClassLoader());
        price = in.readDouble();
        discount = in.readDouble();
        averageRating = in.readDouble();
        isProduct = in.readBoolean();
        photos = new ArrayList<>();
        in.readStringList(photos);
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public GetOfferingCategoryDTO getCategory() {
        return category;
    }

    public void setCategory(GetOfferingCategoryDTO category) {
        this.category = category;
    }

    public GetProviderDTO getProvider() {
        return provider;
    }

    public void setProvider(GetProviderDTO provider) {
        this.provider = provider;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public GetLocationDTO getLocation() {
        return location;
    }

    public void setLocation(GetLocationDTO location) {
        this.location = location;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public boolean isProduct() {
        return isProduct;
    }

    public void setProduct(boolean product) {
        isProduct = product;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }
}
