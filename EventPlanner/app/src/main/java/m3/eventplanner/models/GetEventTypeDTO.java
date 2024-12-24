package m3.eventplanner.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class GetEventTypeDTO implements Parcelable, Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("active")
    private Boolean active;

    @SerializedName("recommendedCategories")
    private List<GetOfferingCategoryDTO> recommendedCategories;

    public GetEventTypeDTO() {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeByte((byte) (active ? 1 : 0));
        dest.writeTypedList(recommendedCategories);
    }

    public static final Creator<GetEventTypeDTO> CREATOR = new Creator<GetEventTypeDTO>() {
        @Override
        public GetEventTypeDTO createFromParcel(Parcel in) {
            return new GetEventTypeDTO(in);
        }

        @Override
        public GetEventTypeDTO[] newArray(int size) {
            return new GetEventTypeDTO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected GetEventTypeDTO(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        active = in.readByte() != 0;
        recommendedCategories = in.createTypedArrayList(GetOfferingCategoryDTO.CREATOR);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<GetOfferingCategoryDTO> getRecommendedCategories() {
        return recommendedCategories;
    }

    public void setRecommendedCategories(List<GetOfferingCategoryDTO> recommendedCategories) {
        this.recommendedCategories = recommendedCategories;
    }
}
