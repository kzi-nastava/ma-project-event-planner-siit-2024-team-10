package m3.eventplanner.models;

public class CreateAccountReportDTO {
    private String description;
    private Integer reporterId;
    private Integer reporteeId;

    public CreateAccountReportDTO() {}

    public CreateAccountReportDTO(String description, Integer reporterId, Integer reporteeId){
        this.description = description;
        this.reporterId = reporterId;
        this.reporteeId = reporteeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getReporterId() {
        return reporterId;
    }

    public void setReporterId(Integer reporterId) {
        this.reporterId = reporterId;
    }

    public Integer getReporteeId() {
        return reporteeId;
    }

    public void setReporteeId(Integer reporteeId) {
        this.reporteeId = reporteeId;
    }
}
