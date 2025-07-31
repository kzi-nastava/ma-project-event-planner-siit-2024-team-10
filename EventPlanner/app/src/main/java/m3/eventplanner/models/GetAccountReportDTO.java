package m3.eventplanner.models;

import android.os.Parcel;
import android.os.Parcelable;

public class GetAccountReportDTO implements Parcelable {
    private int id;
    private String description;
    private String reporterEmail;
    private String reporteeEmail;

    // Constructors
    public GetAccountReportDTO() {
    }

    public GetAccountReportDTO(int id, String description, String reporterEmail, String reporteeEmail) {
        this.id = id;
        this.description = description;
        this.reporterEmail = reporterEmail;
        this.reporteeEmail = reporteeEmail;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getReporterEmail() {
        return reporterEmail;
    }

    public String getReporteeEmail() {
        return reporteeEmail;
    }

    // Parcelable implementation
    protected GetAccountReportDTO(Parcel in) {
        id = in.readInt();
        description = in.readString();
        reporterEmail = in.readString();
        reporteeEmail = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(description);
        dest.writeString(reporterEmail);
        dest.writeString(reporteeEmail);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GetAccountReportDTO> CREATOR = new Creator<GetAccountReportDTO>() {
        @Override
        public GetAccountReportDTO createFromParcel(Parcel in) {
            return new GetAccountReportDTO(in);
        }

        @Override
        public GetAccountReportDTO[] newArray(int size) {
            return new GetAccountReportDTO[size];
        }
    };
}
