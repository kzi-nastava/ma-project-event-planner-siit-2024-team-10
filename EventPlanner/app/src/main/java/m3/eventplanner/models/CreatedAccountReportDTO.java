package m3.eventplanner.models;

public class CreatedAccountReportDTO {
    private int id;
    private String description;
    private String reporterEmail;
    private String reporteeEmail;

    public CreatedAccountReportDTO() {
    }

    public CreatedAccountReportDTO(int id, String description, String reporterEmail, String reporteeEmail) {
        this.id = id;
        this.description = description;
        this.reporterEmail = reporterEmail;
        this.reporteeEmail = reporteeEmail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReporterEmail() {
        return reporterEmail;
    }

    public void setReporterEmail(String reporterEmail) {
        this.reporterEmail = reporterEmail;
    }

    public String getReporteeEmail() {
        return reporteeEmail;
    }

    public void setReporteeEmail(String reporteeEmail) {
        this.reporteeEmail = reporteeEmail;
    }
}
