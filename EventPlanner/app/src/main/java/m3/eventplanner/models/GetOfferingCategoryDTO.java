package m3.eventplanner.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GetOfferingCategoryDTO implements Parcelable, Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("isDeleted")
    private boolean isDeleted;

    @SerializedName("pending")
    private boolean pending;

    @SerializedName("creatorId")
    private int creatorId;

    public GetOfferingCategoryDTO() {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeByte((byte) (isDeleted ? 1 : 0));
        dest.writeByte((byte) (pending ? 1 : 0));
        dest.writeInt(creatorId);
    }

    public static final Creator<GetOfferingCategoryDTO> CREATOR = new Creator<GetOfferingCategoryDTO>() {
        @Override
        public GetOfferingCategoryDTO createFromParcel(Parcel in) {
            return new GetOfferingCategoryDTO(in);
        }

        @Override
        public GetOfferingCategoryDTO[] newArray(int size) {
            return new GetOfferingCategoryDTO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected GetOfferingCategoryDTO(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        isDeleted = in.readByte() != 0;
        pending = in.readByte() != 0;
        creatorId = in.readInt();
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

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }
}
