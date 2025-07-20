package m3.eventplanner.models;

import android.os.Parcel;
import android.os.Parcelable;

public class GetCommentDTO implements Parcelable {
    private int id;
    private String content;
    private Status status;
    private int accountId;
    private int rating;
    private String user;

    public GetCommentDTO() {
    }

    public GetCommentDTO(int id, String content, Status status, int accountId, int rating, String user) {
        this.id = id;
        this.content = content;
        this.status = status;
        this.accountId = accountId;
        this.rating = rating;
        this.user = user;
    }

    protected GetCommentDTO(Parcel in) {
        id = in.readInt();
        content = in.readString();
        String statusName = in.readString();
        status = statusName == null ? null : Status.valueOf(statusName);

        accountId = in.readInt();
        rating = in.readInt();
        user = in.readString();
    }

    public static final Creator<GetCommentDTO> CREATOR = new Creator<GetCommentDTO>() {
        @Override
        public GetCommentDTO createFromParcel(Parcel in) {
            return new GetCommentDTO(in);
        }

        @Override
        public GetCommentDTO[] newArray(int size) {
            return new GetCommentDTO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(id);
        parcel.writeString(content);
        parcel.writeString(status == null ? null : status.name());
        parcel.writeInt(accountId);
        parcel.writeInt(rating);
        parcel.writeString(user);
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
