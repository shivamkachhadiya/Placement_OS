package placement_OS.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CompanyRequestDTO {

    @NotBlank(message = "Company Name is required")
    private String companyName;

    @NotBlank(message = "Role is required")
    private String role;

    @NotNull(message = "CTC is required")
    private Double ctc;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Job Type is required")
    private String jobType;

    @NotBlank(message = "Batch is required")
    private String batch;

    public CompanyRequestDTO() {
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Double getCtc() {
        return ctc;
    }

    public void setCtc(Double ctc) {
        this.ctc = ctc;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }
}