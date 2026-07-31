package placement_OS.demo.mapper;

import placement_OS.demo.dto.CompanyRequestDTO;
import placement_OS.demo.dto.CompanyResponseDTO;
import placement_OS.demo.entity.Company;

public class CompanyMapper {

    public static Company toEntity(CompanyRequestDTO request) {

        Company company = new Company();

        company.setCompanyName(request.getCompanyName());
        company.setRole(request.getRole());
        company.setCtc(request.getCtc());
        company.setLocation(request.getLocation());
        company.setJobType(request.getJobType());
        company.setBatch(request.getBatch());
        company.setActive(true);

        return company;
    }

    public static CompanyResponseDTO toResponse(Company company) {

        CompanyResponseDTO response = new CompanyResponseDTO();

        response.setId(company.getId());
        response.setCompanyName(company.getCompanyName());
        response.setRole(company.getRole());
        response.setCtc(company.getCtc());
        response.setLocation(company.getLocation());
        response.setJobType(company.getJobType());
        response.setBatch(company.getBatch());
        response.setActive(company.getActive());

        return response;
    }
}